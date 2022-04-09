package com.beeswork.balance.ui.registeractivity.balancegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentBalanceGameBinding
import com.beeswork.balance.ui.balancegamedialog.BalanceGameResultListener
import com.beeswork.balance.ui.balancegamedialog.CardBalanceGameDialog
import com.beeswork.balance.ui.balancegamedialog.ProfileBalanceGameDialog
import com.beeswork.balance.ui.balancegamedialog.RegisterBalanceGameDialog
import com.beeswork.balance.ui.registeractivity.BaseRegisterStepFragment
import kotlinx.coroutines.launch
import java.util.*

class BalanceGameFragment: BaseRegisterStepFragment(), BalanceGameResultListener {

    private lateinit var binding: FragmentBalanceGameBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBalanceGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupLaunchBalanceGameBtnListener()
    }

    private fun setupLaunchBalanceGameBtnListener() {
        binding.btnRegisterBalanceGameLaunch.setOnClickListener {
//            RegisterBalanceGameDialog(this).show(childFragmentManager, RegisterBalanceGameDialog.TAG)
//            ProfileBalanceGameDialog().show(childFragmentManager, RegisterBalanceGameDialog.TAG)
            CardBalanceGameDialog(UUID.fromString("a514e4ab-a5b0-4efb-a2ac-b96edfb0e77d"), "").show(childFragmentManager, CardBalanceGameDialog.TAG)
        }
    }

    override fun onBalanceGameAnswersSaved() {
        moveToNextTab()
    }
}