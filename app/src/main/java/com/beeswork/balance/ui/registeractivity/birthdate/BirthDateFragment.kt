package com.beeswork.balance.ui.registeractivity.birthdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentBirthDateBinding
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.registeractivity.BaseRegisterStepFragment
import com.beeswork.balance.ui.registeractivity.RegisterActivity
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class BirthDateFragment : BaseRegisterStepFragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentBirthDateBinding
    private lateinit var viewModel: BirthDateViewModel
    private val viewModelFactory: BirthDateViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBirthDateBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BirthDateViewModel::class.java)
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
                binding.dpRegisterBirthDate.updateDate(birthDate.year, birthDate.monthValue - 1, birthDate.dayOfMonth)
            }
        }
        viewModel.getBirthDate()
    }

    private fun observeSaveBirthDateLiveData() {
        viewModel.saveBirthDateUIStateLiveData.observe(viewLifecycleOwner) { saveBirthDateUIState ->
            if (saveBirthDateUIState.saved) {
                moveToNextTab()
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