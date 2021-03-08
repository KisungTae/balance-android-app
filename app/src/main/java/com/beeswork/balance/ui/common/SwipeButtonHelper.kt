package com.beeswork.balance.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class SwipeButtonHelper(
    context: Context,
    private val recyclerView: RecyclerView,
    internal var buttonWidth: Int
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var swipeButtons: MutableList<SwipeButton>? = null
    lateinit var gestureDetector: GestureDetector
    var swipePosition = 0
    var swipeThreshold = 0.5f
    val swipeButtonBuffer: MutableMap<Int, MutableList<SwipeButton>>
    lateinit var removerQueue: LinkedList<Int>

    abstract fun initiateSwipeButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<SwipeButton>)

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            for (swipeButton in swipeButtons!!) {
                if (swipeButton.onClick(e!!.x, e.y))
                    break
            }
            return true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { v, motionEvent ->
        if (swipePosition < 0) return@OnTouchListener false
        val point = Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
        val swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition)
        val swipedItem = swipedViewHolder!!.itemView
        val rect = Rect()
        swipedItem.getGlobalVisibleRect(rect)

        if (motionEvent.action == MotionEvent.ACTION_DOWN ||
                motionEvent.action == MotionEvent.ACTION_MOVE ||
                motionEvent.action == MotionEvent.ACTION_UP) {
            if (rect.top < point.y && rect.bottom > point.y)
                gestureDetector.onTouchEvent(motionEvent)
            else {
                removerQueue.add(swipePosition)
                swipePosition = -1
                recoverSwipeItem()
            }
        }
        false
    }

    @Synchronized
    private fun recoverSwipeItem() {
        while (!removerQueue.isEmpty()) {
            val pos = removerQueue.poll()!!.toInt()
            if (pos > -1) recyclerView.adapter!!.notifyItemChanged(pos)
        }
    }

    init {
        this.swipeButtons = ArrayList()
        this.gestureDetector = GestureDetector(context, gestureListener)
        this.recyclerView.setOnTouchListener(onTouchListener)
        this.swipeButtonBuffer = HashMap()
        this.removerQueue = InitLinkedList()
        attachSwipe()
    }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    class InitLinkedList: LinkedList<Int>() {
        override fun contains(element: Int): Boolean {
            return false
        }

        override fun lastIndexOf(element: Int): Int {
            return element
        }

        override fun remove(element: Int): Boolean {
            return false
        }

        override fun indexOf(element: Int): Int {
            return element
        }

        override fun add(element: Int): Boolean {
            return if (contains(element)) false
            else super.add(element)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipePosition != pos)
            removerQueue.add(swipePosition)
        swipePosition = pos
        if (swipeButtonBuffer.containsKey(swipePosition))
            swipeButtons = swipeButtonBuffer[swipePosition]
        else swipeButtons!!.clear()
        swipeButtonBuffer.clear()
        swipeThreshold = 0.5f * swipeButtons!!.size.toFloat() * buttonWidth.toFloat()
        recoverSwipeItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        var itemView = viewHolder.itemView
        if (pos < 0) {
            swipePosition = pos
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<SwipeButton> = ArrayList()
                if (!swipeButtonBuffer.containsKey(pos)) {
                    initiateSwipeButton(viewHolder, buffer)
                    swipeButtonBuffer[pos] = buffer
                } else {
                    buffer = swipeButtonBuffer[pos]!!
                }
                translationX = dX * buffer.size.toFloat() * buttonWidth.toFloat() / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawButton(c: Canvas, itemView: View, buffer: MutableList<SwipeButton>, pos: Int, translationX: Float) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * translationX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(c, RectF(left, itemView.top.toFloat(), right, itemView.bottom.toFloat()), pos)
            right = left
        }
    }
}