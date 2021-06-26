package com.beeswork.balance.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.login.LoginFragment
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import java.lang.Exception

abstract class BaseFragment : Fragment() {

    protected fun validateAccount(error: String?, errorMessage: String?): Boolean {
        return error?.let {
            return when (it) {
                ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION,
                ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
                ExceptionCode.ACCOUNT_DELETED_EXCEPTION -> popToLoginFragment(errorMessage)
                else -> true
            }
        } ?: true
    }

    protected fun popToLoginFragment(errorMessage: String?): Boolean {
        val loginFragment = LoginFragment()
        val arguments = Bundle()
        errorMessage?.let { arguments.putString(BundleKey.ERROR_MESSAGE, it) }

        activity?.supportFragmentManager?.let {
            if (it.backStackEntryCount > 0)
                it.popBackStack(it.getBackStackEntryAt(0).id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            it.beginTransaction().replace(R.id.fcvMain, loginFragment).commit()
        }
        return false
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

    protected fun showErrorDialog(
        errorTitle: String,
        errorMessage: String?,
        onDismissListener: ErrorDialog.OnDismissListener
    ) {
        ErrorDialog(null, errorTitle, errorMessage, null, null, onDismissListener).show(
            childFragmentManager,
            ErrorDialog.TAG
        )
    }

    protected fun showErrorDialog(
        error: String?,
        errorTitle: String,
        errorMessage: String?,
        requestCode: Int,
        retryListener: ErrorDialog.OnRetryListener
    ) {
        ErrorDialog(error, errorTitle, errorMessage, requestCode, retryListener, null).show(
            childFragmentManager,
            ErrorDialog.TAG
        )
    }

    protected fun showErrorDialog(
        error: String?,
        errorTitle: String,
        errorMessage: String?,
    ) {
        ErrorDialog(error, errorTitle, errorMessage, null, null, null).show(
            childFragmentManager,
            ErrorDialog.TAG
        )
    }
}

// TODO: remove access token when pop to login fragment