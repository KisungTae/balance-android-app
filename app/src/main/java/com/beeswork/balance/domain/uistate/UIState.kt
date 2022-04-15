package com.beeswork.balance.domain.uistate

import com.beeswork.balance.internal.constant.ExceptionCode

open class UIState(
    val showLoading: Boolean,
    val showError: Boolean,
    val shouldLogout: Boolean,
    val exception: Throwable?
)