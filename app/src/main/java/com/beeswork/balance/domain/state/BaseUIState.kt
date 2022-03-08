package com.beeswork.balance.domain.state

import com.beeswork.balance.data.network.response.Resource

abstract class BaseUIState (
    val showLoading: Boolean,
    val showError: Boolean,
    val shouldLogout: Boolean,
    val exception: Throwable?
)