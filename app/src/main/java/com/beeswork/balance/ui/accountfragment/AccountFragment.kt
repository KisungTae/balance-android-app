package com.beeswork.balance.ui.accountfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentAccountBinding
import com.beeswork.balance.domain.uistate.profile.ProfileUIState
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment
import com.beeswork.balance.ui.profilefragment.ProfileFragment
import com.beeswork.balance.ui.settingfragment.SettingFragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AccountFragment : BaseFragment(), KodeinAware, ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: AccountViewModelFactory by instance()
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
//        observeNameLiveData()
//        observeProfilePhotoLiveData()
        setupListeners()
        observeProfileUIStateLiveData()
        viewModel.fetchProfile()
        observeEmailLiveData()
        viewModel.fetchEmail()

    }

    private fun observeEmailLiveData() {
        viewModel.emailLiveData.observe(viewLifecycleOwner) { email ->
            binding.tvAccountEmail.text = email
        }
    }

    private fun observeProfileUIStateLiveData() {
        viewModel.profileUIStateLiveData.observe(viewLifecycleOwner) { profileUIState ->
            binding.tvAccountName.text = profileUIState.name
            binding.tvAccountAge.text = profileUIState.age?.toString()
        }
    }

    private suspend fun observeProfilePhotoLiveData() {
//        viewModel.profilePhotoKeyLiveData.await().observe(viewLifecycleOwner) {
//            val profilePhotoKey = EndPoint.ofPhoto(preferenceProvider.getAccountId(), it)
//            Glide.with(requireContext())
//                .load(R.drawable.person4)
//                .apply(GlideHelper.profilePhotoGlideOptions())
//                .circleCrop()
//                .into(binding.ivAccountProfile)
//        }
    }

    private suspend fun observeNameLiveData() {
//        viewModel.nameLiveData.await().observe(viewLifecycleOwner) {
//            binding.tvAccountName.text = it ?: ""
//        }
    }

    private fun setupListeners() {
        binding.llAccountEditProfile.setOnClickListener {
            Navigator.moveToFragment(activity, ProfileFragment(), R.id.fcvMain, MainViewPagerFragment.TAG)
        }
        binding.llAccountChargePoint.setOnClickListener {
        }
        binding.llAccountSetting.setOnClickListener {
            Navigator.moveToFragment(activity, SettingFragment(), R.id.fcvMain, MainViewPagerFragment.TAG)
        }
    }

    override fun onFragmentSelected() {
        println("account fragment: onFragmentSelected")
    }
}