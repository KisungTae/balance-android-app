package com.beeswork.balance.data.network.rds.photo

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import java.util.*

class PhotoRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), PhotoRDS {

    override suspend fun listPhotos(accountId: UUID?, identityToken: UUID?): Resource<List<PhotoDTO>> {
        return getResult {
            balanceAPI.listPhotos(accountId, identityToken)
        }
    }
}