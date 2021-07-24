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
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.databinding.FragmentClickBinding
import com.beeswork.balance.databinding.SnackBarNewClickBinding
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.SnackBarHelper
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.swipe.balancegame.SwipeBalanceGameDialog
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


class ClickFragment : BaseFragment(),
    KodeinAware,
    ClickPagingDataAdapter.OnClickListener,
    ErrorDialog.OnRetryListener,
    ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: ClickViewModelFactory by instance()
    private lateinit var viewModel: ClickViewModel
    private lateinit var clickPagingDataAdapter: ClickPagingDataAdapter
    private lateinit var clickPagingRefreshAdapter: PagingRefreshAdapter<ClickDomain, RecyclerView.ViewHolder>
    private lateinit var binding: FragmentClickBinding
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
        observeExceptionLiveData(viewModel)
        bindUI()
//        viewModel.fetchClicks()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupClickRecyclerView()
        setupClickInvalidationObserver()
        setupFetchClicksObserver()
        setupNewClickLiveDataObserver()
        setupClickPagingDataObserver()
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.btnClickRefresh.setOnClickListener { viewModel.fetchClicks() }
    }

    private suspend fun setupNewClickLiveDataObserver() {
        viewModel.newClickLiveData.await().observe(viewLifecycleOwner) {
            showNewClickSnackBar(it)
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

    private fun setupFetchClicksObserver() {
        viewModel.fetchClicks.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.btnClickRefresh.visibility = View.GONE
                    binding.skvClickLoading.visibility = View.INVISIBLE
                }
                Resource.Status.LOADING -> {
                    binding.btnClickRefresh.visibility = View.GONE
                    binding.skvClickLoading.visibility = View.VISIBLE
                }
                Resource.Status.ERROR -> {
                    binding.btnClickRefresh.visibility = View.VISIBLE
                    binding.skvClickLoading.visibility = View.GONE
                    val errorTitle = getString(R.string.fetch_clicks_exception_title)
                    showErrorDialog(it.error, errorTitle, it.errorMessage, RequestCode.FETCH_CLICKS, this@ClickFragment)
                }
            }
        }
    }

    private fun setupClickPagingDataObserver() {
        viewModel.initClickPagingData().observe(viewLifecycleOwner) {
            lifecycleScope.launch { clickPagingDataAdapter.submitData(it) }
        }
    }

    private suspend fun setupClickInvalidationObserver() {
        viewModel.clickInvalidation.await().observe(viewLifecycleOwner) {
            clickPagingRefreshAdapter.refresh()
        }
    }

    private fun setupClickRecyclerView() {
        clickPagingDataAdapter = ClickPagingDataAdapter(this)
        val gridLayoutManager = GridLayoutManager(this@ClickFragment.context, SPAN_COUNT)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (clickPagingDataAdapter.getItemViewType(position)) {
                    ClickDomain.Type.ITEM.ordinal -> ITEM_SPAN_COUNT
                    ClickDomain.Type.HEADER.ordinal -> SPAN_COUNT
                    else -> ITEM_SPAN_COUNT
                }
            }
        }
        binding.rvClick.itemAnimator = null
        binding.rvClick.adapter = clickPagingDataAdapter
        binding.rvClick.layoutManager = gridLayoutManager
        clickPagingRefreshAdapter = PagingRefreshAdapter(binding.rvClick, clickPagingDataAdapter)
    }

    override fun onFragmentSelected() {
        viewModel.fetchClicks()
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
        const val SPAN_COUNT = 2
        const val ITEM_SPAN_COUNT = 1
    }

    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.FETCH_CLICKS -> viewModel.fetchClicks()
        }
    }

}