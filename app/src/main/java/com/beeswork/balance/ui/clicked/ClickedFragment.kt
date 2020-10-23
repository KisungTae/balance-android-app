package com.beeswork.balance.ui.clicked

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.R
import com.beeswork.balance.ui.base.ScopeFragment
import com.beeswork.balance.ui.match.MatchViewModel
import com.beeswork.balance.ui.match.MatchViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ClickedFragment: ScopeFragment(), KodeinAware, ClickedPagedListAdapter.OnSwipeListener {

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

        clickedPagedListAdapter = ClickedPagedListAdapter(this@ClickedFragment)

        viewModel.clicked.await().observe(viewLifecycleOwner, { pagedClickedList ->
            clickedPagedListAdapter.submitList(pagedClickedList)
        })

        viewModel.fetchClicked()
    }

    override fun onSwipeRight() {
        println("swipe right")
    }

    override fun onSwipeLeft() {
        println("swipe left")
    }

}