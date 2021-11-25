package com.beeswork.balance.ui.setting.push

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogPushSettingBinding
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.ui.common.BaseDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class PushSettingDialog : BaseDialog(), KodeinAware, ErrorDialog.OnDismissListener, ErrorDialog.OnRetryListener {

    override val kodein by closestKodein()

    private lateinit var binding: DialogPushSettingBinding
    private lateinit var viewModel: PushSettingViewModel

    private val viewModelFactory: PushSettingViewModelFactory by instance()
    private val errorDialogs = mutableMapOf<UUID, ErrorDialog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogPushSettingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PushSettingViewModel::class.java)
        bindUI()
        viewModel.fetchPushSetting()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupListeners()
        observeFetchPushSettingLiveData()
        observeSavePushSettingLiveData()
    }

    private fun observeSavePushSettingLiveData() {
        viewModel.savePushSettingLiveData.observe(viewLifecycleOwner) {
            when {
                it.isSuccess() -> showSavePushSettingSuccess()
                it.isLoading() -> {
                    disableEdit()
                    showLoading()
                }
                it.isError() -> showSavePushSettingError(it.data, it.error, it.errorMessage)
            }
        }
    }

    private fun showSavePushSettingSuccess() {
        val message = getString(R.string.save_push_setting_success_message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        hideLoadingAndRefreshBtn()
        enableEdit()
    }

    private fun showSavePushSettingError(pushSettingDomain: PushSettingDomain?, error: String?, errorMessage: String?) {
        setupPushSetting(pushSettingDomain)
        enableEdit()
        hideLoadingAndRefreshBtn()
        val errorTitle = getString(R.string.error_title_save_push_setting)
        ErrorDialog.show(error, errorTitle, errorMessage, childFragmentManager)
    }

    private fun observeFetchPushSettingLiveData() {
        viewModel.fetchPushSettingLiveData.observe(viewLifecycleOwner) {
            when {
                it.isSuccess() -> showFetchPushSettingSuccess(it.data)
                it.isLoading() -> {
                    disableEdit()
                    showLoading()
                    setupPushSetting(it.data)
                }
                it.isError() -> showFetchPushSettingError(it.error, it.errorMessage)
            }
        }
    }

    private fun disableEdit() {
        binding.scMatchPush.isEnabled = false
        binding.scChatMessagePush.isEnabled = false
        binding.scClickedPush.isEnabled = false
        binding.scEmailPush.isEnabled = false
        binding.btnPushSettingSave.isEnabled = false
    }

    private fun enableEdit() {
        binding.scMatchPush.isEnabled = true
        binding.scChatMessagePush.isEnabled = true
        binding.scClickedPush.isEnabled = true
        binding.scEmailPush.isEnabled = true
        binding.btnPushSettingSave.isEnabled = true
    }

    private fun showLoading() {
        binding.btnPushSettingRefresh.visibility = View.GONE
        binding.skvPushSettingLoading.visibility = View.VISIBLE
    }

    private fun hideLoadingAndRefreshBtn() {
        binding.btnPushSettingRefresh.visibility = View.INVISIBLE
        binding.skvPushSettingLoading.visibility = View.GONE
    }

    private fun showRefreshBtn() {
        binding.btnPushSettingRefresh.visibility = View.VISIBLE
        binding.skvPushSettingLoading.visibility = View.GONE
    }

    private fun showFetchPushSettingSuccess(pushSettingDomain: PushSettingDomain?) {
        hideLoadingAndRefreshBtn()
        enableEdit()
        setupPushSetting(pushSettingDomain)
    }

    private fun setupPushSetting(pushSettingDomain: PushSettingDomain?) {
        pushSettingDomain?.let { _pushSettingDomain ->
            binding.scMatchPush.isChecked = _pushSettingDomain.matchPush
            binding.scEmailPush.isChecked = _pushSettingDomain.emailPush
            binding.scClickedPush.isChecked = _pushSettingDomain.clickedPush
            binding.scChatMessagePush.isChecked = _pushSettingDomain.chatMessagePush
        }
    }

    private fun showFetchPushSettingError(error: String?, errorMessage: String?) {
        showRefreshBtn()
        disableEdit()
        val errorTitle = getString(R.string.error_title_fetch_push_setting)
        ErrorDialog.show(error, errorTitle, errorMessage, RequestCode.FETCH_PUSH_SETTING, this, childFragmentManager)
    }


    private fun setupListeners() {
        binding.btnNotificationSettingBack.setOnClickListener { dismiss() }
        binding.btnPushSettingSave.setOnClickListener {
            viewModel.savePushSetting(
                binding.scMatchPush.isChecked,
                binding.scClickedPush.isChecked,
                binding.scChatMessagePush.isChecked,
                binding.scEmailPush.isChecked
            )
        }
        binding.btnPushSettingRefresh.setOnClickListener { viewModel.fetchPushSetting() }
    }

    companion object {
        const val TAG = "notificationSettingDialog"
    }

    override fun onDismissErrorDialog(id: UUID?) {
        id?.let { _id -> errorDialogs.remove(_id) }
    }

    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.FETCH_PUSH_SETTING -> viewModel.fetchPushSetting()
        }
    }
}