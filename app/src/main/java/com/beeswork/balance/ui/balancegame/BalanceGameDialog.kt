package com.beeswork.balance.ui.balancegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.QuestionResponse
import com.beeswork.balance.databinding.DialogBalanceGameBinding
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseDialog
import com.beeswork.balance.ui.profile.QuestionDomain
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


class BalanceGameDialog(
    private val swipedId: UUID,
    private val swipedName: String,
    private val swipedProfilePhotoKey: String?
) : BaseDialog(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: BalanceGameDialogViewModelFactory by instance()
    private val preferenceProvider: PreferenceProvider by instance()
    private lateinit var viewModel: BalanceGameDialogViewModel
    private lateinit var binding: DialogBalanceGameBinding

    private lateinit var questions: List<QuestionDomain>
    private val answers: MutableMap<Int, Boolean> = mutableMapOf()

    private var swipeId: Long? = null
    private var currentIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBalanceGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BalanceGameDialogViewModel::class.java)
        bindUI()
    }


    private fun bindUI() {
//        setupSwipeLiveDataObserver()
//        setupClickLiveDataObserver()
//        setupBalanceGameObserver()
//        setupClickResponseObserver()
//        setupListeners()
    }

    private fun setupSwipeLiveDataObserver() {
        viewModel.swipeLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading(getString(R.string.balance_game_loading_text))
                it.isError() -> showError(
                    View.VISIBLE,
                    View.GONE,
                    getString(R.string.error_title_fetch_question),
                    it.error,
                    it.errorMessage
                )
                it.isSuccess() -> it.data?.let { newQuestions -> setupBalanceGame(newQuestions) }
            }
        }
    }

    private fun setupClickLiveDataObserver() {
        viewModel.clickLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading(getString(R.string.balance_game_checking_text))
                it.isError() -> showError(
                    View.GONE,
                    View.VISIBLE,
                    getString(R.string.error_title_click),
                    it.error,
                    it.errorMessage
                )
                it.isSuccess() -> it.data?.let { pushType ->
                    when (pushType) {
                        PushType.MISSED -> showLayouts(View.GONE, View.GONE, View.GONE, View.VISIBLE)
                        PushType.CLICKED -> showClicked()
                        PushType.MATCHED -> showMatched()
                        else -> { }
                    }
                }
            }
        }
    }

    private fun showClicked() {
        showLayouts(View.GONE, View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameClicked.visibility = View.VISIBLE
    }

    private fun showMatched() {
        showLayouts(View.GONE, View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameDialogMatched.visibility = View.VISIBLE
    }



    private fun setupBalanceGame(newQuestions: List<QuestionDomain>) {
        currentIndex = -1
        answers.clear()
        questions = newQuestions
        nextQuestion()
        showLayouts(View.VISIBLE, View.GONE, View.GONE, View.GONE)
    }

    private fun nextQuestion() {
        currentIndex++
        if (currentIndex < questions.size) {
            val question = questions[currentIndex]
            binding.btnBalanceGameTopOption.text = question.topOption
            binding.btnBalanceGameBottomOption.text = question.bottomOption
        } else viewModel.click(swipedId, answers)
    }

    private fun showLoading(loadingMessage: String) {
        showLayouts(View.GONE, View.VISIBLE, View.GONE, View.GONE)
        binding.tvLoadingMessage.text = loadingMessage
    }

    private fun showError(fetchBtn: Int, saveBtn: Int, errorTitle: String, error: String?, errorMessage: String?) {
        showLayouts(View.GONE, View.GONE, View.VISIBLE, View.GONE)
        binding.btnBalanceGameDialogFetch.visibility = fetchBtn
        binding.btnBalanceGameDialogSave.visibility = saveBtn
        binding.tvBalanceGameDialogErrorTitle.text = errorTitle
        binding.tvBalanceGameDialogErrorMessage.text = errorMessage
        setupErrorMessage(error, errorMessage, binding.tvBalanceGameDialogErrorMessage)
    }

    private fun showLayouts(balanceGame: Int, loading: Int, error: Int, missed: Int) {
        binding.llBalanceGameWrapper.visibility = balanceGame
        binding.llBalanceGameLoading.visibility = loading
        binding.llBalanceGameError.visibility = error
        binding.llBalanceGameDialogMissed.visibility = missed
    }

    private fun setupBalanceObserver() {
//        viewModel.balanceGame.observe(viewLifecycleOwner, { balanceGameResource ->
//            when (balanceGameResource.status) {
//                Resource.Status.SUCCESS -> {
//                    setBalanceGame(
//                        balanceGameResource.data!!.swipeId,
//                        balanceGameResource.data.questions
//                    )
//                }
//                Resource.Status.LOADING -> {
//                    setBalanceGameLoading(getString(R.string.question_loading))
//                }
//                Resource.Status.ERROR -> {
//
//                    var enableReloadBtn = true
//
//                    when (balanceGameResource.error) {
//                        ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
//                        ExceptionCode.ACCOUNT_SHORT_OF_POINT_EXCEPTION,
//                        ExceptionCode.SWIPE_CLICKED_EXISTS_EXCEPTION,
//                        ExceptionCode.SWIPED_BLOCKED_EXCEPTION,
//                        ExceptionCode.SWIPED_NOT_FOUND_EXCEPTION
//                        -> {
//                            enableReloadBtn = false
//                        }
//                    }
//
//                    setBalanceGameLoadError(
//                        enableReloadBtn,
//                        balanceGameResource.errorMessage!!
//                    )
//                }
//            }
//        })
    }

    private fun setupClickResponseObserver() {
//        viewModel.clickResponse.observe(viewLifecycleOwner, { clickResponse ->
//            when (clickResponse.status) {
//                Resource.Status.SUCCESS -> {
//
//                    val match = clickResponse.data!!.match
//
//                    when (clickResponse.data.result) {
////                        NotificationType.CLICKED -> setBalanceGameClicked(match.profilePhotoKey)
//                        NotificationType.NOT_CLICKED -> setBalanceGameNotClicked()
//                        NotificationType.MATCH -> {
//                            dismiss()
////                            balanceGameListener.onBalanceGameMatch(match.profilePhotoKey)
//                        }
//                    }
//                }
//                Resource.Status.LOADING -> {
//                    setBalanceGameLoading(getString(R.string.question_checking))
//                }
//                Resource.Status.ERROR -> {
//                    var enableClickBtn = true
//                    var enableRefreshBtn = false
//
//                    when (clickResponse.error) {
//                        ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
//                        ExceptionCode.ACCOUNT_SHORT_OF_POINT_EXCEPTION,
//                        ExceptionCode.SWIPE_CLICKED_EXISTS_EXCEPTION,
//                        ExceptionCode.SWIPED_BLOCKED_EXCEPTION,
//                        ExceptionCode.SWIPED_NOT_FOUND_EXCEPTION -> {
//                            enableClickBtn = false
//                        }
//                        ExceptionCode.QUESTION_SET_CHANGED_EXCEPTION -> {
//                            enableClickBtn = false
//                            enableRefreshBtn = true
//                        }
//                    }
//
//                    setBalanceGameClickError(
//                        enableClickBtn,
//                        enableRefreshBtn,
//                        clickResponse.errorMessage!!
//                    )
//                }
//            }
//        })
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
//        binding.llBalanceGame.visibility = LinearLayout.VISIBLE

        swipeId = newSwipeId
//        questions = newQuestionResponses
        nextQuestion()
    }

    private fun selectAnswer(answer: Boolean) {
        val question = questions[currentIndex]
        answers[question.id] = answer
        nextQuestion()
    }


    private fun setBalanceGameLoadError(enableReloadBtn: Boolean, exceptionMessage: String) {
        hideLayouts()
//        binding.llBalanceGameLoadError.visibility = LinearLayout.VISIBLE
//        binding.btnBalanceGameReload.isEnabled = enableReloadBtn
//        binding.btnBalanceGameReload.visibility = if (enableReloadBtn) View.GONE else View.VISIBLE
//        binding.tvBalanceGameLoadErrorMessage.text = exceptionMessage
    }

    private fun setBalanceGameClickError(enableClickBtn: Boolean, enableRefreshBtn: Boolean, exceptionMessage: String) {
        hideLayouts()
//        binding.llBalanceGameClickError.visibility = LinearLayout.VISIBLE
//        binding.btnBalanceGameClick.visibility = if (enableClickBtn) View.VISIBLE else View.GONE
//        binding.btnBalanceGameRefresh.visibility = if (enableRefreshBtn) View.VISIBLE else View.GONE
//        binding.tvBalanceGameClickErrorMessage.text =exceptionMessage
    }

    private fun setBalanceGameNotClicked() {
        hideLayouts()
//        binding.llBalanceGameNotClick.visibility = LinearLayout.VISIBLE
    }

    private fun setBalanceGameClicked(swipedPhotoKey: String) {
        hideLayouts()
//        binding.llBalanceGameClicked.visibility = LinearLayout.VISIBLE
    }

    private fun setBalanceGameLoading(message: String) {
        hideLayouts()
//        binding.llBalanceGameLoading.visibility = LinearLayout.VISIBLE
//        binding.tvBalanceGameLoadingMessage.text = message
    }

    private fun hideLayouts() {
//        binding.llBalanceGameLoading.visibility = LinearLayout.GONE
//        binding.llBalanceGame.visibility = LinearLayout.GONE
//        binding.llBalanceGameLoadError.visibility = LinearLayout.GONE
//        binding.llBalanceGameClicked.visibility = LinearLayout.GONE
//        binding.llBalanceGameClickError.visibility = LinearLayout.GONE
//        binding.llBalanceGameNotClick.visibility = LinearLayout.GONE
    }

    private fun setupListeners() {
//        binding.btnTopOption.setOnClickListener { selectAnswer(BalanceGameAnswer.TOP) }
//        binding.btnBottomOption.setOnClickListener {
//            println("bottom option clicked!!!!!")
//            selectAnswer(BalanceGameAnswer.BOTTOM)
//        }

//        binding.btnBalanceGameReload.setOnClickListener { viewModel.swipe(swipeId, swipedId) }
//        binding.btnBalanceGameLoadErrorClose.setOnClickListener { dismiss() }

//        binding.btnBalanceGameClick.setOnClickListener { viewModel.click(swipedId, swipeId!!, answers) }
//        binding.btnBalanceGameClickErrorCloseBtn.setOnClickListener { dismiss() }
//        binding.btnBalanceGameRefresh.setOnClickListener { viewModel.swipe(swipeId, swipedId) }

//        binding.btnBalanceGameRetry.setOnClickListener { viewModel.swipe(swipeId, swipedId) }
//        binding.btnBalanceGameNotClickClose.setOnClickListener { dismiss() }

//        binding.btnBalanceGameClickedClose.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = "balanceDialog"
    }

}
