package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.match.MatchDomain
import com.beeswork.balance.data.database.repository.match.MatchProfileTuple

interface MatchMapper {
    fun toProfileTuple(match: Match): MatchProfileTuple
    fun toMatch(matchDTO: MatchDTO): Match
    fun toMatchDomain(match: Match): MatchDomain
}