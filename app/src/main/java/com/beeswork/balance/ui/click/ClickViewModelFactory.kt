package com.beeswork.balance.ui.click

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.BalanceRepository

class ClickViewModelFactory(
    private val balanceRepository: BalanceRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClickViewModel(balanceRepository) as T
    }
}