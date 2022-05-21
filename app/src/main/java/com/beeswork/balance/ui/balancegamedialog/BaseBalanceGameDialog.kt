package com.beeswork.balance.ui.balancegamedialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogBalanceGameBinding
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.common.BaseDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

abstract class BaseBalanceGameDialog : BaseDialog(), KodeinAware, BalanceGameViewPagerAdapter.BalanceGameOptionListener {

    override val kodein by closestKodein()
    private val viewModelFactory: BalanceGameViewModelFactory by instance()
    protected lateinit var viewModel: BalanceGameViewModel
    protected lateinit var binding: DialogBalanceGameBinding
    protected lateinit var balanceGameViewPagerAdapter: BalanceGameViewPagerAdapter


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
        viewModel = ViewModelProvider(this, viewModelFactory).get(BalanceGameViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupBalanceGameViewPager()
        setupBtnListeners()
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

    private fun setupBtnListeners() {
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

    protected fun observeFetchQuestionsUIStateLiveData(showRefreshBtn: Boolean) {
        viewModel.fetchQuestionsUIStateLiveData.observe(viewLifecycleOwner) { fetchQuestionUIState ->
            when {
                fetchQuestionUIState.questionItemUIStates != null && fetchQuestionUIState.point != null -> {
                    if (showRefreshBtn) {
                        showRefreshBtn()
                    } else {
                        hideRefreshBtn()
                    }
                    showLayouts(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE)
                    balanceGameViewPagerAdapter.submit(fetchQuestionUIState.questionItemUIStates)
                    addBalanceGameTabs(fetchQuestionUIState.questionItemUIStates.size)
                    binding.tvBalanceGamePoint.text = fetchQuestionUIState.point.toString()
                    binding.vpBalanceGame.currentItem = 0
                }
                fetchQuestionUIState.showLoading -> {
                    showLoading(getString(R.string.fetch_question_message))
                }
                fetchQuestionUIState.showError -> {
                    val title = getString(R.string.error_title_fetch_question)
                    val message = MessageSource.getMessage(requireContext(), fetchQuestionUIState.exception)
                    showError(title, message)
                    showErrorBtn(View.GONE, View.VISIBLE, View.GONE)
                }
            }
        }
    }

    private fun addBalanceGameTabs(size: Int) {
        binding.tlBalanceGame.removeAllTabs()
        for (i in 1..size) {
            binding.tlBalanceGame.addTab(binding.tlBalanceGame.newTab())
        }
    }

    protected fun showLoading(message: String) {
        hideBackBtn()
        hideRefreshBtn()
        showLayouts(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE)
        binding.tvBalanceGameLoadingMessage.text = message
    }

    protected fun showError(title: String, message: String?) {
        hideBackBtn()
        hideRefreshBtn()
        showLayouts(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE)
        binding.tvBalanceGameErrorTitle.text = title
        binding.tvBalanceGameErrorMessage.text = message
    }


    protected fun showBackBtn() {
        if (binding.vpBalanceGame.currentItem > 0) {
            binding.btnBalanceGameBack.visibility = View.VISIBLE
            binding.btnBalanceGameBack.isEnabled = true
        }
    }

    protected fun hideBackBtn() {
        binding.btnBalanceGameBack.visibility = View.INVISIBLE
        binding.btnBalanceGameBack.isEnabled = false
    }

    protected fun showRefreshBtn() {
        binding.btnBalanceGameFetchRandomQuestion.visibility = View.VISIBLE
        binding.btnBalanceGameFetchRandomQuestion.isEnabled = true
    }

    private fun hideRefreshBtn() {
        binding.btnBalanceGameFetchRandomQuestion.visibility = View.INVISIBLE
        binding.btnBalanceGameFetchRandomQuestion.isEnabled = false
    }

    protected fun showErrorBtn(resave: Int, refetch: Int, reclick: Int) {
        binding.btnBalanceGameResave.visibility = resave
        binding.btnBalanceGameRefetch.visibility = refetch
        binding.btnBalanceGameReclick.visibility = reclick
    }

    protected fun showLayouts(loading: Int, error: Int, clicked: Int, matched: Int, missed: Int) {
        binding.llBalanceGameLoadingWrapper.visibility = loading
        binding.llBalanceGameErrorWrapper.visibility = error
        binding.llBalanceGameClickedWrapper.visibility = clicked
        binding.llBalanceGameMatchedWrapper.visibility = matched
        binding.llBalanceGameMissedWrapper.visibility = missed
    }

    protected fun isBalanceGameFinished(position: Int): Boolean {
        return position >= (balanceGameViewPagerAdapter.itemCount - 1)
    }

    protected fun moveToNextTab(position: Int) {
        if (!isBalanceGameFinished(position)) {
            binding.vpBalanceGame.currentItem = binding.vpBalanceGame.currentItem + 1
        }
    }
}