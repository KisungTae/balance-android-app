package com.beeswork.balance.ui.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.exception.*
import com.beeswork.balance.internal.util.Validator
import com.beeswork.balance.ui.mainactivity.MainActivity

abstract class BaseFragment : Fragment() {

    protected fun validateLoginFromResponse(response: Resource<Any>): Boolean {
        if (Validator.validateLogin(response.error)) return true
        moveToLoginActivity(response.error, response.errorMessage)
        return false
    }

    protected fun moveToLoginActivity(error: String?, errorMessage: String?) {
        if (activity is MainActivity) (activity as MainActivity).moveToLoginActivity(error, errorMessage)
    }

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
        requireActivity().supportFragmentManager.popBackStack(
            fragmentTag,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}

