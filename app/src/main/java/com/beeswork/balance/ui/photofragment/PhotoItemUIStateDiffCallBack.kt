package com.beeswork.balance.ui.photofragment

import androidx.recyclerview.widget.DiffUtil

class PhotoItemUIStateDiffCallBack(
    private val oldPhotoItemUIStates: List<PhotoItemUIState>,
    private val newPhotoItemUIStates: List<PhotoItemUIState>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldPhotoItemUIStates.size
    }

    override fun getNewListSize(): Int {
        return newPhotoItemUIStates.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPhotoItemUIStates[oldItemPosition].key == newPhotoItemUIStates[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPhotoItemUIStates[oldItemPosition] == newPhotoItemUIStates[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        return oldPhotoItemUIStates[oldItemPosition].status == newPhotoItemUIStates[newItemPosition].status
    }
}