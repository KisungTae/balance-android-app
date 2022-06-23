package com.beeswork.balance.ui.common.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutLoadStateBinding

class LoadStateAdapter(
    private val retry: () -> Unit
) : RecyclerView.Adapter<LoadStateAdapter.ViewHolder>() {

    var loadState: LoadState = LoadState.Loaded(LoadType.REFRESH)
        set(loadState) {
            if (field != loadState) {
                val displayOldLoadState = displayLoadState(field)
                val displayNewLoadState = displayLoadState(loadState)

                if (displayOldLoadState && !displayNewLoadState) {
                    notifyItemRemoved(0)
                } else if (!displayOldLoadState && displayNewLoadState) {
                    notifyItemInserted(0)
                } else if (displayOldLoadState && displayNewLoadState) {
                    notifyItemChanged(0)
                }
                field = loadState
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

    class ViewHolder(
        private val binding: LayoutLoadStateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState, retry: () -> Unit) {
            if (loadState is LoadState.Error) {
                binding.tvLoadStateErrorMessage.text = loadState.errorMessage
                binding.btnLoadStateRetry.setOnClickListener {
                    retry.invoke()
                }
            }
            binding.skvLoadStateProgressBar.isVisible = loadState is LoadState.Loading
            binding.tvLoadStateErrorMessage.isVisible = loadState is LoadState.Error
            binding.btnLoadStateRetry.isVisible = loadState is LoadState.Error
        }

    }
}