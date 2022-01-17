package com.beeswork.balance.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutLoadStateBinding

class LoadStateViewHolder(
    parent: ViewGroup,
    retry: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.layout_load_state, parent, false)
) {
    private val binding = LayoutLoadStateBinding.bind(itemView)
    private val skvProgressBar = binding.skvProgressBar
    private val tvErrorMessage = binding.tvErrorMessage
    private val btnRetry: Button = binding.btnRetry.also {
        it.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            tvErrorMessage.text = loadState.error.localizedMessage
        }
        skvProgressBar.isVisible = loadState is LoadState.Loading
        tvErrorMessage.isVisible = loadState is LoadState.Error
        btnRetry.isVisible = loadState is LoadState.Error
    }
}