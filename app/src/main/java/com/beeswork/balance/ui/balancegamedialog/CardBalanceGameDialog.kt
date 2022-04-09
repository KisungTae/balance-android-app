package com.beeswork.balance.ui.balancegamedialog

import android.os.Bundle
import android.view.View
import com.beeswork.balance.R
import com.beeswork.balance.domain.uistate.match.MatchNotificationUIState
import com.beeswork.balance.internal.constant.ClickOutcome
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.MessageSource
import com.bumptech.glide.Glide
import java.util.*

class CardBalanceGameDialog(
    private val swipedId: UUID,
    private val swipedName: String?,
    private val swipedProfilePhotoUrl: String?
): BaseBalanceGameDialog() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
//        viewModel.like(swipedId)
    }

    private fun bindUI() {
        setupBtnListeners()
        observeFetchQuestionsUIStateLiveData(false)
        observeClickUIStateLiveData()





    }

    private fun setupBtnListeners() {
        binding.btnBalanceGameReclick.setOnClickListener {
            viewModel.click(swipedId, balanceGameViewPagerAdapter.getAnswers())
        }
        binding.btnBalanceGameMissedRetry.setOnClickListener {
            viewModel.like(swipedId)
        }
        binding.btnBalanceGameClickedGoToSwipe.setOnClickListener {
            // implement listener to go to swipe
            dismiss()
        }
        binding.btnBalanceGameMatchedGoToMatch.setOnClickListener {
            // implement listener to go to match
            dismiss()
        }
        binding.btnBalanceGameClickedClose.setOnClickListener {
            dismiss()
        }
        binding.btnBalanceGameMatchedClose.setOnClickListener {
            dismiss()
        }
        binding.btnBalanceGameMissedClose.setOnClickListener {
            dismiss()
        }

    }

    private fun observeClickUIStateLiveData() {
        viewModel.clickUIStateLiveData.observe(viewLifecycleOwner) { clickUIState ->
            when {
                clickUIState.clickOutcome != null -> {
                    binding.tvBalanceGameMatchedSwipedName.text = swipedName ?: ""
                    when (clickUIState.clickOutcome) {
                        ClickOutcome.MATCHED -> {
                            showMatched(clickUIState.matchNotificationUIState)
                        }
                        ClickOutcome.CLICKED -> {
                            showClicked()
                        }
                        ClickOutcome.MISSED -> {
                            showMissed()
                        }
                    }
                }
                clickUIState.showLoading -> {
                    showLoading(getString(R.string.msg_check_answers))
                }
                clickUIState.showError -> {
                    val title = getString(R.string.error_title_check_answers)
                    val message = MessageSource.getMessage(requireContext(), clickUIState.exception)
                    showErrorBtn(View.GONE, View.GONE, View.VISIBLE)
                    showError(title, message)
                }
            }
        }
    }

    private fun showMatched(matchNotificationUIState: MatchNotificationUIState?) {
        showLayouts(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE)

        Glide.with(requireContext())
            .load(swipedProfilePhotoUrl)
            .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
            .into(binding.ivBalanceGameMatchedSwipedProfilePhoto)

        Glide.with(requireContext())
            .load(matchNotificationUIState?.swiperProfilePhotoUrl)
            .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
            .into(binding.ivBalanceGameClickedSwipedProfilePhoto)
    }

    private fun showClicked() {
        showLayouts(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE)
        Glide.with(requireContext())
            .load(swipedProfilePhotoUrl)
            .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
            .into(binding.ivBalanceGameMatchedSwiperProfilePhoto)
    }

    private fun showMissed() {
        showLayouts(View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE)
    }


    override fun onOptionSelected() {
        if (isBalanceGameFinished()) {
            viewModel.click(swipedId, balanceGameViewPagerAdapter.getAnswers())
        } else {
            moveToNextTab()
        }
    }

    companion object {
        const val TAG = "cardBalanceGameDialog"
    }
}