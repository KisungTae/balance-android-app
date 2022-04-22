package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.domain.uistate.match.MatchNotificationUIState
import com.beeswork.balance.ui.matchfragment.MatchItemUIState

interface MatchMapper {
    fun toMatch(matchDTO: MatchDTO): Match
    fun toItemUIState(match: Match, balancePhotoBucketUrl: String?): MatchItemUIState
    fun toMatchNotificationUIState(match: Match, balancePhotoBucketUrl: String?): MatchNotificationUIState
}