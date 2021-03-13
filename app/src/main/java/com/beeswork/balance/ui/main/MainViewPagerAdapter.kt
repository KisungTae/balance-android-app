package com.beeswork.balance.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.beeswork.balance.ui.account.AccountFragment
import com.beeswork.balance.ui.chat.ChatFragment
import com.beeswork.balance.ui.clicker.ClickerFragment
import com.beeswork.balance.ui.match.MatchFragment
import com.beeswork.balance.ui.swipe.SwipeFragment

class MainViewPagerAdapter(
    activity: AppCompatActivity
): FragmentStateAdapter(activity) {

    lateinit var chatFragment: ChatFragment private set

    override fun getItemCount(): Int {
        return FRAGMENT_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FragmentPosition.ACCOUNT.ordinal -> AccountFragment()
            FragmentPosition.SWIPE.ordinal -> SwipeFragment()
            FragmentPosition.CLICKER.ordinal -> ClickerFragment()
            FragmentPosition.MATCH.ordinal -> MatchFragment()
            FragmentPosition.CHAT.ordinal -> {
                chatFragment = ChatFragment()
                chatFragment
            }
            else -> SwipeFragment()
        }
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    companion object {
        const val FRAGMENT_COUNT = 5
    }

    enum class FragmentPosition {
        ACCOUNT,
        SWIPE,
        CLICKER,
        MATCH,
        CHAT
    }
}