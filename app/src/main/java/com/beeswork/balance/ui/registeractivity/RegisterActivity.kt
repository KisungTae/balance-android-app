package com.beeswork.balance.ui.registeractivity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ActivityRegisterBinding
import com.beeswork.balance.internal.util.hideKeyboard
import com.beeswork.balance.ui.common.BaseLocationActivity
import com.beeswork.balance.ui.common.LocationPermissionListener
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.common.LocationRequestListener
import com.beeswork.balance.ui.registeractivity.location.LocationFragment
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

class RegisterActivity : BaseLocationActivity(false), LocationPermissionListener, RegisterStepListener, LocationRequestListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private val viewModelFactory: RegisterViewModelFactory by instance()
    private lateinit var registerViewPagerAdapter: RegisterViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RegisterViewModel::class.java)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.Primary)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(viewModel, this@RegisterActivity)
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
        registerViewPagerAdapter = RegisterViewPagerAdapter(supportFragmentManager, lifecycle, this@RegisterActivity, this@RegisterActivity)
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

        //todo: remove me
        binding.vpRegister.currentItem = RegisterViewPagerTabPosition.ABOUT.ordinal
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

    override fun onLocationPermissionChanged(granted: Boolean) {
        val locationFragment = supportFragmentManager.findFragmentByTag("f${RegisterViewPagerTabPosition.LOCATION.ordinal}")
        if (locationFragment != null && locationFragment is LocationFragment) {
            locationFragment.onLocationPermissionChanged(granted)
        }
    }

    override fun onMoveToNextStep() {
        moveToNextTab()
    }

    override fun onRequestLocationPermission() {
        setupLocationManager()
    }

    override fun onCheckLocationPermission() {
        doCheckLocationPermission()
    }
}