package com.beeswork.balance.ui.chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.internal.inflate
import kotlinx.android.synthetic.main.item_chat_message_received.view.*
import kotlinx.android.synthetic.main.item_chat_message_sent.view.*

class ChatRecyclerViewAdapter :
    RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatMessageViewHolder>() {

    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        return when (viewType) {
            ChatMessage.Status.RECEIVED.ordinal -> ChatMessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
            ChatMessage.Status.SENT.ordinal -> ChatMessageViewHolder(parent.inflate(R.layout.item_chat_message_sent))
            else -> ChatMessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
        }
    }

    override fun onBindViewHolder(holderChat: ChatMessageViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        when (holderChat.itemViewType) {
            ChatMessage.Status.RECEIVED.ordinal -> holderChat.bindMessageReceived(chatMessage)
            ChatMessage.Status.SENT.ordinal -> holderChat.bindMessageSent(chatMessage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = chatMessages[position]
        return if (chatMessage.status == ChatMessage.Status.RECEIVED) ChatMessage.Status.RECEIVED.ordinal
        else ChatMessage.Status.SENT.ordinal
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    fun submitList(chatMessages: List<ChatMessage>) {

    }



    class ChatMessageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bindMessageSent(chatMessage: ChatMessage) {
            itemView.tvChatMessageSentBody.text =
                "messageId: ${chatMessage.messageId} | ${chatMessage.body}"

            when (chatMessage.status) {
                ChatMessage.Status.SENT -> {
                    itemView.tvChatMessageSentCreatedAt.text = chatMessage.createdAt.toString()
                    showLayout(itemView, View.VISIBLE, View.GONE, View.GONE)
                }
                ChatMessage.Status.SENDING -> showLayout(
                    itemView,
                    View.GONE,
                    View.VISIBLE,
                    View.GONE
                )
                ChatMessage.Status.ERROR -> showLayout(itemView, View.GONE, View.GONE, View.VISIBLE)
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

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean =
                oldItem == newItem
        }
    }

}