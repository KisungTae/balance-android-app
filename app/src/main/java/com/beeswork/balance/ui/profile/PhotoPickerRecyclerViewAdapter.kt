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
import com.bumptech.glide.signature.ObjectKey
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.android.synthetic.main.item_photo_picker.view.*
import org.threeten.bp.Instant
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
            photoPickers.find { p ->
                p.key == it.tag?.let { tag -> return@let tag.toString() }
            }?.let { p ->
                photoPickerListener.onClickPhotoPicker(p.key, p.status, p.uri)
            }
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        updateViewHolder(holder, photoPickers[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        updateViewHolder(holder, photoPickers[position])
    }

    private fun updateViewHolder(holder: ViewHolder, photoPicker: PhotoPicker) {
        val text = "${photoPicker.sequence}: ${holder.itemView.elevation}: ${holder.itemView.z}"
        holder.itemView.tvSequence.text = text

        holder.itemView.tag = photoPicker.key
        when (photoPicker.status) {
            PhotoPicker.Status.EMPTY -> {
                showLayout(holder.itemView, View.GONE, View.GONE, View.GONE)
                holder.itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_PHOTO)
                    .setImageResource(R.drawable.ic_baseline_add)
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
                photoPicker.uri = null
                showLayout(holder.itemView, View.GONE, View.GONE, View.GONE)
            }
        }
    }

    override fun getItemCount(): Int = photoPickers.size

    private fun loadPhotoFromUri(holder: ViewHolder, photoUri: Uri?) {
        holder.itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_PHOTO).setImageURI(photoUri)
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
                }).into(holder.itemView.findViewWithTag(IV_PHOTO_PICKER_PHOTO))
        }
    }

    // NOTE 1. DiskCacheStrategy.NONE, then image is not stored in cache directory
    private fun glideRequestOptions(): RequestOptions {
        return RequestOptions()
            .centerCrop()
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
        itemView.findViewWithTag<SpinKitView>(SKV_PHOTO_PICKER_LOADING).visibility = loading
        itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_UPLOAD_ERROR).visibility = uploadError
        itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_DOWNLOAD_ERROR).visibility = downloadError
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

    fun deletePhoto(photoKey: String) {
        photoPickers.find { it.key == photoKey }?.let {
            val index = photoPickers.indexOf(it)
            photoPickers.removeAt(index)
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
        if (photoPickers[to].status == PhotoPicker.Status.OCCUPIED) {
//            Collections.swap(photoPickers, from, to)
            val photoPicker = photoPickers.removeAt(from)
            photoPickers.add(to, photoPicker)
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

    fun getPhotoPickerSequences(): Map<String, Int> {
        val photoPickerSequences = mutableMapOf<String, Int>()
        var isOutOfOrder = false
        var smallestSequence = photoPickers[0].sequence

        for (i in 0 until photoPickers.size - 1) {
            val next = photoPickers[i + 1]
            if (next.status != PhotoPicker.Status.OCCUPIED) break
            if (photoPickers[i].sequence > next.sequence) isOutOfOrder = true
            if (next.sequence < smallestSequence) smallestSequence = next.sequence
        }
        if (!isOutOfOrder) return photoPickerSequences

        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            if (photoPicker.status == PhotoPicker.Status.OCCUPIED && photoPicker.sequence != smallestSequence) {
                photoPicker.key?.let {
                    photoPickerSequences[it] = smallestSequence
                    photoPicker.status = PhotoPicker.Status.LOADING
                    notifyItemChanged(i, PHOTO_PICKER_PAYLOAD)
                }
            }
            smallestSequence++
        }
        return photoPickerSequences
    }

    fun reorderPhotoPickers(photoPickerSequences: Map<String, Int>) {

//        TODO: drag item goes under another

        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            val sequence = photoPickerSequences[photoPicker.key]
            sequence?.let { s ->
                photoPicker.sequence = s
                photoPicker.status = PhotoPicker.Status.OCCUPIED
                notifyItemChanged(i, PHOTO_PICKER_PAYLOAD)
            }
        }
    }

    fun revertPhotoPickersSequence(photoPickerSequences: Map<String, Int>) {
        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            if (photoPickerSequences.containsKey(photoPicker.key))
                photoPicker.status = PhotoPicker.Status.OCCUPIED
        }
        photoPickers.sortBy { p -> p.sequence }
        notifyItemRangeChanged(0, photoPickers.size - 1, PHOTO_PICKER_PAYLOAD)
    }



    companion object {
        private const val IV_PHOTO_PICKER_PHOTO = "photoPickerPhoto"
        private const val SKV_PHOTO_PICKER_LOADING = "photoPickerLoading"
        private const val IV_PHOTO_PICKER_UPLOAD_ERROR = "photoPickerUploadError"
        private const val IV_PHOTO_PICKER_DOWNLOAD_ERROR = "photoPickerDownloadError"
        private const val MAXIMUM_NUM_OF_PHOTOS = 6
        private const val PHOTO_PICKER_PAYLOAD = "photoPickerPayload"
    }

    interface PhotoPickerListener {
        fun onClickPhotoPicker(
            photoKey: String?,
            photoPickerStatus: PhotoPicker.Status,
            photoUri: Uri?
        )
    }

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)

}


// TODO: reorder image disappears
// TODO: delete cropped image and compressed image - done
// TODO: when no order changed then refresh image why?
// TODO: recyclerview scroll disable
// TODO: check delete error goes bakc to its status - done
