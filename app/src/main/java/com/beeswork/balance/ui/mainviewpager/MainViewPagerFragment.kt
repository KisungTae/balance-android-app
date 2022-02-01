package com.beeswork.balance.ui.mainviewpager

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMainViewPagerBinding
import com.beeswork.balance.databinding.SnackBarNewClickBinding
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.SnackBarHelper
import com.beeswork.balance.ui.click.ClickDomain
import com.beeswork.balance.ui.common.BaseFragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class MainViewPagerFragment : BaseFragment(), KodeinAware {

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
    }

    private fun bindUI() = lifecycleScope.launch {
        setupViewPager()
        setupViewPagerTab()
        setupUnreadMatchCountObserver()
        setupClickCountObserver()
    }

    private suspend fun setupClickCountObserver() {
        viewModel.clickCount.await().observe(viewLifecycleOwner) { count ->
            showBadgeWithCount(MainViewPagerTabPosition.CLICK.ordinal, count)
        }
    }

    private suspend fun setupUnreadMatchCountObserver() {
        viewModel.unreadMatchCount.await().observe(viewLifecycleOwner) { count ->
            showBadgeWithCount(MainViewPagerTabPosition.MATCH.ordinal, count)
        }
    }

    private fun showBadgeWithCount(position: Int, count: Int) {
        if (count > 0) showTabBadge(position, count)
        else hideTabBadge(position)
    }

    private fun setupViewPager() {
        mainViewPagerAdapter = MainViewPagerAdapter(childFragmentManager, lifecycle)
        binding.vpMain.adapter = mainViewPagerAdapter
        binding.vpMain.offscreenPageLimit = MainViewPagerTabPosition.values().size
        binding.vpMain.setPageTransformer(null)
        binding.vpMain.setCurrentItem(MainViewPagerTabPosition.SWIPE.ordinal, false)
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
                MainViewPagerTabPosition.ACCOUNT.ordinal -> tab.setIcon(R.drawable.ic_baseline_account_circle)
                MainViewPagerTabPosition.SWIPE.ordinal -> tab.setIcon(R.drawable.ic_baseline_thumb_up)
                MainViewPagerTabPosition.CLICK.ordinal -> tab.setIcon(R.drawable.ic_baseline_favorite)
                MainViewPagerTabPosition.MATCH.ordinal -> tab.setIcon(R.drawable.ic_baseline_chat_bubble)
            }
        }
        tabLayoutMediator.attach()
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

//    private fun showNewClickSnackBar(clickDomain: ClickDomain) {
//        val binding = SnackBarNewClickBinding.inflate(layoutInflater)
//        val topPadding = resources.getDimension(R.dimen.snack_bar_top_padding).toInt()
//        val snackBar = SnackBarHelper.make(requireView(), Gravity.TOP, topPadding, 0, binding.root)
//        snackBar.view.setOnClickListener { newClickSnackBar?.dismiss() }
//
//        val swiperProfilePhoto = EndPoint.ofPhoto(clickDomain.swiperId, clickDomain.profilePhotoKey)
//        val swiperProfilePhoto = R.drawable.person2
//
//        Glide.with(requireContext())
//            .load(swiperProfilePhoto)
//            .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
//            .into(binding.ivNewClickSnackBarSwiper)
//
//        snackBar.addCallback(object : Snackbar.Callback() {
//            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
//                super.onDismissed(transientBottomBar, event)
//                if (transientBottomBar === newClickSnackBar) newClickSnackBar = null
//            }
//        })
//
//        newClickSnackBar?.dismiss()
//        newClickSnackBar = snackBar
//        snackBar.show()
//    }

    companion object {
        const val TAG = "mainViewPagerFragment"
        const val BADGE_MAX_CHAR_COUNT = 3
    }


}