package com.beeswork.balance.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.databinding.FragmentProfileBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
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
    AddPhotoOptionDialog.AddPhotoOptionListener {

    override val kodein by closestKodein()
    private val preferenceProvider: PreferenceProvider by instance()
    private val viewModelFactory: ProfileViewModelFactory by instance()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var photoPickerRecyclerViewAdapter: PhotoPickerRecyclerViewAdapter

    private val requestPermissionForGallery = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) selectPhotoFromGallery()
    }

    private val readFromGalleryActivityResult = registerForActivityResult(StartActivityForResult()) { result ->
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
        setupFetchProfileLiveDataObserver()
        setupPhotoPickerRecyclerView()
        setupSaveAboutLiveDataObserver()
//        setupFetchPhotosLiveDataObserver()
        setupPhotosLiveDataObserver()
        setupListeners()
        setupUploadPhotoLiveData()
    }

    private fun setupUploadPhotoLiveData() {
        viewModel.uploadPhotoLiveData.observe(viewLifecycleOwner) {
            if (it.isError()
                && validateAccount(it.error, it.errorMessage)
                && it.error != ExceptionCode.PHOTO_ALREADY_EXIST_EXCEPTION)
                showErrorDialog(it.error, getString(R.string.error_title_add_photo), it.errorMessage)
        }
    }

    private fun setupPhotosLiveDataObserver() {
        viewModel.getPhotosLiveData().observe(viewLifecycleOwner) {
            photoPickerRecyclerViewAdapter.submit(it)
        }
        viewModel.syncPhotos()
    }

    private fun setupSaveAboutLiveDataObserver() {
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

    private fun setupFetchProfileLiveDataObserver() {
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
            PhotoStatus.EMPTY -> AddPhotoOptionDialog(this).show(childFragmentManager, AddPhotoOptionDialog.TAG)
//            PhotoPicker.Status.OCCUPIED,
//            PhotoPicker.Status.UPLOAD_ERROR,
//            PhotoPicker.Status.DOWNLOAD_ERROR -> EditPhotoOptionDialog().show(
//                childFragmentManager,
//                EditPhotoOptionDialog.TAG
//            )
//            else -> println("")
        }
    }

    private fun hasExternalStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onUploadPhotoFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasExternalStoragePermission()) selectPhotoFromGallery()
            else requestPermissionForGallery.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else selectPhotoFromGallery()
    }

    private fun selectPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK);
        intent.type = ProfileDialog.PHOTO_INTENT_TYPE
        intent.putExtra(Intent.EXTRA_MIME_TYPES, ProfileDialog.PHOTO_MIME_TYPES)
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

    private fun uploadPhoto(photoUri: Uri?) {
//        viewModel.uploadPhoto(photoUri)
//        photoUri?.path?.let { path ->
//            val extension = MimeTypeMap.getFileExtensionFromUrl(path)
//            val key = photoKey ?: "${generatePhotoKey()}.$extension"
//            val adapter = photoPickerRecyclerViewAdapter()
//            val sequence = adapter.uploadPhoto(key, photoUri)
//            if (sequence == -1) return
//
//            CoroutineScope(Dispatchers.IO).launch {
//                val response = balanceRepository.uploadPhoto(key, extension, path, sequence)
//                withContext(Dispatchers.Main) {
//                    if (response.isSuccess())
//                        adapter.updatePhotoPickerStatus(key, PhotoPicker.Status.OCCUPIED)
//                    else if (response.isError()) {
//                        ExceptionDialog(response.errorMessage, null).show(
//                            childFragmentManager,
//                            ExceptionDialog.TAG
//                        )
//                        adapter.updatePhotoPickerStatus(key, PhotoPicker.Status.UPLOAD_ERROR)
//                    }
//                }
//            }
//        } ?: ExceptionDialog(getString(R.string.photo_not_found_exception), null).show(
//            childFragmentManager,
//            ExceptionDialog.TAG
//        )
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


    override fun onUploadPhotoFromCapture() {
        println("onUploadPhotoFromCapture")
    }


//    private fun setupFetchPhotosLiveDataObserver() {
//        viewModel.fetchPhotosLiveData.observe(viewLifecycleOwner) {
//            when {
//                it.isSuccess() -> it.data?.let { photos -> photoPickerRecyclerViewAdapter.initPhotoPicker(photos) }
//                it.isError() && validateAccount(it.error, it.errorMessage) -> {
//                    binding.btnProfileRefresh.visibility = View.VISIBLE
//                    showErrorDialog(
//                        it.error,
//                        getString(R.string.error_title_fetch_photos),
//                        it.errorMessage,
//                        RequestCode.FETCH_PHOTOS,
//                        this
//                    )
//                }
//            }
//        }
//    }
}