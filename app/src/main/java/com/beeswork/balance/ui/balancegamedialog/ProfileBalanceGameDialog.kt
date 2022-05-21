package com.beeswork.balance.ui.balancegamedialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch

class ProfileBalanceGameDialog(
    private val profilePhotoUrl: String?,
    private val balanceGameListener: BalanceGameListener?
): BaseBalanceGameDialog() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupProfilePhoto()
        setupBtnListeners()
        observeFetchQuestionsUIStateLiveData(true)
        observeFetchRandomQuestionUIStateLiveData()
        observeSaveAnswersUIStateLiveData()
        viewModel.fetchQuestions()
    }

    private fun setupProfilePhoto() {
        if (profilePhotoUrl == null) {
            viewModel.profilePhotoUrlLiveData.observe(viewLifecycleOwner) { profilePhotoUrl ->
                balanceGameViewPagerAdapter.setupProfilePhotoULR(profilePhotoUrl)
            }
            viewModel.fetchProfilePhotoUrl()
        } else {
            balanceGameViewPagerAdapter.setupProfilePhotoULR(profilePhotoUrl)
        }
    }

    private fun observeFetchRandomQuestionUIStateLiveData() {
        viewModel.fetchRandomQuestionUIStateLiveData.observe(viewLifecycleOwner) { fetchRandomQuestionUIState ->
            when {
                fetchRandomQuestionUIState.questionItemUIState != null -> {
                    balanceGameViewPagerAdapter.replaceQuestion(
                        binding.vpBalanceGame.currentItem,
                        fetchRandomQuestionUIState.questionItemUIState
                    )
                    showLayouts(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE)
                    showRefreshBtn()
                    showBackBtn()
                }
                fetchRandomQuestionUIState.showLoading -> {
                    showLoading(getString(R.string.fetch_question_message))
                }
                fetchRandomQuestionUIState.showError -> {
                    showLayouts(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE)
                    showRefreshBtn()
                    showBackBtn()
                    val title = getString(R.string.error_title_fetch_question)
                    val message = MessageSource.getMessage(requireContext(), fetchRandomQuestionUIState.exception)
                    ErrorDialog.show(title, message, childFragmentManager)
                }
            }
        }
    }

    private fun observeSaveAnswersUIStateLiveData() {
        viewModel.saveAnswersUIStateLiveData.observe(viewLifecycleOwner) { saveAnswersUIState ->
            when {
                saveAnswersUIState.saved -> {
                    balanceGameListener?.onBalanceGameAnswersSaved()
                    dismiss()
                }
                saveAnswersUIState.showLoading -> {
                    showLoading(getString(R.string.balance_game_saving_answers_text))
                }
                saveAnswersUIState.showError -> {
                    val title = getString(R.string.error_title_save_answers)
                    val message = MessageSource.getMessage(requireContext(), saveAnswersUIState.exception)
                    showError(title, message)
                    showErrorBtn(View.VISIBLE, View.GONE, View.GONE)
                }
            }
        }
    }

    private fun setupBtnListeners() {
        binding.btnBalanceGameFetchRandomQuestion.setOnClickListener {
            viewModel.fetchRandomQuestion(balanceGameViewPagerAdapter.getQuestionIds())
        }
        binding.btnBalanceGameResave.setOnClickListener {
            viewModel.saveAnswers(balanceGameViewPagerAdapter.getAnswers())
        }
        binding.btnBalanceGameRefetch.setOnClickListener {
            viewModel.fetchQuestions()
        }
        binding.btnBalanceGameResave.setOnClickListener {
            viewModel.saveAnswers(balanceGameViewPagerAdapter.getAnswers())
        }
    }

    override fun onBalanceGameOptionSelected(position: Int, answer: Boolean) {
        balanceGameViewPagerAdapter.updateAnswer(position, answer)
        if (isBalanceGameFinished(position)) {
            viewModel.saveAnswers(balanceGameViewPagerAdapter.getAnswers())
        } else {
            moveToNextTab(position)
        }
    }

    companion object {
        const val TAG = "profileBalanceGameDialog"
    }
}