package com.beeswork.balance.ui.common

import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.dialog.ErrorDialog

open class BaseDialog : DialogFragment() {

    protected fun setupErrorMessage(error: String?, errorMessage: String?, errorMessageTextView: TextView) {
        errorMessageTextView.text = getErrorMessage(error, errorMessage)
        errorMessageTextView.visibility = View.VISIBLE
    }

    private fun getErrorMessage(error: String?, errorMessage: String?): String {
        return errorMessage ?: kotlin.run {
            safeLet(error, context) { e, c ->
                val resourceId = resources.getIdentifier(e, "string", c.packageName)
                if (resourceId > 0) return@safeLet getString(resourceId)
                else return@safeLet getString(R.string.error_message_generic)
            } ?: getString(R.string.error_message_generic)
        }
    }

    protected fun showErrorDialog(
        error: String?,
        errorTitle: String,
        errorMessage: String?,
    ) {
        ErrorDialog(error, errorTitle, errorMessage, null, null, null).show(childFragmentManager, ErrorDialog.TAG)
    }
}