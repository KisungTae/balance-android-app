package com.beeswork.balance.ui.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.DragShadowBuilder
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.ui.base.ScopeFragment
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : ScopeFragment(), PhotoUploadOptionDialog.PhotoUploadOptionListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private lateinit var lastTouch: Point
    private lateinit var photoShadowBuilder: CustomDragShadowBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (i in 0 until llPhotoPicker.childCount) {
            val childView = llPhotoPicker.getChildAt(i)

            childView.setOnTouchListener { v, event ->
                lastTouch = Point(event.x.toInt(), event.y.toInt())
                v.performClick()
                false
            }

            childView.setOnLongClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    it.visibility = View.INVISIBLE
                    it.startDragAndDrop(null, CustomDragShadowBuilder(it, lastTouch), it, 0)
                } else it.startDrag(null, CustomDragShadowBuilder(it, lastTouch), it, 0)
                true
            }


            childView.setOnDragListener { v, event ->
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> true
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        if (v.tag is PhotoTag) {
                            val draggingView = event.localState as View
                            val droppedIndex = llPhotoPicker.indexOfChild(v)
                            val draggingViewIndex = llPhotoPicker.indexOfChild(draggingView)
                            llPhotoPicker.removeViewAt(draggingViewIndex)
                            llPhotoPicker.addView(draggingView, droppedIndex)
                            true
                        }
                        false
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> true
                    DragEvent.ACTION_DRAG_EXITED -> true
                    DragEvent.ACTION_DROP -> true
                    DragEvent.ACTION_DRAG_ENDED -> {
                        val draggingView = event.localState as View
                        draggingView.post { run { draggingView.visibility = View.VISIBLE } }
                        true
                    }
                    else -> false
                }
            }

            childView.tag = PhotoTag("photo-$i", PhotoStatus.EMPTY)
        }

//        llPhotoPicker.setOnDragListener { v, event -> onDragPhoto(v, event) }
    }

    private fun onDragPhoto(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> return true
            DragEvent.ACTION_DRAG_ENTERED -> {
                if (v.tag is PhotoTag) {
                    val draggingView = event.localState as View
                    val droppedIndex = llPhotoPicker.indexOfChild(v)
                    val draggingViewIndex = llPhotoPicker.indexOfChild(draggingView)
                    llPhotoPicker.removeViewAt(draggingViewIndex)
                    llPhotoPicker.addView(draggingView, droppedIndex)
                    return true
                }
                return false
            }
            DragEvent.ACTION_DRAG_LOCATION -> return true
            DragEvent.ACTION_DRAG_EXITED -> return true
            DragEvent.ACTION_DROP -> return true
            DragEvent.ACTION_DRAG_ENDED -> {
                val draggingView = event.localState as View
                draggingView.post { run { draggingView.visibility = View.VISIBLE } }
                return true
            }
            else -> return false
        }
    }

    private fun reorder() {

    }

    override fun onUploadFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasExternalStoragePermission()) {
                selectPhotoFromGallery()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    RequestCode.READ_PHOTO
                )
            }
        } else {
            selectPhotoFromGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestCode.READ_PHOTO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectPhotoFromGallery()
        }
    }

    private fun selectPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK);
        intent.type = "image/*"

        startActivityForResult(intent, RequestCode.READ_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && requestCode == RequestCode.READ_PHOTO) {

            for (i in 0 until llPhotoPicker.childCount) {
                val view = llPhotoPicker.getChildAt(i) as ImageView
                if (!(view.tag as Boolean)) {

                }
            }
        }
    }


    private fun hasExternalStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onUploadFromCapture() {
    }

    class PhotoTag(
        var key: String,
        var photoStatus: PhotoStatus
    )

    enum class PhotoStatus {
        EMPTY,
        LOADING,
        OCCUPIED,
        PLACEHOLDER
    }

    class CustomDragShadowBuilder(
        v: View,
        private val lastTouch: Point?
    ) : View.DragShadowBuilder(v) {

        override fun onProvideShadowMetrics(size: Point?, touch: Point) {
            println("onProvideShadowMetrics: size: ${size.toString()}")
            super.onProvideShadowMetrics(size, touch)

            if (lastTouch != null)
                touch.set(lastTouch.x, lastTouch.y)
        }
    }

}