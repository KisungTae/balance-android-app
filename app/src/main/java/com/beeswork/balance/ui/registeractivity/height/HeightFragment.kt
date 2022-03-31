package com.beeswork.balance.ui.registeractivity.height

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentGenderBinding
import com.beeswork.balance.databinding.FragmentHeightBinding
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.registeractivity.RegisterActivity
import com.beeswork.balance.ui.registeractivity.gender.GenderViewModel
import com.beeswork.balance.ui.registeractivity.gender.GenderViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class HeightFragment: Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentHeightBinding
    private lateinit var viewModel: HeightViewModel
    private val viewModelFactory: HeightViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHeightBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HeightViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeHeightLiveData()
        observeSaveHeightLiveData()
        setupNextBtnListener()
        setupHeightNumberPicker()
    }

    private fun setupHeightNumberPicker() {
        binding.npRegisterHeight.maxValue = MAX_HEIGHT
        binding.npRegisterHeight.minValue = MIN_HEIGHT
    }

    private fun observeHeightLiveData() {
        viewModel.heightLiveData.observe(viewLifecycleOwner) { height ->
            if (height == null) {
                binding.npRegisterHeight.value = DEFAULT_HEIGHT
            } else {
                binding.npRegisterHeight.value = height
            }
        }
        viewModel.getHeight()
    }

    private fun observeSaveHeightLiveData() {
        viewModel.saveHeightUIStateLiveData.observe(viewLifecycleOwner) { saveHeightUIState ->
            if (saveHeightUIState.saved) {
                activity?.let { _activity ->
                    (_activity as RegisterActivity).moveToNextTab()
                }
            } else if (saveHeightUIState.showError) {
                val title = getString(R.string.error_title_save_height)
                val message = MessageSource.getMessage(requireContext(), saveHeightUIState.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun setupNextBtnListener() {
        binding.btnRegisterHeightNext.setOnClickListener {
            viewModel.saveHeight(binding.npRegisterHeight.value)
        }
    }

    companion object {
        const val MAX_HEIGHT = 300
        const val MIN_HEIGHT = 100
        const val DEFAULT_HEIGHT = 150
    }

}