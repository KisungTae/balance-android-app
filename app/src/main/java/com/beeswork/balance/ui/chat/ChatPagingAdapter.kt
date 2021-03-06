package com.beeswork.balance.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.databinding.ItemChatMessageReceivedBinding
import com.beeswork.balance.databinding.ItemChatMessageSentBinding
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.inflate


class ChatPagingAdapter : PagedListAdapter<ChatMessage, ChatPagingAdapter.ChatMessageViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        return when (viewType) {
            ChatMessageStatus.RECEIVED.ordinal -> ChatMessageViewHolder(
                ItemChatMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ChatMessageStatus.SENT.ordinal -> ChatMessageViewHolder(
                ItemChatMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ChatMessageViewHolder(
                ItemChatMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
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
        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
//                println("areItemsTheSame: oldItemId: ${oldItem.messageId} - newItemId: ${newItem.messageId}")
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
//                println("areContentsTheSame: oldItemId: ${oldItem.messageId} - newItemId: ${newItem.messageId}")
                return oldItem == newItem
            }

        }
    }

    class ChatMessageViewHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindMessageSent(chatMessage: ChatMessage) {
            val sentBinding = binding as ItemChatMessageSentBinding
            sentBinding.tvChatMessageSentBody.text =
                "messageId: ${chatMessage.messageId} | ${chatMessage.body} | id: ${chatMessage.id}"

            when (chatMessage.status) {
                ChatMessageStatus.SENT -> {
                    sentBinding.tvChatMessageSentCreatedAt.text = chatMessage.createdAt.toString()
                    showLayout(sentBinding, View.VISIBLE, View.GONE, View.GONE)
                }
                ChatMessageStatus.SENDING -> showLayout(sentBinding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(sentBinding, View.GONE, View.GONE, View.VISIBLE)
            }
        }

        fun bindMessageReceived(chatMessage: ChatMessage) {
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