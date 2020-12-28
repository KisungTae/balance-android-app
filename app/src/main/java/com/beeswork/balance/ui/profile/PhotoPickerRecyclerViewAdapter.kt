package com.beeswork.balance.ui.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.internal.constant.BalanceURL
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
import java.util.*

class PhotoPickerRecyclerViewAdapter(
    private val context: Context,
    private val photoPickerListener: PhotoPickerListener,
    private val accountId: String
) : RecyclerView.Adapter<PhotoPickerRecyclerViewAdapter.ViewHolder>() {

    private val photoPickers: MutableList<PhotoPicker> = mutableListOf()

    init {
        repeat(MAXIMUM_NUM_OF_PHOTOS) {
            photoPickers.add(PhotoPicker(null, PhotoPicker.Status.LOADING, null, Int.MAX_VALUE))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_photo_picker)
        view.setOnClickListener {
            val photoKey = it.tag
            if (photoKey == null)
                photoPickerListener.onClickPhotoPicker(null, PhotoPicker.Status.EMPTY)
            else {
                val photoPicker = photoPickers.find { p -> p.key == view.tag.toString() }
                if (photoPicker != null)
                    photoPickerListener.onClickPhotoPicker(photoPicker.key, photoPicker.status)
            }
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val photoPicker = photoPickers[position]
        holder.itemView.tag = photoPicker.key

        when (photoPicker.status) {
            PhotoPicker.Status.EMPTY -> {
                showLayout(holder.itemView, View.GONE, View.GONE, View.GONE)
            }
            PhotoPicker.Status.LOADING -> {
                showLayout(holder.itemView, View.VISIBLE, View.GONE, View.GONE)
            }
            PhotoPicker.Status.UPLOADING -> {
                loadPhotoFromUri(holder, photoPicker.uri)
                showLayout(holder.itemView, View.VISIBLE, View.GONE, View.GONE)
            }
            PhotoPicker.Status.UPLOAD_ERROR -> {
                showLayout(holder.itemView, View.GONE, View.VISIBLE, View.GONE)
            }
            PhotoPicker.Status.DOWNLOADING -> {
                loadPhotoFromUrl(holder, photoPicker)
            }
            PhotoPicker.Status.DOWNLOAD_ERROR -> {
                showLayout(holder.itemView, View.GONE, View.GONE, View.VISIBLE)
            }
            PhotoPicker.Status.OCCUPIED -> {
                showLayout(holder.itemView, View.GONE, View.GONE, View.GONE)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

    override fun getItemCount(): Int = photoPickers.size

    private fun loadPhotoFromUri(holder: ViewHolder, photoUri: Uri?) {
        holder.itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_PHOTO_TAG).setImageURI(photoUri)
    }

    private fun loadPhotoFromUrl(holder: ViewHolder, photoPicker: PhotoPicker) {
        photoPicker.key?.let {
            val photoUrl = "${BalanceURL.PHOTO_BUCKET}/$accountId/${it}"
            showLayout(holder.itemView, View.VISIBLE, View.GONE, View.GONE)
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
                }).into(holder.itemView.findViewWithTag(IV_PHOTO_PICKER_PHOTO_TAG))
        }
    }

    private fun glideRequestOptions(): RequestOptions {
        return RequestOptions().centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .centerCrop()
    }

    private fun showLayout(
        itemView: View,
        loading: Int,
        uploadError: Int,
        downloadError: Int
    ) {
        itemView.findViewWithTag<SpinKitView>(SKV_PHOTO_PICKER_LOADING_TAG).visibility = loading
        itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_UPLOAD_ERROR_TAG).visibility =
            uploadError
        itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_DOWNLOAD_ERROR_TAG).visibility =
            downloadError
    }

    fun initializePhotoPickers(photos: List<Photo>) {
        for (i in photos.indices) {
            val photoPicker = photoPickers[i]
            val photo = photos[i]
            photoPicker.status = PhotoPicker.Status.DOWNLOADING
            photoPicker.key = photo.key
            photoPicker.sequence = photo.sequence
        }

        for (i in photos.size until photoPickers.size) {
            photoPickers[i].status = PhotoPicker.Status.EMPTY
        }
        notifyDataSetChanged()
    }

    fun uploadPhoto(photoKey: String, photoUri: Uri?): Int {
        var sequence = 0
        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            when {
                photoPicker.status == PhotoPicker.Status.EMPTY -> {
                    sequence++
                    photoPicker.key = photoKey
                    photoPicker.status = PhotoPicker.Status.UPLOADING
                    photoPicker.uri = photoUri
                    photoPicker.sequence = sequence
                    notifyItemChanged(photoPickers.indexOf(photoPicker))
                    return sequence
                }
                photoPicker.key == photoKey -> {
                    photoPicker.status = PhotoPicker.Status.UPLOADING
                    notifyItemChanged(photoPickers.indexOf(photoPicker))
                    return photoPicker.sequence
                }
                photoPicker.sequence > sequence -> {
                    sequence = photoPicker.sequence
                }
            }
        }
        return -1
    }

    fun getUriByPhotoKey(photoKey: String): Uri? {
        return photoPickers.find { it.key == photoKey }?.uri
    }

    fun deletePhoto(photoKey: String) {
        photoPickers.find { it.key == photoKey }?.let {
            val index = photoPickers.indexOf(it)
            photoPickers.remove(it)
            notifyItemRemoved(index)
        }

        if (photoPickers.size < MAXIMUM_NUM_OF_PHOTOS) {
            photoPickers.add(PhotoPicker.asEmpty())
            notifyItemInserted((photoPickers.size - 1))
        }
    }

    fun updatePhotoPickerStatus(photoKey: String, photoPickerStatus: PhotoPicker.Status) {
        photoPickers.find { it.key == photoKey }?.let {
            it.status = photoPickerStatus
            notifyItemChanged(photoPickers.indexOf(it), PHOTO_PICKER_PAYLOAD)
        }
    }

    fun swapPhotos(from: Int, to: Int) {
        val toPhotoPicker = photoPickers[to]
        if (toPhotoPicker.status == PhotoPicker.Status.OCCUPIED) {
            Collections.swap(photoPickers, from, to)
            notifyItemMoved(from, to)
        }
    }

    fun isPhotoPickerDraggable(position: Int): Boolean {
        val photoPicker = photoPickers[position]
        return photoPicker.status == PhotoPicker.Status.OCCUPIED
    }

    fun isOrderable(): Boolean {
        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            if (photoPicker.status != PhotoPicker.Status.EMPTY && photoPicker.status != PhotoPicker.Status.OCCUPIED)
                return false
        }
        return true
    }

    fun getPhotoPickerSequences(): Map<String, Int>? {
        var isOutOfOrder = false
        var prevSequence = Int.MIN_VALUE
        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            if (photoPicker.status == PhotoPicker.Status.EMPTY) break
            if (prevSequence > photoPicker.sequence) {
                isOutOfOrder = true
                break
            } else prevSequence = photoPicker.sequence
        }
        if (!isOutOfOrder) return null

        val photoPickerSequences = mutableMapOf<String, Int>()
        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            if (photoPicker.status == PhotoPicker.Status.OCCUPIED) {
                photoPicker.key?.let { photoPickerSequences.put(it, i) }
                photoPicker.status = PhotoPicker.Status.LOADING
                notifyItemChanged(i)
            }
        }
        return photoPickerSequences
    }

    fun reorderPhotoPickers(photoPickerSequences: Map<String, Int>?) {
        photoPickerSequences?.let { sequences ->
            for (i in photoPickers.indices) {
                val photoPicker = photoPickers[i]
                val sequence = sequences[photoPicker.key]
                sequence?.let {
                    photoPicker.status = PhotoPicker.Status.OCCUPIED
                    photoPicker.sequence = it
                }
            }
        }
        photoPickers.sortBy { it.sequence }
        notifyDataSetChanged()
    }

    companion object {
        private const val IV_PHOTO_PICKER_PHOTO_TAG = "photoPickerPhoto"
        private const val SKV_PHOTO_PICKER_LOADING_TAG = "photoPickerLoading"
        private const val IV_PHOTO_PICKER_UPLOAD_ERROR_TAG = "photoPickerUploadError"
        private const val IV_PHOTO_PICKER_DOWNLOAD_ERROR_TAG = "photoPickerDownloadError"
        private const val MAXIMUM_NUM_OF_PHOTOS = 6
        private const val PHOTO_PICKER_PAYLOAD = "photoPickerPayload"
    }

    interface PhotoPickerListener {
        fun onClickPhotoPicker(photoKey: String?, photoPickerStatus: PhotoPicker.Status)
    }

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)


}


// TODO: reorder image disappears
// TODO: delete cropped image and compressed image
// TODO: when no order changed then refresh image why?
// TODO: recyclerview scroll disable
// TODO: check delete error goes bakc to its status
// TODO: glide cache background thread
