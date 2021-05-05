package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.dao.ProfileDAO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

class ProfileRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val profileDAO: ProfileDAO
): ProfileRepository {
}