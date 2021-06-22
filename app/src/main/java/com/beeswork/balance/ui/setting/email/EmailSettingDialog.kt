package com.beeswork.balance.ui.setting.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogEmailSettingBinding
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.ui.common.BaseDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class EmailSettingDialog : BaseDialog(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var binding: DialogEmailSettingBinding
    private lateinit var viewModel: EmailSettingViewModel

    private val viewModelFactory: EmailSettingViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogEmailSettingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EmailSettingViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupListeners()
        viewModel.fetchEmail()
        observeSaveEmailLiveData()
        observeEmailLiveData()
    }

    private suspend fun observeEmailLiveData() {
        viewModel.emailLiveData.await().observe(viewLifecycleOwner) { email ->
            if (binding.etEmailSettingEmail.text.toString() != email)
                binding.etEmailSettingEmail.setText(email)
        }
    }

    private fun observeSaveEmailLiveData() {
        viewModel.saveEmailLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading()
                it.isError() -> {
                    if (it.error == ExceptionCode.INVALID_EMAIL_EXCEPTION) {
                        setupErrorMessage(it.error, it.errorMessage, binding.tvEmailSettingError)
                    } else {
                        val errorTitle = getString(R.string.error_title_save_email)
                        showErrorDialog(it.error, errorTitle, it.errorMessage)
                        binding.tvEmailSettingError.visibility = View.GONE
                    }
                    hideLoading()
                }
                else -> {
                    val message = getString(R.string.save_email_success_message)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    hideLoading()
                }
            }
        }
    }

    private fun showLoading() {
        binding.btnEmailSettingSave.visibility = View.GONE
        binding.llEmailSettingLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.btnEmailSettingSave.visibility = View.VISIBLE
        binding.llEmailSettingLoading.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.btnEmailSettingBack.setOnClickListener { dismiss() }
        binding.btnEmailSettingSave.setOnClickListener {
            viewModel.saveEmail(binding.etEmailSettingEmail.text.toString())
        }
    }

    companion object {
        const val TAG = "emailSettingDialog"
    }
}