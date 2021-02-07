package com.beeswork.balance.ui.chat

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.inflate
import kotlinx.android.synthetic.main.item_chat_message_received.view.*
import kotlinx.android.synthetic.main.item_chat_message_sent.view.*


class ChatPagingAdapter : PagedListAdapter<ChatMessage, ChatPagingAdapter.MessageViewHolder>(
    diffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            ChatMessageStatus.RECEIVED.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
            ChatMessageStatus.SENT.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_sent))
            else -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder.itemViewType) {
                ChatMessageStatus.RECEIVED.ordinal -> holder.bindMessageReceived(it)
                ChatMessageStatus.SENT.ordinal -> holder.bindMessageSent(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            return if (it.status == ChatMessageStatus.RECEIVED) ChatMessageStatus.RECEIVED.ordinal else ChatMessageStatus.SENT.ordinal
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

    class MessageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bindMessageSent(chatMessage: ChatMessage) {
            itemView.tvChatMessageSentBody.text =
                "messageId: ${chatMessage.messageId} | ${chatMessage.body} | id: ${chatMessage.id}"

            when (chatMessage.status) {
                ChatMessageStatus.SENT -> {
                    itemView.tvChatMessageSentCreatedAt.text = chatMessage.createdAt.toString()
                    showLayout(itemView, View.VISIBLE, View.GONE, View.GONE)
                }
                ChatMessageStatus.SENDING -> showLayout(
                    itemView,
                    View.GONE,
                    View.VISIBLE,
                    View.GONE
                )
                ChatMessageStatus.ERROR -> showLayout(itemView, View.GONE, View.GONE, View.VISIBLE)
            }
        }

        fun bindMessageReceived(chatMessage: ChatMessage) {
            itemView.tvChatMessageReceivedBody.text = chatMessage.body
            itemView.tvChatMessageReceivedCreatedAt.text = chatMessage.createdAt.toString()
        }

        companion object {
            fun showLayout(itemView: View, createdAt: Int, loading: Int, errorOptions: Int) {
                itemView.tvChatMessageSentCreatedAt.visibility = createdAt
                itemView.skvChatMessageSentLoading.visibility = loading
                itemView.llChatMessageSentErrorOptions.visibility = errorOptions
            }
        }
    }


}