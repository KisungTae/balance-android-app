package com.beeswork.balance.ui.profilebalancegamedialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogBalanceGameBinding
import com.beeswork.balance.domain.uistate.balancegame.BalanceGameQuestionItemUIState
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.observeResource
import com.beeswork.balance.ui.balancegameviewpageradapter.BalanceGameListener
import com.beeswork.balance.ui.balancegameviewpageradapter.BalanceGameViewPagerAdapter
import com.beeswork.balance.ui.common.BalanceGame
import com.beeswork.balance.ui.registeractivity.RegisterViewPagerTabPosition
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileBalanceGameDialog : BalanceGame(), KodeinAware, BalanceGameListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ProfileBalanceGameViewModelFactory by instance()
    private lateinit var viewModel: ProfileBalanceGameViewModel
    private lateinit var binding: DialogBalanceGameBinding
    private lateinit var balanceGameViewPagerAdapter: BalanceGameViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_PrimaryFullScreen)
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
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileBalanceGameViewModel::class.java)
        bindUI()
//        viewModel.fetchQuestions()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupBalanceGameViewPager()


//        setupFetchQuestionsLiveDataObserver()
//        setupSaveAnswersLiveDataObserver()
    }

    private fun setupBalanceGameViewPager() {
        //todo: remove me
        val questions = mutableListOf<BalanceGameQuestionItemUIState>()
        for (i in 0..3) {
            questions.add(BalanceGameQuestionItemUIState(i, "", "topOption-$i", "bottomOption-$i", null))
        }

        for (i in 1..questions.size) {
            binding.tlBalanceGame.addTab(binding.tlBalanceGame.newTab())
        }

        binding.tlBalanceGame.selectTab(binding.tlBalanceGame.getTabAt(0))

        balanceGameViewPagerAdapter = BalanceGameViewPagerAdapter(questions, this)
        binding.vpBalanceGame.adapter = balanceGameViewPagerAdapter
    }

    private fun setupSaveAnswersLiveDataObserver() {
        viewModel.saveAnswersLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> dismiss()
                resource.isLoading() -> showLoading(getString(R.string.balance_game_saving_answers_text))
                resource.isError() -> showSaveError(MessageSource.getMessage(requireContext(), resource.exception))
            }
        }
    }

    private fun setupFetchQuestionsLiveDataObserver() {
        viewModel.fetchQuestionsLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> resource.data?.let { newQuestions -> setupBalanceGame(newQuestions) }
                resource.isLoading() -> showLoading(getString(R.string.balance_game_loading_text))
                resource.isError() -> showFetchQuestionsError(MessageSource.getMessage(requireContext(), resource.exception))
            }
        }
    }

    override fun onSaveBalanceGame(answers: Map<Int, Boolean>) {
        viewModel.saveQuestions(answers)
    }

    override fun onFetchBalanceGame() {
        viewModel.fetchQuestions()
    }

    companion object {
        const val TAG = "profileBalanceGameDialog"
    }

}