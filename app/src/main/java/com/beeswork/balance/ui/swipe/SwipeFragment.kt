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
import com.beeswork.balance.ui.base.ScopeFragment
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
        setupBalanceGameObserver()
        setupClickResponseObserver()

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
    }

    private fun setupClickResponseObserver() {
        viewModel.clickResponse.observe(viewLifecycleOwner, { clickResponse ->
            when (clickResponse.status) {
                Resource.Status.SUCCESS -> {

                }
                Resource.Status.LOADING -> {

                }
                Resource.Status.EXCEPTION -> {

                }
            }
        })
    }

    private fun setupBalanceGameObserver() {

        viewModel.balanceGame.observe(viewLifecycleOwner, { balanceGameResource ->

            val balanceGameDialog =
                childFragmentManager.findFragmentByTag(DialogTag.BALANCE_DIALOG) as BalanceGameDialog

            when (balanceGameResource.status) {
                Resource.Status.SUCCESS -> {
                    balanceGameDialog.setBalanceGame(
                        balanceGameResource.data!!.swipeId,
                        balanceGameResource.data.questions
                    )
                }
                Resource.Status.LOADING -> {
                    balanceGameDialog.setBalanceGameLoading(getString(R.string.question_loading))
                }
                Resource.Status.EXCEPTION -> {
                    var reloadable = true

                    when (balanceGameResource.exceptionCode) {
                        ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
                        ExceptionCode.ACCOUNT_SHORT_OF_POINT_EXCEPTION,
                        ExceptionCode.SWIPE_CLICKED_EXISTS_EXCEPTION -> {
//                            println("balanceGameError - errorCode: ${balanceGameResource.exceptionCode} | errorMessage: ${balanceGameResource.exceptionMessage}")
                            reloadable = false
                        }
                    }

                    balanceGameDialog.setBalanceGameLoadError(
                        reloadable,
                        balanceGameResource.exceptionMessage!!
                    )
                }
            }
        })
    }

    private fun setupCardsObserver() {
        viewModel.cards.observe(viewLifecycleOwner, { cardResource ->
            when (cardResource.status) {
                Resource.Status.SUCCESS -> {
                    cardStackAdapter.addCards(cardResource.data!!)
                }
                Resource.Status.LOADING -> {
                    println("loading")
                }
                Resource.Status.EXCEPTION -> {
                    println("error")
                }
            }
        })
    }

    private fun setupSwipeCardStackView() {
        cardStackAdapter = CardStackAdapter()

        val cardStackLayoutManager = CardStackLayoutManager(context, this@SwipeFragment)
        cardStackLayoutManager.setCanScrollVertical(false)
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual)

        stvSwipe.layoutManager = cardStackLayoutManager
        stvSwipe.adapter = cardStackAdapter
        stvSwipe.itemAnimator = DefaultItemAnimator()
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

    override fun onBalanceGameClick(swipedId: String, swipeId: Long, answers: Map<Long, Boolean>) {
        println("swipedId: $swipeId | swipeId: $swipeId")
        viewModel.click(swipedId, swipeId, answers)
    }

    override fun onBalanceGameReload(swipedId: String) {
        viewModel.swipe(swipedId)
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
