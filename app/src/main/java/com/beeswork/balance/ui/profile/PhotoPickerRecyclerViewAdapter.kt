package com.beeswork.balance.ui.profile

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.internal.inflate
import com.beeswork.balance.ui.chat.ChatPagedListAdapter
import com.github.ybq.android.spinkit.SpinKitView

class PhotoPickerRecyclerViewAdapter(
    private val photoPickers: MutableList<PhotoPicker>,
    private val photoPickerListener: PhotoPickerListener
) : RecyclerView.Adapter<PhotoPickerRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        println("onCreateViewHolder")
        return ViewHolder(parent.inflate(R.layout.item_photo_picker), photoPickerListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (photoPickers[position].status) {
            PhotoPicker.Status.LOADING -> showLayout(holder.itemView, loading = true, error = false)
        }



        println("onBindViewHolder")
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

    fun updateFromPhotos(photos: List<Photo>?) {

    }

    companion object {
        private const val PHOTO_PICKER_LOADING_VIEW_TAG = "skvPhotoPickerLoading"
        private const val PHOTO_PICKER_ERROR_VIEW_TAG = "ivPhotoPickerError"
    }

    interface PhotoPickerListener {
        fun onClickPhotoPicker()
    }

    class ViewHolder(
        view: View,
        private val photoPickerListener: PhotoPickerListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        override fun onClick(v: View?) {
            photoPickerListener.onClickPhotoPicker()
        }
    }

}