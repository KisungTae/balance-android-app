package com.beeswork.balance.ui.photofragment

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

    fun submit(photoItemUIStates: List<PhotoItemUIState>) {
        val photoItemUIStateDiffCallBack = PhotoItemUIStateDiffCallBack(this.photoItemUIStates, photoItemUIStates)
        val diffResult = DiffUtil.calculateDiff(photoItemUIStateDiffCallBack)

        this.photoItemUIStates.clear()
        this.photoItemUIStates.addAll(photoItemUIStates)
        diffResult.dispatchUpdatesTo(this)
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
            if (photoPicker.status != PhotoStatus.OCCUPIED && photoPicker.status != PhotoStatus.EMPTY) {
                return false
            }
        }
        return true
    }

    companion object {
        const val NUM_OF_COLUMNS = 3
    }

    interface PhotoPickerListener {
        fun onClickPhoto(position: Int)
        fun onDownloadPhotoError(photoKey: String?)
        fun onDownloadPhotoSuccess(photoKey: String?)
    }

    class ViewHolder(
        private val binding: ItemPhotoPickerBinding,
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
                .into(binding.ivPhotoPickerPhoto)
        }

        private fun loadPhoto(photoItemUIState: PhotoItemUIState) {
            showLoading()
            Glide.with(context).load(photoItemUIState.url)
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
                }).into(binding.ivPhotoPickerPhoto)
        }

        private fun showEmpty() {
            val photoImageView = binding.ivPhotoPickerPhoto
            photoImageView.setImageResource(0)
            photoImageView.setImageBitmap(null)
            showLayout(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE)
        }

        private fun showLoading() {
            showLayout(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE)
        }

        private fun showUploadError(photoUri: Uri?) {
            loadPhoto(photoUri)
            showLayout(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE)
        }

        private fun showDownloadError() {
            showLayout(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE)
        }

        private fun showOccupied() {
            showLayout(View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE)
        }

        private fun showLayout(
            add: Int,
            loading: Int,
            uploadError: Int,
            downloadError: Int,
            delete: Int
        ) {
            binding.ivPhotoPickerAdd.visibility = add
            binding.skvPhotoPickerLoading.visibility = loading
            binding.ivUploadError.visibility = uploadError
            binding.ivDownloadError.visibility = downloadError
            val maskVisibility = if (uploadError == View.VISIBLE || downloadError == View.VISIBLE) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.vPhotoPickerMask.visibility = maskVisibility
            binding.llPhotoPickerDeleteIconWrapper.visibility = delete
        }

        override fun onClick(view: View?) {
            photoPickerListener.onClickPhoto(absoluteAdapterPosition)
        }

        companion object {
            private const val PHOTO_ROUND_CORNER_DP = 15
        }
    }
}
