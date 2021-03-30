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
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.databinding.DialogBalanceGameBinding
import com.beeswork.balance.internal.constant.BalanceGameAnswer
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.NotificationType
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class BalanceGameDialog(
    private val swipedId: String,
    private val balanceGameListener: BalanceGameListener
): DialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: BalanceGameDialogViewModelFactory by instance()
    private lateinit var viewModel: BalanceGameDialogViewModel
    private lateinit var binding: DialogBalanceGameBinding

    private lateinit var questions: List<QuestionResponse>
    private var swipeId: Long? = null
    private var currentIndex = -1
    private val answers: MutableMap<Int, Boolean> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BalanceGameDialogViewModel::class.java)
        binding = DialogBalanceGameBinding.inflate(layoutInflater)
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
                Resource.Status.ERROR -> {

                    var enableReloadBtn = true

                    when (balanceGameResource.error) {
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
                        balanceGameResource.errorMessage!!
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

                    when (clickResponse.data.result) {
//                        NotificationType.CLICKED -> setBalanceGameClicked(match.repPhotoKey)
                        NotificationType.NOT_CLICKED -> setBalanceGameNotClicked()
                        NotificationType.MATCH -> {
                            dismiss()
//                            balanceGameListener.onBalanceGameMatch(match.repPhotoKey)
                        }
                    }
                }
                Resource.Status.LOADING -> {
                    setBalanceGameLoading(getString(R.string.question_checking))
                }
                Resource.Status.ERROR -> {
                    var enableClickBtn = true
                    var enableRefreshBtn = false

                    when (clickResponse.error) {
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
                        clickResponse.errorMessage!!
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
        binding.llBalanceGame.visibility = LinearLayout.VISIBLE

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
            binding.tvQuestionDescription.text = question.description
            binding.btnTopOption.text = question.topOption
            binding.btnBottomOption.text = question.bottomOption
        } else {
            viewModel.click(swipedId, swipeId!!, answers)
            setBalanceGameLoading(getString(R.string.question_checking))
        }
    }

    private fun setBalanceGameLoadError(enableReloadBtn: Boolean, exceptionMessage: String) {
        hideLayouts()
        binding.llBalanceGameLoadError.visibility = LinearLayout.VISIBLE
        binding.btnBalanceGameReload.isEnabled = enableReloadBtn
        binding.btnBalanceGameReload.visibility = if (enableReloadBtn) View.GONE else View.VISIBLE
        binding.tvBalanceGameLoadErrorMessage.text = exceptionMessage
    }

    private fun setBalanceGameClickError(enableClickBtn: Boolean, enableRefreshBtn: Boolean, exceptionMessage: String) {
        hideLayouts()
        binding.llBalanceGameClickError.visibility = LinearLayout.VISIBLE
        binding.btnBalanceGameClick.visibility = if (enableClickBtn) View.VISIBLE else View.GONE
        binding.btnBalanceGameRefresh.visibility = if (enableRefreshBtn) View.VISIBLE else View.GONE
        binding.tvBalanceGameClickErrorMessage.text =exceptionMessage
    }

    private fun setBalanceGameNotClicked() {
        hideLayouts()
        binding.llBalanceGameNotClick.visibility = LinearLayout.VISIBLE
    }

    private fun setBalanceGameClicked(swipedPhotoKey: String) {
        hideLayouts()
        binding.llBalanceGameClicked.visibility = LinearLayout.VISIBLE
    }

    private fun setBalanceGameLoading(message: String) {
        hideLayouts()
        binding.llBalanceGameLoading.visibility = LinearLayout.VISIBLE
        binding.tvBalanceGameLoadingMessage.text = message
    }

    private fun hideLayouts() {
        binding.llBalanceGameLoading.visibility = LinearLayout.GONE
        binding.llBalanceGame.visibility = LinearLayout.GONE
        binding.llBalanceGameLoadError.visibility = LinearLayout.GONE
        binding.llBalanceGameClicked.visibility = LinearLayout.GONE
        binding.llBalanceGameClickError.visibility = LinearLayout.GONE
        binding.llBalanceGameNotClick.visibility = LinearLayout.GONE
    }

    private fun setupListeners() {
        binding.btnTopOption.setOnClickListener { selectAnswer(BalanceGameAnswer.TOP) }
        binding.btnBottomOption.setOnClickListener { selectAnswer(BalanceGameAnswer.BOTTOM) }

        binding.btnBalanceGameReload.setOnClickListener { viewModel.swipe(swipeId, swipedId) }
        binding.btnBalanceGameLoadErrorClose.setOnClickListener { dismiss() }

        binding.btnBalanceGameClick.setOnClickListener { viewModel.click(swipedId, swipeId!!, answers) }
        binding.btnBalanceGameClickErrorCloseBtn.setOnClickListener { dismiss() }
        binding.btnBalanceGameRefresh.setOnClickListener { viewModel.swipe(swipeId, swipedId) }

        binding.btnBalanceGameRetry.setOnClickListener { viewModel.swipe(swipeId, swipedId) }
        binding.btnBalanceGameNotClickClose.setOnClickListener { dismiss() }

        binding.btnBalanceGameClickedClose.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = "balanceGameDialog"
    }

    interface BalanceGameListener {
        fun onBalanceGameMatch(matchedPhotoKey: String)
    }

}
