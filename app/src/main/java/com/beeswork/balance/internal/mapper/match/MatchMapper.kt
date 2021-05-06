package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.mapper.common.Mapper
import com.beeswork.balance.ui.match.MatchDomain
import com.beeswork.balance.data.database.tuple.MatchProfileTuple

interface MatchMapper: Mapper<MatchDTO, Match, MatchDomain> {
    fun fromEntityToProfileTuple(entity: Match): MatchProfileTuple
}