package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.ui.profilefragment.ProfileDomain
import java.util.*

interface ProfileMapper {
    fun toProfile(accountId:UUID, profileDTO: ProfileDTO): Profile
    fun toProfileDomain(profile: Profile): ProfileDomain
    fun toProfileDTO(profile: Profile): ProfileDTO?
}