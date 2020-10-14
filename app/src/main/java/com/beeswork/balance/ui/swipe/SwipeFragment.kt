package com.beeswork.balance.ui.swipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.beeswork.balance.R
import com.beeswork.balance.data.dao.FCMTokenDAO
import com.beeswork.balance.data.network.BalanceService
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.DialogTag
import com.beeswork.balance.internal.constant.MIN_CARD_STACK_SIZE
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.ui.base.ScopeFragment
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.android.synthetic.main.fragment_swipe.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance




class SwipeFragment : ScopeFragment(), KodeinAware, CardStackListener,
    BalanceGameDialog.BalanceGameListener {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeViewModelFactory by instance()
    private lateinit var viewModel: SwipeViewModel
    private lateinit var cardStackAdapter: CardStackAdapter
    private val preferenceProvider: PreferenceProvider by instance()

    //  TODO: remove me
    private val balanceService: BalanceService by instance()
    private val balanceRepository: BalanceRepository by instance()
    private val fcmTokenDAO: FCMTokenDAO by instance()


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
    }

    private fun bindUI() = launch {

        btnSwipeFilter.setOnClickListener {
//            TODO: remove me fetchCards
            viewModel.fetchCards()
            SwipeFilterDialog(preferenceProvider).show(childFragmentManager, DialogTag.SWIPE_FILTER_DIALOG)
        }



        photoBtn.setOnClickListener {
//            balanceRepository.insertMatch()
//            balanceRepository.insertFCMToken("test token")
            CoroutineScope(Dispatchers.IO).launch {
                println("fcm token:"+ fcmTokenDAO.get()[0].token)
            }

        }

        cardStackAdapter = CardStackAdapter()
        val manager = CardStackLayoutManager(context, this@SwipeFragment)

        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.Manual)

        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter
        cardStackView.itemAnimator = DefaultItemAnimator()

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

        viewModel.balanceGame.observe(viewLifecycleOwner, { balanceGameResource ->

            var balanceGameDialog = childFragmentManager.findFragmentByTag(DialogTag.BALANCE_DIALOG)
            if (balanceGameDialog != null) {
                balanceGameDialog = balanceGameDialog as BalanceGameDialog

                when (balanceGameResource.status) {
                    Resource.Status.SUCCESS -> {
                        println(balanceGameResource.data!!.questions)
                        balanceGameDialog.setBalanceGame(balanceGameResource.data!!.swipeId,
                                                         balanceGameResource.data.questions)
                    }
                    Resource.Status.LOADING -> {
                        balanceGameDialog.setBalanceGameLoading()
                    }
                    Resource.Status.EXCEPTION -> {
                        var reloadable = true

                        when (balanceGameResource.exceptionCode) {
                            ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
                            ExceptionCode.ACCOUNT_SHORT_OF_POINT_EXCEPTION,
                            ExceptionCode.SWIPE_CLICKED_EXISTS_EXCEPTION -> {
                                println("balanceGameError - errorCode: ${balanceGameResource.exceptionCode} | errorMessage: ${balanceGameResource.exceptionMessage}")
                                reloadable = false
                            }
                        }

                        balanceGameDialog.setBalanceGameError(reloadable,
                                                              balanceGameResource.exceptionMessage!!)
                    }
                }
            }

        })
    }

    override fun onBalanceGameClicked(swipedId: String, swipeId: Long) {
        println("swipedId: $swipeId | swipeId: $swipeId")
        viewModel.click(swipedId, swipeId)
    }

    override fun onBalanceGameReload(swipedId: String) {
        viewModel.swipe(swipedId)
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {

        val removedCard = cardStackAdapter.removeCard()

        if (cardStackAdapter.itemCount < MIN_CARD_STACK_SIZE)
            viewModel.fetchCards()

        if (direction == Direction.Right && removedCard != null) {
            viewModel.swipe(removedCard.accountId)
            BalanceGameDialog(removedCard.accountId, this@SwipeFragment).show(childFragmentManager,
                DialogTag.BALANCE_DIALOG)
        }
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
