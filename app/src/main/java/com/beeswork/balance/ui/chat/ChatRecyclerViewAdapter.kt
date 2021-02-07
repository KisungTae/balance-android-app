package com.beeswork.balance.ui.chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.inflate
import kotlinx.android.synthetic.main.item_chat_message_received.view.*
import kotlinx.android.synthetic.main.item_chat_message_sent.view.*

class ChatRecyclerViewAdapter : RecyclerView.Adapter<ChatRecyclerViewAdapter.MessageViewHolder>() {

    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            ChatMessageStatus.RECEIVED.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
            ChatMessageStatus.SENT.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_sent))
            else -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        chatMessages[position].let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int {
        chatMessages[position].let {
            return if (it.status == ChatMessageStatus.RECEIVED) ChatMessageStatus.RECEIVED.ordinal
            else ChatMessageStatus.SENT.ordinal
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    fun fetchInitial(chatMessages: List<ChatMessage>) {
        
    }

    fun append() {

    }

    fun prepend() {

    }

    fun submit(newChatMessages: List<ChatMessage>) {
        chatMessages.addAll(0, newChatMessages)
        notifyDataSetChanged()
    }

    class MessageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatMessage: ChatMessage) {
            when (chatMessage.status) {
                ChatMessageStatus.SENT -> {
                    itemView.tvChatMessageSentBody.text =
                        "messageId: ${chatMessage.messageId} | ${chatMessage.body} | id: ${chatMessage.id}"
                    itemView.tvChatMessageSentCreatedAt.text = chatMessage.createdAt.toString()
                    showLayout(itemView, View.VISIBLE, View.GONE, View.GONE)
                }
                ChatMessageStatus.SENDING -> {
                    itemView.tvChatMessageSentBody.text =
                        "messageId: ${chatMessage.messageId} | ${chatMessage.body} | id: ${chatMessage.id}"
                    showLayout(itemView, View.GONE, View.VISIBLE, View.GONE)
                }
                ChatMessageStatus.ERROR -> showLayout(itemView, View.GONE, View.GONE, View.VISIBLE)
                ChatMessageStatus.RECEIVED -> {
                    itemView.tvChatMessageReceivedBody.text = chatMessage.body
                    itemView.tvChatMessageReceivedCreatedAt.text = chatMessage.createdAt.toString()
                }
            }
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