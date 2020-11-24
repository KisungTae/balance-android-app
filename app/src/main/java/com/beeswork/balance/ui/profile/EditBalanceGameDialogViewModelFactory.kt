package com.beeswork.balance.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.ui.balancegame.BalanceGameDialogViewModel


class EditBalanceGameDialogViewModelFactory(
    private val balanceRepository: BalanceRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditBalanceGameDialogViewModel(balanceRepository) as T
    }
}