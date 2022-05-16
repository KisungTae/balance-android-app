package com.beeswork.balance.ui.reportdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.report.ReportUIState
import com.beeswork.balance.domain.usecase.report.ReportMatchUseCase
import com.beeswork.balance.domain.usecase.report.ReportProfileUseCase
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.constant.ReportType
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class ReportViewModel(
    private val reportProfileUseCase: ReportProfileUseCase,
    private val reportMatchUseCase: ReportMatchUseCase
): BaseViewModel() {

    private val _reportUIStateLiveData = MutableLiveData<ReportUIState>()
    val reportUIStateLiveData: LiveData<ReportUIState> = _reportUIStateLiveData

    fun report(reportType: ReportType, reportedId: UUID, reportReason: ReportReason, reportDescription: String?) {
        viewModelScope.launch {
            _reportUIStateLiveData.postValue(ReportUIState.ofLoading())
            val response = when (reportType) {
                ReportType.REPORT_PROFILE -> reportProfileUseCase.invoke(reportedId, reportReason, reportDescription)
                ReportType.REPORT_MATCH ->  reportMatchUseCase.invoke(reportedId, reportReason, reportDescription)
            }
            val reportUIState = if (response.isSuccess()) {
                ReportUIState.ofSuccess()
            } else {
                ReportUIState.ofError(response.exception)
            }
            _reportUIStateLiveData.postValue(reportUIState)
        }
    }
}