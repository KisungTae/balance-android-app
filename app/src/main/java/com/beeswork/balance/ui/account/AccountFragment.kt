package com.beeswork.balance.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.beeswork.balance.R
import com.beeswork.balance.ui.base.ScopeFragment
import com.beeswork.balance.ui.profile.ProfileDialog
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        llProfile.setOnClickListener {
            ProfileDialog().show(childFragmentManager, ProfileDialog.TAG)
        }
    }
}