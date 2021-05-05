package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.mapper.common.Mapper
import com.beeswork.balance.ui.match.MatchDomain
import com.beeswork.balance.data.database.response.NewMatch
import java.util.*

interface MatchMapper: Mapper<MatchDTO, Match, MatchDomain> {

    fun fromEntityToNewMatch(entity: Match, accountId: UUID?, profilePhotoKey: String?): NewMatch
}