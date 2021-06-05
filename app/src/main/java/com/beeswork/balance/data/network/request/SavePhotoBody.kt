package com.beeswork.balance.data.network.request

import retrofit2.http.Query
import java.util.*

data class SavePhotoBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val photoKey: UUID,
    val sequence: Int
)