package com.beeswork.balance.internal.mapper.login

import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.ui.loginactivity.LoginDomain

interface LoginMapper {
    fun toLoginDomain(loginDTO: LoginDTO): LoginDomain
}