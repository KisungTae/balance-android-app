package com.beeswork.balance.ui.common

import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.AccountBlockedException
import com.beeswork.balance.internal.exception.AccountDeletedException
import com.beeswork.balance.internal.exception.AccountNotFoundException
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainactivity.MainActivity

open class BaseDialog : DialogFragment() {

    protected fun observeExceptionLiveData(baseViewModel: BaseViewModel) {
        baseViewModel.exceptionLiveData.observe(viewLifecycleOwner) { exception -> catchException(exception) }
    }

    private fun catchException(throwable: Throwable) {
        when (throwable) {
            is AccountNotFoundException,
            is AccountDeletedException,
            is AccountBlockedException -> moveToLoginActivity(null, throwable.message)
            else -> throw throwable
        }
    }

    private fun moveToLoginActivity(error: String?, errorMessage: String?) {
        if (activity is MainActivity) (activity as MainActivity).moveToLoginActivity(error, errorMessage)
    }

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