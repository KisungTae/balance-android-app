package com.beeswork.balance.ui.settingfragment.push

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
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.observeResource
import com.beeswork.balance.ui.common.BaseDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class PushSettingDialog : BaseDialog(), KodeinAware, ErrorDialog.DismissListener, ErrorDialog.RetryListener {

    override val kodein by closestKodein()

    private lateinit var binding: DialogPushSettingBinding
    private lateinit var viewModel: PushSettingViewModel

    private val viewModelFactory: PushSettingViewModelFactory by instance()
    private val errorDialogs = mutableMapOf<UUID, ErrorDialog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_WhiteFullScreen)
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
        viewModel.savePushSettingLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> {
                    showSavePushSettingSuccess()
                }
                resource.isLoading() -> {
                    disableEdit()
                    showLoading()
                }
                resource.isError() -> {
                    showSavePushSettingError(resource.data, resource.exception)
                }
            }
        }
    }

    private fun showSavePushSettingSuccess() {
        val message = getString(R.string.save_push_setting_success_message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        hideLoadingAndRefreshBtn()
        enableEdit()
    }

    private fun showSavePushSettingError(pushSettingDomain: PushSettingDomain?, exception: Throwable?) {
        setupPushSetting(pushSettingDomain)
        enableEdit()
        hideLoadingAndRefreshBtn()
        val title = getString(R.string.error_title_save_push_setting)
        val message = MessageSource.getMessage(exception)
        ErrorDialog.show(title, message, childFragmentManager)
    }

    private fun observeFetchPushSettingLiveData() {
        viewModel.fetchPushSettingLiveData.observe(viewLifecycleOwner) { resource ->
            when {
                resource.isSuccess() -> showFetchPushSettingSuccess(resource.data)
                resource.isLoading() -> {
                    disableEdit()
                    showLoading()
                    setupPushSetting(resource.data)
                }
                resource.isError() -> showFetchPushSettingError(resource.exception)
            }
        }
    }

    private fun disableEdit() {
        binding.scMatchPush.isEnabled = false
        binding.scChatMessagePush.isEnabled = false
        binding.scSwipePush.isEnabled = false
        binding.scEmailPush.isEnabled = false
        binding.btnPushSettingSave.isEnabled = false
    }

    private fun enableEdit() {
        binding.scMatchPush.isEnabled = true
        binding.scChatMessagePush.isEnabled = true
        binding.scSwipePush.isEnabled = true
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
            binding.scSwipePush.isChecked = _pushSettingDomain.swipePush
            binding.scChatMessagePush.isChecked = _pushSettingDomain.chatMessagePush
        }
    }

    private fun showFetchPushSettingError(exception: Throwable?) {
        showRefreshBtn()
        disableEdit()
        val title = getString(R.string.error_title_fetch_push_setting)
        val message = MessageSource.getMessage(exception)
        ErrorDialog.show(title, message, RequestCode.FETCH_PUSH_SETTING, this, childFragmentManager)
    }


    private fun setupListeners() {
        binding.btnNotificationSettingBack.setOnClickListener { dismiss() }
        binding.btnPushSettingSave.setOnClickListener {
            viewModel.savePushSetting(
                binding.scMatchPush.isChecked,
                binding.scSwipePush.isChecked,
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