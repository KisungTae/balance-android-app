package com.beeswork.balance.data.database.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.Message
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.internal.Resource

interface BalanceRepository {

    // clicked
    suspend fun getClickedList(): DataSource.Factory<Int, Clicked>
    fun fetchClickedList()
    val fetchClickedListResource: LiveData<Resource<List<Clicked>>>
    suspend fun getClickedCount(): LiveData<Int>

    // match
    fun fetchMatches()
    val fetchMatchesResource: LiveData<Resource<List<Match>>>
    fun unmatch()
    suspend fun getMatches(): DataSource.Factory<Int, Match>

    // message
    suspend fun getMessages(chatId: Long): DataSource.Factory<Int, Message>
    fun insertMessage(chatId: Long)

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
