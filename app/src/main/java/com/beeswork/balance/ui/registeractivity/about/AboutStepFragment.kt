package com.beeswork.balance.ui.registeractivity.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentAboutBinding
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AboutStepFragment(
    private val registerStepListener: RegisterStepListener
): BaseFragment(), KodeinAware {

    override val kodein by closestKodein()
    private lateinit var binding: FragmentAboutBinding
    private lateinit var viewModel: AboutStepViewModel
    private val viewModelFactory: AboutStepViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAboutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AboutStepViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeAboutLiveData()
        observeSaveAboutLiveData()
        setupNextBtnListener()
    }

    private fun observeAboutLiveData() {
        viewModel.aboutLiveData.observe(viewLifecycleOwner) { about ->
            if (about != null && about.isNotBlank()) {
                binding.etRegisterAbout.setText(about)
            }
        }
        viewModel.getAbout()
    }

    private fun observeSaveAboutLiveData() {
        viewModel.saveAboutUIStateLiveData.observe(viewLifecycleOwner) { saveAboutUIState ->
            if (saveAboutUIState.saved) {
                registerStepListener.onMoveToNextStep()
            } else if (saveAboutUIState.showError) {
                val title = getString(R.string.error_title_save_about)
                val message = MessageSource.getMessage(saveAboutUIState.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun setupNextBtnListener() {
        binding.btnRegisterAboutNext.setOnClickListener {
            viewModel.saveAbout(binding.etRegisterAbout.text.toString())
        }
    }
}