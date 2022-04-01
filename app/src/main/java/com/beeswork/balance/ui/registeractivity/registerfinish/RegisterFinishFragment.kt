package com.beeswork.balance.ui.registeractivity.registerfinish

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentGenderBinding
import com.beeswork.balance.databinding.FragmentRegisterFinishBinding
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.ui.mainactivity.MainActivity
import com.beeswork.balance.ui.registeractivity.RegisterActivity
import com.beeswork.balance.ui.registeractivity.RegisterViewPagerAdapter
import com.beeswork.balance.ui.registeractivity.photo.PhotoViewModel
import com.beeswork.balance.ui.registeractivity.photo.PhotoViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class RegisterFinishFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentRegisterFinishBinding
    private lateinit var viewModel: RegisterFinishViewModel
    private val viewModelFactory: RegisterFinishViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterFinishBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RegisterFinishViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeSaveProfileUIStateLiveData()
        setupRegisterFinishRetryBtnListener()
        viewModel.saveProfile()
    }

    private fun setupRegisterFinishRetryBtnListener() {
        binding.btnRegisterFinishRetry.setOnClickListener {
            viewModel.saveProfile()
        }
    }

    private fun observeSaveProfileUIStateLiveData() {
        viewModel.saveProfileUIStateLiveData.observe(viewLifecycleOwner) { saveProfileUIState ->
            when {
                saveProfileUIState.showLoading -> {
                    binding.llRegisterFinishLoadingWrapper.visibility = View.VISIBLE
                    binding.llRegisterFinishErrorWrapper.visibility = View.GONE
                    binding.btnRegisterFinishRetry.visibility = View.INVISIBLE
                    binding.btnRegisterFinishRetry.isEnabled = false
                }
                saveProfileUIState.saved -> {
                    activity?.let { _activity ->
                        if (_activity is RegisterActivity) {
                            _activity.moveToNextTab()
                        }
                    }
                }
                else -> {
                    binding.llRegisterFinishLoadingWrapper.visibility = View.GONE
                    binding.llRegisterFinishErrorWrapper.visibility = View.VISIBLE
                    binding.btnRegisterFinishRetry.visibility = View.VISIBLE
                    binding.btnRegisterFinishRetry.isEnabled = true

                    val title = getString(R.string.error_title_save_profile)
                    val message = MessageSource.getMessage(requireContext(), saveProfileUIState.exception)
                    binding.tvRegisterFinishErrorTitle.text = title
                    binding.tvRegisterFinishErrorMessage.text = message
                }
            }
        }
    }


}