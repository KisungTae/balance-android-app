package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.match.MatchDomain
import com.beeswork.balance.data.database.response.NewMatch
import org.threeten.bp.ZoneId
import java.util.*

class MatchMapperImpl : MatchMapper {
    override fun fromEntityToNewMatch(entity: Match, accountId: UUID?, profilePhotoKey: String?): NewMatch {
        return NewMatch(entity.swipedId, entity.name, entity.profilePhotoKey, accountId, profilePhotoKey)
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