package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS

class ClickRDSImpl(
    private val balanceAPI: BalanceAPI
): BaseRDS(), ClickRDS {
}