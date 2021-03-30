package com.beeswork.balance.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentAccountBinding
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.profile.ProfileDialog
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

class AccountFragment: ScopeFragment(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
//        llProfile.setOnClickListener {
//            ProfileDialog().show(childFragmentManager, ProfileDialog.TAG)
//        }
    }
}