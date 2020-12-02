package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.Resource
import kotlinx.android.synthetic.main.dialog_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ProfileDialog : DialogFragment(), KodeinAware, PhotoUploadOptionDialog.PhotoUploadOptionListener,
    PhotoPickerRecyclerViewAdapter.PhotoPickerListener {

    override val kodein by closestKodein()
    private val balanceRepository: BalanceRepository by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        println("bindUI")
        btnProfileDialogClose.setOnClickListener { dismiss() }
        btnProfileDialogReloadPhotos.setOnClickListener { fetchPhotos() }
        setupPhotoPickerRecyclerView()
        fetchPhotos()



//        tvEditBalanceGame.setOnClickListener {
//            EditBalanceGameDialog().show(childFragmentManager, EditBalanceGameDialog.TAG)
//        }
    }

    private fun setupPhotoPickerRecyclerView() {

        val photoPickers = mutableListOf<PhotoPicker>()
        for (i in 0 until MAXIMUM_NUM_OF_PHOTOS) {
            photoPickers.add(i, PhotoPicker(null, PhotoPicker.Status.EMPTY))
        }
        rvPhotoPicker.adapter = PhotoPickerRecyclerViewAdapter(photoPickers, this)
        rvPhotoPicker.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private fun fetchPhotos() {

        val adapter = rvPhotoPicker.adapter as PhotoPickerRecyclerViewAdapter
        adapter.showAllLoadingViews()
        llPhotoPickerGalleryError.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {

            val response = balanceRepository.fetchPhotos()

            withContext(Dispatchers.Main) {
                if (response.status == Resource.Status.EXCEPTION)
                    llPhotoPickerGalleryError.visibility = View.VISIBLE
                else if (response.status == Resource.Status.SUCCESS) {
                    adapter.updateFromPhotos(response.data)
                }
            }
        }
    }


    private fun setupPhotoPicker() {
        val photos = mutableListOf("a", "b", "c", "d", "e", "f")


        rvPhotoPicker.layoutManager = GridLayoutManager(requireContext(), 3)




        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

//                val from = viewHolder.adapterPosition
//                val to = target.adapterPosition
//                Collections.swap(photos, from, to)
//                recyclerView.adapter?.notifyItemMoved(from, to)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                println("clearView")
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rvPhotoPicker)
    }

    override fun onUploadFromGallery() {

    }

    override fun onUploadFromCapture() {

    }

    override fun onClickPhotoPicker() {

    }

    companion object {
        const val TAG = "profileDialog"
        const val MAXIMUM_NUM_OF_PHOTOS = 6

    }



//    private fun setupDragListener() {


//

//
//
//    override fun onUploadFromGallery() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (hasExternalStoragePermission()) {
//                selectPhotoFromGallery()
//            } else {
//                requestPermissions(
//                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    RequestCode.READ_PHOTO
//                )
//            }
//        } else {
//            selectPhotoFromGallery()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == RequestCode.READ_PHOTO) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                selectPhotoFromGallery()
//        }
//    }
//
//    private fun selectPhotoFromGallery() {
//        val intent = Intent(Intent.ACTION_PICK);
//        intent.type = "image/*"
//
//        startActivityForResult(intent, RequestCode.READ_PHOTO)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        if (resultCode == RESULT_OK && requestCode == RequestCode.READ_PHOTO) {
//
//            for (i in 0 until llPhotoPicker.childCount) {
//                val view = llPhotoPicker.getChildAt(i) as ImageView
//                if (!(view.tag as Boolean)) {
//
//                }
//            }
//        }
//    }
//
//
//    private fun hasExternalStoragePermission(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//
//    override fun onUploadFromCapture() {
//    }
//
//    class PhotoTag(
//        var key: String,
//        var photoStatus: PhotoStatus
//    )
//
//    enum class PhotoStatus {
//        EMPTY,
//        LOADING,
//        OCCUPIED,
//        PLACEHOLDER
//    }
//
//    class CustomDragShadowBuilder(
//        v: View,
//        private val lastTouch: Point?
//    ) : View.DragShadowBuilder(v) {
//
//        override fun onProvideShadowMetrics(size: Point?, touch: Point) {
//            println("onProvideShadowMetrics: size: ${size.toString()}")
//            super.onProvideShadowMetrics(size, touch)
//
//            if (lastTouch != null)
//                touch.set(lastTouch.x, lastTouch.y)
//        }
//    }

}