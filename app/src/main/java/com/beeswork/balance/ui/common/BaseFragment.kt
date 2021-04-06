package com.beeswork.balance.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.ui.login.LoginFragment
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import java.lang.Exception

abstract class BaseFragment : Fragment() {

    protected fun validateAccount(error: String?, errorMessage: String?) {
        error?.let {
            if (it == ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION ||
                it == ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION ||
                it == ExceptionCode.ACCOUNT_DELETED_EXCEPTION)
                popToLoginFragment(errorMessage)
        }
    }

    private fun popToLoginFragment(errorMessage: String?) {
        val loginFragment = LoginFragment()
        val arguments = Bundle()
        errorMessage?.let { arguments.putString(BundleKey.ERROR_MESSAGE, it) }
        activity?.supportFragmentManager?.beginTransaction()?.let {
            it.add(R.id.fcvMain, loginFragment)
            it.commit()
        }
    }
}