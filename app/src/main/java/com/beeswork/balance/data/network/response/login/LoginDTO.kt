package com.beeswork.balance.data.network.response.login

import com.beeswork.balance.data.network.response.photo.PhotoDTO
import java.util.*

data class LoginDTO(
    val accountId: UUID,
    val profileExists: Boolean,
    val accessToken: String,
    val refreshToken: String?,
    val email: String?,
    val photoDomain: String,
    val profilePhotoDTO: PhotoDTO?
)