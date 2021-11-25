package com.beeswork.balance.ui.common

import androidx.fragment.app.DialogFragment
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.util.Validator
import com.beeswork.balance.ui.mainactivity.MainActivity

abstract class BaseDialog : DialogFragment() {

    protected fun validateLoginFromResource(resource: Resource<Any>): Boolean {
        if (Validator.validateLogin(resource.error)) return true
        if (activity is MainActivity) (activity as MainActivity).moveToLoginActivity(resource.error, resource.errorMessage)
        return false
    }
}