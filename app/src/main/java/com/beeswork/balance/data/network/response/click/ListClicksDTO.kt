package com.beeswork.balance.data.network.response.click

import java.util.*

data class ListClicksDTO(
    val clickDTOs: List<ClickDTO>,
    val deletedSwiperIds: List<UUID>
)