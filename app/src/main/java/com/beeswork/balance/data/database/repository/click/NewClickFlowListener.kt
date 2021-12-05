package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.entity.click.Click

interface NewClickFlowListener {
    fun onReceive(click: Click)
}