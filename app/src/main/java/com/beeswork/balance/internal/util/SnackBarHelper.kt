package com.beeswork.balance.internal.util

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

class SnackBarHelper {

    companion object {
        fun make(parentView: View, gravity: Int, topPadding: Int, bottomPadding: Int, customView: View): Snackbar {
            val snackBar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG)
            snackBar.view.setBackgroundColor(Color.TRANSPARENT)
            val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
            snackBarLayout.addView(customView, 0)
            snackBarLayout.setPadding(10, topPadding, 10, bottomPadding)

            if (parentView !is CoordinatorLayout) {
                val layoutParams = snackBar.view.layoutParams as FrameLayout.LayoutParams
                layoutParams.gravity = gravity
                snackBar.view.layoutParams = layoutParams
            }

            return snackBar
        }

        fun make(parentView: View, topPadding: Int, bottomPadding: Int, customView: View): Snackbar {
            return make(parentView, Gravity.BOTTOM, topPadding, bottomPadding, customView)
        }
    }
}