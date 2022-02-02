package com.beeswork.balance.internal.mapper.click

import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.click.ClickDomain
import java.util.*

class ClickMapperImpl : ClickMapper {
    override fun toClick(clickDTO: ClickDTO): Click? {
        return safeLet(
            clickDTO.id,
            clickDTO.swipedId,
            clickDTO.name,
            clickDTO.profilePhotoKey
        ) { id, swipedId, name, profilePhotoKey ->
            return@safeLet Click(id, clickDTO.swiperId, swipedId, name, clickDTO.clicked, profilePhotoKey)
        }
    }

    override fun toClickDomain(click: Click): ClickDomain {
        return ClickDomain(click.swiperId, click.name, click.clicked, click.profilePhotoKey)
    }
}