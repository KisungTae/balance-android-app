package com.beeswork.balance.ui.registeractivity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.databinding.ActivityRegisterBinding
import com.beeswork.balance.ui.common.BaseActivity
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class RegisterActivity: BaseActivity(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: ActivityRegisterBinding

//  TODO: remove me
    val settingRepository: SettingRepository by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.Primary)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTest.setOnClickListener {
            lifecycleScope.launch {
                settingRepository.syncFCMTokenAsync()
            }

        }
    }
}