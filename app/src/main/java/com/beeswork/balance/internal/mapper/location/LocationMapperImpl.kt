package com.beeswork.balance.internal.mapper.location

import com.beeswork.balance.data.database.entity.setting.Location
import com.beeswork.balance.ui.setting.LocationDomain

class LocationMapperImpl: LocationMapper {
    override fun toLocationDomain(location: Location): LocationDomain {
        return LocationDomain(location.latitude, location.longitude)
    }
}