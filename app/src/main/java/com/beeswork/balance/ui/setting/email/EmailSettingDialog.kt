package com.beeswork.balance.ui.setting.email

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
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.ui.common.BaseDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class EmailSettingDialog : BaseDialog(), KodeinAware, ErrorDialog.OnRetryListener {

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
        observeExceptionLiveData(viewModel)
        bindUI()
        viewModel.fetchLoginType()
        viewModel.fetchEmail()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupListeners()
        observeLoginTypeLiveData()
        observeFetchEmailLiveData()
        observeSaveEmailLiveData()
        observeEmailLiveData()
    }

    private fun observeLoginTypeLiveData() {
        viewModel.loginTypeLiveData.observe(viewLifecycleOwner) { loginType ->
            if (!loginType.isEmailEditable()) setAsNotEditable()
        }
    }

    private fun observeEmailLiveData() {
        viewModel.emailLiveData.observe(viewLifecycleOwner) {

        }
    }

    private fun setAsNotEditable() {
//        binding.etEmailSettingEmail.isEnabled = false
//        binding.etEmailSettingEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
//        binding.btnEmailSettingSave.isEnabled = false
    }

    private fun observeSaveEmailLiveData() {
        viewModel.saveEmailLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading()
                it.isSuccess() -> showSaveEmailSuccess()
                it.isError() -> showSaveEmailError(it.error, it.errorMessage)
                else -> println()
            }
        }
    }

    private fun showSaveEmailError(error: String?, errorMessage: String?) {
        val errorTitle = getString(R.string.error_title_save_email)
        showErrorDialog(error, errorTitle, errorMessage)
        hideLoading()
    }

    private fun showSaveEmailSuccess() {
        val message = getString(R.string.save_email_success_message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        hideLoading()
    }

    private fun showLoading() {
        binding.btnEmailSettingRefresh.visibility = View.GONE
        binding.skvEmailSettingLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.btnEmailSettingRefresh.visibility = View.INVISIBLE
        binding.skvEmailSettingLoading.visibility = View.GONE
    }

    private fun showRefreshBtn() {
        binding.btnEmailSettingRefresh.visibility = View.VISIBLE
        binding.skvEmailSettingLoading.visibility = View.GONE
    }

    private fun observeFetchEmailLiveData() {
        viewModel.fetchEmailLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading()
                it.isSuccess() -> {
                    hideLoading()
                    binding.btnEmailSettingRefresh.visibility = View.INVISIBLE
                }
                it.isError() -> {
                    hideLoading()
                    binding.btnEmailSettingRefresh.visibility = View.VISIBLE
                    val errorTitle = getString(R.string.error_title_fetch_email)
                    ErrorDialog(it.error, errorTitle, it.errorMessage, RequestCode.FETCH_EMAIL, this).show(
                        childFragmentManager,
                        ErrorDialog.TAG
                    )
                }
            }
        }
    }




    private fun showFetchEmailError() {

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