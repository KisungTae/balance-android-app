package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.domain.uistate.profile.ProfileUIState

interface ProfileMapper {
    fun toProfile(profileDTO: ProfileDTO): Profile
    fun toProfileDomain(profile: Profile): ProfileUIState
//    fun toProfileDTO(profile: Profile): ProfileDTO?
}