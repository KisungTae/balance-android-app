package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.entity.Profile

interface ProfileRepository {
    suspend fun fetchProfile(): Profile
    fun test()
}
