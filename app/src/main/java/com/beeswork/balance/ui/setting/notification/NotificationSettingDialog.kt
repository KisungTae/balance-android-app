package com.beeswork.balance.ui.setting.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogEmailSettingBinding
import com.beeswork.balance.databinding.DialogNotificationSettingBinding
import com.beeswork.balance.ui.common.BaseDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class NotificationSettingDialog: BaseDialog(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var binding: DialogNotificationSettingBinding
    private lateinit var viewModel: NotificationSettingViewModel

    private val viewModelFactory: NotificationSettingViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogNotificationSettingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NotificationSettingViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        binding.btnNotificationSettingBack.setOnClickListener { dismiss() }
        binding.scChatMessageNotification.setOnCheckedChangeListener { _, checked ->
            println("checked: $checked")
        }
    }

    companion object {
        const val TAG = "notificationSettingDialog"
    }
}