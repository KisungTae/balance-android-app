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
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
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
import java.io.File


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

    private var fetchPhotosStatus = Resource.Status.SUCCESS
    private var fetchProfileStatus = Resource.Status.SUCCESS

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
        if (result.resultCode == Activity.RESULT_OK) {
            launchCropImage(getCapturedPhotoUri())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        bindUI()
        viewModel.fetchPhotos()
        viewModel.syncPhotos()
        viewModel.fetchProfile()

    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        observeFetchProfileLiveData()
        observeSaveAboutLiveData()
        setupListeners()
        observeFetchPhotosLiveData()
        setupPhotoPickerRecyclerView()
        observeSyncPhotosLiveData()
        observeUploadPhotoLiveData()
        observeOrderPhotosLiveData()
        observeDeletePhotoLiveData()


    }


    private fun observeFetchPhotosLiveData() {
        viewModel.fetchPhotosLiveData.observe(viewLifecycleOwner) { resource ->
            fetchPhotosStatus = resource.status
            updateRefreshBtn()
            if (resource.isError() && validateLogin(resource)) showFetchPhotosError(resource.error, resource.errorMessage)
        }
    }

    private fun showFetchPhotosError(error: String?, errorMessage: String?) {
        val errorTitle = getString(R.string.error_title_fetch_photos)
        ErrorDialog.show(error, errorTitle, errorMessage, RequestCode.FETCH_PHOTOS, this, childFragmentManager)
    }


    private fun observeDeletePhotoLiveData() {
        viewModel.deletePhotoLiveData.observe(viewLifecycleOwner) { resource ->
            if (resource.isError() && validateLogin(resource)) {
                val errorTitle = getString(R.string.error_title_delete_photo)
                ErrorDialog.show(resource.error, errorTitle, resource.errorMessage, childFragmentManager)
            }
        }
    }

    private fun observeOrderPhotosLiveData() {
        viewModel.orderPhotosLiveData.observe(viewLifecycleOwner) { resource ->
            if (resource.isError() && validateLogin(resource)) {
                val errorTitle = getString(R.string.error_title_order_photos)
                ErrorDialog.show(resource.error, errorTitle, resource.errorMessage, childFragmentManager)
            }
        }
    }

    private fun observeSyncPhotosLiveData() {
        viewModel.syncPhotosLiveData.observe(viewLifecycleOwner) {
            if (it) observePhotosLiveData()
        }
    }

    private fun observeUploadPhotoLiveData() {
        viewModel.uploadPhotoLiveData.observe(viewLifecycleOwner) { resource ->
            if (resource.isError()
                && resource.error != ExceptionCode.PHOTO_ALREADY_EXIST_EXCEPTION
                && validateLogin(resource)) {
                val errorTitle = getString(R.string.error_title_add_photo)
                ErrorDialog.show(resource.error, errorTitle, resource.errorMessage, childFragmentManager)
            }
        }
    }

    private fun observePhotosLiveData() {
        viewModel.getPhotosLiveData().observe(viewLifecycleOwner) {
            photoPickerRecyclerViewAdapter.submit(it)
        }
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
            if (fetchProfileStatus == Resource.Status.ERROR) viewModel.fetchProfile()
            if (fetchPhotosStatus == Resource.Status.ERROR) viewModel.fetchPhotos()
        }
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
            PHOTO_PICKER_NUM_OF_COLUMNS
        ) {
            override fun canScrollVertically(): Boolean = false
            override fun canScrollHorizontally(): Boolean = false
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0
        ) {
            override fun isLongPressDragEnabled(): Boolean {
                val isSwipeable = photoPickerRecyclerViewAdapter.isSwipeable()
                if (!isSwipeable) {
                    val errorTitle = getString(R.string.error_title_order_photos)
                    val errorMessage = getString(R.string.photo_not_orderable_exception)
                    ErrorDialog.show(null, errorTitle, errorMessage, childFragmentManager)
                }
                return isSwipeable
            }

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
        binding.btnProfileBack.setOnClickListener { popBackStack(MainViewPagerFragment.TAG) }
    }

    override fun onHeightChanged(height: Int) {
        binding.tvProfileHeight.text = height.toString()
    }


    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.FETCH_PROFILE -> viewModel.fetchProfile()
            RequestCode.FETCH_PHOTOS -> viewModel.fetchPhotos()
        }
    }

    override fun onClickPhotoPicker(position: Int) {
        if (fetchPhotosStatus == Resource.Status.LOADING || fetchPhotosStatus == Resource.Status.ERROR) {
            val message = getString(R.string.fetching_photos_message)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (fetchPhotosStatus == Resource.Status.ERROR) viewModel.fetchPhotos()
            return
        }

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
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val errorTitle = getString(R.string.error_title_crop_image)
                    val errorMessage = result.error.localizedMessage ?: ""
                    ErrorDialog.show(null, errorTitle, errorMessage, childFragmentManager)
                }
                deleteCapturedPhoto()
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCapturedPhotoUri())
        readFromCaptureActivityResult.launch(intent)
    }

    private fun getCapturedPhotoUri(): Uri {
        return FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName.toString() + FILE_PROVIDER_SUFFIX,
            getCapturedPhotoFile()
        )
    }

    private fun getCapturedPhotoFile(): File {
        return File(requireContext().getExternalFilesDir(null), CAPTURED_PHOTO_NAME)
    }

    private fun deleteCapturedPhoto() {
        val file = getCapturedPhotoFile()
        if (file.exists()) file.delete()
    }


    private fun observeFetchProfileLiveData() {
        viewModel.fetchProfileLiveData.observe(viewLifecycleOwner) { resource ->
            fetchProfileStatus = resource.status
            updateRefreshBtn()
            when {
                resource.isSuccess() -> showFetchProfileSuccess(resource.data)
                resource.isLoading() -> {
                    disableProfileEdit()
                    setupProfile(resource.data)
                }
                resource.isError() && validateLogin(resource) -> showFetchProfileError(resource.error, resource.errorMessage)
            }
        }
    }

    private fun showFetchProfileSuccess(profileDomain: ProfileDomain?) {
        enableProfileEdit()
        setupProfile(profileDomain)
    }

    private fun showFetchProfileError(error: String?, errorMessage: String?) {
        disableProfileEdit()
        val errorTitle = getString(R.string.error_title_fetch_profile)
        ErrorDialog.show(error, errorTitle, errorMessage, RequestCode.FETCH_PROFILE, this, childFragmentManager)
    }

    private fun setupProfile(profileDomain: ProfileDomain?) {
        profileDomain?.let { _profileDomain ->
            binding.tvProfileName.text = _profileDomain.name
            binding.tvProfileDateOfBirth.text = _profileDomain.birth.format(DateTimePattern.ofDate())
            binding.tvProfileHeight.text = _profileDomain.height?.toString() ?: ""
            binding.etProfileAbout.setText(_profileDomain.about)
            when (_profileDomain.gender) {
                Gender.FEMALE -> setupGender(binding.tvProfileGenderFemale, R.drawable.sh_radio_button_left_checked)
                else -> setupGender(binding.tvProfileGenderMale, R.drawable.sh_radio_button_right_checked)
            }
        }
    }

    private fun observeSaveAboutLiveData() {
        viewModel.saveAboutLiveData.observe(viewLifecycleOwner) { resource ->
            when {
                resource.isLoading() -> {
                    hideFieldErrors()
                    disableProfileEdit()
                    showLoading()
                }
                resource.isSuccess() -> showSaveAboutSuccess()
                resource.isError() && validateLogin(resource) -> {
                    showSaveAboutError(resource.data, resource.error, resource.errorMessage, resource.fieldErrorMessages)
                }
            }
        }
    }

    private fun setupGender(textView: TextView, backgroundId: Int) {
        textView.background = ContextCompat.getDrawable(requireContext(), backgroundId)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary))
    }

    private fun showSaveAboutSuccess() {
        hideLoadingAndRefreshBtn()
        hideFieldErrors()
        enableProfileEdit()
        Toast.makeText(requireContext(), getString(R.string.save_about_success_message), Toast.LENGTH_SHORT).show()
    }

    private fun showSaveAboutError(
        profileDomain: ProfileDomain?,
        error: String?,
        errorMessage: String?,
        fieldErrorMessages: Map<String, String>?
    ) {
        enableProfileEdit()
        hideLoadingAndRefreshBtn()

        fieldErrorMessages?.let { _fieldErrorMessages ->
            for ((key, value) in _fieldErrorMessages) {
                val errorTextView = getErrorViewByTag(key)
                errorTextView.text = value
                errorTextView.visibility = View.VISIBLE
            }
        } ?: kotlin.run {
            setupProfile(profileDomain)
            hideFieldErrors()
            val errorTitle = getString(R.string.error_title_save_about)
            ErrorDialog.show(error, errorTitle, errorMessage, childFragmentManager)
        }
    }

    private fun hideFieldErrors() {
        binding.tvAboutError.visibility = View.GONE
    }

    private fun getErrorViewByTag(tag: String): TextView {
        return binding.root.findViewWithTag("${tag}Error")
    }

    private fun saveAbout(): Boolean {
        val height = binding.tvProfileHeight.text.toString().toIntOrNull()
        val about = binding.etProfileAbout.text.toString()
        viewModel.saveAbout(height, about)
        return true
    }

    private fun disableProfileEdit() {
        binding.btnProfileSave.isEnabled = false
        binding.etProfileAbout.isEnabled = false
        binding.llProfileHeightWrapper.isClickable = false
    }

    private fun enableProfileEdit() {
        binding.btnProfileSave.isEnabled = true
        binding.etProfileAbout.isEnabled = true
        binding.llProfileHeightWrapper.isClickable = true
    }


    private fun showLoading() {
        binding.btnProfileRefresh.visibility = View.GONE
        binding.skvProfileLoading.visibility = View.VISIBLE
    }

    private fun showRefreshBtn() {
        binding.btnProfileRefresh.visibility = View.VISIBLE
        binding.skvProfileLoading.visibility = View.GONE
    }

    private fun hideLoadingAndRefreshBtn() {
        binding.btnProfileRefresh.visibility = View.INVISIBLE
        binding.skvProfileLoading.visibility = View.GONE
    }

    private fun updateRefreshBtn() {
        if (fetchProfileStatus == Resource.Status.LOADING || fetchPhotosStatus == Resource.Status.LOADING) showLoading()
        else if (fetchProfileStatus == Resource.Status.ERROR || fetchPhotosStatus == Resource.Status.ERROR) showRefreshBtn()
        else hideLoadingAndRefreshBtn()
    }


    companion object {
        private const val CAPTURED_PHOTO_NAME = "capturedPhoto.jpg"
        private const val FILE_PROVIDER_SUFFIX = ".fileProvider"
        private const val PHOTO_PICKER_NUM_OF_COLUMNS = 3
    }
}