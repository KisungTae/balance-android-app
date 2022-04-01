package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.ui.profilefragment.ProfileDomain
import java.util.*

class ProfileMapperImpl : ProfileMapper {
    override fun toProfile(accountId: UUID, profileDTO: ProfileDTO): Profile {
        return Profile(
            accountId,
            profileDTO.name,
            profileDTO.birthDate,
            profileDTO.gender,
            profileDTO.height,
            profileDTO.about,
            true
        )
    }

    override fun toProfileDomain(profile: Profile): ProfileDomain {
        return ProfileDomain(profile.name, profile.birthDate, profile.gender, profile.height, profile.about)
    }

    override fun toProfileDTO(profile: Profile): ProfileDTO? {
        if (profile.name != null && profile.birthDate != null && profile.gender != null) {
            return ProfileDTO(profile.name, profile.birthDate, profile.gender, profile.height, profile.about)
        }
        return null
    }

}