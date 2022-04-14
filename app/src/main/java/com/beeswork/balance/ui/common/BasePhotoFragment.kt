package com.beeswork.balance.ui.common

import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutPhotoPickerBinding
import com.beeswork.balance.internal.util.observeUIState
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.profilefragment.photo.PhotoPickerOptionListener
import com.beeswork.balance.ui.profilefragment.photo.PhotoPickerRecyclerViewAdapter
import com.beeswork.balance.ui.registeractivity.photo.PhotoViewModel

open class BasePhotoFragment : BaseFragment(), PhotoPickerRecyclerViewAdapter.PhotoPickerListener, PhotoPickerOptionListener {

    private lateinit var photoPickerRecyclerViewAdapter: PhotoPickerRecyclerViewAdapter
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var binding: LayoutPhotoPickerBinding


    protected fun onViewCreated(viewModel: PhotoViewModel, layoutPhotoPickerBinding: LayoutPhotoPickerBinding) {
        this.photoViewModel = viewModel
        this.binding = layoutPhotoPickerBinding
        setupPhotoPickerRecyclerView(binding.rvPhotoPicker)
        setupBtnListeners()
        observeFetchPhotosUIStateLiveData()
//        viewModel.fetchPhotos()
    }

    private fun setupBtnListeners() {
        binding.btnPhotoPickerRefetch.setOnClickListener {
            photoViewModel.fetchPhotos()
        }
    }

    private fun observeFetchPhotosUIStateLiveData() {
        photoViewModel.fetchPhotosUIStateLiveData.observeUIState(viewLifecycleOwner, requireActivity()) { fetchPhotosUIState ->
            if (fetchPhotosUIState.showLoading) {
                binding.llPhotoPickerErrorWrapper.visibility = View.GONE
            } else if (fetchPhotosUIState.showError) {
                binding.llPhotoPickerErrorWrapper.visibility = View.VISIBLE
            }
        }
    }

    private fun setupPhotoPickerRecyclerView(photoPickerRecyclerView: RecyclerView) {
        photoPickerRecyclerViewAdapter = PhotoPickerRecyclerViewAdapter(this)
        photoPickerRecyclerView.adapter = photoPickerRecyclerViewAdapter
        photoPickerRecyclerView.layoutManager = object : GridLayoutManager(
            requireContext(),
            PhotoPickerRecyclerViewAdapter.NUM_OF_COLUMNS
        ) {
            override fun canScrollVertically(): Boolean = false
            override fun canScrollHorizontally(): Boolean = false
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0
        ) {
            override fun isLongPressDragEnabled(): Boolean {
                val isSwipeable = photoPickerRecyclerViewAdapter.isSwipeable()
                if (!isSwipeable) {
                    val title = getString(R.string.error_title_order_photos)
                    val message = getString(R.string.photo_not_orderable_exception)
                    ErrorDialog.show(title, message, childFragmentManager)
                }
                return isSwipeable
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                photoPickerRecyclerViewAdapter.swapPhotos(
                    viewHolder.absoluteAdapterPosition,
                    target.absoluteAdapterPosition
                )
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                viewHolder.itemView.setTag(androidx.recyclerview.R.id.item_touch_helper_previous_elevation, null)
//                photoViewModel.orderPhotos(photoPickerRecyclerViewAdapter.getPhotoPickerSequences())
            }

        })
        itemTouchHelper.attachToRecyclerView(photoPickerRecyclerView)
    }


    override fun onClickPhoto(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onDownloadPhotoError(photoKey: String?) {
        TODO("Not yet implemented")
    }

    override fun onDownloadPhotoSuccess(photoKey: String?) {
        TODO("Not yet implemented")
    }

    override fun reuploadPhoto(photoUri: Uri?, photoKey: String?) {
        TODO("Not yet implemented")
    }

    override fun redownloadPhoto(photoKey: String?) {
        TODO("Not yet implemented")
    }

    override fun deletePhoto(photoKey: String?) {
        TODO("Not yet implemented")
    }

    override fun uploadPhotoFromGallery() {
        TODO("Not yet implemented")
    }

    override fun uploadPhotoFromCapture() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val CAPTURED_PHOTO_NAME = "capturedPhoto.jpg"
        private const val FILE_PROVIDER_SUFFIX = ".fileProvider"
    }

}