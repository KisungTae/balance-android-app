package com.beeswork.balance.ui.click

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.internal.mapper.click.ClickMapper

class ClickViewModelFactory(
    private val clickRepository: ClickRepository,
    private val clickMapper: ClickMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClickViewModel(clickRepository, clickMapper) as T
    }
}