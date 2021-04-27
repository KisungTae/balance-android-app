package com.beeswork.balance.ui.mainviewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.beeswork.balance.internal.constant.FragmentTabPosition
import com.beeswork.balance.ui.account.AccountFragment
import com.beeswork.balance.ui.clicker.ClickerFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.match.MatchFragment
import com.beeswork.balance.ui.swipe.SwipeFragment

class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifeCycle) {

    private val fragments = mutableListOf<Fragment>()

    override fun getItemCount(): Int {
        return FragmentTabPosition.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FragmentTabPosition.ACCOUNT.ordinal -> addToFragments(AccountFragment())
            FragmentTabPosition.SWIPE.ordinal -> addToFragments(SwipeFragment())
            FragmentTabPosition.CLICKER.ordinal -> addToFragments(ClickerFragment())
            FragmentTabPosition.MATCH.ordinal -> addToFragments(MatchFragment())
            else -> fragments[1]
        }
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    private fun addToFragments(fragment: Fragment): Fragment {
        fragments.add(fragment)
        return fragment
    }

    fun getFragmentAt(position: Int): ViewPagerChildFragment {
        return fragments[position] as ViewPagerChildFragment
    }
}