package com.beeswork.balance.ui.common

import android.view.View
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutBalanceGameBinding
import com.beeswork.balance.databinding.LayoutBalanceGameErrorBinding
import com.beeswork.balance.databinding.LayoutBalanceGameLoadingBinding
import com.beeswork.balance.internal.constant.BalanceGameOption
import com.beeswork.balance.ui.profile.balancegame.QuestionDomain

abstract class BalanceGame : BaseDialog() {

    private lateinit var questions: List<QuestionDomain>
    private val answers: MutableMap<Int, Boolean> = mutableMapOf()
    private var currentIndex = -1

    private lateinit var balanceGameBinding: LayoutBalanceGameBinding
    private lateinit var balanceGameLoadingBinding: LayoutBalanceGameLoadingBinding
    private lateinit var balanceGameErrorBinding: LayoutBalanceGameErrorBinding

    protected fun setupBalanceGame(newQuestions: List<QuestionDomain>) {
        questions = newQuestions
        resetBalanceGame()
    }

    protected fun resetBalanceGame() {
        currentIndex = -1
        answers.clear()
        nextQuestion()
        showLayouts(View.VISIBLE, View.GONE, View.GONE)
    }

    private fun nextQuestion() {
        currentIndex++
        if (currentIndex < questions.size) {
            val question = questions[currentIndex]
            balanceGameBinding.btnBalanceGameTopOption.text = question.topOption
            balanceGameBinding.btnBalanceGameBottomOption.text = question.bottomOption
            question.answer?.let { answer ->
                println("question answer: $answer")
                // TODO: check answer
            }
        } else onSaveBalanceGame(answers)
    }

    private fun selectAnswer(answer: Boolean) {
        val question = questions[currentIndex]
        answers[question.id] = answer
        nextQuestion()
    }

    protected fun initBalanceGameDialogBinding(
        balanceGameBinding: LayoutBalanceGameBinding,
        balanceGameLoadingBinding: LayoutBalanceGameLoadingBinding,
        balanceGameErrorBinding: LayoutBalanceGameErrorBinding
    ) {
        this.balanceGameBinding = balanceGameBinding
        this.balanceGameLoadingBinding = balanceGameLoadingBinding
        this.balanceGameErrorBinding = balanceGameErrorBinding
        setupListeners()
    }

    protected fun showLoading(loadingMessage: String) {
        showLayouts(View.GONE, View.VISIBLE, View.GONE)
        balanceGameLoadingBinding.tvLoadingMessage.text = loadingMessage
    }

    protected fun showSaveError(error: String?, errorMessage: String?) {
        showError(View.GONE, View.VISIBLE, getString(R.string.error_title_click), error, errorMessage)
    }

    protected fun showFetchQuestionsError(error: String?, errorMessage: String?) {
        showError(View.VISIBLE, View.GONE, getString(R.string.error_title_fetch_question), error, errorMessage)
    }

    private fun showError(fetchBtn: Int, saveBtn: Int, errorTitle: String, error: String?, errorMessage: String?) {
        showLayouts(View.GONE, View.GONE, View.VISIBLE)
        balanceGameErrorBinding.btnBalanceGameDialogFetch.visibility = fetchBtn
        balanceGameErrorBinding.btnBalanceGameDialogSave.visibility = saveBtn
        balanceGameErrorBinding.tvBalanceGameDialogErrorTitle.text = errorTitle
        balanceGameErrorBinding.tvBalanceGameDialogErrorMessage.text = errorMessage

//        TODO: check setupErrorMessage
//        setupErrorMessage(error, errorMessage, balanceGameErrorBinding.tvBalanceGameDialogErrorMessage)
    }

    protected fun showLayouts(balanceGame: Int, loading: Int, error: Int) {
        balanceGameBinding.llBalanceGameWrapper.visibility = balanceGame
        balanceGameLoadingBinding.llBalanceGameLoading.visibility = loading
        balanceGameErrorBinding.llBalanceGameError.visibility = error
    }

    private fun setupListeners() {
        balanceGameErrorBinding.btnBalanceGameDialogFetch.setOnClickListener { onFetchBalanceGame() }
        balanceGameErrorBinding.btnBalanceGameDialogSave.setOnClickListener { onSaveBalanceGame(answers) }
        balanceGameErrorBinding.btnBalanceGameDialogErrorClose.setOnClickListener { dismiss() }
        balanceGameBinding.btnBalanceGameTopOption.setOnClickListener { selectAnswer(BalanceGameOption.TOP) }
        balanceGameBinding.btnBalanceGameBottomOption.setOnClickListener { selectAnswer(BalanceGameOption.BOTTOM) }
    }

    abstract fun onSaveBalanceGame(answers: Map<Int, Boolean>)
    abstract fun onFetchBalanceGame()

}