package com.beeswork.balance.ui.profile.balancegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogProfileBalanceGameBinding
import com.beeswork.balance.ui.common.BalanceGame
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileBalanceGameDialog : BalanceGame(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: ProfileBalanceGameViewModelFactory by instance()
    private lateinit var viewModel: ProfileBalanceGameViewModel
    private lateinit var binding: DialogProfileBalanceGameBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogProfileBalanceGameBinding.inflate(layoutInflater)
        initBalanceGameDialogBinding(
            binding.layoutBalanceGame,
            binding.layoutBalanceGameLoading,
            binding.layoutBalanceGameError
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileBalanceGameViewModel::class.java)
        bindUI()
        viewModel.fetchQuestions()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupFetchQuestionsLiveDataObserver()
        setupSaveAnswersLiveDataObserver()
    }

    private fun setupSaveAnswersLiveDataObserver() {
        viewModel.saveAnswersLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading(getString(R.string.balance_game_saving_answers_text))
                it.isError() -> showSaveError(it.error, it.errorMessage)
                it.isSuccess() -> dismiss()
            }
        }
    }

    private fun setupFetchQuestionsLiveDataObserver() {
        viewModel.fetchQuestionsLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading(getString(R.string.balance_game_loading_text))
                it.isError() -> showFetchQuestionsError(it.error, it.errorMessage)
                it.isSuccess() -> it.data?.let { newQuestions -> setupBalanceGame(newQuestions) }
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