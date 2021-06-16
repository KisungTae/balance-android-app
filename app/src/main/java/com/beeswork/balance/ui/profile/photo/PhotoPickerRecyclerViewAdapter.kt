package com.beeswork.balance.ui.profile.photo

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.databinding.ItemPhotoPickerBinding
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.toPx
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.ybq.android.spinkit.SpinKitView
import java.io.File
import java.util.*

class PhotoPickerRecyclerViewAdapter(
    private val context: Context,
    private val photoPickerListener: PhotoPickerListener,
    private val accountId: UUID?
) : RecyclerView.Adapter<PhotoPickerRecyclerViewAdapter.ViewHolder>() {

    private var photoPickers = mutableListOf<PhotoPicker>()
    private var lastFromIndex = 0
    private var lastToIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhotoPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, accountId, photoPickerListener, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.bind(photoPickers[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoPickers[position])
    }

    override fun getItemCount(): Int = photoPickers.size

    fun submit(newPhotoPickers: MutableMap<String, PhotoPicker>) {

        if (photoPickers.isEmpty()) {
            newPhotoPickers.map { photoPickers.add(it.value) }
            repeat((PhotoConstant.MAX_PHOTO_COUNT - newPhotoPickers.size)) {
                photoPickers.add(PhotoPicker.asEmpty())
            }
            notifyDataSetChanged()
            return
        }

        for (i in photoPickers.size - 1 downTo 0) {
            val photoPicker = photoPickers[i]
            photoPicker.key?.let { key ->
                newPhotoPickers[key]?.let { newPhotoPicker ->
                    if (photoPicker.status != newPhotoPicker.status) {
                        photoPicker.status = newPhotoPicker.status
                        notifyItemChanged(i, PHOTO_PICKER_PAYLOAD)
                    }
                } ?: kotlin.run {
                    photoPickers.removeAt(i)
                    notifyItemRemoved(i)
                    photoPickers.add(PhotoPicker.asEmpty())
                    notifyItemInserted(photoPickers.size - 1)
                }
            }
        }

        var index = 0
        while (index < photoPickers.size) {
            val photoPicker = photoPickers[index]
            newPhotoPickers[photoPicker.key]?.let { newPhotoPicker ->
                if (newPhotoPicker.status != PhotoStatus.ORDERING && index != newPhotoPicker.sequence) {
                    swapPhotos(index, newPhotoPicker.sequence)
                    photoPicker.sequence = newPhotoPicker.sequence
                } else index++
                newPhotoPickers.remove(photoPicker.key)
            } ?: kotlin.run { index++ }
        }

        newPhotoPickers.forEach {
            val newPhotoPicker = it.value
            photoPickers.removeAt(newPhotoPicker.sequence)
            notifyItemRemoved(newPhotoPicker.sequence)
            photoPickers.add(newPhotoPicker.sequence, newPhotoPicker)
            notifyItemInserted(newPhotoPicker.sequence)
        }
    }

    fun getPhotoPicker(position: Int): PhotoPicker {
        return photoPickers[position]
    }

    fun swapPhotos(from: Int, to: Int) {
        if (photoPickers[to].status == PhotoStatus.OCCUPIED && from != to) {
            val photoPicker = photoPickers.removeAt(from)
            photoPickers.add(to, photoPicker)
            lastFromIndex = from
            lastToIndex = to
            notifyItemMoved(from, to)
        }
    }


    fun downloadPhoto(photoKey: String) {
        val index = photoPickers.indexOfFirst { it.key == photoKey }
        val photoPicker = photoPickers[index]
        photoPicker.status = PhotoStatus.OCCUPIED
        notifyItemChanged(index)
    }


    fun uploadPhoto(photoKey: String, photoUri: Uri?): Int {
        var sequence = 0
        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            when {
//                photoPicker.status == PhotoPicker.Status.EMPTY -> {
//                    sequence++
//                    photoPicker.key = photoKey
//                    photoPicker.status = PhotoPicker.Status.UPLOADING
//                    photoPicker.uri = photoUri
//                    photoPicker.sequence = sequence
//                    notifyItemChanged(photoPickers.indexOf(photoPicker))
//                    return sequence
//                }
//                photoPicker.key == photoKey -> {
//                    photoPicker.status = PhotoPicker.Status.UPLOADING
//                    notifyItemChanged(photoPickers.indexOf(photoPicker))
//                    return photoPicker.sequence
//                }
                photoPicker.sequence > sequence -> {
                    sequence = photoPicker.sequence
                }
            }
        }
        return -1
    }

    fun deletePhoto(photoKey: String) {
//        photoPickers.find { it.key == photoKey }?.let {
//            val index = photoPickers.indexOf(it)
//            photoPickers.removeAt(index)
//            notifyItemRemoved(index)
//        }

//        if (photoPickers.size < MAXIMUM_NUM_OF_PHOTOS) {
//            photoPickers.add(PhotoPicker.asEmpty())
//            notifyItemInserted((photoPickers.size - 1))
//        }
    }

    fun updatePhotoPickerStatus(photoKey: String, photoPickerStatus: PhotoStatus) {
//        photoPickers.find { it.key == photoKey }?.let {
//            it.status = photoPickerStatus
//            notifyItemChanged(photoPickers.indexOf(it), PHOTO_PICKER_PAYLOAD)
//        }
    }


    fun isPhotoPickerDraggable(position: Int): Boolean {
//        val photoPicker = photoPickers[position]
//        return photoPicker.status == PhotoPicker.Status.OCCUPIED
        return false
    }

    fun isOrderable(): Boolean {
//        for (i in photoPickers.indices) {
//            val photoPicker = photoPickers[i]
//            if (photoPicker.status != PhotoPicker.Status.EMPTY && photoPicker.status != PhotoPicker.Status.OCCUPIED)
//                return false
//        }
        return true
    }

    fun getPhotoPickerSequences(): Map<String, Int> {
        val photoPickerSequences = mutableMapOf<String, Int>()
        photoPickers.forEachIndexed { index, photoPicker ->
            photoPicker.key?.let { key -> photoPickerSequences[key] = index }
        }
        return photoPickerSequences
    }

    fun reorderPhotoPickers(photoPickerSequences: Map<String, Int>) {
        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            val sequence = photoPickerSequences[photoPicker.key]
            sequence?.let { s ->
//                photoPicker.sequence = s
//                photoPicker.status = PhotoPicker.Status.OCCUPIED
                notifyItemChanged(i, PHOTO_PICKER_PAYLOAD)

            }
        }
    }

    fun revertPhotoPickersSequence(photoPickerSequences: Map<String, Int>) {
        val removed = photoPickers.removeAt(lastToIndex)
        photoPickers.add(lastFromIndex, removed)
        notifyItemMoved(lastToIndex, lastFromIndex)

        for (i in photoPickers.indices) {
            val photoPicker = photoPickers[i]
            if (photoPickerSequences.containsKey(photoPicker.key)) {
//                photoPicker.status = PhotoPicker.Status.OCCUPIED
                notifyItemChanged(i, PHOTO_PICKER_PAYLOAD)
            }
        }
    }

    companion object {
        private const val PHOTO_PICKER_PAYLOAD = "photoPickerPayload"
    }

    interface PhotoPickerListener {
        fun onClickPhotoPicker(position: Int)
        fun onDownloadPhotoError(photoKey: String?)
        fun onDownloadPhotoSuccess(photoKey: String?)
    }

    class ViewHolder(
        binding: ItemPhotoPickerBinding,
        private val accountId: UUID?,
        private val photoPickerListener: PhotoPickerListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(photoPicker: PhotoPicker) {
            when (photoPicker.status) {
                PhotoStatus.EMPTY -> showEmpty()
                PhotoStatus.LOADING -> showLoading()
                PhotoStatus.DOWNLOADING -> loadPhoto(photoPicker.uri, photoPicker.key)
                PhotoStatus.DOWNLOAD_ERROR -> showDownloadError()
                PhotoStatus.UPLOADING -> loadPhoto(photoPicker.uri)
                PhotoStatus.UPLOAD_ERROR -> showUploadError(photoPicker.uri)
                PhotoStatus.ORDERING -> showLoading()
                PhotoStatus.OCCUPIED -> showOccupied()
                else -> println()
            }
        }

        private fun loadPhoto(photoUri: Uri?) {
            showLoading()
            Glide.with(context)
                .load(photoUri)
                .transform(CenterCrop(), RoundedCorners(PHOTO_ROUND_CORNER_DP.toPx()))
                .apply(GlideHelper.photoPickerGlideOptions())
                .into(itemView.findViewWithTag(IV_PHOTO_PICKER_PHOTO))

        }

        private fun loadPhoto(photoUri: Uri?, photoKey: String?) {
            showLoading()
            val photoEndPoint = photoUri?.path?.let { path ->
                val photoFile = File(path)
                if (photoFile.exists()) path else EndPoint.ofPhoto(accountId, photoKey)
            } ?: EndPoint.ofPhoto(accountId, photoKey)

            Glide.with(context).load(photoEndPoint)
                .transform(CenterCrop(), RoundedCorners(PHOTO_ROUND_CORNER_DP.toPx()))
                .apply(GlideHelper.photoPickerGlideOptions())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        photoPickerListener.onDownloadPhotoError(photoKey)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        photoPickerListener.onDownloadPhotoSuccess(photoKey)
                        return false
                    }
                }).into(itemView.findViewWithTag(IV_PHOTO_PICKER_PHOTO))
        }

        private fun showEmpty() {
            val photoImageView = itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_PHOTO)
            photoImageView.setImageResource(0)
            photoImageView.setImageBitmap(null)
            showLayout(itemView, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE)
        }

        private fun showLoading() {
            showLayout(itemView, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE)
        }

        private fun showUploadError(photoUri: Uri?) {
            loadPhoto(photoUri)
            showLayout(itemView, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE)
        }

        private fun showDownloadError() {
            showLayout(itemView, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE)
        }

        private fun showOccupied() {
            showLayout(itemView, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE)
        }

        private fun showLayout(
            itemView: View,
            add: Int,
            loading: Int,
            uploadError: Int,
            downloadError: Int,
            delete: Int
        ) {
            itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_ADD).visibility = add
            itemView.findViewWithTag<SpinKitView>(SKV_PHOTO_PICKER_LOADING).visibility = loading
            itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_UPLOAD_ERROR).visibility = uploadError
            itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_DOWNLOAD_ERROR).visibility = downloadError
            val maskVisibility = if (uploadError == View.VISIBLE || downloadError == View.VISIBLE) View.VISIBLE else View.GONE
            itemView.findViewWithTag<View>(VIEW_PHOTO_PICKER_MASK).visibility = maskVisibility
            itemView.findViewWithTag<ImageView>(IV_PHOTO_PICKER_DELETE_ICON).visibility = delete
        }

        override fun onClick(view: View?) {
            photoPickerListener.onClickPhotoPicker(absoluteAdapterPosition)
        }

        companion object {
            private const val IV_PHOTO_PICKER_PHOTO = "photoPickerPhoto"
            private const val IV_PHOTO_PICKER_ADD = "photoPickerAdd"
            private const val SKV_PHOTO_PICKER_LOADING = "photoPickerLoading"
            private const val IV_PHOTO_PICKER_UPLOAD_ERROR = "photoPickerUploadError"
            private const val IV_PHOTO_PICKER_DOWNLOAD_ERROR = "photoPickerDownloadError"
            private const val VIEW_PHOTO_PICKER_MASK = "photoPickerMask"
            private const val IV_PHOTO_PICKER_DELETE_ICON = "photoPickerDeleteIcon"
            private const val PHOTO_ROUND_CORNER_DP = 5
        }
    }
}
