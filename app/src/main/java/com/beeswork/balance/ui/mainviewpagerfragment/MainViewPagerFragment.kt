package com.beeswork.balance.ui.mainviewpagerfragment

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMainViewPagerBinding
import com.beeswork.balance.databinding.SnackBarNewMatchBinding
import com.beeswork.balance.databinding.SnackBarNewSwipeBinding
import com.beeswork.balance.domain.uistate.match.MatchNotificationUIState
import com.beeswork.balance.domain.uistate.swipe.SwipeNotificationUIState
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.SnackBarHelper
import com.beeswork.balance.ui.balancegamedialog.CardBalanceGameListener
import com.beeswork.balance.ui.cardfragment.CardFragment
import com.beeswork.balance.ui.common.BaseFragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class MainViewPagerFragment : BaseFragment(), KodeinAware, CardBalanceGameListener {

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
        observeSwipeNotificationUIStateLiveData()
        observeSwipeCountLiveData()
        observeMatchCountLiveData()
        observeMatchNotificationUIStateLiveData()
    }


    private suspend fun observeSwipeNotificationUIStateLiveData() {
        viewModel.swipeNotificationUIStateLiveData.await().observe(viewLifecycleOwner) { swipeNotificationUIState ->
            showNewSwipeSnackBar(swipeNotificationUIState)
        }
    }

    private suspend fun observeSwipeCountLiveData() {
        viewModel.swipeCountLiveData.await().observe(viewLifecycleOwner) { count ->
            showBadgeWithCount(MainViewPagerTabPosition.SWIPE.ordinal, count)
        }
    }

    private suspend fun observeMatchCountLiveData() {
        viewModel.matchCountLiveData.await().observe(viewLifecycleOwner) { count ->
            showBadgeWithCount(MainViewPagerTabPosition.MATCH.ordinal, count)
        }
    }

    private suspend fun observeMatchNotificationUIStateLiveData() {
        viewModel.matchNotificationUIStateLiveData.await().observe(viewLifecycleOwner) { matchNotificationUIState ->
            showNewMatchSnackBar(matchNotificationUIState)
        }
    }

    private fun showBadgeWithCount(position: Int, count: Long?) {
        if (count == null) {
            return
        }
        if (count > 0) {
            showTabBadge(position, count)
        } else {
            hideTabBadge(position)
        }
    }

    private fun setupViewPager() {
        mainViewPagerAdapter = MainViewPagerAdapter(childFragmentManager, lifecycle, this@MainViewPagerFragment)
        binding.vpMain.adapter = mainViewPagerAdapter
        binding.vpMain.offscreenPageLimit = MainViewPagerTabPosition.values().size
        binding.vpMain.setPageTransformer(null)
        binding.vpMain.setCurrentItem(MainViewPagerTabPosition.CARD.ordinal, false)
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
                MainViewPagerTabPosition.CARD.ordinal -> tab.setIcon(R.drawable.ic_baseline_thumb_up)
                MainViewPagerTabPosition.SWIPE.ordinal -> tab.setIcon(R.drawable.ic_baseline_favorite)
                MainViewPagerTabPosition.MATCH.ordinal -> tab.setIcon(R.drawable.ic_baseline_chat_bubble)
                MainViewPagerTabPosition.ACCOUNT.ordinal -> tab.setIcon(R.drawable.round_account)
            }
        }
        tabLayoutMediator.attach()
    }

    private fun showTabBadge(position: Int, count: Long) {
        binding.tlMain.getTabAt(position)?.let { tab ->
            val badge = tab.orCreateBadge
            badge.number = count.toInt()
            badge.maxCharacterCount = BADGE_MAX_CHAR_COUNT
            badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.WarningRed)
            badge.isVisible = true
        }
    }

    private fun hideTabBadge(position: Int) {
        binding.tlMain.getTabAt(position)?.let { tab ->
            tab.badge?.let { badgeDrawable ->
                badgeDrawable.isVisible = false
            }
        }
    }

    private fun showNewSwipeSnackBar(swipeNotificationUIState: SwipeNotificationUIState) {
        val snackBarNewSwipeBinding = SnackBarNewSwipeBinding.inflate(layoutInflater)
        val topPadding = resources.getDimension(R.dimen.snack_bar_top_padding).toInt()
        val snackBar = SnackBarHelper.make(requireView(), Gravity.TOP, topPadding, 0, snackBarNewSwipeBinding.root)
        snackBar.view.setOnClickListener {
            binding.tlMain.getTabAt(MainViewPagerTabPosition.SWIPE.ordinal)?.select()
        }
        setupProfilePhoto(swipeNotificationUIState.swiperProfilePhotoUrl, snackBarNewSwipeBinding.ivNewSwipeSnackBarProfilePhoto)

        if (swipeNotificationUIState.clicked) {
            snackBarNewSwipeBinding.tvNewSwipeSnackBarTitle.text = getString(R.string.title_new_swipe_clicked)
            snackBarNewSwipeBinding.tvNewSwipeSnackBarMessage.text = getString(R.string.body_new_swipe_clicked)
        } else {
            snackBarNewSwipeBinding.tvNewSwipeSnackBarTitle.text = getString(R.string.title_new_swipe_liked)
            snackBarNewSwipeBinding.tvNewSwipeSnackBarMessage.text = getString(R.string.body_new_swipe_liked)
        }
        snackBarNewSwipeBinding.tvNewSwipeSnackBarClose.setOnClickListener {
            snackBar.dismiss()
        }
        snackBar.show()
    }

    private fun showNewMatchSnackBar(matchNotificationUIState: MatchNotificationUIState) {
        val snackBarNewSwipeBinding = SnackBarNewMatchBinding.inflate(layoutInflater)
        val topPadding = resources.getDimension(R.dimen.snack_bar_top_padding).toInt()
        val snackBar = SnackBarHelper.make(requireView(), Gravity.TOP, topPadding, 0, snackBarNewSwipeBinding.root)
        snackBar.view.setOnClickListener {
            binding.tlMain.getTabAt(MainViewPagerTabPosition.MATCH.ordinal)?.select()
            snackBar.dismiss()
        }
        snackBarNewSwipeBinding.tvNewMatchSnackBarClose.setOnClickListener {
            snackBar.dismiss()
        }
        setupProfilePhoto(matchNotificationUIState.swiperProfilePhotoUrl, snackBarNewSwipeBinding.ivNewMatchSnackBarSwiper)
        setupProfilePhoto(matchNotificationUIState.swipedProfilePhotoUrl, snackBarNewSwipeBinding.ivNewMatchSnackBarSwiped)
        snackBar.show()
    }

    private fun setupProfilePhoto(photoUrl: String?, imageView: ImageView) {
        Glide.with(requireContext())
            .load(photoUrl)
            .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
            .into(imageView)
    }

    override fun onGoToSwipeSelected() {
        binding.tlMain.getTabAt(MainViewPagerTabPosition.SWIPE.ordinal)?.select()
    }

    override fun onGoToMatchSelected() {
        binding.tlMain.getTabAt(MainViewPagerTabPosition.MATCH.ordinal)?.select()
    }

    companion object {
        const val TAG = "mainViewPagerFragment"
        const val BADGE_MAX_CHAR_COUNT = 3
    }



}