package com.beeswork.balance.ui.common

import androidx.fragment.app.DialogFragment
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.ui.mainactivity.MainActivity

abstract class BaseDialog : DialogFragment() {

//    protected fun validateLogin(exception: Throwable?): Boolean {
//        if (ExceptionCode.isLoginException(exception)) {
//            if (activity is MainActivity) {
//                (activity as MainActivity).moveToLoginActivity(exception)
//            }
//            return false
//        }
//        return true
//    }
}