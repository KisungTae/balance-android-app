package com.beeswork.balance.ui.profile

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.internal.inflate
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.ybq.android.spinkit.SpinKitView

class PhotoPickerRecyclerViewAdapter(
    private val context: Context,
    private val photoPickerListener: PhotoPickerListener,
    private val accountId: String
) : RecyclerView.Adapter<PhotoPickerRecyclerViewAdapter.ViewHolder>() {

    private val photoPickers: MutableList<PhotoPicker> = mutableListOf()

    init {
        repeat(MAXIMUM_NUM_OF_PHOTOS) {
            photoPickers.add(PhotoPicker(null, PhotoPicker.Status.LOADING, null))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_photo_picker), photoPickerListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val photoPicker = photoPickers[position]
        holder.itemView.tag = photoPicker.key

        when (photoPicker.status) {
            PhotoPicker.Status.EMPTY -> showLayout(holder.itemView, View.GONE, View.GONE, View.GONE)
            PhotoPicker.Status.LOADING -> showLayout(holder.itemView, View.VISIBLE, View.GONE, View.GONE)
            PhotoPicker.Status.UPLOADING -> {
                if (photoPicker.uri != null) {
                    val ivPhotoPickerPhoto =
                        holder.itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_PHOTO_TAG)
                    Glide.with(context).load(photoPicker.uri)
                        .apply(glideRequestOptions())
                        .into(ivPhotoPickerPhoto)
                    showLayout(holder.itemView, View.VISIBLE, View.GONE, View.GONE)
                }
            }
            PhotoPicker.Status.UPLOAD_ERROR -> showLayout(holder.itemView, View.GONE, View.VISIBLE, View.GONE)
            PhotoPicker.Status.DOWNLOAD_ERROR -> showLayout(holder.itemView, View.GONE, View.GONE, View.VISIBLE)
            PhotoPicker.Status.OCCUPIED -> showLayout(holder.itemView, View.GONE, View.GONE, View.GONE)
            PhotoPicker.Status.DOWNLOADING -> {
                if (photoPicker.key != null) {
                    val photoUrl = "$BALANCE_PHOTO_BUCKET_URL/$accountId/${photoPicker.key}"
                    val ivPhotoPickerPhoto =
                        holder.itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_PHOTO_TAG)

                    Glide.with(context).load(photoUrl)
                        .apply(glideRequestOptions())
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                photoPicker.status = PhotoPicker.Status.DOWNLOAD_ERROR
                                showLayout(holder.itemView, View.GONE, View.GONE, View.VISIBLE)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                photoPicker.status = PhotoPicker.Status.OCCUPIED
                                showLayout(holder.itemView, View.GONE, View.GONE, View.GONE)
                                return false
                            }
                        }).into(ivPhotoPickerPhoto)
                }
            }
        }
    }


    override fun getItemCount(): Int = photoPickers.size


    private fun glideRequestOptions(): RequestOptions {
        return RequestOptions().centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
    }


    private fun downloadPhoto() {

    }

    private fun showLayout(
        itemView: View,
        loading: Int,
        uploadError: Int,
        downloadError: Int
    ) {
        itemView.findViewWithTag<SpinKitView>(SKV_PHOTO_PICKER_LOADING_TAG).visibility = loading
        itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_UPLOAD_ERROR_TAG).visibility = uploadError
        itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_DOWNLOAD_ERROR_TAG).visibility = downloadError
    }

    fun initializePhotoPickers(photos: List<Photo>) {
        for (i in photos.indices) {
            val photoPicker = photoPickers[i]
            photoPicker.status = PhotoPicker.Status.DOWNLOADING
            photoPicker.key = photos[i].key
        }

        for (i in photos.size until photoPickers.size) {
            photoPickers[i].status = PhotoPicker.Status.EMPTY
        }
    }

    fun uploadPhoto(photoKey: String, photoUri: Uri) {
        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            if (photoPicker.status == PhotoPicker.Status.EMPTY) {
                photoPicker.key = photoKey
                photoPicker.status = PhotoPicker.Status.UPLOADING
                photoPicker.uri = photoUri
                notifyItemChanged(i)
                break
            }
        }
    }

    fun deletePhoto(photoKey: String) {
        val photoToDelete = photoPickers.find { it.key == photoKey }
        photoPickers.remove(photoToDelete)

        if (photoPickers.size < MAXIMUM_NUM_OF_PHOTOS)
            photoPickers.add(PhotoPicker.empty())
    }

    fun onPhotoUploaded(photoKey: String) {
        updatePhotoPickerStatus(photoKey, PhotoPicker.Status.OCCUPIED)
    }

    fun onPhotoUploadError(photoKey: String) {
        updatePhotoPickerStatus(photoKey, PhotoPicker.Status.UPLOAD_ERROR)
    }

    private fun updatePhotoPickerStatus(photoKey: String, photoPickerStatus: PhotoPicker.Status) {
        val photoPicker = photoPickers.find { it.key == photoKey }
        if (photoPicker != null) {
            photoPicker.status = photoPickerStatus
            notifyItemChanged(photoPickers.indexOf(photoPicker))
        }
    }

    companion object {
        private const val IV_PHOTO_PICKER_PHOTO_TAG = "photoPickerPhoto"
        private const val SKV_PHOTO_PICKER_LOADING_TAG = "photoPickerLoading"
        private const val IV_PHOTO_PICKER_UPLOAD_ERROR_TAG = "photoPickerUploadError"
        private const val IV_PHOTO_PICKER_DOWNLOAD_ERROR_TAG = "photoPickerDownloadError"
        private const val BALANCE_PHOTO_BUCKET_URL =
            "https://balance-photo-bucket.s3.ap-northeast-2.amazonaws.com"
        private const val MAXIMUM_NUM_OF_PHOTOS = 6
    }

    interface PhotoPickerListener {
        fun onClickAddPhoto()
        fun onClickDeletePhoto(key: String)
        fun onClickPhotoUploadError(key: String)
    }

    class ViewHolder(
        view: View,
        private val photoPickerListener: PhotoPickerListener
    ) : RecyclerView.ViewHolder(view) {

        init {
            view.findViewWithTag<ImageView>(IV_PHOTO_PICKER_PHOTO_TAG).setOnClickListener {
                val key = (it.parent as View).tag
                if (key == null) photoPickerListener.onClickAddPhoto()
                else photoPickerListener.onClickDeletePhoto(key.toString())
            }

            view.findViewWithTag<ImageView>(IV_PHOTO_PICKER_UPLOAD_ERROR_TAG).setOnClickListener {
                println("click on error")
            }

            view.findViewWithTag<ImageView>(IV_PHOTO_PICKER_DOWNLOAD_ERROR_TAG).setOnClickListener {

            }
        }
    }

}