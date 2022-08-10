package com.beeswork.balance.ui.settingfragment

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentSettingBinding
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.internal.util.observeResource
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment
import com.beeswork.balance.ui.settingfragment.email.EmailSettingDialog
import com.beeswork.balance.ui.settingfragment.push.PushSettingDialog
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
//        viewModel.fetchEmail()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupListeners()
        observeEmailLiveData()
        observeLocationLiveData()
        observeDeleteAccountLiveData()
    }

    private suspend fun observeEmailLiveData() {
        viewModel.emailLiveData.await().observe(viewLifecycleOwner) { email ->
            binding.tvSettingEmail.text = email
        }
    }

    private fun observeDeleteAccountLiveData() {
        viewModel.deleteAccountLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> Navigator.finishToLoginActivity(requireActivity(), null)
                resource.isLoading() -> binding.llSettingLoading.visibility = View.VISIBLE
                resource.isError() -> {
                    binding.llSettingLoading.visibility = View.GONE
                    val title = getString(R.string.error_title_delete_account)
                    val message = MessageSource.getMessage(resource.exception)
                    ErrorDialog.show(title, message, childFragmentManager)
                }
            }
        }
    }

    private suspend fun observeLocationLiveData() {
        viewModel.locationLiveData.await().observe(viewLifecycleOwner) { location ->
            location?.let { _location ->
                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                val address = geoCoder.getFromLocation(_location.latitude, _location.longitude, 1)
                binding.tvSettingLocation.text = address[0].getAddressLine(0)
            }
        }
    }

    private fun setupListeners() {
        binding.btnSettingBack.setOnClickListener {
            Navigator.popBackStack(activity, MainViewPagerFragment.TAG)
        }
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