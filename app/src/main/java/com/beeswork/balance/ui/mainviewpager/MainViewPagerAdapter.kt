package com.beeswork.balance.ui.mainviewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.beeswork.balance.ui.account.AccountFragment
import com.beeswork.balance.ui.click.ClickFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.match.MatchFragment
import com.beeswork.balance.ui.swipe.SwipeFragment

class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifeCycle) {

    private val fragments = mutableListOf<Fragment>()

    override fun getItemCount(): Int {
        return MainViewPagerTabPosition.values().size
    }


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            MainViewPagerTabPosition.ACCOUNT.ordinal -> addToFragments(position, AccountFragment())
            MainViewPagerTabPosition.SWIPE.ordinal -> addToFragments(position, SwipeFragment())
            MainViewPagerTabPosition.CLICK.ordinal -> addToFragments(position, ClickFragment())
            MainViewPagerTabPosition.MATCH.ordinal -> addToFragments(position, MatchFragment())
            else -> fragments[1]
        }
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    private fun addToFragments(position: Int, fragment: Fragment): Fragment {
        fragments.add(position, fragment)
        return fragment
    }

    fun getFragmentAt(position: Int): ViewPagerChildFragment {
        return fragments[position] as ViewPagerChildFragment
    }
}