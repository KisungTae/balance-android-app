package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.database.entity.Profile
import com.beeswork.balance.ui.profile.ProfileDomain

interface ProfileMapper {

    fun toProfileDomain(profile: Profile): ProfileDomain
}