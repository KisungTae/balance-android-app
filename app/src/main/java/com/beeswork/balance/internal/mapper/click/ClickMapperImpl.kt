package com.beeswork.balance.internal.mapper.click

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.ui.click.ClickDomain

class ClickMapperImpl: ClickMapper {
    override fun toClick(clickDTO: ClickDTO): Click {
        return Click(clickDTO.swiperId, clickDTO.profilePhotoKey, clickDTO.updatedAt)
    }

    override fun toClickDomain(click: Click): ClickDomain {
        return ClickDomain(click.swiperId, click.profilePhotoKey)
    }
}