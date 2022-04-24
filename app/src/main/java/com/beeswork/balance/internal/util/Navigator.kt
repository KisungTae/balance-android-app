package com.beeswork.balance.internal.util

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.exception.BaseException
import com.beeswork.balance.ui.loginactivity.LoginActivity

class Navigator {

    companion object {

        fun finishToActivity(fromActivity: Activity, toActivity: Class<*>) {
            val intent = Intent(fromActivity, toActivity)
            finishToActivity(fromActivity, intent)
        }

        private fun finishToActivity(fromActivity: Activity, intent: Intent) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            fromActivity.startActivity(intent)
            fromActivity.finish()
        }

        fun finishToLoginActivity(fromActivity: Activity, message: String?) {
            val intent = Intent(fromActivity, LoginActivity::class.java)
            if (message != null) {
                intent.putExtra(BundleKey.ERROR_MESSAGE, message)
            }
            finishToActivity(fromActivity, intent)
        }

        fun moveToFragment(fragmentActivity: FragmentActivity?, toFragment: Fragment, fromFragmentId: Int, fromFragmentTag: String) {
            fragmentActivity?.supportFragmentManager?.beginTransaction()?.let { transaction ->
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

        fun popBackStack(fragmentActivity: FragmentActivity?, fragmentTag: String) {
            fragmentActivity?.supportFragmentManager?.popBackStack(
                fragmentTag,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }
}