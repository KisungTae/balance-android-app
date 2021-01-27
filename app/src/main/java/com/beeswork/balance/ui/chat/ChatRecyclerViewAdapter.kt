package com.beeswork.balance.ui.chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.internal.inflate
import kotlinx.android.synthetic.main.item_chat_message_received.view.*
import kotlinx.android.synthetic.main.item_chat_message_sent.view.*
import kotlin.random.Random

class ChatRecyclerViewAdapter: RecyclerView.Adapter<ChatRecyclerViewAdapter.MessageViewHolder>() {

    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            ChatMessage.Status.RECEIVED.ordinal -> MessageViewHolder(
                parent.inflate(
                    R.layout.item_chat_message_received
                )
            )
            ChatMessage.Status.SENT.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_sent))
            else -> MessageViewHolder(parent.inflate(R.layout.item_chat_message_received))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        chatMessages[position].let {
            println("onBindViewHolder: ${it.messageId}")
            when (holder.itemViewType) {
                ChatMessage.Status.RECEIVED.ordinal -> holder.bindMessageReceived(it)
                ChatMessage.Status.SENT.ordinal -> holder.bindMessageSent(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        chatMessages[position].let {
            return if (it.status == ChatMessage.Status.RECEIVED) ChatMessage.Status.RECEIVED.ordinal else ChatMessage.Status.SENT.ordinal
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    fun submit(newChatMessages: List<ChatMessage>) {
        chatMessages.addAll(0, newChatMessages)
        notifyDataSetChanged()
    }

    fun updateItem() {
        val item = chatMessages[9]
        val random = Random(10)
        item.body = "updated chat message ${random.nextInt()}"
//        notifyItemChanged(9)
    }


    class MessageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bindMessageSent(chatMessage: ChatMessage) {
            itemView.tvChatMessageSentBody.text =
                "messageId: ${chatMessage.messageId} | ${chatMessage.body} | id: ${chatMessage.id}"

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


}