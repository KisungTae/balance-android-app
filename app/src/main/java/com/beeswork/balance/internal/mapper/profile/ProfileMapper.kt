package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.database.entity.Profile
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.ui.profile.ProfileDomain

interface ProfileMapper {
    fun toProfile(profileDTO: ProfileDTO): Profile
    fun toProfileDomain(profile: Profile): ProfileDomain
}