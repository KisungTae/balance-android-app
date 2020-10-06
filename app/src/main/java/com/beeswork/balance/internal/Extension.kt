package com.beeswork.balance.internal

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun SharedPreferences.Editor.putDouble(key: String, value: Double): SharedPreferences.Editor =
    putLong(key, java.lang.Double.doubleToRawLongBits(value))

fun SharedPreferences.getDouble(key: String, default: Double) =
    java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))


