package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.match.MatchDomain
import com.beeswork.balance.ui.match.NewMatchDomain

class MatchMapperImpl : MatchMapper {
    override fun fromEntityToNewMatchDomain(entity: Match?): NewMatchDomain? {
        return entity?.let { NewMatchDomain(entity.matchedId, entity.name, entity.repPhotoKey ?: "") }
    }

    override fun fromDTOToEntity(dto: MatchDTO): Match {
        return Match(
            dto.chatId,
            dto.matchedId,
            dto.active,
            dto.unmatched,
            dto.name,
            dto.repPhotoKey,
            dto.deleted,
            dto.createdAt
        )
    }

    override fun fromEntityToDomain(entity: Match): MatchDomain {
        return MatchDomain(
            entity.chatId,
            entity.matchedId,
            entity.active,
            entity.name,
            entity.repPhotoKey,
            entity.updatedAt,
            entity.unread,
            entity.recentChatMessage,
            entity.isValid()
        )
    }
}