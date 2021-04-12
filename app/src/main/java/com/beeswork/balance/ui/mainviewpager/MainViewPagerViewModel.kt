package com.beeswork.balance.ui.mainviewpager

import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.match.MatchMapper

class MainViewPagerViewModel(
    private val matchRepository: MatchRepository,
    private val matchMapper: MatchMapper
) : ViewModel() {
}