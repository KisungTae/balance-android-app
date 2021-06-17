package com.beeswork.balance.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.databinding.FragmentProfileBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.beeswork.balance.ui.profile.balancegame.ProfileBalanceGameDialog
import com.beeswork.balance.ui.profile.photo.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : BaseFragment(),
    KodeinAware,
    HeightOptionDialog.HeightOptionDialogListener,
    PhotoPickerRecyclerViewAdapter.PhotoPickerListener,
    ErrorDialog.OnRetryListener,
    PhotoPickerOptionListener {

    override val kodein by closestKodein()
    private val preferenceProvider: PreferenceProvider by instance()
    private val viewModelFactory: ProfileViewModelFactory by instance()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var photoPickerRecyclerViewAdapter: PhotoPickerRecyclerViewAdapter

    private val requestGalleryPermission = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) selectPhotoFromGallery()
    }

    private val requestCameraPermission = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) selectPhotoFromCapture()
    }

    private val readFromGalleryActivityResult = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) result.data?.data?.let { uri -> launchCropImage(uri) }
    }

    private val readFromCaptureActivityResult = registerForActivityResult(StartActivityForResult()) { result ->
        println("result.resultCode ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) result.data?.data?.let { uri -> launchCropImage(uri) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        bindUI()
//        viewModel.test()
//        viewModel.fetchPhotos()
        viewModel.fetchProfile()

    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        observeFetchProfileLiveData()
        setupPhotoPickerRecyclerView()
        observeSaveAboutLiveData()
        setupListeners()
        observeUploadPhotoLiveData()
        observeSyncPhotosLiveData()
        observeOrderPhotosLiveData()
        observeDeletePhotoLiveData()
        viewModel.syncPhotos()
    }

    private fun observeDeletePhotoLiveData() {
        viewModel.deletePhotoLiveData.observe(viewLifecycleOwner) {
            if (it.isError() && validateAccount(it.error, it.errorMessage)) showErrorDialog(
                it.error,
                getString(R.string.error_title_delete_photo),
                it.errorMessage
            )
        }
    }

    private fun observeOrderPhotosLiveData() {
        viewModel.orderPhotosLiveData.observe(viewLifecycleOwner) {
            if (it.isError() && validateAccount(it.error, it.errorMessage)) showErrorDialog(
                it.error,
                getString(R.string.error_title_order_photos),
                it.errorMessage
            )
        }
    }

    private fun observeSyncPhotosLiveData() {
        viewModel.syncPhotosLiveData.observe(viewLifecycleOwner) {
            if (it) observePhotosLiveData()
        }
    }

    private fun observeUploadPhotoLiveData() {
        viewModel.uploadPhotoLiveData.observe(viewLifecycleOwner) {
            if (it.isError()
                && validateAccount(it.error, it.errorMessage)
                && it.error != ExceptionCode.PHOTO_ALREADY_EXIST_EXCEPTION
            ) showErrorDialog(it.error, getString(R.string.error_title_add_photo), it.errorMessage)
        }
    }

    private fun observePhotosLiveData() {
        viewModel.getPhotosLiveData().observe(viewLifecycleOwner) {
            photoPickerRecyclerViewAdapter.submit(it)
        }
    }

    private fun observeSaveAboutLiveData() {
        viewModel.saveAboutLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading()
                it.isError() && validateAccount(it.error, it.errorMessage) -> showSaveAboutError(it)
                it.isSuccess() -> showSaveAboutSuccessToast()
            }
        }
    }

    private fun showSaveAboutSuccessToast() {
        Toast.makeText(requireContext(), getString(R.string.save_about_success_message), Toast.LENGTH_SHORT).show()
    }

    private fun showSaveAboutError(resource: Resource<EmptyResponse>) {
        hideLoading()
        val errorTitle = getString(R.string.error_title_save_about)
        showErrorDialog(resource.error, errorTitle, resource.errorMessage, RequestCode.SAVE_ABOUT, this)
    }

    private fun showLoading() {
        binding.btnProfileSave.visibility = View.GONE
        binding.spvProfileLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.btnProfileSave.visibility = View.VISIBLE
        binding.spvProfileLoading.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.llProfileHeightWrapper.setOnClickListener {
            val height = binding.tvProfileHeight.text.toString().toIntOrNull()
            HeightOptionDialog(this, height).show(childFragmentManager, HeightOptionDialog.TAG)
        }
        binding.btnProfileEditBalanceGame.setOnClickListener {
            ProfileBalanceGameDialog().show(childFragmentManager, ProfileBalanceGameDialog.TAG)
        }
        binding.btnProfileRefresh.setOnClickListener {
            if (it.visibility == View.VISIBLE) {
//                viewModel.fetchPhotos()
                it.visibility = View.INVISIBLE
            }
        }
    }

    private fun observeFetchProfileLiveData() {
        viewModel.fetchProfileLiveData.observe(viewLifecycleOwner) {
            binding.tvProfileName.text = it.name
            binding.tvProfileDateOfBirth.text = it.birth.format(DateTimePattern.ofDate())
            binding.tvProfileHeight.text = it.height?.toString() ?: ""
            binding.etProfileAbout.setText(it.about)
            when (it.gender) {
                Gender.FEMALE -> setupGender(binding.tvProfileGenderFemale, R.drawable.sh_radio_button_left_checked)
                else -> setupGender(binding.tvProfileGenderMale, R.drawable.sh_radio_button_right_checked)
            }
        }
    }

    private fun setupGender(textView: TextView, backgroundId: Int) {
        textView.background = ContextCompat.getDrawable(requireContext(), backgroundId)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary))
    }

    private fun setupPhotoPickerRecyclerView() {
        photoPickerRecyclerViewAdapter = PhotoPickerRecyclerViewAdapter(
            requireContext(),
            this,
            preferenceProvider.getAccountId()
        )
        binding.rvPhotoPicker.adapter = photoPickerRecyclerViewAdapter
        binding.rvPhotoPicker.layoutManager = object : GridLayoutManager(
            requireContext(),
            ProfileDialog.PHOTO_PICKER_GALLERY_COLUMN_NUM
        ) {
            override fun canScrollVertically(): Boolean = false
            override fun canScrollHorizontally(): Boolean = false
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                photoPickerRecyclerViewAdapter.swapPhotos(
                    viewHolder.absoluteAdapterPosition,
                    target.absoluteAdapterPosition
                )
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                viewHolder.itemView.setTag(androidx.recyclerview.R.id.item_touch_helper_previous_elevation, null)
                viewModel.orderPhotos(photoPickerRecyclerViewAdapter.getPhotoPickerSequences())
            }

        })
        itemTouchHelper.attachToRecyclerView(binding.rvPhotoPicker)
    }

    private fun setupToolBar() {
        binding.btnProfileSave.setOnClickListener { saveAbout() }
        binding.btnProfileBack.setOnClickListener { popBackStack() }
    }

    private fun saveAbout(): Boolean {
        val height = binding.tvProfileHeight.text.toString().toIntOrNull()
        val about = binding.etProfileAbout.text.toString()
        viewModel.saveAbout(height, about)
        return true
    }

    private suspend fun test() {
        repeat(1000) {
            println("ui lifecyclecope: $it")
        }
    }

    private fun popBackStack() {
        requireActivity().supportFragmentManager.popBackStack(
            MainViewPagerFragment.TAG,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    override fun onHeightChanged(height: Int) {
        binding.tvProfileHeight.text = height.toString()
    }


    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.SAVE_ABOUT -> saveAbout()
//            RequestCode.FETCH_PHOTOS -> viewModel.fetchPhotos()
        }
    }

    override fun onClickPhotoPicker(position: Int) {
        val photoPicker = photoPickerRecyclerViewAdapter.getPhotoPicker(position)
        when (photoPicker.status) {
            PhotoStatus.EMPTY -> UploadPhotoOptionDialog(this).show(childFragmentManager, UploadPhotoOptionDialog.TAG)
            PhotoStatus.OCCUPIED -> DeletePhotoOptionDialog(photoPicker.key, this).show(
                childFragmentManager,
                DeletePhotoOptionDialog.TAG
            )
            PhotoStatus.UPLOAD_ERROR -> UploadPhotoErrorOptionDialog(photoPicker.uri, photoPicker.key, this).show(
                childFragmentManager,
                UploadPhotoErrorOptionDialog.TAG
            )
            PhotoStatus.DOWNLOAD_ERROR -> DownloadPhotoErrorOptionDialog(photoPicker.key, this).show(
                childFragmentManager,
                DownloadPhotoErrorOptionDialog.TAG
            )
            else -> println("")
        }
    }

    override fun onDownloadPhotoError(photoKey: String?) {
        viewModel.onDownloadPhotoError(photoKey)
    }

    override fun onDownloadPhotoSuccess(photoKey: String?) {
        viewModel.onDownloadPhotoSuccess(photoKey)
    }

    private fun hasExternalStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun selectPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = PhotoConstant.PHOTO_INTENT_TYPE
        intent.putExtra(Intent.EXTRA_MIME_TYPES, PhotoConstant.PHOTO_MIME_TYPES)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        readFromGalleryActivityResult.launch(intent)
    }

    // CASE 1. Read an image from gallery and deleted when launch CropImage --> CropImage activity will result in error
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) viewModel.uploadPhoto(result.uri, null)
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) showErrorDialog(
                    getString(R.string.error_title_crop_image),
                    result.error.localizedMessage ?: "",
                    null
                )
            }
        }
    }

    private fun launchCropImage(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(PhotoPicker.MAX_PHOTO_WIDTH, PhotoPicker.MAX_PHOTO_HEIGHT)
            .setAutoZoomEnabled(false)
            .setFixAspectRatio(true)
            .setMaxCropResultSize(PhotoPicker.MAX_PHOTO_WIDTH, PhotoPicker.MAX_PHOTO_HEIGHT)
            .setMinCropResultSize(PhotoPicker.MAX_PHOTO_WIDTH, PhotoPicker.MAX_PHOTO_HEIGHT)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(requireContext(), this)
    }


    override fun reuploadPhoto(photoUri: Uri?, photoKey: String?) {
        viewModel.uploadPhoto(photoUri, photoKey)
    }

    override fun redownloadPhoto(photoKey: String?) {
        viewModel.downloadPhoto(photoKey)
    }

    override fun deletePhoto(photoKey: String?) {
        viewModel.deletePhoto(photoKey)
    }

    override fun uploadPhotoFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasExternalStoragePermission()) selectPhotoFromGallery()
            else requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else selectPhotoFromGallery()
    }

    override fun uploadPhotoFromCapture() {
        val cameraIsAvailable = activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ?: false
        if (cameraIsAvailable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (hasCameraPermission()) selectPhotoFromCapture()
                else requestCameraPermission.launch(Manifest.permission.CAMERA)
            } else selectPhotoFromCapture()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun selectPhotoFromCapture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.type = ProfileDialog.PHOTO_INTENT_TYPE
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, ProfileDialog.PHOTO_MIME_TYPES)
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        readFromCaptureActivityResult.launch(intent)
    }
}