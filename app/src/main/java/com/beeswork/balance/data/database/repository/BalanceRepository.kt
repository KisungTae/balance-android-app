package com.beeswork.balance.data.database.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.Message
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.internal.Resource

interface BalanceRepository {

    // swipe
    val fetchClickedListResponse: LiveData<Resource<List<Clicked>>>
    val balanceGame: LiveData<Resource<BalanceGameResponse>>
    val clickResponse: LiveData<Resource<ClickResponse>>

    suspend fun getClickedList(): DataSource.Factory<Int, Clicked>
    suspend fun getClickedCount(): LiveData<Int>

    fun fetchClickedList()
    fun swipe(swipeId: Long?, swipedId: String)
    fun click(swipedId: String, swipeId:Long, answers: Map<Int, Boolean>)

    // match
    val fetchMatchesResponse: LiveData<Resource<List<Match>>>

    fun fetchMatches()
    fun unmatch()

    suspend fun getMatches(): DataSource.Factory<Int, Match>
    suspend fun getUnreadMessageCount(): LiveData<Int>

    // message
    suspend fun getMessages(chatId: Long): DataSource.Factory<Int, Message>
    fun insertMessage(chatId: Long)

    // account
    val cards: LiveData<Resource<List<CardResponse>>>
    fun fetchCards(reset: Boolean)

    fun insertFCMToken(token: String)

    val questions: LiveData<Resource<List<QuestionResponse>>>
    fun fetchQuestions()

    val saveAnswers: LiveData<Resource<EmptyJsonResponse>>
    fun saveAnswers(answers: Map<Int, Boolean>)

    val fetchRandomQuestion: LiveData<Resource<QuestionResponse>>
    fun fetchRandomQuestion(questionIds: List<Int>)

    // location
    fun saveLocation(latitude: Double, longitude: Double)
}