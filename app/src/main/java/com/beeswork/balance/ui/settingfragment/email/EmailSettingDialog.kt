package com.beeswork.balance.ui.settingfragment.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogEmailSettingBinding
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.observeResource
import com.beeswork.balance.ui.common.BaseDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class EmailSettingDialog : BaseDialog(), KodeinAware, ErrorDialog.RetryListener {

    override val kodein by closestKodein()

    private lateinit var binding: DialogEmailSettingBinding
    private lateinit var viewModel: EmailSettingViewModel

    private val viewModelFactory: EmailSettingViewModelFactory by instance()

    private var loginType: LoginType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_WhiteFullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogEmailSettingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EmailSettingViewModel::class.java)
        bindUI()
        viewModel.fetchLoginType()
        viewModel.fetchEmail()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupListeners()
        observeLoginTypeLiveData()
        observeFetchEmailLiveData()
        observeSaveEmailLiveData()
    }

    private fun observeLoginTypeLiveData() {
        viewModel.loginTypeLiveData.observe(viewLifecycleOwner) { loginType ->
            this.loginType = loginType
            if (loginType != null && !loginType.isEmailEditable()) disableEdit()
        }
    }

    private fun observeFetchEmailLiveData() {
        viewModel.fetchEmailLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> showFetchEmailSuccess(resource.data)
                resource.isLoading() -> {
                    disableEdit()
                    showLoading()
                    binding.etEmailSettingEmail.setText(resource.data)
                }
                resource.isError() -> showFetchEmailError(resource.exception)
            }
        }
    }

    private fun showFetchEmailSuccess(email: String?) {
        hideLoadingAndRefreshBtn()
        enableEdit()
        binding.etEmailSettingEmail.setText(email)
    }

    private fun showFetchEmailError(exception: Throwable?) {
        showRefreshBtn()
        disableEdit()
        val title = getString(R.string.error_title_fetch_email)
        val message = MessageSource.getMessage(requireContext(), exception)
        ErrorDialog.show(title, message, RequestCode.FETCH_EMAIL, this, childFragmentManager)
    }

    private fun observeSaveEmailLiveData() {
        viewModel.saveEmailLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> {
                    showSaveEmailSuccess()
                }
                resource.isLoading() -> {
                    disableEdit()
                    showLoading()
                }
                resource.isError() -> {
                    showSaveEmailError(resource.exception)
                }
            }
        }
    }

    private fun showSaveEmailSuccess() {
        val message = getString(R.string.save_email_success_message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        enableEdit()
        hideLoadingAndRefreshBtn()
    }

    private fun showSaveEmailError(exception: Throwable?) {
        enableEdit()
        val title = getString(R.string.error_title_save_email)
        val message = MessageSource.getMessage(requireContext(), exception)
        ErrorDialog.show(title, message, childFragmentManager)
        hideLoadingAndRefreshBtn()
    }

    private fun showLoading() {
        binding.btnEmailSettingRefresh.visibility = View.GONE
        binding.skvEmailSettingLoading.visibility = View.VISIBLE
    }

    private fun hideLoadingAndRefreshBtn() {
        binding.btnEmailSettingRefresh.visibility = View.INVISIBLE
        binding.skvEmailSettingLoading.visibility = View.GONE
    }

    private fun showRefreshBtn() {
        binding.btnEmailSettingRefresh.visibility = View.VISIBLE
        binding.skvEmailSettingLoading.visibility = View.GONE
    }


    private fun disableEdit() {
        binding.etEmailSettingEmail.isEnabled = false
        binding.etEmailSettingEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
        binding.btnEmailSettingSave.isEnabled = false
    }

    private fun enableEdit() {
        if (loginType?.isEmailEditable() != false) {
            binding.etEmailSettingEmail.isEnabled = true
            binding.etEmailSettingEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextBlack))
            binding.btnEmailSettingSave.isEnabled = true
        }
    }

    private fun setupListeners() {
        binding.btnEmailSettingBack.setOnClickListener { dismiss() }
        binding.btnEmailSettingSave.setOnClickListener { saveEmail() }
        binding.btnEmailSettingRefresh.setOnClickListener { viewModel.fetchEmail() }
    }

    private fun saveEmail() {
        viewModel.saveEmail(binding.etEmailSettingEmail.text.toString())
    }


    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.FETCH_EMAIL -> viewModel.fetchEmail()
        }
    }

    companion object {
        const val TAG = "emailSettingDialog"

    }
}