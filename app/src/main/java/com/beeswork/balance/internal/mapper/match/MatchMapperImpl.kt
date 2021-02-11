package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchResponse
import com.beeswork.balance.ui.match.MatchDomain
import org.threeten.bp.OffsetDateTime

class MatchMapperImpl : MatchMapper {
    override fun fromResponseToEntity(input: MatchResponse): Match {
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
            input.matchedId,
            input.unmatched,
            input.updatedAt,
            input.name,
            input.repPhotoKey,
            input.blocked,
            input.deleted,
            input.accountUpdatedAt,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        )
    }
}