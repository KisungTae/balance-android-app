package com.beeswork.balance.ui.swipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.QuestionResponse
import com.beeswork.balance.internal.constant.AnswerOption
import kotlinx.android.synthetic.main.dialog_balance_game.*

class BalanceGameDialog(
    private val swipedId: String,
    private val balanceGameListener: BalanceGameListener
): DialogFragment() {

    private lateinit var questions: List<QuestionResponse>
    private var swipeId: Long = -1
    private var currentIndex = -1
    private val answers: MutableMap<Long, Boolean> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_balance_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnTopOption.setOnClickListener { selectAnswer(AnswerOption.TOP) }
        btnBottomOption.setOnClickListener { selectAnswer(AnswerOption.BOTTOM) }

        btnBalanceGameReload.setOnClickListener { balanceGameListener.onBalanceGameReload(swipedId) }
        btnBalanceGameLoadErrorClose.setOnClickListener { dismiss() }

        btnBalanceGameClick.setOnClickListener { balanceGameListener.onBalanceGameClick(swipedId, swipeId, answers) }

        btnBalanceGameClickErrorCloseBtn.setOnClickListener { dismiss() }

        btnBalanceGameRetry.setOnClickListener { balanceGameListener.onBalanceGameReload(swipedId) }
        btnBalanceGameNotClickClose.setOnClickListener { dismiss() }

        btnBalanceGameClickedClose.setOnClickListener { dismiss() }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    }

    fun setBalanceGame(newSwipeId: Long, newQuestionResponses: List<QuestionResponse>) {
        println("balance game starts with swipeId: $newSwipeId and swipedId: $swipedId")
        currentIndex = -1
        answers.clear()
        hideLayouts()
        llBalanceGame.visibility = LinearLayout.VISIBLE

        swipeId = newSwipeId
        questions = newQuestionResponses
        nextQuestion()
    }

    private fun selectAnswer(answer: Boolean) {
        val question = questions[currentIndex]
        answers[question.id] = answer
        nextQuestion()
    }

    private fun nextQuestion() {
        currentIndex++
        if (currentIndex < questions.size) {
            val question = questions[currentIndex]
            tvQuestionDescription.text = question.description
            btnTopOption.text = question.topOption
            btnBottomOption.text = question.bottomOption
        } else {
            balanceGameListener.onBalanceGameClick(swipedId, swipeId, answers)
            setBalanceGameLoading(getString(R.string.question_checking))
        }
    }

    fun setBalanceGameLoadError(enableReloadBtn: Boolean, exceptionMessage: String) {
        hideLayouts()
        llBalanceGameLoadError.visibility = LinearLayout.VISIBLE
        btnBalanceGameReload.isEnabled = enableReloadBtn
        btnBalanceGameReload.visibility = if (enableReloadBtn) View.GONE else View.VISIBLE
        tvBalanceGameLoadErrorMessage.text = exceptionMessage
    }

    fun setBalanceGameClickError(enableClickBtn: Boolean, exceptionMessage: String) {
        hideLayouts()
        llBalanceGameClickError.visibility = LinearLayout.VISIBLE
        btnBalanceGameClick.isEnabled = enableClickBtn
        btnBalanceGameClick.visibility = if (enableClickBtn) View.GONE else View.VISIBLE
        tvBalanceGameClickErrorMessage.text =exceptionMessage
    }

    fun setBalanceGameNotClicked() {
        hideLayouts()
        llBalanceGameNotClick.visibility = LinearLayout.VISIBLE
    }

    fun setBalanceGameClicked(swipedPhotoKey: String) {
        hideLayouts()
        llBalanceGameClicked.visibility = LinearLayout.VISIBLE
    }

    fun setBalanceGameLoading(message: String) {
        hideLayouts()
        llBalanceGameLoading.visibility = LinearLayout.VISIBLE
        tvBalanceGameLoadingMessage.text = message
    }

    private fun hideLayouts() {
        llBalanceGameLoading.visibility = LinearLayout.GONE
        llBalanceGame.visibility = LinearLayout.GONE
        llBalanceGameLoadError.visibility = LinearLayout.GONE
        llBalanceGameClicked.visibility = LinearLayout.GONE
        llBalanceGameClickError.visibility = LinearLayout.GONE
        llBalanceGameNotClick.visibility = LinearLayout.GONE
    }

    interface BalanceGameListener {
        fun onBalanceGameClick(swipedId:String, swipeId: Long, answers: Map<Long, Boolean>)
        fun onBalanceGameReload(swipedId:String)
    }

}
