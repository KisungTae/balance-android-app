package com.beeswork.balance.data.database.repository.match

interface NewMatchFlowListener {
    fun onReceive(matchProfileTuple: MatchProfileTuple)
}