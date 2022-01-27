package com.beeswork.balance.internal.mapper.click

import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.ui.click.ClickDomain
import java.util.*

interface ClickMapper {
    fun toClick(clickDTO: ClickDTO): Click?
    fun toClickDomain(click: Click): ClickDomain
}