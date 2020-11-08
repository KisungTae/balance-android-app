package com.beeswork.balance.ui.clicked

import android.R.attr.data
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.ui.base.ScopeFragment
import kotlinx.android.synthetic.main.fragment_clicked.*
import kotlinx.android.synthetic.main.fragment_swipe.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.math.ceil


class ClickedFragment : ScopeFragment(), KodeinAware,
    ClickedPagedListAdapter.OnClickedSwipeListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ClickedViewModelFactory by instance()
    private lateinit var viewModel: ClickedViewModel
    private lateinit var clickedPagedListAdapter: ClickedPagedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clicked, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ClickedViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {

        clickedPagedListAdapter = ClickedPagedListAdapter(
            this@ClickedFragment
        )

        rvClicked.adapter = clickedPagedListAdapter
        val gridLayoutManager = GridLayoutManager(this@ClickedFragment.context, 2)
        rvClicked.layoutManager = gridLayoutManager


        viewModel.fetchClickedList()

        viewModel.clickedList.await().observe(viewLifecycleOwner, { pagedClickedList ->
            clickedPagedListAdapter.submitList(pagedClickedList)
        })

        viewModel.fetchClickedListResponse.observe(viewLifecycleOwner, { fetchClickedListResponse ->

            when (fetchClickedListResponse.status) {
                Resource.Status.SUCCESS -> {
                    println("fetchClickedList success")
                }

                Resource.Status.EXCEPTION -> {
                    println("fetchClickedList exception")
                }

                Resource.Status.LOADING -> {
                    println("fetchClickedList loading")
                }
            }
        })
    }

    override fun onSwipeRight() {
        println("swipe right")
    }

    override fun onSwipeLeft() {
        println("swipe left")
    }

}