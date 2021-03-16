package com.beeswork.balance.ui.common

import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.LoadType

class PageSource<T>(
    private val items: List<T>,
    private val loadType: LoadType
)