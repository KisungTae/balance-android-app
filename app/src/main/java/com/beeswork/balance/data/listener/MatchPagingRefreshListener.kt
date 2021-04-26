package com.beeswork.balance.data.listener

import com.beeswork.balance.data.database.response.MatchPagingRefresh

interface MatchPagingRefreshListener {
    fun onRefresh(matchPagingRefresh: MatchPagingRefresh)
}