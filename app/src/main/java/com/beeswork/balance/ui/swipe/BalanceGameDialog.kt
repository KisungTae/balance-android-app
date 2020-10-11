package com.beeswork.balance.ui.swipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Question
import com.beeswork.balance.internal.constant.SelectionOption
import kotlinx.android.synthetic.main.dialog_balance_game.*

class BalanceGameDialog(
    private val swipedId: String,
    private val balanceGameListener: BalanceGameListener
): DialogFragment() {

    lateinit var questions: List<Question>
    var swipeId: Long = -1
    private var currentIndex = -1
    private var click = true

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
        btnTopOption.setOnClickListener { checkAnswer(SelectionOption.TOP) }
        btnBottomOption.setOnClickListener { checkAnswer(SelectionOption.BOTTOM) }
        btnBalanceGameReload.setOnClickListener { balanceGameListener.onBalanceGameReload(swipedId) }
        btnBalanceGameErrorClose.setOnClickListener { dismiss() }
        btnBalanceGameCompletionClose.setOnClickListener { dismiss() }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    }

    private fun checkAnswer(answer: Boolean) {
        val question = questions[currentIndex]
        if (question.selected != answer) click = false
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
            if (click) balanceGameListener.onBalanceGameClicked(swipedId, swipeId)
            setBalanceGameCompletion()
        }
    }

    fun setBalanceGame(newSwipeId: Long, newQuestions: List<Question>) {
        hideLayouts()
        llBalanceGame.visibility = LinearLayout.VISIBLE

        swipeId = newSwipeId
        questions = newQuestions
        nextQuestion()
    }

    fun setBalanceGameError(reloadable: Boolean, exceptionMessage: String) {
        hideLayouts()
        llBalanceGameError.visibility = LinearLayout.VISIBLE
        btnBalanceGameReload.isEnabled = reloadable
        tvBalanceGameExceptionMessage.text = exceptionMessage
    }

    fun setBalanceGameLoading() {
        hideLayouts()
        llBalanceGameLoading.visibility = LinearLayout.VISIBLE
    }

    private fun setBalanceGameCompletion() {
        hideLayouts()
        llBalanceGameCompletion.visibility = LinearLayout.VISIBLE
    }

    private fun hideLayouts() {
        llBalanceGameLoading.visibility = LinearLayout.GONE
        llBalanceGame.visibility = LinearLayout.GONE
        llBalanceGameError.visibility = LinearLayout.GONE
        llBalanceGameCompletion.visibility = LinearLayout.GONE
    }

    interface BalanceGameListener {
        fun onBalanceGameClicked(swipedId:String, swipeId: Long)
        fun onBalanceGameReload(swipedId:String)
    }

}


// TODO: loading page, error page,