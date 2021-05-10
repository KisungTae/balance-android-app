package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.tuple.MatchProfileTuple

interface NewMatchFlowListener {
    fun onReceive(matchProfileTuple: MatchProfileTuple)
}