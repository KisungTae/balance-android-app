package com.beeswork.balance.ui.common.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.LayoutLoadStateBinding

class LoadStatusAdapter(
    private val retry: () -> Unit
) : RecyclerView.Adapter<LoadStatusAdapter.ViewHolder>() {

    var loadStatus: LoadStatus = LoadStatus.Loaded(LoadType.REFRESH)
        set(loadStatus) {
            if (field != loadStatus) {
                val displayOldLoadStatus = displayLoadStatus(field)
                val displayNewLoadStatus = displayLoadStatus(loadStatus)

                if (displayOldLoadStatus && !displayNewLoadStatus) {
                    notifyItemRemoved(0)
                } else if (!displayOldLoadStatus && displayNewLoadStatus) {
                    notifyItemInserted(0)
                } else if (displayOldLoadStatus && displayNewLoadStatus) {
                    notifyItemChanged(0)
                }
                field = loadStatus
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(loadStatus, retry)
    }

    override fun getItemCount(): Int {
        return if (displayLoadStatus(loadStatus)) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.layout_load_state
    }

    private fun displayLoadStatus(loadStatus: LoadStatus): Boolean {
        return loadStatus is LoadStatus.Loading || loadStatus is LoadStatus.Error
    }


    class ViewHolder(
        private val binding: LayoutLoadStateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadStatus: LoadStatus, retry: () -> Unit) {
            if (loadStatus is LoadStatus.Error) {
                binding.tvLoadStateErrorMessage.text = loadStatus.errorMessage
                binding.btnLoadStateRetry.setOnClickListener {
                    retry.invoke()
                }
            }
            binding.skvLoadStateProgressBar.isVisible = loadStatus is LoadStatus.Loading
            binding.tvLoadStateErrorMessage.isVisible = loadStatus is LoadStatus.Error
            binding.btnLoadStateRetry.isVisible = loadStatus is LoadStatus.Error
        }

    }
}