package com.beeswork.balance.ui.balancegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogBalanceGameBinding
import com.beeswork.balance.internal.constant.BalanceGameOption
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseDialog
import com.beeswork.balance.ui.profile.QuestionDomain
import kotlinx.coroutines.launch
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
        viewModel.swipe(swipedId)
    }


    private fun bindUI() = lifecycleScope.launch {
        setupSwipeLiveDataObserver()
        setupClickLiveDataObserver()
        setupOptionBtnListeners()
        setupListeners()
    }

    private fun setupOptionBtnListeners() {
        binding.btnBalanceGameTopOption.setOnClickListener { selectAnswer(BalanceGameOption.TOP) }
        binding.btnBalanceGameBottomOption.setOnClickListener { selectAnswer(BalanceGameOption.BOTTOM) }
    }

    private fun selectAnswer(answer: Boolean) {
        val question = questions[currentIndex]
        answers[question.id] = answer
        nextQuestion()
    }

    private fun setupSwipeLiveDataObserver() {
        viewModel.swipeLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading(getString(R.string.balance_game_loading_text))
                it.isError() -> showFetchQuestionsError(it.error, it.errorMessage)
                it.isSuccess() -> it.data?.let { newQuestions -> setupBalanceGame(newQuestions) }
            }
        }
    }

    private fun setupClickLiveDataObserver() {
        viewModel.clickLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading(getString(R.string.balance_game_checking_text))
                it.isError() -> showClickError(it.error, it.errorMessage)
                it.isSuccess() -> it.data?.let { pushType ->
                    when (pushType) {
                        PushType.MISSED -> showLayouts(View.GONE, View.GONE, View.GONE, View.VISIBLE)
                        PushType.CLICKED -> showClicked()
                        PushType.MATCHED -> showMatched()
                        else -> println("")
                    }
                }
            }
        }
    }

    private fun showClicked() {
        showLayouts(View.GONE, View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameClicked.visibility = View.VISIBLE

//      TODO: set proflie pictures?

    }

    private fun showMatched() {
        showLayouts(View.GONE, View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameDialogMatched.visibility = View.VISIBLE

//      TODO: set proflie pictures?
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

    private fun showClickError(error: String?, errorMessage: String?) {
        showError(View.GONE, View.VISIBLE, getString(R.string.error_title_click), error, errorMessage)
    }

    private fun showFetchQuestionsError(error: String?, errorMessage: String?) {
        showError(View.VISIBLE, View.GONE, getString(R.string.error_title_fetch_question), error, errorMessage)
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

    private fun setupListeners() {
        binding.btnBalanceGameDialogFetch.setOnClickListener { viewModel.swipe(swipedId) }
        binding.btnBalanceGameDialogSave.setOnClickListener { viewModel.click(swipedId, answers) }
        binding.btnBalanceGameRetry.setOnClickListener { setupBalanceGame(questions) }
        binding.btnBalanceGameDialogMissedClose.setOnClickListener { dismiss() }
        binding.btnBalanceGameClickedClose.setOnClickListener { dismiss() }
        binding.btnBalanceGameDialogErrorClose.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = "balanceDialog"
    }

}
