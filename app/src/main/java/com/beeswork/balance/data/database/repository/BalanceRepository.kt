package com.beeswork.balance.data.database.repository

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.swipe.CardDTO

interface BalanceRepository {

    // swipe
    val fetchClickListResponse: LiveData<Resource<List<Click>>>
    val balanceGame: LiveData<Resource<BalanceGameResponse>>
    val clickResponse: LiveData<Resource<ClickResponse>>

    fun fetchClickedList()
    fun swipe(swipeId: Long?, swipedId: String)
    fun click(swipedId: String, swipeId: Long, answers: Map<Int, Boolean>)

    // match
    val fetchMatchesResponse: LiveData<Resource<List<Match>>>

    fun fetchMatches()




    // account
    val cards: LiveData<Resource<List<CardDTO>>>
    fun fetchCards(reset: Boolean)

//    suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse>
    suspend fun fetchRandomQuestion(questionIds: List<Int>): Resource<QuestionResponse>
    suspend fun fetchQuestions(): Resource<List<QuestionResponse>>


    // photo
//    suspend fun fetchPhotos(): Resource<List<Photo>>
//    suspend fun uploadPhoto(
//        photoKey: String,
//        photoExtension: String,
//        photoPath: String,
//        photoSequence: Int
//    ): Resource<EmptyResponse>

    suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse>
//    suspend fun reorderPhoto(photoOrders: Map<String, Int>): Resource<EmptyResponse>
}
