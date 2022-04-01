package com.beeswork.balance.ui.registeractivity

import com.beeswork.balance.domain.usecase.main.SaveLocationPermissionUseCase
import com.beeswork.balance.domain.usecase.main.SaveLocationUseCase
import com.beeswork.balance.ui.common.BaseLocationViewModel

class RegisterViewModel(
    saveLocationUseCase: SaveLocationUseCase,
    saveLocationPermissionUseCase: SaveLocationPermissionUseCase
): BaseLocationViewModel(saveLocationUseCase, saveLocationPermissionUseCase) {


}