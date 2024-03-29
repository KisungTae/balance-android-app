package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.domain.uistate.profile.ProfileUIState
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.Period
import org.threeten.bp.ZoneOffset

class ProfileMapperImpl : ProfileMapper {
    override fun toProfile(profileDTO: ProfileDTO): Profile {
        return Profile(
            profileDTO.accountId,
            profileDTO.name,
            profileDTO.birthDate,
            profileDTO.gender,
            profileDTO.height,
            profileDTO.about,
            true
        )
    }

    override fun toProfileUIState(profile: Profile): ProfileUIState {
        return ProfileUIState(
            profile.name,
            profile.birthDate,
            profile.gender,
            profile.height,
            profile.about,
            Period.between(profile.birthDate, LocalDate.now()).years
        )
    }

}