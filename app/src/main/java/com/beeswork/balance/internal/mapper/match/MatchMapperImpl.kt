package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.matchfragment.MatchDomain
import com.beeswork.balance.data.database.entity.match.MatchProfileTuple
import org.threeten.bp.ZoneId

class MatchMapperImpl : MatchMapper {
    override fun toProfileTuple(match: Match): MatchProfileTuple {
        return MatchProfileTuple(match.swipedId, match.swipedName, match.swipedProfilePhotoKey)
    }

    override fun toMatch(matchDTO: MatchDTO): Match {
        return Match(
            matchDTO.chatId,
            matchDTO.swiperId,
            matchDTO.swipedId,
            matchDTO.active,
            matchDTO.unmatched,
            matchDTO.swipedName,
            matchDTO.swipedProfilePhotoKey,
            matchDTO.swipedDeleted,
            null
        )
    }

    override fun toMatchDomain(match: Match): MatchDomain {
        return MatchDomain(
            match.chatId,
            match.swipedId,
            match.active,
            match.unmatched,
            match.swipedName,
            match.swipedProfilePhotoKey,
            match.updatedAt?.atZoneSameInstant(ZoneId.systemDefault()),
            match.unread,
            match.recentChatMessage
        )
    }
}