package com.beeswork.balance.internal.util

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.Snackbar

class SnackBarHelper {

    companion object {
        fun make(parentView: View, gravity: Int, topMargin: Int, bottomMargin: Int, customView: View): Snackbar {
            val snackBar = Snackbar.make(parentView, "", Snackbar.LENGTH_INDEFINITE)
            snackBar.view.setBackgroundColor(Color.TRANSPARENT)
            val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
            snackBarLayout.addView(customView, 0)
            snackBarLayout.setPadding(0, 0, 0, 0)

            if (parentView is CoordinatorLayout) {
                val layoutParams = snackBar.view.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.gravity = gravity
                layoutParams.setMargins(0, topMargin, 0, bottomMargin)
                snackBar.view.layoutParams = layoutParams
            }
            return snackBar
        }
    }
}