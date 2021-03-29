package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.match.MatchDomain
import org.threeten.bp.OffsetDateTime

class MatchMapperImpl : MatchMapper {
    override fun fromDTOToEntity(dto: MatchDTO): Match {
        return Match(
            dto.chatId,
            dto.matchedId,
            dto.active,
            dto.unmatched,
            dto.name,
            dto.repPhotoKey ?: "",
            dto.deleted,
            dto.createdAt
        )
    }

    override fun fromEntityToDomain(entity: Match): MatchDomain {
        return MatchDomain(
            entity.chatId,
            entity.matchedId,
            entity.active,
            entity.unmatched,
            entity.name,
            entity.repPhotoKey,
            entity.deleted,
            entity.updatedAt,
            entity.unread,
            entity.recentChatMessage,
            entity.lastReadChatMessageId,
            entity.isValid()
        )
    }
}