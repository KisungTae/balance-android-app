package com.beeswork.balance.ui.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.internal.util.MessageSource
import com.github.ybq.android.spinkit.SpinKitView

class PagingInitialPageAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    private val pagingDataAdapter: PagingDataAdapter<T, VH>,
    private val initialLoadingPage: ViewGroup,
    private val initialErrorPage: ViewGroup,
    private val initialEmptyPage: ViewGroup,
    private val tvErrorMessage: TextView,
    private val context: Context
) {

    fun updateUI(loadState: CombinedLoadStates) {
        if (pagingDataAdapter.itemCount <= 1) {
            if (loadState.append is LoadState.Loading) {
                updateUI(View.VISIBLE, View.GONE, View.GONE)
            } else if (loadState.append is LoadState.Error) {
                val exception = (loadState.append as LoadState.Error).error
                val message = MessageSource.getMessage(context, exception)
                if (message != null) {
                    tvErrorMessage.text = message
                }
                updateUI(View.GONE, View.VISIBLE, View.GONE)
            } else if (loadState.append is LoadState.NotLoading && loadState.append.endOfPaginationReached) {
                updateUI(View.GONE, View.GONE, View.VISIBLE)
            } else {
                updateUI(View.GONE, View.GONE, View.GONE)
            }
        } else {
            updateUI(View.GONE, View.GONE, View.GONE)
        }
    }

    private fun updateUI(loadingPageVisibility: Int, errorPageVisibility: Int, emptyPageVisibility: Int) {
        initialLoadingPage.visibility = loadingPageVisibility
        initialErrorPage.visibility = errorPageVisibility
        initialEmptyPage.visibility = emptyPageVisibility
    }

}