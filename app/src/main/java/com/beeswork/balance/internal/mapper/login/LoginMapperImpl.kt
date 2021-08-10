package com.beeswork.balance.internal.mapper.login

import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.ui.loginactivity.LoginDomain

class LoginMapperImpl: LoginMapper {

    override fun toLoginDomain(loginDTO: LoginDTO): LoginDomain {
        return LoginDomain(loginDTO.profileExists)
    }
}