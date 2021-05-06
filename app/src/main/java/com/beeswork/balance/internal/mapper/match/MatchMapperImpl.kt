package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.match.MatchDomain
import com.beeswork.balance.data.database.tuple.MatchProfileTuple
import org.threeten.bp.ZoneId

class MatchMapperImpl : MatchMapper {
    override fun fromEntityToProfileTuple(entity: Match): MatchProfileTuple {
        return MatchProfileTuple(entity.swipedId, entity.name, entity.profilePhotoKey)
    }

    override fun fromDTOToEntity(dto: MatchDTO): Match {
        return Match(
            dto.chatId,
            dto.swipedId,
            dto.active,
            dto.unmatched,
            dto.name,
            dto.profilePhotoKey,
            dto.createdAt
        )
    }

    override fun fromEntityToDomain(entity: Match): MatchDomain {
        return MatchDomain(
            entity.chatId,
            entity.swipedId,
            entity.active,
            entity.unmatched,
            entity.name,
            entity.profilePhotoKey,
            entity.updatedAt?.atZoneSameInstant(ZoneId.systemDefault()),
            entity.unread,
            entity.recentChatMessage
        )
    }
}