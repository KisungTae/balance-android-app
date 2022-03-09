package com.beeswork.balance.domain.uistate

abstract class BaseUIState (
    val showLoading: Boolean,
    val showError: Boolean,
    val shouldLogout: Boolean,
    val exception: Throwable?
)