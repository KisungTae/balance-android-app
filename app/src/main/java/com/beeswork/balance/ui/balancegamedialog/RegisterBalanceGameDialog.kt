package com.beeswork.balance.ui.balancegamedialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import kotlinx.coroutines.launch

class RegisterBalanceGameDialog(
    private val registerBalanceGameListener: RegisterBalanceGameListener
): BaseBalanceGameDialog() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        viewModel.fetchQuestions()
    }

    private fun observeProfilePhotoUrlLiveData() {
        viewModel.profilePhotoUrlLiveData.observe(viewLifecycleOwner) { profilePhotoUrl ->
            balanceGameViewPagerAdapter.setupProfilePhotoULR(profilePhotoUrl)
        }
    }

    private fun bindUI() = lifecycleScope.launch {
        observeProfilePhotoUrlLiveData()
        viewModel.fetchProfilePhotoUrl()
        observeSaveAnswersUIStateLiveData()
        observeFetchQuestionsUIStateLiveData(true)
        observeFetchRandomQuestionUIStateLiveData()
        binding.btnBalanceGameRefetch.setOnClickListener {
            viewModel.fetchQuestions()
        }
        binding.btnBalanceGameResave.setOnClickListener {
            viewModel.saveAnswers(balanceGameViewPagerAdapter.getAnswers())
        }
    }

    private fun observeSaveAnswersUIStateLiveData() {
        viewModel.saveAnswersUIStateLiveData.observe(viewLifecycleOwner) { saveAnswersUIState ->
            when {
                saveAnswersUIState.saved -> {
                    registerBalanceGameListener.onBalanceGameAnswersSaved()
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

    override fun onBalanceGameOptionSelected(position: Int) {
        if (isBalanceGameFinished(position)) {
            viewModel.saveAnswers(balanceGameViewPagerAdapter.getAnswers())
        } else {
            moveToNextTab(position)
        }
    }

    companion object {
        const val TAG = "registerBalanceGameDialog"
    }

}