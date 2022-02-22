package com.beeswork.balance.internal.mapper.match

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.ui.matchfragment.MatchDomain
import com.beeswork.balance.data.database.entity.match.MatchProfileTuple

interface MatchMapper {
    fun toMatch(matchDTO: MatchDTO): Match
    fun toMatchDomain(match: Match): MatchDomain
}