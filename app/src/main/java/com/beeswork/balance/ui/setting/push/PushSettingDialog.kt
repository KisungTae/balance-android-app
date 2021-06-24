package com.beeswork.balance.ui.setting.push

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.databinding.DialogPushSettingBinding
import com.beeswork.balance.ui.common.BaseDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class PushSettingDialog : BaseDialog(), KodeinAware, ErrorDialog.OnDismissListener {

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
    }

    private fun bindUI() = lifecycleScope.launch {
        setupListeners()
        observeSavePushSettings()
        observeSyncPushSettings()
        viewModel.syncPushSettings()
        observePushSettings()
    }

    private fun observeSyncPushSettings() {
        viewModel.syncPushSettingsLiveData.observe(viewLifecycleOwner) {
            if (it.isLoading()) return@observe
        }
    }

    private fun observeSavePushSettings() {
        viewModel.saveMatchPushLiveData.observe(viewLifecycleOwner) {
            onObserveSavePushSettings(it, binding.skvMatchPushLoading, binding.scMatchPush)
        }
        viewModel.saveClickedPushLiveData.observe(viewLifecycleOwner) {
            onObserveSavePushSettings(it, binding.skvClickedPushLoading, binding.scClickedPush)
        }
        viewModel.saveChatMessagePushLiveData.observe(viewLifecycleOwner) {
            onObserveSavePushSettings(it, binding.skvChatMessagePushLoading, binding.scChatMessagePush)
        }
    }

    private fun onObserveSavePushSettings(
        resource: Resource<EmptyResponse>,
        loadingView: SpinKitView,
        switchCompat: SwitchCompat
    ) {
        when {
            resource.isSuccess() -> {
                switchCompat.isEnabled = true
                loadingView.visibility = View.INVISIBLE
            }
            resource.isLoading() -> {
                switchCompat.isEnabled = false
                loadingView.visibility = View.VISIBLE
            }
            resource.isError() -> {
                switchCompat.isEnabled = true
                loadingView.visibility = View.INVISIBLE
                showErrorDialog(resource)
            }
        }
    }

    private fun showErrorDialog(resource: Resource<EmptyResponse>) {
        println("showErrorDialog")
        for (errorDialog in errorDialogs.values) {
            if (errorDialog.isErrorEqualTo(resource.error)) return
        }
        val errorDialogId = UUID.randomUUID()
        val errorDialog = ErrorDialog(
            resource.error,
            getString(R.string.error_title_save_push_setting),
            resource.errorMessage,
            this,
            errorDialogId
        )
        errorDialogs[errorDialogId] = errorDialog
        errorDialog.show(childFragmentManager, ErrorDialog.TAG)
    }

    private suspend fun observePushSettings() {
        viewModel.pushSettings.await().observe(viewLifecycleOwner) { pushSettings ->
            binding.scMatchPush.isChecked = pushSettings.matchPush
            binding.scClickedPush.isChecked = pushSettings.clickedPush
            binding.scChatMessagePush.isChecked = pushSettings.chatMessagePush
        }
    }

    private fun setupListeners() {
        binding.btnNotificationSettingBack.setOnClickListener {
            viewModel.test()
            //            dismiss()
        }
        binding.scMatchPush.setOnCheckedChangeListener { _, checked -> viewModel.saveMatchPush(checked) }
        binding.scClickedPush.setOnCheckedChangeListener { _, checked -> viewModel.saveClickedPush(checked) }
        binding.scChatMessagePush.setOnCheckedChangeListener { _, checked -> viewModel.saveChatMessagePush(checked) }
    }

    companion object {
        const val TAG = "notificationSettingDialog"
    }

    override fun onDismissErrorDialog(id: UUID?) {
        id?.let { _id -> errorDialogs.remove(_id) }
    }
}