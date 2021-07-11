package com.beeswork.balance.ui.mainviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMainViewPagerBinding
import com.beeswork.balance.internal.constant.FragmentTabPosition
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class MainViewPagerFragment : BaseFragment(), KodeinAware, ErrorDialog.OnRetryListener {

    override val kodein by closestKodein()
    private val viewModelFactory: MainViewPagerViewModelFactory by instance()
    private lateinit var binding: FragmentMainViewPagerBinding
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private lateinit var viewModel: MainViewPagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.BackgroundWhite)
        binding = FragmentMainViewPagerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewPagerViewModel::class.java)
        bindUI()
//        viewModel.connectStomp()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupViewPager()
        setupViewPagerTab()
        setupWebSocketEventObserver()
        setupUnreadMatchCountObserver()
        setupClickCountObserver()
        viewModel.prepopulate()
    }

    private suspend fun setupClickCountObserver() {
        viewModel.clickCount.await().observe(viewLifecycleOwner) { count ->
            showBadgeWithCount(FragmentTabPosition.CLICK.ordinal, count)
        }
    }

    private suspend fun setupUnreadMatchCountObserver() {
        viewModel.unreadMatchCount.await().observe(viewLifecycleOwner) { count ->
            showBadgeWithCount(FragmentTabPosition.MATCH.ordinal, count)
        }
    }

    private fun showBadgeWithCount(position: Int, count: Int) {
        if (count > 0) showTabBadge(position, count)
        else hideTabBadge(position)
    }

    private fun setupWebSocketEventObserver() {
        viewModel.webSocketEventLiveData.observe(viewLifecycleOwner) {
            if (it.isError() && validateAccount(it.error, it.errorMessage)) ErrorDialog(
                it.error,
                getString(R.string.error_title_web_socket_disconnected),
                it.errorMessage,
                RequestCode.CONNECT_TO_WEB_SOCKET,
                this@MainViewPagerFragment,
                null
            ).show(childFragmentManager, FetchErrorDialog.TAG)
        }
    }

    private fun setupViewPager() {
        mainViewPagerAdapter = MainViewPagerAdapter(childFragmentManager, lifecycle)
        binding.vpMain.adapter = mainViewPagerAdapter
        binding.vpMain.offscreenPageLimit = FragmentTabPosition.values().size
        binding.vpMain.setPageTransformer(null)
        binding.vpMain.setCurrentItem(FragmentTabPosition.SWIPE.ordinal, false)
        binding.vpMain.isUserInputEnabled = false
        binding.vpMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mainViewPagerAdapter.getFragmentAt(position).onFragmentSelected()
            }
        })

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
                FragmentTabPosition.SWIPE.ordinal -> tab.setIcon(R.drawable.ic_baseline_thumb_up)
                FragmentTabPosition.CLICK.ordinal -> tab.setIcon(R.drawable.ic_baseline_favorite)
                FragmentTabPosition.MATCH.ordinal -> tab.setIcon(R.drawable.ic_baseline_chat_bubble)
            }
        }
        tabLayoutMediator.attach()
    }

    override fun onResume() {
        super.onResume()
//        viewModel.connectStomp()
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnectStomp()
    }

    private fun showTabBadge(position: Int, count: Int?) {
        binding.tlMain.getTabAt(position)?.let {
            val badge = it.orCreateBadge
            count?.let { badge.number = it }
            badge.maxCharacterCount = BADGE_MAX_CHAR_COUNT
            badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.WarningRed)
            badge.isVisible = true
        }
    }

    private fun hideTabBadge(position: Int) {
        binding.tlMain.getTabAt(position)?.let { tab ->
            tab.badge?.let { badgeDrawable -> badgeDrawable.isVisible = false }
        }
    }

    override fun onRetry(requestCode: Int?) {
        requestCode?.let {
            when (it) {
                RequestCode.CONNECT_TO_WEB_SOCKET -> viewModel.connectStomp()
            }
        }
    }

    companion object {
        const val TAG = "mainViewPagerFragment"
        const val BADGE_MAX_CHAR_COUNT = 3
    }


}