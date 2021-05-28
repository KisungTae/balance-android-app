package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.entity.Profile
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface ProfileRepository {
    suspend fun fetchProfile(): Profile
    suspend fun saveAbout(height: Int?, about: String): Resource<EmptyResponse>
    fun test()
}
