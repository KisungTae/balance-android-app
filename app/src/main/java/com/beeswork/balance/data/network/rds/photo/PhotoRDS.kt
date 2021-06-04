package com.beeswork.balance.data.network.rds.photo

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import java.util.*

interface PhotoRDS {



    suspend fun listPhotos(
        accountId: UUID?,
        identityToken: UUID?
    ): Resource<List<PhotoDTO>>
}