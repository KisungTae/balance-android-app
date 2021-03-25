package com.beeswork.balance.ui.mainviewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.beeswork.balance.internal.constant.FragmentTabPosition
import com.beeswork.balance.ui.account.AccountFragment
import com.beeswork.balance.ui.clicker.ClickerFragment
import com.beeswork.balance.ui.match.MatchFragment
import com.beeswork.balance.ui.swipe.SwipeFragment

class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int {
        return FragmentTabPosition.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FragmentTabPosition.ACCOUNT.ordinal -> AccountFragment()
            FragmentTabPosition.SWIPE.ordinal -> SwipeFragment()
            FragmentTabPosition.CLICKER.ordinal -> ClickerFragment()
            FragmentTabPosition.MATCH.ordinal -> MatchFragment()
            else -> SwipeFragment()
        }
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }
}