package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.domain.uistate.match.MatchNotificationUIState
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.ui.matchfragment.MatchItemUIState

class MatchMapperImpl : MatchMapper {

    override fun toMatch(matchDTO: MatchDTO): Match {
        return Match(
            matchDTO.id,
            matchDTO.chatId,
            matchDTO.swiperId,
            matchDTO.swipedId,
            matchDTO.unmatched,
            matchDTO.lastReceivedChatMessageId,
            matchDTO.lastReadReceivedChatMessageId,
            matchDTO.lastReadByChatMessageId,
            matchDTO.lastChatMessageId,
            matchDTO.lastChatMessageBody,
            matchDTO.createdAt,
            matchDTO.swipedName,
            matchDTO.swipedProfilePhotoKey,
            matchDTO.swipedDeleted
        )
    }

    override fun toItemUIState(match: Match, balancePhotoBucketUrl: String?): MatchItemUIState {
        return MatchItemUIState(
            match.chatId,
            match.swipedId,
            match.lastChatMessageId > 0L,
            match.lastReadReceivedChatMessageId < match.lastReceivedChatMessageId,
            match.unmatched || match.swipedDeleted,
            match.lastChatMessageBody ?: "",
            match.swipedName,
            EndPoint.ofPhoto(balancePhotoBucketUrl, match.swipedId, match.swipedProfilePhotoKey)
        )
    }

    override fun toMatchNotificationUIState(match: Match, balancePhotoBucketUrl: String?): MatchNotificationUIState {
        return MatchNotificationUIState(
            EndPoint.ofPhoto(balancePhotoBucketUrl, match.swiperId, match.swiperProfilePhotoKey),
            EndPoint.ofPhoto(balancePhotoBucketUrl, match.swipedId, match.swipedProfilePhotoKey),
            match.swipedName
        )
    }
}