package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface LoginRepository {
    suspend fun login(): Resource<EmptyResponse>
}