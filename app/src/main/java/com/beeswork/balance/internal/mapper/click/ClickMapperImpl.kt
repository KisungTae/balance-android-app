package com.beeswork.balance.internal.mapper.click

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.network.response.swipe.ClickDTO
import com.beeswork.balance.ui.click.ClickDomain

class ClickMapperImpl: ClickMapper {
    override fun fromDTOToEntity(dto: ClickDTO): Click {
        return Click(dto.swiperId, dto.profilePhotoKey, dto.updatedAt)
    }

    override fun fromEntityToDomain(entity: Click): ClickDomain {
        return ClickDomain(entity.swiperId, entity.profilePhotoKey)
    }
}