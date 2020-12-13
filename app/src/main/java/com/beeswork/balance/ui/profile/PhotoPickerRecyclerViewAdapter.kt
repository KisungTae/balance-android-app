package com.beeswork.balance.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.internal.inflate
import com.bumptech.glide.Glide
import com.github.ybq.android.spinkit.SpinKitView
import java.net.URI

class PhotoPickerRecyclerViewAdapter(
    private val context: Context,
    private val photoPickers: MutableList<PhotoPicker>,
    private val photoPickerListener: PhotoPickerListener,
    private val accountId: String
) : RecyclerView.Adapter<PhotoPickerRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        println("onCreateViewHolder")
        return ViewHolder(parent.inflate(R.layout.item_photo_picker), photoPickerListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val photoPicker = photoPickers[position]

        when (photoPicker.status) {
            PhotoPicker.Status.EMPTY -> showLayout(holder.itemView, loading = false, error = false)
            PhotoPicker.Status.LOADING -> showLayout(holder.itemView, loading = true, error = false)
            PhotoPicker.Status.ERROR -> showLayout(holder.itemView, loading = false, error = true)
            PhotoPicker.Status.UPLOADED -> showLayout(holder.itemView, loading = false, error = false)
        }

        holder.itemView.tag = photoPicker.key

        val photoImageView = holder.itemView.findViewWithTag<ImageView>("photoPickerPhoto")

        if (photoPicker.uri != null)
            Glide.with(context).load(photoPicker.uri).into(photoImageView)
        else if (photoPicker.key != null) {


        }



        when (position) {
            0 -> holder.itemView.setBackgroundColor(Color.parseColor("#f8b4b4"))
            1 -> holder.itemView.setBackgroundColor(Color.parseColor("#d6d6d6"))
            2 -> holder.itemView.setBackgroundColor(Color.parseColor("#b4f8b4"))
            3 -> holder.itemView.setBackgroundColor(Color.parseColor("#d6b4f8"))
            4 -> holder.itemView.setBackgroundColor(Color.parseColor("#211203"))
            5 -> holder.itemView.setBackgroundColor(Color.parseColor("#1515e7"))
        }
    }

    override fun getItemCount(): Int = photoPickers.size

    private fun showLayout(itemView: View, loading: Boolean, error: Boolean) {

        val loadingView = itemView.findViewWithTag<SpinKitView>(PHOTO_PICKER_LOADING_VIEW_TAG)
        loadingView.visibility = if (loading) View.VISIBLE else View.GONE

        val errorView = itemView.findViewWithTag<ImageView>(PHOTO_PICKER_ERROR_VIEW_TAG)
        errorView.visibility = if (error) View.VISIBLE else View.GONE
    }

    fun showAllLoadingViews() {

        for (i in 0 until photoPickers.size) {
            photoPickers[i].status = PhotoPicker.Status.LOADING
            notifyItemChanged(i)
        }
    }

    fun initializePhotoPickers(photos: List<Photo>) {

        for (i in photos.indices) {
            photoPickers[i].key = photos[i].key
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
                photoPicker.status = PhotoPicker.Status.LOADING
                photoPicker.uri = photoUri
                notifyItemChanged(i)
                break
            }
        }
    }

    fun deletePhoto(photoKey: String) {
        val photoToDelete = photoPickers.find { it.key == photoKey }
        photoPickers.remove(photoToDelete)

        if (photoPickers.size < PhotoPicker.MAXIMUM_NUM_OF_PHOTOS)
            photoPickers.add(PhotoPicker.empty())
    }

    fun onPhotoUploaded(photoKey: String) {
        updatePhotoPickerStatus(photoKey, PhotoPicker.Status.UPLOADED)
    }

    fun onPhotoUploadError(photoKey: String) {
        updatePhotoPickerStatus(photoKey, PhotoPicker.Status.ERROR)
    }

    private fun updatePhotoPickerStatus(photoKey: String, photoPickerStatus: PhotoPicker.Status) {
        val photoPicker = photoPickers.find { it.key == photoKey }
        photoPicker?.status = photoPickerStatus
        notifyItemChanged(photoPickers.indexOf(photoPicker))
    }

    fun showPhoto(bitmap: Bitmap) {

    }

    companion object {
        private const val PHOTO_PICKER_PHOTO_VIEW_TAG = "photoPickerPhoto"
        private const val PHOTO_PICKER_LOADING_VIEW_TAG = "photoPickerLoading"
        private const val PHOTO_PICKER_ERROR_VIEW_TAG = "photoPickerError"
        private const val BALANCE_PHOTO_BUCKET_URL = "https://balance-photo-bucket.s3.ap-northeast-2.amazonaws.com"
    }

    interface PhotoPickerListener {
        fun onClickAddPhoto()
        fun onClickDeletePhoto(key: String)
        fun onClickPhotoUploadError(key: String)
    }

    class ViewHolder(
        view: View,
        private val photoPickerListener: PhotoPickerListener
    ): RecyclerView.ViewHolder(view) {

        init {
            view.findViewWithTag<ImageView>(PHOTO_PICKER_PHOTO_VIEW_TAG).setOnClickListener {
                val key = (it.parent as View).tag
                if (key == null) photoPickerListener.onClickAddPhoto()
                else photoPickerListener.onClickDeletePhoto(key.toString())
            }

            view.findViewWithTag<ImageView>(PHOTO_PICKER_ERROR_VIEW_TAG).setOnClickListener {
                println("click on error")
            }
        }
    }

}