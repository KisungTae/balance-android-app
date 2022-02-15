package com.beeswork.balance.ui.cardfragment

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentCardBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.observeResource

import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.cardfragment.balancegame.CardBalanceGameDialog
import com.beeswork.balance.ui.cardfragment.card.CardStackAdapter
import com.beeswork.balance.ui.cardfragment.filter.CardFilterDialog
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class CardFragment : BaseFragment(),
    KodeinAware,
    CardStackListener,
    ViewPagerChildFragment,
    CardFilterDialog.CardFilterDialogListener {

    override val kodein by closestKodein()
    private val viewModelFactory: CardViewModelFactory by instance()

    private lateinit var viewModel: CardViewModel
    private lateinit var cardStackAdapter: CardStackAdapter
    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var binding: FragmentCardBinding

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
        observeLocationPermissionResultLiveData()
        binding.btnCardStackReload.setOnClickListener { viewModel.fetchCards() }
    }

    private suspend fun observeLocationPermissionResultLiveData() {
        viewModel.locationPermissionResultLiveData.await().observe(viewLifecycleOwner) { granted ->
            granted?.let { _granted ->
                if (_granted) {
                    updateCardLayouts(View.GONE, View.GONE, View.GONE, View.GONE)
//                    viewModel.fetchCards()
                } else updateCardLayouts(View.GONE, View.GONE, View.GONE, View.VISIBLE)
            }
        }
    }

    private fun observeFetchCardsLiveData() {
        viewModel.fetchCards.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> resource.data?.let { cardDomains ->
                    if (cardDomains.isEmpty()) {
                        updateCardLayouts(View.GONE, View.VISIBLE, View.GONE, View.GONE)
                    } else {
                        updateCardLayouts(View.VISIBLE, View.GONE, View.GONE, View.GONE)
                        cardStackAdapter.submitCards(cardDomains)
                        binding.csvCard.visibility = View.VISIBLE
                    }
                }
                resource.isLoading() -> updateCardLayouts(View.VISIBLE, View.GONE, View.GONE, View.GONE)
                resource.isError() -> {
                    updateCardLayouts(View.GONE, View.GONE, View.VISIBLE, View.GONE)
                    val title = getString(R.string.fetch_card_exception_title)
                    val message = MessageSource.getMessage(requireContext(), resource.exception)
                    ErrorDialog.show(title, message, childFragmentManager)
                }
            }
        }
    }

    private fun updateCardLayouts(loading: Int, empty: Int, error: Int, location: Int) {
        binding.llCardStackLoading.visibility = loading
        binding.llCardStackEmpty.visibility = empty
        binding.llCardStackError.visibility = error
        binding.llCardStackLocationNotPermitted.visibility = location
    }

    private fun setupToolBar() {
        binding.tbCard.inflateMenu(R.menu.card_tool_bar)
        binding.tbCard.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.miCardFilter -> {
                    CardFilterDialog(this).show(childFragmentManager, CardFilterDialog.TAG)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupCardStackView() {
        cardStackAdapter = CardStackAdapter()
        cardStackLayoutManager = CardStackLayoutManager(context, this@CardFragment)
        cardStackLayoutManager.setCanScrollVertical(false)
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual)
        cardStackLayoutManager.setSwipeThreshold(0.5f)
        cardStackLayoutManager.setMaxDegree(0.0f)

        binding.csvCard.layoutManager = cardStackLayoutManager
        binding.csvCard.adapter = cardStackAdapter
        binding.csvCard.itemAnimator = DefaultItemAnimator()
    }

    override fun onCardSwiped(direction: Direction?) {
        val removedCard = cardStackAdapter.removeCard()
        if (cardStackAdapter.itemCount == 0) binding.csvCard.visibility = View.GONE
        if (cardStackAdapter.itemCount < MIN_CARD_STACK_SIZE) viewModel.fetchCards()

        if (direction == Direction.Right) removedCard?.let { _removedCard ->
            val profilePhotoKey = if (_removedCard.photoKeys.isNotEmpty()) _removedCard.photoKeys[0] else null
            CardBalanceGameDialog(_removedCard.accountId, _removedCard.name, profilePhotoKey).show(
                childFragmentManager,
                CardBalanceGameDialog.TAG
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

    override fun onApplyCardFilter() {
        println("onApplySwipeFilter")
    }

}
