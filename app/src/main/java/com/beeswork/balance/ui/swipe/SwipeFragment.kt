package com.beeswork.balance.ui.swipe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.beeswork.balance.R
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.ui.balancegame.BalanceGameDialog
import com.beeswork.balance.ui.base.ScopeFragment
import com.beeswork.balance.ui.dialog.MatchDialog
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.android.synthetic.main.fragment_swipe.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class SwipeFragment : ScopeFragment(), KodeinAware, CardStackListener,
    BalanceGameDialog.BalanceGameListener {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeViewModelFactory by instance()
    private val preferenceProvider: PreferenceProvider by instance()

    private lateinit var viewModel: SwipeViewModel
    private lateinit var cardStackAdapter: CardStackAdapter
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBroadcastReceiver()
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    IntentAction.RECEIVED_FCM_NOTIFICATION -> {
                        when (intent.getStringExtra(FCMDataKey.NOTIFICATION_TYPE)) {
                            NotificationType.MATCH -> viewModel.fetchMatches()
                            NotificationType.CLICKED -> viewModel.fetchClickedList()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SwipeViewModel::class.java)
        bindUI()
        viewModel.fetchCards()
    }

    private fun bindUI() = launch {
        setupSwipeCardStackView()
        setupCardsObserver()

        viewModel.clickedCount.await().observe(viewLifecycleOwner, { clickedCount ->
            tvClickedCount.text = clickedCount.toString()
        })

        viewModel.unreadMessageCount.await().observe(viewLifecycleOwner, { unreadMessageCount ->
            tvUnreadMessageCount.text = unreadMessageCount.toString()
        })

        btnSwipeFilter.setOnClickListener {
            SwipeFilterDialog(preferenceProvider).show(
                childFragmentManager,
                DialogTag.SWIPE_FILTER_DIALOG
            )
        }

        btnCardStackReload.setOnClickListener {
            viewModel.fetchCards()
        }
    }

    private fun setupCardsObserver() {
        viewModel.cards.observe(viewLifecycleOwner, { cardResource ->
            when (cardResource.status) {
                Resource.Status.SUCCESS -> {
                    cardStackAdapter.addCards(cardResource.data!!)
                    csvSwipe.visibility = View.VISIBLE
                }
                Resource.Status.LOADING -> {
                    llCardStackLoading.visibility = View.VISIBLE
                    llCardStackLoadError.visibility = View.GONE
                    csvSwipe.visibility = View.GONE
                }
                Resource.Status.EXCEPTION -> {
                    llCardStackLoading.visibility = View.GONE
                    llCardStackLoadError.visibility = View.VISIBLE
                    csvSwipe.visibility = View.GONE
                }
            }
        })
    }

    private fun setupSwipeCardStackView() {
        cardStackAdapter = CardStackAdapter()

        val cardStackLayoutManager = CardStackLayoutManager(context, this@SwipeFragment)
        cardStackLayoutManager.setCanScrollVertical(false)
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual)

        csvSwipe.layoutManager = cardStackLayoutManager
        csvSwipe.adapter = cardStackAdapter
        csvSwipe.itemAnimator = DefaultItemAnimator()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            broadcastReceiver,
            IntentFilter(IntentAction.RECEIVED_FCM_NOTIFICATION)
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    override fun onBalanceGameMatch(matchedPhotoKey: String) {
        MatchDialog("", matchedPhotoKey).show(
            childFragmentManager,
            DialogTag.MATCH_DIALOG
        )
    }

    override fun onCardSwiped(direction: Direction?) {

        val removedCard = cardStackAdapter.removeCard()

        if (cardStackAdapter.itemCount < MIN_CARD_STACK_SIZE)
            viewModel.fetchCards()

        if (direction == Direction.Right && removedCard != null) {
            BalanceGameDialog(removedCard.accountId, this@SwipeFragment).show(
                childFragmentManager,
                DialogTag.BALANCE_DIALOG
            )

            viewModel.swipe(removedCard.accountId)
        }
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


}
