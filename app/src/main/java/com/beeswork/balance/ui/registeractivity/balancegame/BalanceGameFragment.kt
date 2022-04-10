package com.beeswork.balance.ui.registeractivity.balancegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentBalanceGameBinding
import com.beeswork.balance.ui.balancegamedialog.*
import com.beeswork.balance.ui.registeractivity.BaseRegisterStepFragment
import kotlinx.coroutines.launch
import java.util.*

class BalanceGameFragment : BaseRegisterStepFragment(), RegisterBalanceGameListener {

    private lateinit var binding: FragmentBalanceGameBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
            ProfileBalanceGameDialog(null, this).show(childFragmentManager, ProfileBalanceGameDialog.TAG)
        }
    }

    override fun onBalanceGameAnswersSaved() {
        moveToNextTab()
    }
}