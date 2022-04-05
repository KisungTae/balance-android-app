package com.beeswork.balance.ui.profilebalancegamedialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogBalanceGameBinding
import com.beeswork.balance.domain.uistate.balancegame.QuestionItemUIState
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.balancegameviewpageradapter.BalanceGameViewPagerAdapter
import com.beeswork.balance.ui.common.BaseDialog
import com.beeswork.balance.ui.registeractivity.RegisterViewPagerTabPosition
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileBalanceGameDialog : BaseDialog(), KodeinAware, BalanceGameViewPagerAdapter.BalanceGameListener {

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
        viewModel.fetchRandomQuestions()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupBalanceGameViewPager()
        observeFetchRandomQuestionsUIStateLiveData()
        observeSaveAnswersUIStateLiveData()
        setupBtnListeners()
    }

    private fun observeSaveAnswersUIStateLiveData() {
        viewModel.saveAnswersUIStateLiveData.observe(viewLifecycleOwner) { saveAnswersUIState ->
            when {
                saveAnswersUIState.saved -> {

                }
                saveAnswersUIState.showLoading -> {
//                    binding.llBalanceGameErrorWrapper.visibility = View.GONE
//                    binding.llBalanceGameLoadingWrapper.visibility = View.VISIBLE
//                    binding.tvBalanceGameLoadingMessage.text = getString(R.string.balance_game_saving_answers_text)
                }
                saveAnswersUIState.showError -> {

                }
            }
        }
    }

    private fun setupBtnListeners() {
        binding.btnBalanceGameRefetch.setOnClickListener {
            viewModel.fetchRandomQuestions()
        }
        binding.btnBalanceGameClose.setOnClickListener {
            dismiss()
        }
        binding.btnBalanceGameBack.setOnClickListener {
            val currentPosition = binding.vpBalanceGame.currentItem
            if (currentPosition > 0) {
                binding.vpBalanceGame.currentItem = currentPosition - 1
            }
        }
    }

    private fun observeFetchRandomQuestionsUIStateLiveData() {
        viewModel.fetchRandomQuestionsUIStateLiveData.observe(viewLifecycleOwner) { fetchRandomQuestionsUIState ->
            when {
                fetchRandomQuestionsUIState.questionItemUIStates != null -> {
                    binding.llBalanceGameErrorWrapper.visibility = View.GONE
                    binding.llBalanceGameLoadingWrapper.visibility = View.GONE
                    for (i in 1..fetchRandomQuestionsUIState.questionItemUIStates.size) {
                        binding.tlBalanceGame.addTab(binding.tlBalanceGame.newTab())
                    }
                    balanceGameViewPagerAdapter.submit(fetchRandomQuestionsUIState.questionItemUIStates)
                }
                fetchRandomQuestionsUIState.showLoading -> {
                    hideBackBtn()
                    hideRefreshBtn()
                    binding.llBalanceGameErrorWrapper.visibility = View.GONE
                    binding.llBalanceGameLoadingWrapper.visibility = View.VISIBLE
                    binding.tvBalanceGameLoadingMessage.text = getString(R.string.balance_game_loading_text)
                }
                else -> {
                    hideBackBtn()
                    hideRefreshBtn()
                    binding.llBalanceGameErrorWrapper.visibility = View.VISIBLE
                    binding.llBalanceGameLoadingWrapper.visibility = View.GONE
                    binding.btnBalanceGameResave.visibility = View.GONE
                    binding.btnBalanceGameRefetch.visibility = View.VISIBLE
                    binding.tvBalanceGameErrorTitle.text = getString(R.string.error_title_fetch_random_questions)
                    binding.tvBalanceGameErrorMessage.text = MessageSource.getMessage(
                        requireContext(),
                        fetchRandomQuestionsUIState.exception
                    )
                }
            }
        }
    }

    private fun setupBalanceGameViewPager() {
        balanceGameViewPagerAdapter = BalanceGameViewPagerAdapter(this)
        binding.vpBalanceGame.adapter = balanceGameViewPagerAdapter
        binding.vpBalanceGame.isUserInputEnabled = false
        binding.vpBalanceGame.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    hideBackBtn()
                } else {
                    showBackBtn()
                }
                binding.tlBalanceGame.selectTab(binding.tlBalanceGame.getTabAt(position))
            }
        })
    }

    private fun showBackBtn() {
        binding.btnBalanceGameBack.visibility = View.VISIBLE
        binding.btnBalanceGameBack.isEnabled = true
    }

    private fun hideBackBtn() {
        binding.btnBalanceGameBack.visibility = View.INVISIBLE
        binding.btnBalanceGameBack.isEnabled = false
    }

    private fun showRefreshBtn() {
        binding.btnBalanceGameRefresh.visibility = View.VISIBLE
        binding.btnBalanceGameRefresh.isEnabled = true
    }

    private fun hideRefreshBtn() {
        binding.btnBalanceGameRefresh.visibility = View.INVISIBLE
        binding.btnBalanceGameRefresh.isEnabled = false
    }

    companion object {
        const val TAG = "profileBalanceGameDialog"
    }

    override fun onOptionSelected() {
        val currentPosition = binding.vpBalanceGame.currentItem
        if (currentPosition == balanceGameViewPagerAdapter.itemCount - 1) {
            viewModel.saveAnswers(balanceGameViewPagerAdapter.getAnswers())
        } else {
            binding.vpBalanceGame.currentItem = currentPosition + 1
        }
    }

}