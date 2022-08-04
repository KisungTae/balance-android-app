package com.beeswork.balance.ui.common.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutLoadStateBinding

class ItemLoadStateAdapter(
    private val retry: (loadType: LoadType) -> Unit
) : RecyclerView.Adapter<ItemLoadStateAdapter.ViewHolder>(), LoadStateAdapter {

    private var loadState: LoadState = LoadState.Loaded

    override fun onLoadStateUpdated(newLoadState: LoadState) {
        if (loadState != newLoadState) {
            val displayOldLoadState = displayLoadState(loadState)
            val displayNewLoadState = displayLoadState(newLoadState)

            if (displayOldLoadState && !displayNewLoadState) {
                notifyItemRemoved(0)
            } else if (!displayOldLoadState && displayNewLoadState) {
                notifyItemInserted(0)
            } else if (displayOldLoadState && displayNewLoadState) {
                notifyItemChanged(0)
            }
            loadState = newLoadState
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(loadState, retry)
    }

    override fun getItemCount(): Int {
        return if (displayLoadState(loadState)) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.layout_load_state
    }

    private fun displayLoadState(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
    }

    companion object {
        const val PAGING_ITEM_LOAD_STATE_SPAN_COUNT = 2
    }

    class ViewHolder(
        private val binding: LayoutLoadStateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState, retry: (loadType: LoadType) -> Unit) {
            if (loadState is LoadState.Error) {
                if (loadState.errorMessage != null) {
                    binding.tvLoadStateErrorMessage.text = loadState.errorMessage
                }
                binding.btnLoadStateRetry.setOnClickListener {
                    retry(loadState.loadType)
                }
            }
            binding.skvLoadStateProgressBar.isVisible = loadState is LoadState.Loading
            binding.tvLoadStateErrorMessage.isVisible = loadState is LoadState.Error
            binding.btnLoadStateRetry.isVisible = loadState is LoadState.Error
        }

    }


}