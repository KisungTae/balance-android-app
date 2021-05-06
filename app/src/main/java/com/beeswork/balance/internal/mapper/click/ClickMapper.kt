package com.beeswork.balance.internal.mapper.click

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.internal.mapper.common.Mapper
import com.beeswork.balance.ui.click.ClickDomain

interface ClickMapper: Mapper<SwipeDTO, Click, ClickDomain>