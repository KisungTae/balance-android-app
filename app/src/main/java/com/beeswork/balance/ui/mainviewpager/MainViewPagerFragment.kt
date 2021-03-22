package com.beeswork.balance.ui.mainviewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMainViewPagerBinding
import com.beeswork.balance.internal.constant.FragmentTabPosition
import com.google.android.material.tabs.TabLayoutMediator


class MainViewPagerFragment : Fragment() {

    private lateinit var binding: FragmentMainViewPagerBinding
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.BackgroundWhite)
        binding = FragmentMainViewPagerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewPager()
        setupViewPagerTab()
    }

    private fun setupViewPager() {
        mainViewPagerAdapter = MainViewPagerAdapter(childFragmentManager, lifecycle)
        binding.vpMain.adapter = mainViewPagerAdapter
        binding.vpMain.offscreenPageLimit = FragmentTabPosition.values().size
        binding.vpMain.setPageTransformer(null)
        binding.vpMain.setCurrentItem(FragmentTabPosition.SWIPE.ordinal, false)
        binding.vpMain.isUserInputEnabled = false
//        binding.vpMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                binding.vpMain.isUserInputEnabled = position == FragmentTabPosition.CHAT.ordinal
//                super.onPageSelected(position)
//            }
//        })

    }

    private fun setupViewPagerTab() {
        val tabLayoutMediator = TabLayoutMediator(
            binding.tlMain,
            binding.vpMain,
            false,
            false
        ) { tab, position ->
            when (position) {
                FragmentTabPosition.ACCOUNT.ordinal -> tab.setIcon(R.drawable.ic_baseline_account_circle)
                FragmentTabPosition.SWIPE.ordinal -> tab.setIcon(R.drawable.ic_baseline_favorite)
                FragmentTabPosition.CLICKER.ordinal -> tab.setIcon(R.drawable.ic_baseline_thumb_up)
                FragmentTabPosition.MATCH.ordinal -> tab.setIcon(R.drawable.ic_baseline_chat_bubble)
            }
        }
        tabLayoutMediator.attach()
    }

    private fun showTabBadge(position: Int, visible: Boolean) {
//        binding.tlMain.getTabAt(position)?.let {
//            val badge = it.orCreateBadge
//            if (visible) badge.backgroundColor = ContextCompat.getColor(applicationContext, R.color.Primary)
//            badge.isVisible = visible
//        }
    }

    companion object {
        const val TAG = "mainViewPagerFragment"
    }
}