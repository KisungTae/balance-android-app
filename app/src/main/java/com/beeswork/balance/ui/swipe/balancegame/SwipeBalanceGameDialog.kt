package com.beeswork.balance.ui.swipe.balancegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogSwipeBalanceGameBinding
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BalanceGame
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


class SwipeBalanceGameDialog(
    private val swipedId: UUID,
    private val swipedName: String,
    private val swipedProfilePhotoKey: String?
) : BalanceGame(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeBalanceGameViewModelFactory by instance()
    private val preferenceProvider: PreferenceProvider by instance()
    private lateinit var viewModel: SwipeBalanceGameViewModel
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
        viewModel = ViewModelProvider(this, viewModelFactory).get(SwipeBalanceGameViewModel::class.java)
        bindUI()
        viewModel.swipe(swipedId)
    }


    private fun bindUI() = lifecycleScope.launch {
        setupSwipeLiveDataObserver()
        setupClickLiveDataObserver()
        setupListeners()
    }

    private fun setupSwipeLiveDataObserver() {
        viewModel.swipeLiveData.observe(viewLifecycleOwner) { resource ->
            when {
                resource.isSuccess() -> resource.data?.let { newQuestions -> setupBalanceGame(newQuestions) }
                resource.isLoading() -> showLoading(getString(R.string.balance_game_loading_text))
                resource.isError() && validateLogin(resource) -> {
                    showFetchQuestionsError(resource.error, resource.errorMessage)
                }
            }
        }
    }

    private fun setupClickLiveDataObserver() {
        viewModel.clickLiveData.observe(viewLifecycleOwner) { resource ->
            when {
                resource.isSuccess() -> resource.data?.let { pushType ->
                    when (pushType) {
                        PushType.MISSED -> {
                            showLayouts(View.GONE, View.GONE, View.GONE)
                            binding.llBalanceGameDialogMissed.visibility = View.VISIBLE
                        }
                        PushType.CLICKED -> showClicked()
                        PushType.MATCHED -> showMatched()
                        else -> println("")
                    }
                }
                resource.isLoading() -> showLoading(getString(R.string.balance_game_checking_text))
                resource.isError() -> showSaveError(resource.error, resource.errorMessage)
            }
        }
    }

    private fun showClicked() {
        showLayouts(View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameDialogMissed.visibility = View.GONE
        binding.llBalanceGameClicked.visibility = View.VISIBLE

//      TODO: set proflie pictures?

    }

    private fun showMatched() {
        showLayouts(View.GONE, View.GONE, View.GONE)
        binding.llBalanceGameDialogMissed.visibility = View.GONE
        binding.llBalanceGameDialogMatched.visibility = View.VISIBLE

//      TODO: set proflie pictures?
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
        viewModel.swipe(swipedId)
    }

    companion object {
        const val TAG = "swipeBalanceGameDialog"
    }

}
