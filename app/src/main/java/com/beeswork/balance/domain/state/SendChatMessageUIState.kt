package com.beeswork.balance.domain.state

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import java.lang.Exception

class SendChatMessageUIState(
    val clearChatMessageInput: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : BaseUIState(showLoading, showError, shouldLogout, exception)