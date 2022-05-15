package com.beeswork.balance.ui.cardfragment

import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentCardBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.observeUIState
import com.beeswork.balance.ui.balancegamedialog.CardBalanceGameDialog
import com.beeswork.balance.ui.balancegamedialog.CardBalanceGameListener

import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.cardfragment.card.CardStackAdapter
import com.beeswork.balance.ui.cardfragment.filter.CardFilterDialog
import com.beeswork.balance.ui.reportdialog.ReportDialog
import com.google.android.material.snackbar.Snackbar
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class CardFragment(
    private val cardBalanceGameListener: CardBalanceGameListener
) : BaseFragment(),
    KodeinAware,
    CardStackListener,
    ViewPagerChildFragment,
    CardListener,
    ReportDialog.ReportDialogListener {

    override val kodein by closestKodein()
    private val viewModelFactory: CardViewModelFactory by instance()

    private lateinit var viewModel: CardViewModel
    private lateinit var cardStackAdapter: CardStackAdapter
    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var binding: FragmentCardBinding
    private var cardStackReachedEnd: Boolean = false
    private lateinit var locationPermissionStatusObserver: Observer<LocationPermissionStatus>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CardViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupCardStackView()
        observeFetchCardsLiveData()
        observeLocationGrantedLiveData()
        setupBtnListeners()
    }

    private suspend fun observeLocationGrantedLiveData() {
        locationPermissionStatusObserver = Observer<LocationPermissionStatus> { locationPermissionStatus ->
            when (locationPermissionStatus) {
                LocationPermissionStatus.CHECKING -> {
                    updateCardLayouts(View.VISIBLE, View.GONE, View.GONE)
                }
                LocationPermissionStatus.GRANTED -> {
                    binding.llCardStackLocationNotGranted.visibility = View.GONE
                    lifecycleScope.launch {
                        viewModel.locationPermissionStatusLiveData.await().removeObserver(locationPermissionStatusObserver)
                        observeCardFilterUIStateLiveData()
                    }
                }
                LocationPermissionStatus.DENIED -> {
                    binding.llCardStackLocationNotGranted.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
        viewModel.locationPermissionStatusLiveData.await().observe(viewLifecycleOwner, locationPermissionStatusObserver)
    }

    private fun setupBtnListeners() {
        binding.btnCardStackRefetch.setOnClickListener { viewModel.fetchCards(resetPage = false, isFirstFetch = false) }
        binding.btnCardStackReset.setOnClickListener { viewModel.fetchCards(resetPage = true, isFirstFetch = true) }
    }

    private fun setupToolBar() {
        binding.tbCard.inflateMenu(R.menu.card_tool_bar)
        binding.tbCard.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.miCardFilter -> {
                    showCardFilterDialog(showGenderTip = false, cancellable = true)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupCardStackView() {
        cardStackAdapter = CardStackAdapter(this@CardFragment)
        cardStackLayoutManager = CardStackLayoutManager(context, this@CardFragment)
        cardStackLayoutManager.setCanScrollVertical(false)
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual)
        cardStackLayoutManager.setSwipeThreshold(0.5f)
        cardStackLayoutManager.setMaxDegree(0.0f)
        cardStackLayoutManager.setOverlayInterpolator(LinearInterpolator())

        binding.csvCard.layoutManager = cardStackLayoutManager
        binding.csvCard.adapter = cardStackAdapter
        binding.csvCard.itemAnimator = DefaultItemAnimator()
    }

    private suspend fun observeCardFilterUIStateLiveData() {
        viewModel.cardFilterInvalidationLiveData.await().observe(viewLifecycleOwner) { cardFilterInvalidated ->
            if (cardFilterInvalidated) {
                cardStackReachedEnd = false
                cardStackAdapter.clearCards()
                viewModel.fetchCards(resetPage = false, isFirstFetch = true)
            } else {
                showCardFilterDialog(showGenderTip = true, cancellable = false)
            }
        }
    }

    private fun showCardFilterDialog(showGenderTip: Boolean, cancellable: Boolean) {
        val cardFilterDialog = CardFilterDialog(showGenderTip)
        cardFilterDialog.isCancelable = cancellable
        cardFilterDialog.show(childFragmentManager, CardFilterDialog.TAG)
    }

    private fun observeFetchCardsLiveData() {
        viewModel.fetchCardsUIStateLiveData.observeUIState(viewLifecycleOwner, activity) { fetchCardsUIState ->
            when {
                fetchCardsUIState.showLoading -> {
                    updateCardLayouts(View.VISIBLE, View.GONE, View.GONE)
                }
                fetchCardsUIState.showError -> {
                    hideEmptyCardStack()
                    updateCardLayouts(View.GONE, View.GONE, View.VISIBLE)
                    binding.tvCardStackErrorTitle.text = getString(R.string.fetch_card_exception_title)
                    binding.tvCardStackErrorMessage.text = MessageSource.getMessage(requireContext(), fetchCardsUIState.exception)
                }
                fetchCardsUIState.cardItemUIStates.isNullOrEmpty() -> {
                    hideEmptyCardStack()
                    cardStackReachedEnd = true
                    updateCardLayouts(View.GONE, View.VISIBLE, View.GONE)
                }
                else -> {
                    cardStackReachedEnd = false
                    binding.csvCard.visibility = View.VISIBLE
                    updateCardLayouts(View.GONE, View.GONE, View.GONE)
                    cardStackAdapter.submitCards(fetchCardsUIState.cardItemUIStates)
                }
            }
        }
    }

    private fun hideEmptyCardStack() {
        if (cardStackAdapter.itemCount == 0) {
            binding.csvCard.visibility = View.GONE
        }
    }

    override fun onCardSwiped(direction: Direction?) {
        val removedCard = cardStackAdapter.removeCard()
        hideEmptyCardStack()

        if (removedCard != null) {
            viewModel.incrementReadByIndex()
        }

        if (cardStackAdapter.itemCount < MIN_CARD_STACK_SIZE && !cardStackReachedEnd) {
            viewModel.fetchCards(resetPage = false, isFirstFetch = false)
        }

        if (removedCard != null && direction == Direction.Right) {
            CardBalanceGameDialog(
                removedCard.accountId,
                removedCard.name,
                removedCard.photoURLs.getOrNull(0),
                cardBalanceGameListener
            ).show(
                childFragmentManager,
                CardBalanceGameDialog.TAG
            )
        }
    }

    private fun updateCardLayouts(loading: Int, empty: Int, error: Int) {
        binding.llCardStackLoading.visibility = loading
        binding.llCardStackEmpty.visibility = empty
        binding.llCardStackError.visibility = error
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    override fun onFragmentSelected() {

    }

    override fun onReportCard(reportedId: UUID) {
        ReportDialog(this@CardFragment, ReportDialog.Type.REPORT_PROFILE, reportedId).show(childFragmentManager, ReportDialog.TAG)
    }

    override fun onReportSubmitted() {
        binding.csvCard.swipe()
    }

    companion object {
        const val MIN_CARD_STACK_SIZE = 15
    }

}
