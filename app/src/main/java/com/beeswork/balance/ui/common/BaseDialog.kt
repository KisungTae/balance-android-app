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
}