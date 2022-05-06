package com.beeswork.balance.ui.registeractivity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ActivityRegisterBinding
import com.beeswork.balance.internal.util.hideKeyboard
import com.beeswork.balance.ui.common.BaseLocationActivity
import com.beeswork.balance.ui.common.RegisterStepListener
import kotlinx.coroutines.launch

class RegisterActivity : BaseLocationActivity(false), RegisterStepListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewPagerAdapter: RegisterViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.Primary)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupRegisterViewPager()
        setupRegisterTabLayout()
        setupListeners()
    }

    private fun setupRegisterTabLayout() {
        for (i in 1..RegisterViewPagerTabPosition.values().size) {
            binding.tlRegister.addTab(binding.tlRegister.newTab())
        }
    }

    private fun setupListeners() {
        binding.btnRegisterBack.setOnClickListener { moveToPreviousTab() }
    }

    private fun setupRegisterViewPager() {
        registerViewPagerAdapter = RegisterViewPagerAdapter(supportFragmentManager, lifecycle, this@RegisterActivity)
        binding.vpRegister.adapter = registerViewPagerAdapter
        binding.vpRegister.offscreenPageLimit = RegisterViewPagerTabPosition.values().size
        binding.vpRegister.isUserInputEnabled = false
        binding.vpRegister.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == RegisterViewPagerTabPosition.NAME.ordinal) {
                    hideBackBtn()
                } else {
                    showBackBtn()
                }
                binding.tlRegister.selectTab(binding.tlRegister.getTabAt(position))
            }
        })
    }

    private fun showBackBtn() {
        binding.btnRegisterBack.visibility = View.VISIBLE
        binding.btnRegisterBack.isEnabled = true
    }

    private fun hideBackBtn() {
        binding.btnRegisterBack.visibility = View.INVISIBLE
        binding.btnRegisterBack.isEnabled = false
    }

    private fun moveToNextTab() {
        val nextIndex = binding.vpRegister.currentItem + 1
        binding.vpRegister.currentItem = nextIndex
    }

    private fun moveToPreviousTab() {
        val previousIndex = binding.vpRegister.currentItem - 1
        binding.vpRegister.currentItem = previousIndex
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        this.hideKeyboard(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onMoveToNextStep() {
        moveToNextTab()
    }
}