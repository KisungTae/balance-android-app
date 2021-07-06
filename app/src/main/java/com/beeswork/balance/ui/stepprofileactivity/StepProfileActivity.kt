package com.beeswork.balance.ui.stepprofileactivity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.content.ContextCompat
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ActivityLoginBinding
import com.beeswork.balance.databinding.ActivityStepProfileBinding
import com.beeswork.balance.ui.common.BaseActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class StepProfileActivity: BaseActivity(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: ActivityStepProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.Primary)
        binding = ActivityStepProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}