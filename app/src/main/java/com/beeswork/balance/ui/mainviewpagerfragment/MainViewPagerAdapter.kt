package com.beeswork.balance.ui.mainviewpagerfragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.beeswork.balance.ui.accountfragment.AccountFragment
import com.beeswork.balance.ui.balancegamedialog.CardBalanceGameListener
import com.beeswork.balance.ui.swipefragment.SwipeFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.matchfragment.MatchFragment
import com.beeswork.balance.ui.cardfragment.CardFragment
import com.beeswork.balance.ui.common.LocationRequestListener

class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle,
    private val cardBalanceGameListener: CardBalanceGameListener
): FragmentStateAdapter(fragmentManager, lifeCycle) {

    private val fragments = mutableListOf<Fragment>()

    override fun getItemCount(): Int {
        return MainViewPagerTabPosition.values().size
    }


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            MainViewPagerTabPosition.CARD.ordinal -> addToFragments(position, CardFragment(cardBalanceGameListener))
            MainViewPagerTabPosition.SWIPE.ordinal -> addToFragments(position, SwipeFragment(cardBalanceGameListener))
            MainViewPagerTabPosition.MATCH.ordinal -> addToFragments(position, MatchFragment())
            MainViewPagerTabPosition.ACCOUNT.ordinal -> addToFragments(position, AccountFragment())
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