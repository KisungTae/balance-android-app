package com.beeswork.balance.ui.cardfragment.balancegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogSwipeBalanceGameBinding
import com.beeswork.balance.internal.constant.ClickOutcome
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.observeResource
import com.beeswork.balance.ui.common.BalanceGame
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


class CardBalanceGameDialog(
    private val swipedId: UUID,
    private val swipedName: String,
    private val swipedProfilePhotoKey: String?
) : BalanceGame(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: CardBalanceGameViewModelFactory by instance()
    private val preferenceProvider: PreferenceProvider by instance()
    private lateinit var viewModel: CardBalanceGameViewModel
    private lateinit var binding: DialogSwipeBalanceGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSwipeBalanceGameBinding.inflate(layoutInflater)
        initBalanceGameDialogBinding(
            binding.layoutBalanceGame,
            binding.layoutBalanceGameLoading,
            binding.layoutBalanceGameError
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CardBalanceGameViewModel::class.java)
        bindUI()
        viewModel.like(swipedId)
    }


    private fun bindUI() = lifecycleScope.launch {
        setupLikeLiveDataObserver()
        setupClickLiveDataObserver()
        setupListeners()
    }

    private fun setupLikeLiveDataObserver() {
        viewModel.likeLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> {
                    resource.data?.let { newQuestions -> setupBalanceGame(newQuestions) }
                }
                resource.isLoading() -> {
                    showLoading(getString(R.string.fetch_question_message))
                }
                resource.isError() -> {
                    showFetchQuestionsError(MessageSource.getMessage(requireContext(), resource.exception))
                }
            }
        }
    }

    private fun setupClickLiveDataObserver() {
        viewModel.clickLiveData.observe(viewLifecycleOwner) { resource ->
            when {
                resource.isSuccess() -> resource.data?.let { clickDTO ->
                    when (clickDTO.clickOutcome) {
                        ClickOutcome.MISSED -> {
                            showMissed()
                        }
                        ClickOutcome.CLICKED -> {
                            showClicked()
                        }
                        ClickOutcome.MATCHED -> {
                            showMatched()
                        }
                    }
                }
                resource.isLoading() -> {
                    showLoading(getString(R.string.msg_check_answers))
                }
                resource.isError() -> {
                    showSaveError(MessageSource.getMessage(requireContext(), resource.exception))
                }
            }
        }
    }

    private fun showMissed() {
        showLayouts(View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameDialogMissed.visibility = View.VISIBLE
    }

    private fun showClicked() {
//      TODO: set proflie pictures?
        showLayouts(View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameDialogMissed.visibility = View.GONE
        binding.llBalanceGameClicked.visibility = View.VISIBLE
    }

    private fun showMatched() {
        //      TODO: set proflie pictures?
        showLayouts(View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameDialogMissed.visibility = View.GONE
        binding.llBalanceGameDialogMatched.visibility = View.VISIBLE
    }

    private fun setupListeners() {
        binding.btnBalanceGameRetry.setOnClickListener {
            binding.llBalanceGameDialogMissed.visibility = View.GONE
            resetBalanceGame()
        }
        binding.btnBalanceGameDialogMissedClose.setOnClickListener { dismiss() }
        binding.btnBalanceGameClickedClose.setOnClickListener { dismiss() }
    }

    override fun onSaveBalanceGame(answers: Map<Int, Boolean>) {
        viewModel.click(swipedId, answers)
    }

    override fun onFetchBalanceGame() {
        viewModel.like(swipedId)
    }

    companion object {
        const val TAG = "swipeBalanceGameDialog"
    }

}
