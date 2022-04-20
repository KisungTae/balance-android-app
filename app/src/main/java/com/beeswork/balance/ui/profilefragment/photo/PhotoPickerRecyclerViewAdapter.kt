package com.beeswork.balance.ui.profilefragment.photo

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.databinding.ItemPhotoPickerBinding
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.toDP
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.ybq.android.spinkit.SpinKitView
import java.io.File

class PhotoPickerRecyclerViewAdapter(
    private val photoPickerListener: PhotoPickerListener
) : RecyclerView.Adapter<PhotoPickerRecyclerViewAdapter.ViewHolder>() {

    private var photoItemUIStates = mutableListOf<PhotoItemUIState>()

    init {
        for (i in 1..PhotoConstant.MAX_NUM_OF_PHOTOS) {
            photoItemUIStates.add(PhotoItemUIState.asLoading())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhotoPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, photoPickerListener, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.bind(photoItemUIStates[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoItemUIStates[position])
    }

    override fun getItemCount(): Int = photoItemUIStates.size

    fun submit(newPhotoPickers: MutableMap<String, PhotoItemUIState>) {

        if (photoItemUIStates.isEmpty()) {
            newPhotoPickers.map { photoItemUIStates.add(it.value) }
            repeat((PhotoConstant.MAX_NUM_OF_PHOTOS - newPhotoPickers.size)) {
                photoItemUIStates.add(PhotoItemUIState.asEmpty())
            }
            notifyDataSetChanged()
            return
        }

        for (i in photoItemUIStates.size - 1 downTo 0) {
            val photoPicker = photoItemUIStates[i]
            photoPicker.key?.let { key ->
                newPhotoPickers[key]?.let { newPhotoPicker ->
                    if (photoPicker.status != newPhotoPicker.status) {
                        photoPicker.status = newPhotoPicker.status
                        notifyItemChanged(i, PHOTO_PICKER_PAYLOAD)
                    }
                } ?: kotlin.run {
                    photoItemUIStates.removeAt(i)
                    notifyItemRemoved(i)
                    photoItemUIStates.add(PhotoItemUIState.asEmpty())
                    notifyItemInserted(photoItemUIStates.size - 1)
                }
            }
        }

        var index = 0
        while (index < photoItemUIStates.size) {
            val photoPicker = photoItemUIStates[index]
            newPhotoPickers[photoPicker.key]?.let { newPhotoPicker ->
                if (index != newPhotoPicker.sequence) {
                    swapPhotos(index, newPhotoPicker.sequence)
                    photoPicker.sequence = newPhotoPicker.sequence
                } else index++
                newPhotoPickers.remove(photoPicker.key)
            } ?: kotlin.run { index++ }
        }

        newPhotoPickers.forEach {
            val newPhotoPicker = it.value
            photoItemUIStates.removeAt(newPhotoPicker.sequence)
            notifyItemRemoved(newPhotoPicker.sequence)
            photoItemUIStates.add(newPhotoPicker.sequence, newPhotoPicker)
            notifyItemInserted(newPhotoPicker.sequence)
        }
    }

    fun getPhotoPicker(position: Int): PhotoItemUIState {
        return photoItemUIStates[position]
    }

    fun swapPhotos(from: Int, to: Int) {
        if (photoItemUIStates[to].status == PhotoStatus.OCCUPIED && from != to) {
            val photoPicker = photoItemUIStates.removeAt(from)
            photoItemUIStates.add(to, photoPicker)
            notifyItemMoved(from, to)
        }
    }

    fun getPhotoPickerSequences(): Map<String, Int> {
        val photoPickerSequences = mutableMapOf<String, Int>()
        photoItemUIStates.forEachIndexed { index, photoPicker ->
            photoPicker.key?.let { key -> photoPickerSequences[key] = index }
        }
        return photoPickerSequences
    }

    fun isSwipeable(): Boolean {
        photoItemUIStates.forEach { photoPicker ->
            if (photoPicker.status != PhotoStatus.OCCUPIED) return false
        }
        return true
    }

    companion object {
        private const val PHOTO_PICKER_PAYLOAD = "photoPickerPayload"
        const val NUM_OF_COLUMNS = 3
    }

    interface PhotoPickerListener {
        fun onClickPhoto(position: Int)
        fun onDownloadPhotoError(photoKey: String?)
        fun onDownloadPhotoSuccess(photoKey: String?)
    }

    class ViewHolder(
        binding: ItemPhotoPickerBinding,
        private val photoPickerListener: PhotoPickerListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(photoItemUIState: PhotoItemUIState) {
            if (photoItemUIState.deleting) {
                showLoading()
                return
            }
            when (photoItemUIState.status) {
                PhotoStatus.EMPTY -> showEmpty()
                PhotoStatus.LOADING -> showLoading()
                PhotoStatus.DOWNLOADING -> loadPhoto(photoItemUIState)
                PhotoStatus.DOWNLOAD_ERROR -> showDownloadError()
                PhotoStatus.UPLOADING -> loadPhoto(photoItemUIState.uri)
                PhotoStatus.UPLOAD_ERROR -> showUploadError(photoItemUIState.uri)
                PhotoStatus.ORDERING -> showLoading()
                PhotoStatus.OCCUPIED -> showOccupied()
            }
        }

        private fun loadPhoto(photoUri: Uri?) {
            showLoading()
            Glide.with(context)
                .load(photoUri)
                .transform(CenterCrop(), RoundedCorners(PHOTO_ROUND_CORNER_DP.toDP()))
                .apply(GlideHelper.photoPickerGlideOptions())
                .into(itemView.findViewWithTag(IV_PHOTO_PICKER_PHOTO))
        }

        private fun loadPhoto(photoItemUIState: PhotoItemUIState) {
            showLoading()
            val uriPath = photoItemUIState.uri?.path
            val photoEndPoint = when {
                uriPath != null && File(uriPath).exists() -> {
                    uriPath
                }
                photoItemUIState.url != null -> {
                    photoItemUIState.url
                }
                else -> {
                    null
                }
            }

            Glide.with(context).load(photoEndPoint)
                .transform(CenterCrop(), RoundedCorners(PHOTO_ROUND_CORNER_DP.toDP()))
                .apply(GlideHelper.photoPickerGlideOptions())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        photoPickerListener.onDownloadPhotoError(photoItemUIState.key)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        photoPickerListener.onDownloadPhotoSuccess(photoItemUIState.key)
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
            photoPickerListener.onClickPhoto(absoluteAdapterPosition)
        }

        companion object {
            private const val IV_PHOTO_PICKER_PHOTO = "photoPickerPhoto"
            private const val IV_PHOTO_PICKER_ADD = "photoPickerAdd"
            private const val SKV_PHOTO_PICKER_LOADING = "photoPickerLoading"
            private const val IV_PHOTO_PICKER_UPLOAD_ERROR = "photoPickerUploadError"
            private const val IV_PHOTO_PICKER_DOWNLOAD_ERROR = "photoPickerDownloadError"
            private const val VIEW_PHOTO_PICKER_MASK = "photoPickerMask"
            private const val IV_PHOTO_PICKER_DELETE_ICON = "photoPickerDeleteIcon"
            private const val PHOTO_ROUND_CORNER_DP = 15
        }
    }
}
