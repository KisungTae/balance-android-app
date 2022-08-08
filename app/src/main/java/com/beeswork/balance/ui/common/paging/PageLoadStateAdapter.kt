package com.beeswork.balance.ui.common.paging

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

abstract class PageLoadStateAdapter(
    private val recyclerView: RecyclerView,
    private val loadingView: View,
    private val emptyView: View,
    private val errorView: View,
    private val errorMessageTextView: TextView,
    private val retryBtn: Button,
    private val retry: (loadType: LoadType) -> Unit
): LoadStateAdapter {

    override fun onLoadStateUpdated(newLoadState: LoadState) {
        when (newLoadState) {
            LoadState.Loading -> {
                updateViewVisibility(View.GONE, View.VISIBLE, View.GONE, View.GONE)
            }
            LoadState.Loaded -> {
                updateViewVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE)
            }
            LoadState.Empty -> {
                updateViewVisibility(View.GONE, View.GONE, View.VISIBLE, View.GONE)
            }
            is LoadState.Error -> {
                if (newLoadState.errorMessage != null) {
                    errorMessageTextView.text = newLoadState.errorMessage
                }
                retryBtn.setOnClickListener {
                    retry(newLoadState.loadType)
                }
                updateViewVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE)
            }
        }
    }

    private fun updateViewVisibility(recyclerView: Int, loadingView: Int, emptyView: Int, errorView: Int) {
        this.recyclerView.visibility = recyclerView
        this.loadingView.visibility = loadingView
        this.emptyView.visibility = emptyView
        this.errorView.visibility = errorView
    }
}