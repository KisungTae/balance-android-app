package com.beeswork.balance.internal.mapper.location

import com.beeswork.balance.data.database.entity.setting.Location
import com.beeswork.balance.ui.setting.LocationDomain

interface LocationMapper {
    fun toLocationDomain(location: Location): LocationDomain
}