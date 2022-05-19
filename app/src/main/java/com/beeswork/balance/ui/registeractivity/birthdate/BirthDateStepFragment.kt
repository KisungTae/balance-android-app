package com.beeswork.balance.ui.registeractivity.birthdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentBirthDateBinding
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class BirthDateStepFragment(
    private val registerStepListener: RegisterStepListener
) : BaseFragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentBirthDateBinding
    private lateinit var viewModel: BirthDateStepViewModel
    private val viewModelFactory: BirthDateStepViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBirthDateBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BirthDateStepViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeBirthDateLiveData()
        observeSaveBirthDateLiveData()
        setupNextBtnListener()
    }

    private fun observeBirthDateLiveData() {
        viewModel.birthDateLiveData.observe(viewLifecycleOwner) { birthDate ->
            if (birthDate != null) {
                binding.dpRegisterBirthDate.updateDate(birthDate.year, birthDate.monthValue, birthDate.dayOfMonth)
            }
        }
        viewModel.getBirthDate()
    }

    private fun observeSaveBirthDateLiveData() {
        viewModel.saveBirthDateUIStateLiveData.observe(viewLifecycleOwner) { saveBirthDateUIState ->
            if (saveBirthDateUIState.saved) {
                registerStepListener.onMoveToNextStep()
            } else if (saveBirthDateUIState.showError) {
                val title = getString(R.string.error_title_save_birthDate)
                val message = MessageSource.getMessage(requireContext(), saveBirthDateUIState.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun setupNextBtnListener() {
        binding.btnRegisterBirthDateNext.setOnClickListener {
            viewModel.saveBirthDate(
                binding.dpRegisterBirthDate.year,
                binding.dpRegisterBirthDate.month + 1,
                binding.dpRegisterBirthDate.dayOfMonth
            )
        }
    }
}