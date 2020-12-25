package com.beeswork.balance.data.database.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.Message
import com.beeswork.balance.data.database.entity.Photo
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
    fun click(swipedId: String, swipeId: Long, answers: Map<Int, Boolean>)

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


    suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyJsonResponse>
    suspend fun fetchRandomQuestion(questionIds: List<Int>): Resource<QuestionResponse>
    suspend fun fetchQuestions(): Resource<List<QuestionResponse>>

    // location
    fun saveLocation(latitude: Double, longitude: Double)

    // photo
    suspend fun fetchPhotos(): Resource<List<Photo>>
    suspend fun uploadPhoto(
        photoKey: String,
        photoExtension: String,
        photoUri: Uri,
        sequence: Int
    ): Resource<EmptyJsonResponse>

    suspend fun deletePhoto(photoKey: String): Resource<EmptyJsonResponse>
    suspend fun reorderPhoto(photoOrders: Map<String, Long>): Resource<EmptyJsonResponse>
}
