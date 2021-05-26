package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentProfileBinding
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment: BaseFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: ProfileViewModelFactory by instance()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupPhotoPickerRecyclerView()
    }

    private fun setupPhotoPickerRecyclerView() {

    }

    private fun setupToolBar() {
        binding.tbProfile.inflateMenu(R.menu.profile_tool_bar)
        binding.tbProfile.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miProfileSave -> {
                    println("click save")
                    true
                }
                else -> false
            }
        }
        binding.btnProfileBack.setOnClickListener { popBackStack() }
    }

    private fun popBackStack() {
        requireActivity().supportFragmentManager.popBackStack(
            MainViewPagerFragment.TAG,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}