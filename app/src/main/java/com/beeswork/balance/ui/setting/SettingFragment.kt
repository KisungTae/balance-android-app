package com.beeswork.balance.ui.setting

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.beeswork.balance.R
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
import java.util.*

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
        setupListeners()
        observeLocationLiveData()
        observeEmailLiveData()
        observeDeleteAccountLiveData()
    }

    private fun observeDeleteAccountLiveData() {
        viewModel.deleteAccountLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> binding.llSettingLoading.visibility = View.VISIBLE
                it.isSuccess() -> popToLoginFragment(null)
                it.isError() -> {
                    binding.llSettingLoading.visibility = View.GONE
                    val errorTitle = getString(R.string.error_title_delete_account)
                    showErrorDialog(it.error, errorTitle, it.errorMessage)
                }
            }
        }
    }

    private suspend fun observeEmailLiveData() {
        viewModel.email.await().observe(viewLifecycleOwner) { email ->
            email?.let { _email -> binding.tvSettingEmail.text = _email }
        }
    }

    private suspend fun observeLocationLiveData() {
        viewModel.location.await().observe(viewLifecycleOwner) { location ->
            location?.let { _location ->
                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                val address = geoCoder.getFromLocation(_location.latitude, _location.longitude, 1)
                binding.tvSettingLocation.text = address[0].getAddressLine(0)
            }
        }
    }

    private fun setupListeners() {
        binding.btnSettingBack.setOnClickListener { popBackStack(MainViewPagerFragment.TAG) }
        binding.btnSettingEmail.setOnClickListener {
            EmailSettingDialog().show(childFragmentManager, EmailSettingDialog.TAG)
        }
        binding.btnSettingNotification.setOnClickListener {
            PushSettingDialog().show(childFragmentManager, PushSettingDialog.TAG)
        }
        binding.btnSettingLocation.setOnClickListener {
            if (binding.tvSettingLocation.text.isEmpty()) return@setOnClickListener
            Toast.makeText(requireContext(), binding.tvSettingLocation.text, Toast.LENGTH_SHORT).show()
        }
        binding.btnSettingDeleteAccount.setOnClickListener { viewModel.deleteAccount() }
        binding.btnSettingLogout.setOnClickListener {

        }
    }


}