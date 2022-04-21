package com.beeswork.balance.ui.photofragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutPhotoPickerBinding
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.observeUIState
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.profilefragment.ProfileFragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.launch
import java.io.File

open class BasePhotoFragment : BaseFragment(), PhotoPickerRecyclerViewAdapter.PhotoPickerListener, PhotoPickerOptionListener {

    private lateinit var photoPickerRecyclerViewAdapter: PhotoPickerRecyclerViewAdapter
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var binding: LayoutPhotoPickerBinding

    private val requestGalleryPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            selectPhotoFromGallery()
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            selectPhotoFromCapture()
        }
    }

    private val readFromGalleryActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                launchCropImage(uri)
            }
        }
    }

    private val readFromCaptureActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            launchCropImage(getCapturedPhotoUri())
        }
    }

    protected fun onViewCreated(viewModel: PhotoViewModel, layoutPhotoPickerBinding: LayoutPhotoPickerBinding) {
        this.photoViewModel = viewModel
        this.binding = layoutPhotoPickerBinding
        setupPhotoPickerRecyclerView(binding.rvPhotoPicker)
        setupBtnListeners()
        observeSyncPhotosUIStateLiveData()
        observeUploadPhotoUIStateLiveData()
        observeDeletePhotoUIStateLiveData()
        observeOrderPhotosUIStateLiveData()
        photoViewModel.syncPhotos()
    }

    private fun observeOrderPhotosUIStateLiveData() {
        photoViewModel.orderPhotosUIStateLiveData.observeUIState(viewLifecycleOwner, activity) { orderPhotosUIState ->
            if (orderPhotosUIState.showError) {
                val title = getString(R.string.error_title_order_photos)
                val message = MessageSource.getMessage(requireContext(), orderPhotosUIState.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun observeDeletePhotoUIStateLiveData() {
        photoViewModel.deletePhotoUIStateLiveData.observeUIState(viewLifecycleOwner, activity) { deletePhotoUIState ->
            if (deletePhotoUIState.showError) {
                val title = getString(R.string.error_title_delete_photo)
                val message = MessageSource.getMessage(requireContext(), deletePhotoUIState.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun observeUploadPhotoUIStateLiveData() {
        photoViewModel.uploadPhotoUIStateLiveData.observeUIState(viewLifecycleOwner, activity) { uploadPhotoUIState ->
            if (uploadPhotoUIState.showError) {
                val title = getString(R.string.error_title_add_photo)
                val message = MessageSource.getMessage(requireContext(), uploadPhotoUIState.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun setupBtnListeners() {
        binding.btnPhotoPickerRefetch.setOnClickListener {
            photoViewModel.syncPhotos()
        }
    }

    private fun observeSyncPhotosUIStateLiveData() {
        photoViewModel.syncPhotosUIStateLiveData.observeUIState(viewLifecycleOwner, requireActivity()) { fetchPhotosUIState ->
            when {
                fetchPhotosUIState.synced -> lifecycleScope.launch {
                    observePhotoItemUIStatesLiveData()
                }
                fetchPhotosUIState.showLoading -> {
                    binding.llPhotoPickerErrorWrapper.visibility = View.GONE
                }
                fetchPhotosUIState.showError -> {
                    binding.llPhotoPickerErrorWrapper.visibility = View.VISIBLE
                }
            }
        }
    }

    private suspend fun observePhotoItemUIStatesLiveData() {
        photoViewModel.photoItemUIStatesLiveData.await().observe(viewLifecycleOwner) { photoItemUIStates ->
            photoPickerRecyclerViewAdapter.submit(photoItemUIStates)
        }
    }

    private fun setupPhotoPickerRecyclerView(photoPickerRecyclerView: RecyclerView) {
        photoPickerRecyclerViewAdapter = PhotoPickerRecyclerViewAdapter(this)
        photoPickerRecyclerView.adapter = photoPickerRecyclerViewAdapter
        photoPickerRecyclerView.layoutManager = object : GridLayoutManager(
            requireContext(),
            PhotoPickerRecyclerViewAdapter.NUM_OF_COLUMNS
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
                    val title = getString(R.string.error_title_order_photos)
                    val message = getString(R.string.photo_not_orderable_exception)
                    ErrorDialog.show(title, message, childFragmentManager)
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
                photoViewModel.orderPhotos(photoPickerRecyclerViewAdapter.getPhotoPickerSequences())
            }

        })
        itemTouchHelper.attachToRecyclerView(photoPickerRecyclerView)
    }


    override fun onClickPhoto(position: Int) {
        val photoPicker = photoPickerRecyclerViewAdapter.getPhotoPicker(position)
        when (photoPicker.status) {
            PhotoStatus.EMPTY -> {
                UploadPhotoOptionDialog(this).show(childFragmentManager, UploadPhotoOptionDialog.TAG)
            }
            PhotoStatus.OCCUPIED -> {
                DeletePhotoOptionDialog(photoPicker.key, this).show(childFragmentManager, DeletePhotoOptionDialog.TAG)
            }
            PhotoStatus.UPLOAD_ERROR -> {
                UploadPhotoErrorOptionDialog(photoPicker.uri, photoPicker.key, this).show(
                    childFragmentManager,
                    UploadPhotoErrorOptionDialog.TAG
                )
            }
            PhotoStatus.DOWNLOAD_ERROR -> {
                DownloadPhotoErrorOptionDialog(photoPicker.key, this).show(
                    childFragmentManager,
                    DownloadPhotoErrorOptionDialog.TAG
                )
            }
        }
    }

    override fun onDownloadPhotoError(photoKey: String?) {
        photoViewModel.updatePhotoStatus(photoKey, PhotoStatus.DOWNLOAD_ERROR)
    }

    override fun onDownloadPhotoSuccess(photoKey: String?) {
        photoViewModel.updatePhotoStatus(photoKey, PhotoStatus.DOWNLOAD_ERROR)
    }

    override fun reuploadPhoto(photoUri: Uri?, photoKey: String?) {
        photoViewModel.uploadPhoto(photoUri, photoKey)
    }

    override fun redownloadPhoto(photoKey: String?) {
        photoViewModel.updatePhotoStatus(photoKey, PhotoStatus.DOWNLOADING)
    }

    override fun deletePhoto(photoKey: String?) {
        photoViewModel.deletePhoto(photoKey)
    }

    override fun uploadPhotoFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasExternalStoragePermission()) selectPhotoFromGallery()
            else requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            selectPhotoFromGallery()
        }
    }

    override fun uploadPhotoFromCapture() {
        val cameraIsAvailable = activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ?: false
        if (cameraIsAvailable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (hasCameraPermission()) {
                    selectPhotoFromCapture()
                } else {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                }
            } else {
                selectPhotoFromCapture()
            }
        }
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

    // NOTE 1. Read an image from gallery and deleted when launch CropImage --> CropImage activity will result in error
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    photoViewModel.uploadPhoto(result.uri, null)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val title = getString(R.string.error_title_crop_image)
                    val message = result.error.localizedMessage ?: ""
                    ErrorDialog.show(title, message, childFragmentManager)
                }
                deleteCapturedPhoto()
            }
        }
    }

    private fun launchCropImage(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(PhotoItemUIState.MAX_PHOTO_WIDTH, PhotoItemUIState.MAX_PHOTO_HEIGHT)
            .setAutoZoomEnabled(false)
            .setFixAspectRatio(true)
            .setMaxCropResultSize(PhotoItemUIState.MAX_PHOTO_WIDTH, PhotoItemUIState.MAX_PHOTO_HEIGHT)
            .setMinCropResultSize(PhotoItemUIState.MAX_PHOTO_WIDTH, PhotoItemUIState.MAX_PHOTO_HEIGHT)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(requireContext(), this)
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
        if (file.exists()) {
            file.delete()
        }
    }

    companion object {
        private const val CAPTURED_PHOTO_NAME = "capturedPhoto.jpg"
        private const val FILE_PROVIDER_SUFFIX = ".fileProvider"
    }

}