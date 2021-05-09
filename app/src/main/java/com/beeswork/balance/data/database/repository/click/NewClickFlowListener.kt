package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.entity.Click

interface NewClickFlowListener {
    fun onNewClickReceived(click: Click)
}