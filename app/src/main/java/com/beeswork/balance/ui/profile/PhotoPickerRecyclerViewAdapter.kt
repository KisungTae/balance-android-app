package com.beeswork.balance.ui.profile

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.internal.inflate
import com.beeswork.balance.ui.chat.ChatPagedListAdapter

class PhotoPickerRecyclerViewAdapter(
    private val photos: MutableList<String>,
    private val photoPickerListener: PhotoPickerListener
) :
    RecyclerView.Adapter<PhotoPickerRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        println("onCreateViewHolder")
        return ViewHolder(parent.inflate(R.layout.item_photo_picker), photoPickerListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("onBindViewHolder")
        when (position) {
            0 -> holder.view.setBackgroundColor(Color.parseColor("#f8b4b4"))
            1 -> holder.view.setBackgroundColor(Color.parseColor("#d6d6d6"))
            2 -> holder.view.setBackgroundColor(Color.parseColor("#b4f8b4"))
            3 -> holder.view.setBackgroundColor(Color.parseColor("#d6b4f8"))
            4 -> holder.view.setBackgroundColor(Color.parseColor("#211203"))
            5 -> holder.view.setBackgroundColor(Color.parseColor("#1515e7"))
        }
    }

    override fun getItemCount(): Int = photos.size

    fun updateItem() {
        notifyItemChanged(1)
    }

    interface PhotoPickerListener {
        fun onClickPhotoPicker()
    }

    class ViewHolder(
        val view: View,
        private val photoPickerListener: PhotoPickerListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        override fun onClick(v: View?) {
            photoPickerListener.onClickPhotoPicker()
        }
    }

}