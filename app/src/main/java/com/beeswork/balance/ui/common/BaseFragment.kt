package com.beeswork.balance.ui.common

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.beeswork.balance.R
import com.beeswork.balance.internal.exception.*
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainactivity.MainActivity

abstract class BaseFragment : Fragment() {

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

    protected fun moveToLoginActivity() {
        moveToLoginActivity(null, null)
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

