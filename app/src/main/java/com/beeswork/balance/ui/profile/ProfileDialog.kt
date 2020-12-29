package com.beeswork.balance.ui.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.ui.dialog.ExceptionDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.dialog_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter


class ProfileDialog : DialogFragment(), KodeinAware,
    PhotoPickerRecyclerViewAdapter.PhotoPickerListener,
    PhotoPickerOptionDialog.PhotoPickerOptionListener {

    override val kodein by closestKodein()
    private val balanceRepository: BalanceRepository by instance()
    private val preferenceProvider: PreferenceProvider by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        fetchPhotos()
    }

    private fun bindUI() {
        btnProfileDialogClose.setOnClickListener { dismiss() }
        btnProfileDialogReloadPhotos.setOnClickListener { fetchPhotos() }
        setupPhotoPickerRecyclerView()
//        tvEditBalanceGame.setOnClickListener {
        //            EditBalanceGameDialog().show(childFragmentManager, EditBalanceGameDialog.TAG)
//        }

    }

    private fun setupPhotoPickerRecyclerView() {
        rvPhotoPicker.adapter = PhotoPickerRecyclerViewAdapter(
            requireContext(),
            this,
            preferenceProvider.getAccountId()
        )
        rvPhotoPicker.layoutManager =
            GridLayoutManager(requireContext(), PHOTO_PICKER_GALLERY_COLUMN_NUM)
        setupItemTouchHelperToPhotoPickerRecyclerView()
    }

    private fun fetchPhotos() {
        val adapter = photoPickerRecyclerViewAdapter()
        llPhotoPickerGalleryError.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            val response = balanceRepository.fetchPhotos()
            withContext(Dispatchers.Main) {
                response.data?.let {
                    llPhotoPickerGalleryError.visibility = View.GONE
                    adapter.initializePhotoPickers(it)
                } ?: kotlin.run {
                    llPhotoPickerGalleryError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hasExternalStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestCode.READ_PHOTO_FROM_GALLERY &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) selectPhotoFromGallery()
    }

    private fun selectPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK);
        intent.type = PHOTO_INTENT_TYPE
        intent.putExtra(Intent.EXTRA_MIME_TYPES, PHOTO_MIME_TYPES)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, RequestCode.READ_PHOTO_FROM_GALLERY)
    }

    // CASE 1. Read an image from gallery and deleted when launch CropImage --> CropImage activity will result in error
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.READ_PHOTO_FROM_GALLERY -> {
                if (resultCode == RESULT_OK)
                    data?.data?.let { uri -> launchCropImage(uri) }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK)
                    uploadPhoto(result.uri, null)
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    ExceptionDialog(result.error.localizedMessage).show(
                        childFragmentManager,
                        ExceptionDialog.TAG
                    )
            }
        }
    }

    override fun onReuploadPhoto(photoKey: String, photoUri: Uri?) {
        uploadPhoto(photoUri, photoKey)
    }

    private fun uploadPhoto(photoUri: Uri?, photoKey: String?) {
        photoUri?.path?.let { path ->
            val extension = MimeTypeMap.getFileExtensionFromUrl(path)
            val key = photoKey ?: "${generatePhotoKey()}.$extension"
            val adapter = photoPickerRecyclerViewAdapter()
            val sequence = adapter.uploadPhoto(key, photoUri)
            if (sequence == -1) return

            CoroutineScope(Dispatchers.IO).launch {
                val response = balanceRepository.uploadPhoto(key, extension, path, sequence)
                withContext(Dispatchers.Main) {
                    if (response.isSuccess())
                        adapter.updatePhotoPickerStatus(key, PhotoPicker.Status.OCCUPIED)
                    else if (response.isException()) {
                        ExceptionDialog(response.exceptionMessage).show(
                            childFragmentManager,
                            ExceptionDialog.TAG
                        )
                        adapter.updatePhotoPickerStatus(key, PhotoPicker.Status.UPLOAD_ERROR)
                    }
                }
            }
        } ?: ExceptionDialog(getString(R.string.photo_not_found_exception)).show(
            childFragmentManager,
            ExceptionDialog.TAG
        )
    }

    private fun generatePhotoKey(): String {
        var photoKey = DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now(ZoneOffset.UTC))
        photoKey = photoKey.replace(":", "")
        return photoKey.replace(".", "")
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

    override fun onClickPhotoPicker(photoKey: String?, photoPickerStatus: PhotoPicker.Status, photoUri: Uri?) {
        PhotoPickerOptionDialog(this, photoKey, photoPickerStatus, photoUri).show(
            childFragmentManager,
            PhotoPickerOptionDialog.TAG
        )
    }

    override fun onDeletePhoto(photoKey: String, photoPickerStatus: PhotoPicker.Status) {
        val adapter = photoPickerRecyclerViewAdapter()
        adapter.updatePhotoPickerStatus(photoKey, PhotoPicker.Status.LOADING)

        CoroutineScope(Dispatchers.IO).launch {
            val response = balanceRepository.deletePhoto(photoKey)
            withContext(Dispatchers.Main) {
                if (response.isSuccess() || response.exceptionCode == ExceptionCode.PHOTO_NOT_FOUND_EXCEPTION)
                    adapter.deletePhoto(photoKey)
                else if (response.isException()) {
                    ExceptionDialog(response.exceptionMessage).show(
                        childFragmentManager,
                        ExceptionDialog.TAG
                    )
                    adapter.updatePhotoPickerStatus(photoKey, photoPickerStatus)
                }
            }
        }
    }

    override fun onRedownloadPhoto(photoKey: String) {
        photoPickerRecyclerViewAdapter().updatePhotoPickerStatus(
            photoKey,
            PhotoPicker.Status.DOWNLOADING
        )
    }

    override fun onUploadPhotoFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasExternalStoragePermission()) selectPhotoFromGallery()
            else requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RequestCode.READ_PHOTO_FROM_GALLERY
            )
        } else selectPhotoFromGallery()
    }

    override fun onUploadPhotoFromCapture() {

    }

    private fun photoPickerRecyclerViewAdapter(): PhotoPickerRecyclerViewAdapter {
        return rvPhotoPicker.adapter as PhotoPickerRecyclerViewAdapter
    }


    private fun setupItemTouchHelperToPhotoPickerRecyclerView() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                photoPickerRecyclerViewAdapter().swapPhotos(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {

                val sequences = photoPickerRecyclerViewAdapter().getPhotoPickerSequences()
                for ((k, v) in sequences)
                    println("$k - $v")

//                photoPickerRecyclerViewAdapter().getPhotoPickerSequences()?.let {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val response = balanceRepository.reorderPhoto(it)
//                        withContext(Dispatchers.Main) {
//                            if (response.isSuccess()) photoPickerRecyclerViewAdapter().reorderPhotoPickers(
//                                it
//                            )
//                            else if (response.isException()) {
//                                ExceptionDialog(response.exceptionMessage).show(
//                                    childFragmentManager,
//                                    ExceptionDialog.TAG
//                                )
//                                photoPickerRecyclerViewAdapter().reorderPhotoPickers(null)
//                            }
//                        }
//                    }
//                }
            }


            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (!photoPickerRecyclerViewAdapter().isPhotoPickerDraggable(viewHolder.layoutPosition)) return 0
                if (!photoPickerRecyclerViewAdapter().isOrderable()) {
                    ExceptionDialog(getString(R.string.photo_not_orderable_exception)).show(
                        childFragmentManager,
                        ExceptionDialog.TAG
                    )
                    return 0
                }
                return super.getMovementFlags(recyclerView, viewHolder)
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rvPhotoPicker)
    }

    companion object {
        const val TAG = "profileDialog"
        const val PHOTO_PICKER_GALLERY_COLUMN_NUM = 3
        val PHOTO_MIME_TYPES = arrayOf("image/jpeg", "image/png", "image/jpg")
        const val PHOTO_INTENT_TYPE = "image/*"
    }


}