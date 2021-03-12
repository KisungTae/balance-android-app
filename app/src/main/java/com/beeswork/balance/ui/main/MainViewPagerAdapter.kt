package com.beeswork.balance.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beeswork.balance.ui.account.AccountFragment
import com.beeswork.balance.ui.clicker.ClickerFragment
import com.beeswork.balance.ui.match.MatchFragment
import com.beeswork.balance.ui.swipe.SwipeFragment

class MainViewPagerAdapter(
    activity: AppCompatActivity
): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return FRAGMENT_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FragmentPosition.ACCOUNT.ordinal -> AccountFragment()
            FragmentPosition.SWIPE.ordinal -> SwipeFragment()
            FragmentPosition.CLICKER.ordinal -> ClickerFragment()
            FragmentPosition.MATCH.ordinal -> MatchFragment()
            else -> SwipeFragment()
        }
    }

    companion object {
        const val FRAGMENT_COUNT = 4
    }

    enum class FragmentPosition {
        ACCOUNT,
        SWIPE,
        CLICKER,
        MATCH
    }
}