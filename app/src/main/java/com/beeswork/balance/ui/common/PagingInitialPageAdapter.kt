package com.beeswork.balance.ui.common

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView

class PagingInitialPageAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    private val pagingDataAdapter: PagingDataAdapter<T, VH>,
    private val initialLoadingPage: ViewGroup,
    private val initialErrorPage: ViewGroup,
    private val initialEmptyPage: ViewGroup
) {

    fun updateUI(loadState: CombinedLoadStates) {
        if (pagingDataAdapter.itemCount <= 1) {
            if (loadState.refresh is LoadState.Loading) {
                println("show general loading page")
                updateUI(View.VISIBLE, View.GONE, View.GONE)
            } else if (loadState.refresh is LoadState.Error) {
                println("show general error page")
                println("(loadState.refresh as LoadState.Error).error ${(loadState.refresh as LoadState.Error).error}")
                updateUI(View.GONE, View.VISIBLE, View.GONE)
            } else if (loadState.prepend is LoadState.NotLoading && loadState.prepend.endOfPaginationReached) {
                println("show general empty page")
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