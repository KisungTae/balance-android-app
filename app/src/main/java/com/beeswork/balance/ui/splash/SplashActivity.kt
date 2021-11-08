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
import com.beeswork.balance.ui.registeractivity.RegisterActivity
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
        bindUI()
        viewModel.loginWithRefreshToken()
    }

    private fun bindUI() = lifecycleScope.launch {
        viewModel.loginWithRefreshToken.observe(this@SplashActivity) {
            when {
                it.isSuccess() -> {
////                    TODO: remove me
                        finishToActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                    if (it.data?.profileExists == true)
//                        finishToActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                    else
//                        finishToActivity(Intent(this@SplashActivity, RegisterActivity::class.java))
                }
                it.isError() -> finishToActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
        }
    }

}