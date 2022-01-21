package com.beeswork.balance.ui.click

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentClickBinding
import com.beeswork.balance.databinding.SnackBarNewClickBinding
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.SnackBarHelper
import com.beeswork.balance.ui.common.*
import com.beeswork.balance.ui.swipe.balancegame.SwipeBalanceGameDialog
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class ClickFragment : BaseFragment(),
    KodeinAware,
    ClickPagingDataAdapter.OnClickListener,
    ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: ClickViewModelFactory by instance()
    private lateinit var viewModel: ClickViewModel
    private lateinit var binding: FragmentClickBinding
    private lateinit var clickPagingRefreshAdapter: PagingRefreshAdapter<ClickDomain, RecyclerView.ViewHolder>
    private lateinit var clickPagingDataAdapter: ClickPagingDataAdapter
    private lateinit var footerLoadStateAdapter: BalanceLoadStateAdapter
    private lateinit var clickPagingInitialPageAdapter: PagingInitialPageAdapter<ClickDomain, RecyclerView.ViewHolder>
    private var newClickSnackBar: Snackbar? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClickBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ClickViewModel::class.java)
        bindUI()
        viewModel.test()

    }

    private fun bindUI() = lifecycleScope.launch {
        setupClickRecyclerView()
        setupClickPagingInitialPageAdapter()
        observeClickInvalidation()
        observeNewClickLiveData()
        observeClickPagingData()
    }

    private suspend fun observeNewClickLiveData() {
        viewModel.newClickLiveData.await().observe(viewLifecycleOwner) { clickDomain ->
            showNewClickSnackBar(clickDomain)
        }
    }

    private fun showNewClickSnackBar(clickDomain: ClickDomain) {
        val binding = SnackBarNewClickBinding.inflate(layoutInflater)
        val topPadding = resources.getDimension(R.dimen.snack_bar_top_padding).toInt()
        val snackBar = SnackBarHelper.make(requireView(), Gravity.TOP, topPadding, 0, binding.root)
        snackBar.view.setOnClickListener { newClickSnackBar?.dismiss() }

//        val swiperProfilePhoto = EndPoint.ofPhoto(clickDomain.swiperId, clickDomain.profilePhotoKey)
        val swiperProfilePhoto = R.drawable.person2

        Glide.with(requireContext())
            .load(swiperProfilePhoto)
            .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
            .into(binding.ivNewClickSnackBarSwiper)

        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (transientBottomBar === newClickSnackBar) newClickSnackBar = null
            }
        })

        newClickSnackBar?.dismiss()
        newClickSnackBar = snackBar
        snackBar.show()
    }

    private fun observeClickPagingData() {
        viewModel.initClickPagingData().observe(viewLifecycleOwner, {
            clickPagingRefreshAdapter.reset()
            lifecycleScope.launch {
                println("clickPagingDataAdapter.submitData(it)")
                clickPagingDataAdapter.submitData(it)
            }
        })
    }

    private suspend fun observeClickInvalidation() {
        viewModel.clickInvalidation.await().observe(viewLifecycleOwner) {
            clickPagingRefreshAdapter.refresh()
        }
    }



    private fun setupClickRecyclerView() {
        clickPagingDataAdapter = ClickPagingDataAdapter(this@ClickFragment)
        footerLoadStateAdapter = BalanceLoadStateAdapter(clickPagingDataAdapter::retry)
        binding.rvClick.adapter = clickPagingDataAdapter.withLoadStateFooter(
            footer = footerLoadStateAdapter
        )
        val gridLayoutManager = GridLayoutManager(this@ClickFragment.context, CLICK_PAGE_SPAN_COUNT)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // NOTE 1.  if you call clickPagingDataAdapter.getItemViewType(position), it will cause an infinite loop of load()
                //          when it reaches to the max page size. I don't know why but below code only gives the header span count of 2
                // NOTE 2.  if you put delay(10000) in loadClicks(), then it will make the first card of current pages span of 2, but
                //          prepend will always have pages to load so it won't call API so no delay when scroll up

                if (position == clickPagingDataAdapter.itemCount && footerLoadStateAdapter.itemCount > 0) {
                    return FOOTER_SPAN_COUNT
                }

                return if (position == 0) {
                    HEADER_SPAN_COUNT
                } else {
                    ITEM_SPAN_COUNT
                }
            }
        }
        binding.rvClick.layoutManager = gridLayoutManager
        binding.rvClick.itemAnimator = null
        clickPagingRefreshAdapter = PagingRefreshAdapter(binding.rvClick, clickPagingDataAdapter)
    }

    private fun setupClickPagingInitialPageAdapter() {
        binding.btnClickRetry.setOnClickListener {
            clickPagingDataAdapter.retry()
        }
        clickPagingInitialPageAdapter = PagingInitialPageAdapter(
            clickPagingDataAdapter,
            binding.llClickInitialLoadingPage,
            binding.llClickInitialErrorPage,
            binding.llClickInitialEmptyPage,
        )
        lifecycleScope.launch {
            clickPagingDataAdapter.loadStateFlow.collect { loadState ->
                clickPagingInitialPageAdapter.updateUI(loadState)
            }
        }
    }

    override fun onFragmentSelected() {
    }

    override fun onSelectClick(position: Int) {
        clickPagingDataAdapter.getClick(position)?.let { click ->
            SwipeBalanceGameDialog(click.swiperId, click.name, click.profilePhotoKey).show(
                childFragmentManager,
                SwipeBalanceGameDialog.TAG
            )
        }
    }

    companion object {
        const val HEADER_SPAN_COUNT = 2
        const val ITEM_SPAN_COUNT = 1
        const val FOOTER_SPAN_COUNT = 2
        const val CLICK_PAGE_SPAN_COUNT = 2
    }


    override fun onResume() {
        super.onResume()
//        clickPagingDataAdapter.refresh()
    }


}