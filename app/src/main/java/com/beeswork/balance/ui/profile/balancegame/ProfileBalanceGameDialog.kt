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
        viewModel.saveAnswersLiveData.observe(viewLifecycleOwner) { resource ->
            when {
                resource.isSuccess() -> dismiss()
                resource.isLoading() -> showLoading(getString(R.string.balance_game_saving_answers_text))
                resource.isError() && validateLoginFromResource(resource) -> showSaveError(resource.error, resource.errorMessage)
            }
        }
    }

    private fun setupFetchQuestionsLiveDataObserver() {
        viewModel.fetchQuestionsLiveData.observe(viewLifecycleOwner) { resource ->
            when {
                resource.isSuccess() -> resource.data?.let { newQuestions -> setupBalanceGame(newQuestions) }
                resource.isLoading() -> showLoading(getString(R.string.balance_game_loading_text))
                resource.isError() && validateLoginFromResource(resource) -> showFetchQuestionsError(resource.error, resource.errorMessage)
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