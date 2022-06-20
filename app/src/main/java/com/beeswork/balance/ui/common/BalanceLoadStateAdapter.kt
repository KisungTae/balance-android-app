package com.beeswork.balance.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutLoadStateBinding
import com.beeswork.balance.ui.common.LoadStateViewHolder

class BalanceLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder = LoadStateViewHolder(parent, retry)

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) = holder.bind(loadState)

}