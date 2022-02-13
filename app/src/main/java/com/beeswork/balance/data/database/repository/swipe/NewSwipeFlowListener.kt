package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe

interface NewSwipeFlowListener {
    fun onReceive(swipe: Swipe)
}