package com.beeswork.balance.ui.common

import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.util.safeLet

open class BaseDialog: DialogFragment() {

    protected fun setupErrorMessage(error: String?, errorMessage: String?, errorMessageTextView: TextView) {
        errorMessage?.let { message ->
            errorMessageTextView.visibility = View.VISIBLE
            errorMessageTextView.text = message
        } ?: kotlin.run {
            safeLet(error, context) { e, c ->
                val resourceId = resources.getIdentifier(e, "string", c.packageName)
                if (resourceId > 0) errorMessageTextView.text = getString(resourceId)
                else errorMessageTextView.visibility = View.GONE
            } ?: kotlin.run {
                errorMessageTextView.visibility = View.GONE
            }
        }
    }
}