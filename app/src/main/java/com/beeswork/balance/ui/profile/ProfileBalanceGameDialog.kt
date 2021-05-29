package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogProfileBalanceGameBinding
import com.beeswork.balance.databinding.DialogSwipeBalanceGameBinding
import com.beeswork.balance.ui.common.BalanceGame
import com.beeswork.balance.ui.swipe.SwipeBalanceGameViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileBalanceGameDialog: BalanceGame(), KodeinAware {

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
    }

    private fun bindUI() = lifecycleScope.launch {

    }

    override fun onSaveBalanceGame(answers: Map<Int, Boolean>) {
        TODO("Not yet implemented")
    }

    override fun onFetchBalanceGame() {
        TODO("Not yet implemented")
    }

}