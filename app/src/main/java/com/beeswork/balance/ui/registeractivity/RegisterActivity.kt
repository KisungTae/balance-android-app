package com.beeswork.balance.ui.registeractivity

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ActivityRegisterBinding
import com.beeswork.balance.ui.common.BaseActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

class RegisterActivity: BaseActivity(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.Primary)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}