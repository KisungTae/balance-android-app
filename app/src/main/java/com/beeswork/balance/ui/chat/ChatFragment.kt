package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.ChatIdNotFoundException
import com.beeswork.balance.ui.base.ScopeFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance

class ChatFragment: ScopeFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: ((Long) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatPagedListAdapter: ChatPagedListAdapter

    //  TODO: remove me
    private val balanceRepository: BalanceRepository by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs = arguments?.let { ChatFragmentArgs.fromBundle(it) }
        val chatId = safeArgs?.chatId ?: throw ChatIdNotFoundException()
        viewModel = ViewModelProvider(this, viewModelFactory(chatId)).get(ChatViewModel::class.java)

        bindUI()

        btnAddMessage.setOnClickListener {
            println("clicked to add message")
            balanceRepository.insertMessage(chatId)
        }
    }

    private fun bindUI() = launch {
        val messages = viewModel.messages.await()

        chatPagedListAdapter = ChatPagedListAdapter()

        rvChat.adapter = chatPagedListAdapter

        val layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true

        rvChat.layoutManager = layoutManager

        rvChat.scrollToPosition(0)

        messages.observe(viewLifecycleOwner, Observer { pagedMessageList ->
            pagedMessageList?.let {
                chatPagedListAdapter.submitList(pagedMessageList)
            }
        })
    }
}