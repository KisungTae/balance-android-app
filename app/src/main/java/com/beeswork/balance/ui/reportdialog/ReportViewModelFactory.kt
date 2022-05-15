package com.beeswork.balance.ui.reportdialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.domain.usecase.report.ReportMatchUseCase
import com.beeswork.balance.domain.usecase.report.ReportProfileUseCase

class ReportViewModelFactory(
    private val reportProfileUseCase: ReportProfileUseCase,
    private val reportMatchUseCase: ReportMatchUseCase
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReportViewModel(reportProfileUseCase, reportMatchUseCase) as T
    }
}