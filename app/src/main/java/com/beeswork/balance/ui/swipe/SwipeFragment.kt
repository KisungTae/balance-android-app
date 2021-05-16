package com.beeswork.balance.ui.swipe

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentSwipeBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.balancegame.BalanceGameDialog
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class SwipeFragment : BaseFragment(),
    KodeinAware,
    CardStackListener,
    BalanceGameDialog.BalanceGameListener,
    ViewPagerChildFragment,
    SwipeFilterDialog.SwipeFilterDialogListener {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeViewModelFactory by instance()

    private lateinit var viewModel: SwipeViewModel
    private lateinit var cardStackAdapter: CardStackAdapter
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
        viewModel.fetchCards()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupSwipeCardStackView()
//        setupCardsObserver()

//        viewModel.clickedCount.await().observe(viewLifecycleOwner, { clickedCount ->
//            binding.tvClickCount.text = clickedCount.toString()
//        })


//        binding.btnSwipeFilter.setOnClickListener {
//            SwipeFilterDialog(preferenceProvider).show(
//                childFragmentManager,
//                SwipeFilterDialog.TAG
//            )
//        }

//        binding.btnCardStackReload.setOnClickListener {
//            viewModel.fetchCards(false)
//        }
//
//        binding.btnCardStackReset.setOnClickListener {
//            viewModel.fetchCards(true)
//        }
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

    private fun setupCardsObserver() {
//        viewModel.cards.observe(viewLifecycleOwner, { cardResource ->
//            when (cardResource.status) {
//                Resource.Status.SUCCESS -> {
//
//                    val cards = cardResource.data
//
//                    if (cards == null || cards.isEmpty()) {
//                        resetCardStackLayouts()
//                        binding.llCardStackReset.visibility = View.VISIBLE
//                    } else {
//                        cardStackAdapter.addCards(cardResource.data)
//                        binding.csvSwipe.visibility = View.VISIBLE
//                    }
//
//                }
//                Resource.Status.LOADING -> {
//                    resetCardStackLayouts()
//                    binding.llCardStackLoading.visibility = View.VISIBLE
//
//                }
//                Resource.Status.ERROR -> {
//                    resetCardStackLayouts()
//                    binding.llCardStackError.visibility = View.VISIBLE
//                }
//            }
//        })
    }

    private fun resetCardStackLayouts() {
        if (cardStackAdapter.itemCount == 0)
            binding.csvSwipe.visibility = View.GONE

        binding.llCardStackLoading.visibility = View.GONE
        binding.llCardStackError.visibility = View.GONE
//        binding.llCardStackReset.visibility = View.GONE
    }

    private fun setupSwipeCardStackView() {
        cardStackAdapter = CardStackAdapter()

        val cardStackLayoutManager = CardStackLayoutManager(context, this@SwipeFragment)
        cardStackLayoutManager.setCanScrollVertical(false)
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual)

        binding.csvSwipe.layoutManager = cardStackLayoutManager
        binding.csvSwipe.adapter = cardStackAdapter
        binding.csvSwipe.itemAnimator = DefaultItemAnimator()
    }


    override fun onBalanceGameMatch(matchedPhotoKey: String) {
//        NewMatchDialog("", matchedPhotoKey).show(
//            childFragmentManager,
//            NewMatchDialog.TAG
//        )
    }

    override fun onCardSwiped(direction: Direction?) {

        val removedCard = cardStackAdapter.removeCard()

        if (cardStackAdapter.itemCount == 0)
            binding.csvSwipe.visibility = View.GONE

        if (cardStackAdapter.itemCount < MIN_CARD_STACK_SIZE)
            viewModel.fetchCards()

//        if (direction == Direction.Right && removedCard != null) {
//            BalanceGameDialog(removedCard.accountId, this@SwipeFragment).show(
//                childFragmentManager,
//                BalanceGameDialog.TAG
//            )
//
//            viewModel.swipe(removedCard.accountId)
//        }
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
        println("swipe fragment: onFragmentSelected")
    }

    companion object {
        const val MIN_CARD_STACK_SIZE = 15
    }

    override fun onApplySwipeFilter() {
        println("onApplySwipeFilter")
    }

}
