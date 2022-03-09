package com.beeswork.balance.internal.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.domain.uistate.BaseUIState
import com.beeswork.balance.internal.constant.ExceptionCode
import kotlinx.coroutines.*

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    block: (T1, T2, T3, T4) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}


fun <T> CoroutineScope.testLaunch2(final: suspend () -> Resource<T>, body: (Resource<T>) -> Unit) {
    this.launch {
        val response = final.invoke()
        body.invoke(response)
    }
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

const val ANIMATION_DURATION = 100L

fun View.slideInFromLeft() {
    this.translationX = this.width.toFloat().unaryMinus()
    this.visibility = View.VISIBLE
    this.animate()
        .setDuration(ANIMATION_DURATION)
        .translationX(0f)
        .start()
}

fun View.slideInFromRight() {
    this.translationX = this.width.toFloat()
    this.visibility = View.VISIBLE
    this.animate()
        .setDuration(ANIMATION_DURATION)
        .translationX(0f)
        .start()
}

fun View.slideOutToRight() {
    this.animate()
        .withStartAction { this.translationX = 0f }
        .withEndAction { this.visibility = View.GONE }
        .setDuration(ANIMATION_DURATION)
        .translationX(this.width.toFloat())
        .start()
}

fun View.slideOutToLeft() {
    this.animate()
        .withStartAction { this.translationX = 0f }
        .withEndAction { this.visibility = View.GONE }
        .setDuration(ANIMATION_DURATION)
        .translationX(this.width.toFloat().unaryMinus())
        .start()
}

fun View.getText(): String {
    return when (this) {
        is Button -> this.text.toString()
        is TextView -> this.text.toString()
        else -> ""
    }
}

fun <T> lazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {

    return lazy {
        GlobalScope.async(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
    }
}


fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Activity.hideKeyboard(ev: MotionEvent) {
    val v: View? = currentFocus
    if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && v is EditText &&
        !v.javaClass.name.startsWith("android.webkit.")
    ) {
        println("tag: ${v.tag}")
        val sourceCoordinates = IntArray(2)
        v.getLocationOnScreen(sourceCoordinates)
        val x: Float = ev.rawX + v.getLeft() - sourceCoordinates[0]
        val y: Float = ev.rawY + v.getTop() - sourceCoordinates[1]
        if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
            val inputManager = (this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
            inputManager.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
        }
    }
}

fun <T> LiveData<Resource<T>>.observeResource(lifecycleOwner: LifecycleOwner, activity: Activity?, block: (resource: Resource<T>) -> Unit) {
    observe(lifecycleOwner) { resource ->
        println("observeResource inside observe")
        if (activity != null && resource.isError() && ExceptionCode.isLoginException(resource.exception)) {
            println("observeResource resource.isError() && ExceptionCode.isLoginException(resource.exception)")
            val message = MessageSource.getMessage(activity, resource.exception)
            Navigator.finishToLoginActivity(activity, message)
        } else {
            block.invoke(resource)
        }
    }
}

fun <T: BaseUIState> LiveData<T>.observeUIState(lifecycleOwner: LifecycleOwner, activity: Activity?, block: (uiState: T) -> Unit) {
    observe(lifecycleOwner) { uiState ->
        if (activity != null && uiState.shouldLogout) {
            val message = MessageSource.getMessage(activity, uiState.exception)
            Navigator.finishToLoginActivity(activity, message)
        } else {
            block.invoke(uiState)
        }
    }
}


