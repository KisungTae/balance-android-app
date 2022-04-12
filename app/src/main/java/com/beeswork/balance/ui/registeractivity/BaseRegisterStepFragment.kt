package com.beeswork.balance.ui.registeractivity

import androidx.fragment.app.Fragment

abstract class BaseRegisterStepFragment: Fragment() {

    protected fun moveToNextTab() {
        activity?.let { _activity ->
            if (_activity is RegisterActivity) {
                _activity.moveToNextTab()
            }
        }
    }
}