package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.matchfragment.MatchDomain
import com.beeswork.balance.data.database.entity.match.MatchProfileTuple
import org.threeten.bp.ZoneId

class MatchMapperImpl : MatchMapper {

    override fun toMatch(matchDTO: MatchDTO): Match {
        return Match(
            matchDTO.id,
            matchDTO.chatId,
            matchDTO.swiperId,
            matchDTO.swipedId,
            matchDTO.unmatched,
            matchDTO.lastReadChatMessageId,
            matchDTO.lastChatMessageId,
            matchDTO.lastChatMessageBody,
            matchDTO.swipedName,
            matchDTO.swipedProfilePhotoKey,
            matchDTO.swipedDeleted
        )
    }

    override fun toMatchDomain(match: Match): MatchDomain {
        return MatchDomain(
            match.chatId,
            match.swipedId,
            match.lastChatMessageId > 0L,
            match.lastReadChatMessageId < match.lastChatMessageId,
            match.unmatched || match.swipedDeleted,
            match.lastChatMessageBody ?: "",
            match.swipedName,
            match.swipedProfilePhotoKey
        )
    }
}