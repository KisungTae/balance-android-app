package com.beeswork.balance.ui.common

import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.AccountBlockedException
import com.beeswork.balance.internal.exception.AccountDeletedException
import com.beeswork.balance.internal.exception.AccountNotFoundException
import com.beeswork.balance.internal.util.Validator
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainactivity.MainActivity

abstract class BaseDialog : DialogFragment() {

    protected fun validateLoginFromResponse(response: Resource<Any>): Boolean {
        if (Validator.validateLogin(response.error)) return true
        if (activity is MainActivity) (activity as MainActivity).moveToLoginActivity(response.error, response.errorMessage)
        return false
    }
}