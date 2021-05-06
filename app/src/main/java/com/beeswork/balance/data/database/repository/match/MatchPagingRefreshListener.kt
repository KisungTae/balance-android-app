package com.beeswork.balance.data.database.repository.match

interface MatchPagingRefreshListener {
    fun onRefresh(matchPagingRefresh: MatchPagingRefresh)
}