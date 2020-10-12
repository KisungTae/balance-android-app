package com.beeswork.balance.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.entity.Match
import com.beeswork.balance.data.entity.Message
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.data.network.response.Question
import com.beeswork.balance.internal.Resource

interface BalanceRepository {

    // match
    suspend fun fetchMatches()
    fun insertMatch()
    fun unmatch()
    suspend fun getMatches(): LiveData<List<Match>>

    // message
    suspend fun getMessages(matchId: Int): DataSource.Factory<Int, Message>
    fun insertMessage()

    // card
    val cards: LiveData<Resource<List<Card>>>
    fun fetchCards()

    // balance
    val balanceGame: LiveData<Resource<BalanceGame>>
    fun swipe(swipedId: String)

    // match
    fun click(swipedId: String, swipeId:Long)

    // firebaseMessagingToken
    fun insertFCMToken(token: String)
}
