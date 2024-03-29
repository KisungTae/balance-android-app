package com.beeswork.balance.ui.registeractivity.gender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentGenderBinding
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class GenderStepFragment(
    private val registerStepListener: RegisterStepListener
): BaseFragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentGenderBinding
    private lateinit var viewModel: GenderStepViewModel
    private val viewModelFactory: GenderStepViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGenderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(GenderStepViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeGenderLiveData()
        observeSaveGenderLiveData()
        setupNextBtnListener()
    }

    private fun observeGenderLiveData() {
        viewModel.genderLiveData.observe(viewLifecycleOwner) { gender ->
            if (gender != null) {
                binding.rbRegisterFemale.isChecked = gender == Gender.FEMALE
                binding.rbRegisterMale.isChecked = gender == Gender.MALE
            }
        }
        viewModel.getGender()
    }

    private fun observeSaveGenderLiveData() {
        viewModel.saveGenderUIStateLiveData.observe(viewLifecycleOwner) { saveGenderUIState ->
            if (saveGenderUIState.saved) {
                registerStepListener.onMoveToNextStep()
            } else if (saveGenderUIState.showError) {
                val title = getString(R.string.error_title_save_gender)
                val message = MessageSource.getMessage(saveGenderUIState.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun setupNextBtnListener() {
        binding.btnRegisterGenderNext.setOnClickListener {
            val gender = when (binding.rgRegisterGender.checkedRadioButtonId) {
                R.id.rbRegisterFemale -> {
                    Gender.FEMALE
                }
                R.id.rbRegisterMale -> {
                    Gender.MALE
                }
                else -> {
                    null
                }
            }
            viewModel.saveGender(gender)
        }
    }

}