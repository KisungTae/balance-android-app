package com.beeswork.balance.ui.clicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.BalanceRepository

class ClickerViewModelFactory(
    private val balanceRepository: BalanceRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClickedViewModel(balanceRepository) as T
    }
}