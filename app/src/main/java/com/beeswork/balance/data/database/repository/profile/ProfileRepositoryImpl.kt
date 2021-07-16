package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.dao.ProfileDAO
import com.beeswork.balance.data.database.entity.Profile
import com.beeswork.balance.data.network.rds.profile.ProfileRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val profileDAO: ProfileDAO,
    private val profileRDS: ProfileRDS,
    private val ioDispatcher: CoroutineDispatcher
) : ProfileRepository {

    override suspend fun deleteProfile() {
        withContext(ioDispatcher) { profileDAO.deleteById(preferenceProvider.getAccountId()) }
    }

    override suspend fun fetchProfile(): Profile? {
        return withContext(ioDispatcher) {
            return@withContext profileDAO.findById(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveAbout(height: Int?, about: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            profileDAO.updateAbout(preferenceProvider.getAccountId(), height, about)
            val response = profileRDS.postAbout(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                height,
                about
            )
            if (response.isSuccess()) profileDAO.sync(preferenceProvider.getAccountId())
            return@withContext response
        }
    }

    override suspend fun fetchQuestions(): Resource<List<QuestionDTO>> {
        return withContext(ioDispatcher) {
            return@withContext profileRDS.listQuestions(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken()
            )
        }
    }

    override suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            return@withContext profileRDS.saveQuestions(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                answers
            )
        }
    }

    override fun getNameFlow(): Flow<String?> {
        return profileDAO.findNameAsFlow(preferenceProvider.getAccountId())
    }


    override fun test() {

//        CoroutineScope(ioDispatcher).launch {
//            profileDAO.insert(Profile("Michael", OffsetDateTime.now(), true, 177, "I am Michael this is about", false))
//        }
    }
}