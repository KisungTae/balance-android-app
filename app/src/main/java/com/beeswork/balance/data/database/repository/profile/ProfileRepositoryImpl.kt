package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.dao.ProfileDAO
import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.rds.profile.ProfileRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val profileDAO: ProfileDAO,
    private val profileRDS: ProfileRDS,
    private val profileMapper: ProfileMapper,
    private val ioDispatcher: CoroutineDispatcher
) : ProfileRepository {

    override suspend fun getProfile(): Profile? {
        return withContext(ioDispatcher) {
            return@withContext profileDAO.getBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun deleteProfile() {
        withContext(ioDispatcher) { profileDAO.deleteBy(preferenceProvider.getAccountId()) }
    }

    override suspend fun fetchProfile(): Resource<Profile> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())

            val response = profileRDS.fetchProfile()
            if (response.isSuccess()) response.data?.let { profileDTO ->
                val profile = profileMapper.toProfile(accountId, profileDTO)
                profileDAO.insert(profile)
                return@withContext response.map { profile }
            }
            return@withContext response.map { null }
        }
    }

    override suspend fun saveAbout(height: Int?, about: String): Resource<Profile> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())

            profileDAO.updateSyncedBy(accountId, false)
            val response = profileRDS.saveAbout(height, about)

            if (response.isSuccess()) {
                profileDAO.updateAboutBy(accountId, height, about)
                return@withContext response.map { null }
            } else {
                profileDAO.updateSyncedBy(accountId, true)
                return@withContext response.map { profileDAO.getBy(accountId) }
            }
        }
    }

    override suspend fun fetchQuestions(): Resource<List<QuestionDTO>> {
        return withContext(ioDispatcher) {
            return@withContext profileRDS.listQuestions()
        }
    }

    override suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            return@withContext profileRDS.saveQuestions(answers)
        }
    }

    override fun getNameFlow(): Flow<String?> {
        return profileDAO.getNameFlowBy(preferenceProvider.getAccountId())
    }


    override fun test() {

//        CoroutineScope(ioDispatcher).launch {
//            profileDAO.insert(Profile("Michael", OffsetDateTime.now(), true, 177, "I am Michael this is about", false))
//        }
    }
}