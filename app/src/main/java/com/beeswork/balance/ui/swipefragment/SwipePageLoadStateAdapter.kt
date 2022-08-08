package com.beeswork.balance.ui.swipefragment

import com.beeswork.balance.databinding.FragmentSwipeBinding
import com.beeswork.balance.ui.common.paging.LoadType
import com.beeswork.balance.ui.common.paging.PageLoadStateAdapter

class SwipePageLoadStateAdapter(
    binding: FragmentSwipeBinding,
    retry: (loadType: LoadType) -> Unit
) : PageLoadStateAdapter(
    binding.rvSwipe,
    binding.llSwipePagingLoading,
    binding.llSwipePagingEmpty,
    binding.llSwipePagingError,
    binding.tvSwipePagingErrorMessage,
    binding.btnSwipePagingErrorRetry,
    retry
)