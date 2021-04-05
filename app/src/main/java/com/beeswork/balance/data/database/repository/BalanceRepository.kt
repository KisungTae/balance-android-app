package com.beeswork.balance.data.database.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Clicker
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.ui.chat.ChatMessageEvent
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import org.threeten.bp.OffsetDateTime

interface BalanceRepository {

    // swipe
    val fetchClickerListResponse: LiveData<Resource<List<Clicker>>>
    val balanceGame: LiveData<Resource<BalanceGameResponse>>
    val clickResponse: LiveData<Resource<ClickResponse>>

    suspend fun getClickedList(): DataSource.Factory<Int, Clicker>
    suspend fun getClickedCount(): LiveData<Int>

    fun fetchClickedList()
    fun swipe(swipeId: Long?, swipedId: String)
    fun click(swipedId: String, swipeId: Long, answers: Map<Int, Boolean>)

    // match
    val fetchMatchesResponse: LiveData<Resource<List<Match>>>

    fun fetchMatches()




    // account
    val cards: LiveData<Resource<List<CardResponse>>>
    fun fetchCards(reset: Boolean)

    fun insertFCMToken(token: String)


    suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse>
    suspend fun fetchRandomQuestion(questionIds: List<Int>): Resource<QuestionResponse>
    suspend fun fetchQuestions(): Resource<List<QuestionResponse>>

    // location
    fun saveLocation(latitude: Double, longitude: Double)

    // photo
    suspend fun fetchPhotos(): Resource<List<Photo>>
    suspend fun uploadPhoto(
        photoKey: String,
        photoExtension: String,
        photoPath: String,
        photoSequence: Int
    ): Resource<EmptyResponse>

    suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse>
    suspend fun reorderPhoto(photoOrders: Map<String, Int>): Resource<EmptyResponse>
}
