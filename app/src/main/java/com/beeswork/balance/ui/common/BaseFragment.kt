package com.beeswork.balance.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.ExceptionCode
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

    private fun popToLoginFragment(errorMessage: String?): Boolean {
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
}

// TODO: remove access token when pop to login fragment