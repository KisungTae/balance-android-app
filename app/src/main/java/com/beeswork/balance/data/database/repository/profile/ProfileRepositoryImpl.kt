package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.dao.ProfileDAO
import com.beeswork.balance.data.database.entity.Profile
import com.beeswork.balance.data.network.rds.profile.ProfileRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime

class ProfileRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val profileDAO: ProfileDAO,
    private val profileRDS: ProfileRDS
) : ProfileRepository {

    override suspend fun fetchProfile(): Profile {
        return withContext(Dispatchers.IO) {
            return@withContext profileDAO.findById()
        }
    }

    override suspend fun saveAbout(height: Int?, about: String): Resource<EmptyResponse> {
        return withContext(Dispatchers.IO) {
            val response = profileRDS.postAbout(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                height,
                about
            )
            if (response.isSuccess()) profileDAO.updateAbout(height, about)
            return@withContext response
        }
    }

    override fun test() {
        CoroutineScope(Dispatchers.IO).launch {
            profileDAO.insert(Profile("Michael", OffsetDateTime.now(), true, 177, "I am Michael this is about", true))
        }
    }
}