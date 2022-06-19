package com.beeswork.balance.ui.common.paging

import androidx.recyclerview.widget.RecyclerView

abstract class PagingAdapter<T : Any, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected val items = mutableListOf<T>()

}