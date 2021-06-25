package com.beeswork.balance.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentSettingBinding
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.beeswork.balance.ui.setting.email.EmailSettingDialog
import com.beeswork.balance.ui.setting.push.PushSettingDialog
import com.beeswork.balance.ui.setting.push.PushSettingViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class SettingFragment : BaseFragment(), KodeinAware {

//  email, location, push settings, contact, terms of conditions, logout, delete account, version

    override val kodein by closestKodein()

    private lateinit var binding: FragmentSettingBinding
    private lateinit var viewModel: SettingViewModel

    private val viewModelFactory: SettingViewModelFactory by instance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingViewModel::class.java)
        bindUI()
        viewModel.fetchEmail()
    }

    private fun bindUI() = lifecycleScope.launch {
        binding.btnSettingBack.setOnClickListener { popBackStack(MainViewPagerFragment.TAG) }
        binding.btnSettingEmail.setOnClickListener {
            EmailSettingDialog().show(childFragmentManager, EmailSettingDialog.TAG)
        }
        binding.btnSettingNotification.setOnClickListener {
            PushSettingDialog().show(childFragmentManager, PushSettingDialog.TAG)
        }
        viewModel.email.await().observe(viewLifecycleOwner) { email ->
            email?.let { _email -> binding.tvSettingEmail.text = _email }
        }
    }


}