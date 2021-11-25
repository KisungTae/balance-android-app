package com.beeswork.balance.ui.swipe

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentSwipeBinding
import com.beeswork.balance.internal.constant.*

import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.swipe.balancegame.SwipeBalanceGameDialog
import com.beeswork.balance.ui.swipe.card.CardStackAdapter
import com.beeswork.balance.ui.swipe.filter.SwipeFilterDialog
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class SwipeFragment : BaseFragment(),
    KodeinAware,
    CardStackListener,
    ViewPagerChildFragment,
    SwipeFilterDialog.SwipeFilterDialogListener {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeViewModelFactory by instance()

    private lateinit var viewModel: SwipeViewModel
    private lateinit var cardStackAdapter: CardStackAdapter
    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var binding: FragmentSwipeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwipeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SwipeViewModel::class.java)
        bindUI()
    }


    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupSwipeCardStackView()
        setupFetchCardsLiveDataObserver()
        observeLocationPermissionResultLiveData()
        binding.btnCardStackReload.setOnClickListener { viewModel.fetchCards() }
    }

    private suspend fun observeLocationPermissionResultLiveData() {
        viewModel.locationPermissionResultLiveData.await().observe(viewLifecycleOwner) { granted ->
            granted?.let { _granted ->
                if (_granted) {
                    showLayouts(View.GONE, View.GONE, View.GONE, View.GONE)
//                    viewModel.fetchCards()
                } else showLayouts(View.GONE, View.GONE, View.GONE, View.VISIBLE)
            }
        }
    }

    private fun setupFetchCardsLiveDataObserver() {
        viewModel.fetchCards.observe(viewLifecycleOwner) { resource ->
            when {
                resource.isSuccess() -> resource.data?.let { cardDomains ->
                    if (cardDomains.isEmpty()) showLayouts(View.GONE, View.VISIBLE, View.GONE, View.GONE)
                    else {
                        showLayouts(View.VISIBLE, View.GONE, View.GONE, View.GONE)
                        cardStackAdapter.submitCards(cardDomains)
                        binding.csvSwipe.visibility = View.VISIBLE
                    }
                }
                resource.isLoading() -> showLayouts(View.VISIBLE, View.GONE, View.GONE, View.GONE)
                resource.isError() && validateLoginFromResource(resource) -> {
                    val errorTitle = getString(R.string.fetch_card_exception_title)
                    ErrorDialog.show(resource.error, errorTitle, resource.errorMessage, childFragmentManager)
                    showLayouts(View.GONE, View.GONE, View.VISIBLE, View.GONE)
                }
            }
        }
    }

    private fun showLayouts(loading: Int, empty: Int, error: Int, location: Int) {
        binding.llCardStackLoading.visibility = loading
        binding.llCardStackEmpty.visibility = empty
        binding.llCardStackError.visibility = error
        binding.llCardStackLocationNotPermitted.visibility = location
    }

    private fun setupToolBar() {
        binding.tbSwipe.inflateMenu(R.menu.swipe_tool_bar)
        binding.tbSwipe.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miSwipeFilter -> showSwipeFilter()
                else -> false
            }
        }
    }

    private fun showSwipeFilter(): Boolean {
        SwipeFilterDialog(this).show(childFragmentManager, SwipeFilterDialog.TAG)
        return true
    }

    private fun setupSwipeCardStackView() {
        cardStackAdapter = CardStackAdapter()

        cardStackLayoutManager = CardStackLayoutManager(context, this@SwipeFragment)
        cardStackLayoutManager.setCanScrollVertical(false)
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual)
        cardStackLayoutManager.setSwipeThreshold(0.5f)
        cardStackLayoutManager.setMaxDegree(0.0f)

        binding.csvSwipe.layoutManager = cardStackLayoutManager
        binding.csvSwipe.adapter = cardStackAdapter
        binding.csvSwipe.itemAnimator = DefaultItemAnimator()
    }

    override fun onCardSwiped(direction: Direction?) {
        val removedCard = cardStackAdapter.removeCard()
        if (cardStackAdapter.itemCount == 0) binding.csvSwipe.visibility = View.GONE
        if (cardStackAdapter.itemCount < MIN_CARD_STACK_SIZE) viewModel.fetchCards()

        if (direction == Direction.Right) removedCard?.let { _removedCard ->
            val profilePhotoKey = if (_removedCard.photoKeys.isNotEmpty()) _removedCard.photoKeys[0] else null
            SwipeBalanceGameDialog(_removedCard.accountId, _removedCard.name, profilePhotoKey).show(
                childFragmentManager,
                SwipeBalanceGameDialog.TAG
            )
        }
    }

    override fun onResume() {
        super.onResume()
    }


    private fun showLocationNotPermitScreen() {

    }

    private fun hideLocationNotPermittedScreen() {

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardRewound() {
        println("onCardRewound")
    }

    override fun onCardCanceled() {
        println("onCardCanceled")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        println("onCardAppeared")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        println("onCardDisappeared")
    }

    override fun onFragmentSelected() {
        println("swipe fragment: onFragmentSelected")
    }

    companion object {
        const val MIN_CARD_STACK_SIZE = 15
    }

    override fun onApplySwipeFilter() {
        println("onApplySwipeFilter")
    }

}
