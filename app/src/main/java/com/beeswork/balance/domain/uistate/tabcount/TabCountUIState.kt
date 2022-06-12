package com.beeswork.balance.domain.uistate.tabcount

import com.beeswork.balance.internal.constant.TabPosition

data class TabCountUIState(
    val tabPosition: TabPosition,
    val count: Long,
)