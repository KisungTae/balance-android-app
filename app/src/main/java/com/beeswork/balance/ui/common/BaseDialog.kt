package com.beeswork.balance.ui.common

import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.dialog.ErrorDialog

open class BaseDialog: DialogFragment() {

    protected fun setupErrorMessage(error: String?, errorMessage: String?, errorMessageTextView: TextView) {
        errorMessage?.let { message ->
            errorMessageTextView.visibility = View.VISIBLE
            errorMessageTextView.text = message
        } ?: kotlin.run {
            safeLet(error, context) { e, c ->
                val resourceId = resources.getIdentifier(e, "string", c.packageName)
                if (resourceId > 0) {
                    errorMessageTextView.text = getString(resourceId)
                    errorMessageTextView.visibility = View.VISIBLE
                }
                else errorMessageTextView.visibility = View.GONE
            } ?: kotlin.run {
                errorMessageTextView.visibility = View.GONE
            }
        }
    }

    protected fun showErrorDialog(
        error: String?,
        errorTitle: String,
        errorMessage: String?,
    ) {
        ErrorDialog(error, errorTitle, errorMessage, null, null, null).show(
            childFragmentManager,
            ErrorDialog.TAG
        )
    }
}