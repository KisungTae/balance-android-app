package com.beeswork.balance.ui.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.BaseException
import com.beeswork.balance.ui.mainactivity.MainActivity

abstract class BaseFragment : Fragment() {

//    protected fun validateLogin(exception: Throwable?): Boolean {
//        if (ExceptionCode.isLoginException(exception)) {
//            moveToLoginActivity(exception)
//            return false
//        }
//        return true
//    }
//
//    protected fun moveToLoginActivity(exception: Throwable?) {
//        if (activity is MainActivity) {
//            val mainActivity = (activity as MainActivity)
//            if (exception != null && exception is BaseException) {
//                mainActivity.moveToLoginActivity(exception)
//            } else {
//                mainActivity.moveToLoginActivity(null)
//            }
//        }
//    }

    protected fun moveToFragment(toFragment: Fragment, fromFragmentId: Int, fromFragmentTag: String) {
        activity?.supportFragmentManager?.beginTransaction()?.let { transaction ->
            transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
            )
            transaction.add(fromFragmentId, toFragment)
            transaction.addToBackStack(fromFragmentTag)
            transaction.commit()
        }
    }

    protected fun popBackStack(fragmentTag: String) {
        activity?.supportFragmentManager?.popBackStack(
            fragmentTag,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}

