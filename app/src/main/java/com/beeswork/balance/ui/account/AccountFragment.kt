package com.beeswork.balance.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentAccountBinding
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.beeswork.balance.ui.profile.ProfileFragment
import com.beeswork.balance.ui.setting.SettingFragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AccountFragment : BaseFragment(), KodeinAware, ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: AccountViewModelFactory by instance()
    private val preferenceProvider: PreferenceProvider by instance()

    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AccountViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeEmailLiveData()
        observeNameLiveData()
        observeProfilePhotoLiveData()
        setupListeners()
    }

    private suspend fun observeProfilePhotoLiveData() {
        viewModel.profilePhotoKeyLiveData.await().observe(viewLifecycleOwner) {
            val profilePhotoKey = EndPoint.ofPhoto(preferenceProvider.getAccountId(), it)
            Glide.with(requireContext())
                .load(R.drawable.person4)
                .apply(GlideHelper.profilePhotoGlideOptions())
                .circleCrop()
                .into(binding.ivAccountProfile)
        }
    }

    private suspend fun observeNameLiveData() {
        viewModel.nameLiveData.await().observe(viewLifecycleOwner) {
            binding.tvAccountName.text = it ?: ""
        }
    }

    private fun setupListeners() {
        binding.llAccountEditProfile.setOnClickListener {
            moveToFragment(ProfileFragment(), R.id.fcvMain, MainViewPagerFragment.TAG)
        }
        binding.llAccountChargePoint.setOnClickListener { }
        binding.llAccountSetting.setOnClickListener {
            moveToFragment(SettingFragment(), R.id.fcvMain, MainViewPagerFragment.TAG)
        }
    }

    private suspend fun observeEmailLiveData() {
        viewModel.emailLiveData.await().observe(viewLifecycleOwner) { email ->
            binding.tvAccountEmail.text = email ?: ""
        }
    }

    override fun onFragmentSelected() {
        println("account fragment: onFragmentSelected")
    }
}