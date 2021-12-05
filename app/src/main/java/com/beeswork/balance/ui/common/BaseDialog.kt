package com.beeswork.balance.ui.common

import androidx.fragment.app.DialogFragment
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.ui.mainactivity.MainActivity

abstract class BaseDialog : DialogFragment() {

    protected fun validateLogin(resource: Resource<Any>): Boolean {
        if (ExceptionCode.isLoginException(resource.error)) {
            if (activity is MainActivity) {
                (activity as MainActivity).moveToLoginActivity(resource.error, resource.errorMessage)
            }
            return false
        }
        return true
    }
}