package com.beeswork.balance.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.network.response.QuestionResponse
import com.beeswork.balance.internal.Resource

class EditBalanceGameDialogViewModel (
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val questions: LiveData<Resource<List<QuestionResponse>>> = balanceRepository.questions

    fun fetchQuestions() {
        balanceRepository.fetchQuestions()
    }
}