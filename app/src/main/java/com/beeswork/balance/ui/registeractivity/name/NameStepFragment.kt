package com.beeswork.balance.ui.registeractivity.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentNameBinding
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class NameStepFragment(
    private val registerStepListener: RegisterStepListener
) : BaseFragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentNameBinding
    private lateinit var viewModel: NameStepViewModel
    private val viewModelFactory: NameStepViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NameStepViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeNameLiveData()
        observeSaveNameLiveData()
        setupNextBtnListener()
    }

    private fun observeNameLiveData() {
        viewModel.nameLiveData.observe(viewLifecycleOwner) { name ->
            if (name != null && name.isNotBlank()) {
                binding.etRegisterName.setText(name)
            }
        }
        viewModel.getName()
    }

    private fun observeSaveNameLiveData() {
        viewModel.saveNameUIStateLiveData.observe(viewLifecycleOwner) { saveNameUIState ->
            if (saveNameUIState.saved) {
                registerStepListener.onMoveToNextStep()
            } else if (saveNameUIState.showError) {
                val title = getString(R.string.error_title_save_name)
                val message = MessageSource.getMessage(saveNameUIState.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun setupNextBtnListener() {
        binding.btnRegisterNameNext.setOnClickListener {
            viewModel.saveName(binding.etRegisterName.text.toString())
        }
    }

}