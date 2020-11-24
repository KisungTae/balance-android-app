package com.beeswork.balance.ui.balancegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.QuestionResponse
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.NotificationType
import kotlinx.android.synthetic.main.dialog_balance_game.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

const val ANSWER_TOP = true
const val ANSWER_BOTTOM = false

class BalanceGameDialog(
    private val swipedId: String,
    private val balanceGameListener: BalanceGameListener
): DialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: BalanceGameDialogViewModelFactory by instance()
    private lateinit var viewModel: BalanceGameDialogViewModel

    private lateinit var questions: List<QuestionResponse>
    private var swipeId: Long? = null
    private var currentIndex = -1
    private val answers: MutableMap<Int, Boolean> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BalanceGameDialogViewModel::class.java)
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
        bindUI()

    }

    private fun bindUI() {
        setupBalanceGameObserver()
        setupClickResponseObserver()
        setupListeners()
    }

    private fun setupBalanceGameObserver() {
        viewModel.balanceGame.observe(viewLifecycleOwner, { balanceGameResource ->
            when (balanceGameResource.status) {
                Resource.Status.SUCCESS -> {
                    setBalanceGame(
                        balanceGameResource.data!!.swipeId,
                        balanceGameResource.data.questions
                    )
                }
                Resource.Status.LOADING -> {
                    setBalanceGameLoading(getString(R.string.question_loading))
                }
                Resource.Status.EXCEPTION -> {

                    var enableReloadBtn = true

                    when (balanceGameResource.exceptionCode) {
                        ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
                        ExceptionCode.ACCOUNT_SHORT_OF_POINT_EXCEPTION,
                        ExceptionCode.SWIPE_CLICKED_EXISTS_EXCEPTION,
                        ExceptionCode.SWIPED_BLOCKED_EXCEPTION,
                        ExceptionCode.SWIPED_NOT_FOUND_EXCEPTION
                        -> {
                            enableReloadBtn = false
                        }
                    }

                    setBalanceGameLoadError(
                        enableReloadBtn,
                        balanceGameResource.exceptionMessage!!
                    )
                }
            }
        })
    }

    private fun setupClickResponseObserver() {
        viewModel.clickResponse.observe(viewLifecycleOwner, { clickResponse ->
            when (clickResponse.status) {
                Resource.Status.SUCCESS -> {

                    val match = clickResponse.data!!.match

                    when (clickResponse.data.notificationType) {
                        NotificationType.CLICKED -> setBalanceGameClicked(match.photoKey)
                        NotificationType.NOT_CLICKED -> setBalanceGameNotClicked()
                        NotificationType.MATCH -> {
                            dismiss()
                            balanceGameListener.onBalanceGameMatch(match.photoKey)
                        }
                    }
                }
                Resource.Status.LOADING -> {
                    setBalanceGameLoading(getString(R.string.question_checking))
                }
                Resource.Status.EXCEPTION -> {
                    var enableClickBtn = true
                    var enableRefreshBtn = false

                    when (clickResponse.exceptionCode) {
                        ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
                        ExceptionCode.ACCOUNT_SHORT_OF_POINT_EXCEPTION,
                        ExceptionCode.SWIPE_CLICKED_EXISTS_EXCEPTION,
                        ExceptionCode.SWIPED_BLOCKED_EXCEPTION,
                        ExceptionCode.SWIPED_NOT_FOUND_EXCEPTION -> {
                            enableClickBtn = false
                        }
                        ExceptionCode.QUESTION_SET_CHANGED_EXCEPTION -> {
                            enableClickBtn = false
                            enableRefreshBtn = true
                        }
                    }

                    setBalanceGameClickError(
                        enableClickBtn,
                        enableRefreshBtn,
                        clickResponse.exceptionMessage!!
                    )
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    }

    private fun setBalanceGame(newSwipeId: Long, newQuestionResponses: List<QuestionResponse>) {
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
            viewModel.click(swipedId, swipeId!!, answers)
            setBalanceGameLoading(getString(R.string.question_checking))
        }
    }

    private fun setBalanceGameLoadError(enableReloadBtn: Boolean, exceptionMessage: String) {
        hideLayouts()
        llBalanceGameLoadError.visibility = LinearLayout.VISIBLE
        btnBalanceGameReload.isEnabled = enableReloadBtn
        btnBalanceGameReload.visibility = if (enableReloadBtn) View.GONE else View.VISIBLE
        tvBalanceGameLoadErrorMessage.text = exceptionMessage
    }

    private fun setBalanceGameClickError(enableClickBtn: Boolean, enableRefreshBtn: Boolean, exceptionMessage: String) {
        hideLayouts()
        llBalanceGameClickError.visibility = LinearLayout.VISIBLE

        btnBalanceGameClick.visibility = if (enableClickBtn) View.VISIBLE else View.GONE
        btnBalanceGameRefresh.visibility = if (enableRefreshBtn) View.VISIBLE else View.GONE

        tvBalanceGameClickErrorMessage.text =exceptionMessage
    }

    private fun setBalanceGameNotClicked() {
        hideLayouts()
        llBalanceGameNotClick.visibility = LinearLayout.VISIBLE
    }

    private fun setBalanceGameClicked(swipedPhotoKey: String) {
        hideLayouts()
        llBalanceGameClicked.visibility = LinearLayout.VISIBLE
    }

    private fun setBalanceGameLoading(message: String) {
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

    private fun setupListeners() {
        btnTopOption.setOnClickListener { selectAnswer(ANSWER_TOP) }
        btnBottomOption.setOnClickListener { selectAnswer(ANSWER_BOTTOM) }

        btnBalanceGameReload.setOnClickListener { viewModel.swipe(swipeId, swipedId) }
        btnBalanceGameLoadErrorClose.setOnClickListener { dismiss() }

        btnBalanceGameClick.setOnClickListener { viewModel.click(swipedId, swipeId!!, answers) }
        btnBalanceGameClickErrorCloseBtn.setOnClickListener { dismiss() }
        btnBalanceGameRefresh.setOnClickListener { viewModel.swipe(swipeId, swipedId) }

        btnBalanceGameRetry.setOnClickListener { viewModel.swipe(swipeId, swipedId) }
        btnBalanceGameNotClickClose.setOnClickListener { dismiss() }

        btnBalanceGameClickedClose.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = "balanceGameDialog"
    }

    interface BalanceGameListener {
        fun onBalanceGameMatch(matchedPhotoKey: String)
    }

}
