package com.beeswork.balance.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ActivitySplashBinding
import com.beeswork.balance.ui.common.BaseActivity
import com.beeswork.balance.ui.loginactivity.LoginActivity
import com.beeswork.balance.ui.mainactivity.MainActivity
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class SplashActivity : BaseActivity(), KodeinAware {

    override val kodein by closestKodein()
    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel
    private val viewModelFactory: SplashViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.Primary)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SplashViewModel::class.java)
        bind()
        viewModel.login()
    }

    private fun bind() = lifecycleScope.launch {
        viewModel.loginLiveData.observe(this@SplashActivity) {
            when {
                it.isSuccess() -> moveToActivity(Intent(this@SplashActivity, MainActivity::class.java))
                it.isError() -> moveToActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
        }
    }

    private fun moveToActivity(intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this@SplashActivity.finish()
    }
}