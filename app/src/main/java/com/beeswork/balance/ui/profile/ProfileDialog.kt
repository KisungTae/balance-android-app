package com.beeswork.balance.ui.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.beeswork.balance.internal.constant.RequestCode
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
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
    PhotoUploadOptionDialog.PhotoUploadOptionListener,
    PhotoPickerRecyclerViewAdapter.PhotoPickerListener {

    override val kodein by closestKodein()
    private val balanceRepository: BalanceRepository by instance()

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
    }

    private fun bindUI() {
        btnProfileDialogClose.setOnClickListener { dismiss() }
        btnProfileDialogReloadPhotos.setOnClickListener { fetchPhotos() }
        setupPhotoPickerRecyclerView()
//        tvEditBalanceGame.setOnClickListener {
        //            EditBalanceGameDialog().show(childFragmentManager, EditBalanceGameDialog.TAG)
//        }
        fetchPhotos()
    }

    private fun setupPhotoPickerRecyclerView() {

        val photoPickers = mutableListOf<PhotoPicker>()
        for (i in 0 until MAXIMUM_NUM_OF_PHOTOS) {
            photoPickers.add(i, PhotoPicker(null, PhotoPicker.Status.EMPTY, null))
        }
        rvPhotoPicker.adapter = PhotoPickerRecyclerViewAdapter(requireContext(), photoPickers, this)
        rvPhotoPicker.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private fun fetchPhotos() {

        val adapter = rvPhotoPicker.adapter as PhotoPickerRecyclerViewAdapter
        adapter.showAllLoadingViews()
        llPhotoPickerGalleryError.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {

            val response = balanceRepository.fetchPhotos()

            withContext(Dispatchers.Main) {
                if (response.status == Resource.Status.EXCEPTION)
                    llPhotoPickerGalleryError.visibility = View.VISIBLE
                else if (response.status == Resource.Status.SUCCESS) {
                    llPhotoPickerGalleryError.visibility = View.GONE
                    adapter.initializePhotoPickers(response.data!!)
                }
            }
        }
    }

    override fun onClickAddPhoto() {
        PhotoUploadOptionDialog(this).show(childFragmentManager, PhotoUploadOptionDialog.TAG)
    }

    override fun onClickUploadFromGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasExternalStoragePermission()) {
                selectPhotoFromGallery()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    RequestCode.READ_PHOTO_FROM_GALLERY
                )
            }
        } else {
            selectPhotoFromGallery()
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
        if (requestCode == RequestCode.READ_PHOTO_FROM_GALLERY) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectPhotoFromGallery()
        }
    }

    private fun selectPhotoFromGallery() {

        val intent = Intent(Intent.ACTION_PICK);
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, RequestCode.READ_PHOTO_FROM_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            RequestCode.READ_PHOTO_FROM_GALLERY -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    result.uri?.let { uri ->


                        val contentResolver = requireContext().contentResolver

                        var fileType = MimeTypeMap.getSingleton()
                            .getExtensionFromMimeType(contentResolver.getType(uri))

                        if (fileType == null)
                            fileType = "jpeg"

                        val photoKey = DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now(ZoneOffset.UTC))

                        val adapter = rvPhotoPicker.adapter as PhotoPickerRecyclerViewAdapter
                        adapter.uploadPhoto("$photoKey.$fileType", uri)

                        CoroutineScope(Dispatchers.IO).launch {
                            balanceRepository.uploadPhoto(photoKey, fileType, uri.path!!)
                        }

                        Glide.with(this).load(uri).into(cropImageView)
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.d("crop image error", "crop error: ${result.error}")
                }
            }

        }
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity()
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT)
            .setAutoZoomEnabled(false)
            .setFixAspectRatio(true)
            .setMaxCropResultSize(MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT)
            .setMinCropResultSize(MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(requireContext(), this)

    }

    override fun onClickUploadFromCapture() {

    }


    private fun setupPhotoPicker() {
        val photos = mutableListOf("a", "b", "c", "d", "e", "f")


        rvPhotoPicker.layoutManager = GridLayoutManager(requireContext(), 3)


        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

//                val from = viewHolder.adapterPosition
//                val to = target.adapterPosition
//                Collections.swap(photos, from, to)
//                recyclerView.adapter?.notifyItemMoved(from, to)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                println("clearView")
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rvPhotoPicker)
    }


    override fun onClickDeletePhoto(key: String) {
    }

    override fun onClickPhotoUploadError(key: String) {
    }


    companion object {
        const val TAG = "profileDialog"
        const val MAXIMUM_NUM_OF_PHOTOS = 6
        const val MAX_PHOTO_WIDTH = 1200
        const val MAX_PHOTO_HEIGHT = 1920

    }

}