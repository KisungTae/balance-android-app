package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

interface ProfileRepository {

    suspend fun getAbout(): String?
    suspend fun saveAbout(about: String)

    suspend fun getHeight(): Int?
    suspend fun saveHeight(height: Int)

    suspend fun getBirthDate(): OffsetDateTime?
    suspend fun saveBirthDate(birthDate: OffsetDateTime)

    suspend fun getGender(): Boolean?
    suspend fun saveGender(gender: Boolean)

    suspend fun getName(): String?
    suspend fun saveName(name: String): Resource<EmptyResponse>


    suspend fun getProfile(): Profile?
    suspend fun deleteProfile()
    suspend fun fetchProfile(): Resource<Profile>
    suspend fun saveBio(height: Int?, about: String): Resource<Profile>
    suspend fun fetchQuestions(): Resource<List<QuestionDTO>>
    suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse>
    fun getNameFlow(): Flow<String?>
    fun test()
}
