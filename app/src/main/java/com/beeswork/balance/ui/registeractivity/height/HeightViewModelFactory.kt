package com.beeswork.balance.ui.registeractivity.height

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.usecase.register.GetHeightUseCase
import com.beeswork.balance.domain.usecase.register.SaveHeightUseCase
import com.beeswork.balance.ui.registeractivity.name.NameViewModel

class HeightViewModelFactory (
    private val getHeightUseCase: GetHeightUseCase,
    private val saveHeightUseCase: SaveHeightUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HeightViewModel(getHeightUseCase, saveHeightUseCase) as T
    }
}