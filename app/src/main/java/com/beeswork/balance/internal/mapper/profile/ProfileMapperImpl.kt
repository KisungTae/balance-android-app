package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.database.entity.Profile
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.ui.profile.ProfileDomain

class ProfileMapperImpl : ProfileMapper {
    override fun toProfile(profileDTO: ProfileDTO): Profile {
        return Profile(
            profileDTO.accountId,
            profileDTO.name,
            profileDTO.birth,
            profileDTO.gender,
            profileDTO.height,
            profileDTO.about,
            true
        )
    }

    override fun toProfileDomain(profile: Profile): ProfileDomain {
        return ProfileDomain(profile.name, profile.birth, profile.gender, profile.height, profile.about)
    }

}