package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface ProfileRepository {

    suspend fun getName(): String?
    suspend fun saveName(name: String): Resource<EmptyResponse>
    suspend fun getGender(): Boolean?
    suspend fun saveGender(gender: Boolean)
    suspend fun getBirthDate(): LocalDate?
    suspend fun saveBirthDate(birthDate: LocalDate)
    suspend fun getHeight(): Int?
    suspend fun saveHeight(height: Int)
    suspend fun getAbout(): String?
    suspend fun saveAbout(about: String)
    suspend fun getProfile(): Profile?
    suspend fun saveProfile(
        name: String,
        gender: Boolean,
        birthDate: LocalDate,
        height: Int?,
        about: String?,
        latitude: Double,
        longitude: Double
    ): Resource<EmptyResponse>
    suspend fun deleteProfile()
    suspend fun fetchProfile(sync: Boolean): Resource<Profile>
    suspend fun saveBio(height: Int?, about: String?): Resource<EmptyResponse>
    suspend fun fetchQuestions(): Resource<FetchQuestionsDTO>
    suspend fun fetchRandomQuestion(questionIds: List<Int>): Resource<QuestionDTO>
    suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse>
    fun getNameFlow(): Flow<String?>
    fun test()

}
