package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.match.MatchDomain

class MatchMapperImpl : MatchMapper {
    override fun fromDTOToEntity(dto: MatchDTO): Match {
        return Match(
            dto.chatId,
            dto.matchedId,
            dto.unmatched,
            dto.updatedAt,
            dto.name,
            dto.repPhotoKey,
            dto.blocked,
            dto.deleted,
            dto.accountUpdatedAt
        )
    }

    override fun fromEntityToDomain(entity: Match): MatchDomain {
        return MatchDomain(
            entity.chatId
//            entity.matchedId,
//            entity.unmatched,
//            entity.updatedAt,
//            entity.name,
//            entity.repPhotoKey,
//            entity.blocked,
//            entity.deleted,
//            entity.accountUpdatedAt,
//            entity.unreadMessageCount,
//            entity.recentMessage,
//            entity.lastReadChatMessageId,
//            entity.accountViewedAt
        )
    }
}