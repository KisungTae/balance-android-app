package com.beeswork.balance.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutLoadStateBinding

class BalanceLoadStateAdapter(
    private val retry: () -> Unit
) : RecyclerView.Adapter<BalanceLoadStateAdapter.LoadStateViewHolder>() {

//    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder = LoadStateViewHolder(parent, retry)
//
//    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) = holder.bind(loadState)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadStateViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


    class LoadStateViewHolder(
        parent: ViewGroup,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.layout_load_state, parent, false)
    ) {
        private val binding = LayoutLoadStateBinding.bind(itemView)
        private val skvProgressBar = binding.skvLoadStateProgressBar
        private val tvErrorMessage = binding.tvLoadStateErrorMessage
        private val btnRetry: Button = binding.btnLoadStateRetry.also {
            it.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
//            tvErrorMessage.text = loadState.error.localizedMessage
            }
            skvProgressBar.isVisible = loadState is LoadState.Loading
            tvErrorMessage.isVisible = loadState is LoadState.Error
            btnRetry.isVisible = loadState is LoadState.Error
        }
    }

}