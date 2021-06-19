package com.beeswork.balance.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentAccountBinding
import com.beeswork.balance.databinding.FragmentSettingBinding
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.beeswork.balance.ui.setting.email.EmailSettingDialog
import kotlinx.coroutines.launch

class SettingFragment : BaseFragment() {

//  email, location, push settings, contact, terms of conditions, logout, delete account, version


    private lateinit var binding: FragmentSettingBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        binding.btnSettingBack.setOnClickListener { popBackStack(MainViewPagerFragment.TAG) }
        binding.btnSettingEmail.setOnClickListener {
            EmailSettingDialog().show(childFragmentManager, EmailSettingDialog.TAG)
        }
    }


}