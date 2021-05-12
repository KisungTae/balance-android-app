package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.match.MatchDomain
import com.beeswork.balance.data.database.tuple.MatchProfileTuple
import org.threeten.bp.ZoneId

class MatchMapperImpl : MatchMapper {
    override fun toProfileTuple(match: Match): MatchProfileTuple {
        return MatchProfileTuple(match.swipedId, match.name, match.profilePhotoKey)
    }

    override fun toMatch(matchDTO: MatchDTO): Match {
        return Match(
            matchDTO.chatId,
            matchDTO.swipedId,
            matchDTO.active,
            matchDTO.unmatched,
            matchDTO.name,
            matchDTO.profilePhotoKey,
            matchDTO.createdAt
        )
    }

    override fun toMatchDomain(match: Match): MatchDomain {
        return MatchDomain(
            match.chatId,
            match.swipedId,
            match.active,
            match.unmatched,
            match.name,
            match.profilePhotoKey,
            match.updatedAt?.atZoneSameInstant(ZoneId.systemDefault()),
            match.unread,
            match.recentChatMessage
        )
    }
}