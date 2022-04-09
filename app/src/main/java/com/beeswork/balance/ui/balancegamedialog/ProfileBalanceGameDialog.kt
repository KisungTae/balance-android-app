package com.beeswork.balance.ui.balancegamedialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import kotlinx.coroutines.launch

class ProfileBalanceGameDialog: BaseBalanceGameDialog() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFetchQuestionsUIStateLiveData(true)
        observeFetchRandomQuestionUIStateLiveData()
        bindUI()
        viewModel.fetchQuestions()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeSaveAnswersUIStateLiveData()
        binding.btnBalanceGameRefetch.setOnClickListener {
            viewModel.fetchQuestions()
        }
    }

    private fun observeSaveAnswersUIStateLiveData() {
        viewModel.saveAnswersUIStateLiveData.observe(viewLifecycleOwner) { saveAnswersUIState ->
            when {
                saveAnswersUIState.saved -> {
                    dismiss()
                }
                saveAnswersUIState.showLoading -> {
                    showLoading(getString(R.string.balance_game_saving_answers_text))
                }
                saveAnswersUIState.showError -> {
                    showSaveQuestionsError(saveAnswersUIState.exception)
                }
            }
        }
    }

    override fun onOptionSelected() {
        if (isBalanceGameFinished()) {
            viewModel.saveAnswers(balanceGameViewPagerAdapter.getAnswers())
        } else {
            moveToNextTab()
        }
    }

    companion object {
        const val TAG = "profileBalanceGameDialog"
    }
}