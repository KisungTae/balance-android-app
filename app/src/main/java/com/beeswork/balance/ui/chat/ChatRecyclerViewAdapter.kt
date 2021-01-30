package com.beeswork.balance.ui.chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.internal.util.inflate
import kotlinx.android.synthetic.main.item_chat_message_received.view.*
import kotlinx.android.synthetic.main.item_chat_message_sent.view.*

class ChatRecyclerViewAdapter : RecyclerView.Adapter<ChatRecyclerViewAdapter.MessageViewHolder>() {

    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            ChatMessage.Status.RECEIVED.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
            ChatMessage.Status.SENT.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_sent))
            else -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        chatMessages[position].let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int {
        chatMessages[position].let {
            return if (it.status == ChatMessage.Status.RECEIVED) ChatMessage.Status.RECEIVED.ordinal
            else ChatMessage.Status.SENT.ordinal
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
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
                ChatMessage.Status.SENT -> {
                    itemView.tvChatMessageSentBody.text =
                        "messageId: ${chatMessage.messageId} | ${chatMessage.body} | id: ${chatMessage.id}"
                    itemView.tvChatMessageSentCreatedAt.text = chatMessage.createdAt.toString()
                    showLayout(itemView, View.VISIBLE, View.GONE, View.GONE)
                }
                ChatMessage.Status.SENDING -> {
                    itemView.tvChatMessageSentBody.text =
                        "messageId: ${chatMessage.messageId} | ${chatMessage.body} | id: ${chatMessage.id}"
                    showLayout(itemView, View.GONE, View.VISIBLE, View.GONE)
                }
                ChatMessage.Status.ERROR -> showLayout(itemView, View.GONE, View.GONE, View.VISIBLE)
                ChatMessage.Status.RECEIVED -> {
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