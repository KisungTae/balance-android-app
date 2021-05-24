package com.beeswork.balance.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentAccountBinding
import com.beeswork.balance.ui.click.ClickViewModel
import com.beeswork.balance.ui.click.ClickViewModelFactory
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.profile.ProfileDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AccountFragment: BaseFragment(), KodeinAware, ViewPagerChildFragment {

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
//        binding.llProfile.setOnClickListener {
//            ProfileDialog().show(childFragmentManager, ProfileDialog.TAG)
//        }
    }

    override fun onFragmentSelected() {
        println("account fragment: onFragmentSelected")
    }
}