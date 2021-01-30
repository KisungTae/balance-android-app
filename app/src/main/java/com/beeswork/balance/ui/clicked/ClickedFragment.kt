package com.beeswork.balance.ui.clicked

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.data.observable.Resource
import com.beeswork.balance.ui.balancegame.BalanceGameDialog
import com.beeswork.balance.ui.base.ScopeFragment
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import com.beeswork.balance.ui.dialog.MatchDialog
import kotlinx.android.synthetic.main.fragment_clicked.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ClickedFragment : ScopeFragment(), KodeinAware,
    ClickedPagedListAdapter.OnClickedListener, BalanceGameDialog.BalanceGameListener,
    FetchErrorDialog.FetchErrorListener {

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
                Resource.Status.EXCEPTION -> {
                    FetchErrorDialog(
                        fetchClickedListResponse.exceptionMessage,
                        this@ClickedFragment
                    ).show(childFragmentManager, FetchErrorDialog.TAG)
                }
            }
        })
    }

    override fun onClickedClick(swipedId: String) {
        BalanceGameDialog(swipedId, this@ClickedFragment).show(
            childFragmentManager,
            BalanceGameDialog.TAG
        )
        viewModel.swipe(swipedId)
    }

    override fun onBalanceGameMatch(matchedPhotoKey: String) {
        MatchDialog("", matchedPhotoKey).show(
            childFragmentManager,
            MatchDialog.TAG
        )
    }

    override fun onRefetch() {
        viewModel.fetchClickedList()
    }

}