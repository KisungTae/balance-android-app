package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.LocationDAO
import com.beeswork.balance.data.database.dao.ProfileDAO
import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.rds.profile.ProfileRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

class ProfileRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val profileDAO: ProfileDAO,
    private val locationDAO: LocationDAO,
    private val profileRDS: ProfileRDS,
    private val balanceDatabase: BalanceDatabase,
    private val profileMapper: ProfileMapper,
    private val ioDispatcher: CoroutineDispatcher
) : ProfileRepository {

    override suspend fun getName(): String? {
        return withContext(ioDispatcher) {
            return@withContext profileDAO.getName(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveName(name: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
                ?: return@withContext Resource.error<EmptyResponse>(AccountIdNotFoundException())

            balanceDatabase.runInTransaction {
                if (profileDAO.existsBy(accountId)) {
                    profileDAO.saveNameBy(accountId, name)
                } else {
                    val profile = Profile(accountId, name, null, null, null, null, false)
                    profileDAO.insert(profile)
                }
            }
            return@withContext Resource.success(EmptyResponse())
        }
    }

    override suspend fun getGender(): Boolean? {
        return withContext(ioDispatcher) {
            profileDAO.getGender(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveGender(gender: Boolean) {
        withContext(ioDispatcher) {
            profileDAO.saveGenderBy(preferenceProvider.getAccountId(), gender)
        }
    }

    override suspend fun getBirthDate(): LocalDate? {
        return withContext(ioDispatcher) {
            profileDAO.getBirthDate(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveBirthDate(birthDate: LocalDate) {
        withContext(ioDispatcher) {
            profileDAO.saveBirthDateBy(preferenceProvider.getAccountId(), birthDate)
        }
    }

    override suspend fun getHeight(): Int? {
        return withContext(ioDispatcher) {
            profileDAO.getHeight(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveHeight(height: Int) {
        withContext(ioDispatcher) {
            profileDAO.saveHeightBy(preferenceProvider.getAccountId(), height)
        }
    }

    override suspend fun getAbout(): String? {
        return withContext(ioDispatcher) {
            profileDAO.getAbout(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveAbout(about: String) {
        withContext(ioDispatcher) {
            profileDAO.saveAboutBy(preferenceProvider.getAccountId(), about)
        }
    }

    override suspend fun getProfile(): Profile? {
        return withContext(ioDispatcher) {
            return@withContext profileDAO.getBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveProfile(
        name: String,
        gender: Boolean,
        birthDate: LocalDate,
        height: Int?,
        about: String?,
        latitude: Double,
        longitude: Double
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = profileRDS.saveProfile(name, gender, birthDate, height, about, latitude, longitude)
            if (response.isSuccess()) {
                profileDAO.updateSyncedBy(preferenceProvider.getAccountId(), true)
                locationDAO.updateSyncedBy(true)
            }
            return@withContext response
        }
    }


    override suspend fun deleteProfile() {
        withContext(ioDispatcher) { profileDAO.deleteBy(preferenceProvider.getAccountId()) }
    }

    override suspend fun fetchProfile(sync: Boolean): Resource<Profile> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val profile = profileDAO.getBy(accountId)
            if (profile == null || (!profile.synced && sync)) {
                return@withContext profileRDS.fetchProfile().map { profileDTO ->
                    profileDTO?.let { _profileDTO ->
                        val fetchedProfile = profileMapper.toProfile(_profileDTO)
                        profileDAO.insert(fetchedProfile)
                        fetchedProfile
                    }
                }
            }
            return@withContext Resource.success(profile)
        }
    }

    override suspend fun saveBio(height: Int?, about: String): Resource<Profile> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())

            profileDAO.updateSyncedBy(accountId, false)
            val response = profileRDS.saveBio(height, about)

            if (response.isSuccess()) {
                profileDAO.updateAboutBy(accountId, height, about)
                return@withContext response.map { null }
            } else {
                profileDAO.updateSyncedBy(accountId, true)
                return@withContext response.map { profileDAO.getBy(accountId) }
            }
        }
    }

    override suspend fun fetchQuestions(): Resource<FetchQuestionsDTO> {
        return withContext(ioDispatcher) {
            return@withContext profileRDS.fetchQuestions()
        }
    }

    override suspend fun fetchRandomQuestion(questionIds: List<Int>): Resource<QuestionDTO> {
        return withContext(ioDispatcher) {
            return@withContext profileRDS.fetchRandomQuestion(questionIds)
        }
    }

    override suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            return@withContext profileRDS.saveAnswers(answers)
        }
    }

    override fun getNameFlow(): Flow<String?> {
        return profileDAO.getNameFlowBy(preferenceProvider.getAccountId())
    }


    override fun test() {
    }
}