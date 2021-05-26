package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.database.entity.Profile
import com.beeswork.balance.ui.profile.ProfileDomain

class ProfileMapperImpl : ProfileMapper {
    override fun toProfileDomain(profile: Profile): ProfileDomain {
        return ProfileDomain(profile.name, profile.birth, profile.gender, profile.height, profile.about)
    }

}