package com.beeswork.balance.data.network.request.profile

import retrofit2.http.Query
import java.util.*

data class SavePhotoBody(
    val photoKey: String,
    val sequence: Int
)