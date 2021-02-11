package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.match.MatchDomain

class MatchMapperImpl : MatchMapper {
    override fun fromDTOToEntity(input: MatchDTO): Match {
        return Match(
            input.chatId,
            input.matchedId,
            input.unmatched,
            input.updatedAt,
            input.name,
            input.repPhotoKey,
            input.blocked,
            input.deleted,
            input.accountUpdatedAt
        )
    }

    override fun fromEntityToDomain(input: Match): MatchDomain {
        return MatchDomain(
            input.chatId,
//            input.matchedId,
//            input.unmatched,
//            input.updatedAt,
//            input.name,
//            input.repPhotoKey,
//            input.blocked,
//            input.deleted,
//            input.accountUpdatedAt,
//            OffsetDateTime.now(),
//            OffsetDateTime.now()
        )
    }
}