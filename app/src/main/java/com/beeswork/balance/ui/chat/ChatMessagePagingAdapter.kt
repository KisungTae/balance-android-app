package com.beeswork.balance.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.beeswork.balance.databinding.ItemChatMessageReceivedBinding
import com.beeswork.balance.databinding.ItemChatMessageSentBinding
import com.beeswork.balance.internal.constant.ChatMessageStatus


class ChatMessagePagingAdapter : PagingDataAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ChatMessageStatus.RECEIVED.ordinal -> ViewHolder(
                ItemChatMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ChatMessageStatus.SENT.ordinal -> ViewHolder(
                ItemChatMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ViewHolder(
                ItemChatMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder.itemViewType) {
                ChatMessageStatus.RECEIVED.ordinal -> holder.bindMessageReceived(it)
                ChatMessageStatus.SENT.ordinal -> holder.bindMessageSent(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            return if (it.status == ChatMessageStatus.RECEIVED) ChatMessageStatus.RECEIVED.ordinal
            else ChatMessageStatus.SENT.ordinal
        } ?: kotlin.run {
            return ChatMessageStatus.RECEIVED.ordinal
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessageDomain>() {
            override fun areItemsTheSame(oldItem: ChatMessageDomain, newItem: ChatMessageDomain): Boolean {
//                println("areItemsTheSame: oldItemId: ${oldItem.messageId} - newItemId: ${newItem.messageId}")
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChatMessageDomain, newItem: ChatMessageDomain): Boolean {
//                println("areContentsTheSame: oldItemId: ${oldItem.messageId} - newItemId: ${newItem.messageId}")
                return oldItem == newItem
            }

        }
    }

    class ViewHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindMessageSent(chatMessage: ChatMessageDomain) {
            val sentBinding = binding as ItemChatMessageSentBinding
            sentBinding.tvChatMessageSentBody.text =
                "messageId: ${chatMessage.key} | ${chatMessage.body} | id: ${chatMessage.id}"

            when (chatMessage.status) {
                ChatMessageStatus.SENT -> {
                    sentBinding.tvChatMessageSentCreatedAt.text = chatMessage.createdAt.toString()
                    showLayout(sentBinding, View.VISIBLE, View.GONE, View.GONE)
                }
                ChatMessageStatus.SENDING -> showLayout(sentBinding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(sentBinding, View.GONE, View.GONE, View.VISIBLE)
            }
        }

        fun bindMessageReceived(chatMessage: ChatMessageDomain) {
            val receivedBinding = binding as ItemChatMessageReceivedBinding
            receivedBinding.tvChatMessageReceivedBody.text = chatMessage.body
            receivedBinding.tvChatMessageReceivedCreatedAt.text = chatMessage.createdAt.toString()
        }

        companion object {
            fun showLayout(sentBinding: ItemChatMessageSentBinding, createdAt: Int, loading: Int, errorOptions: Int) {
                sentBinding.tvChatMessageSentCreatedAt.visibility = createdAt
                sentBinding.skvChatMessageSentLoading.visibility = loading
                sentBinding.llChatMessageSentErrorOptions.visibility = errorOptions
            }
        }
    }


}