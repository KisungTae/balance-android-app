package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.entity.match.MatchProfileTuple

interface NewMatchFlowListener {
    fun onReceive(matchProfileTuple: MatchProfileTuple)
}