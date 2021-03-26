package com.beeswork.balance.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemChatMessageReceivedBinding
import com.beeswork.balance.databinding.ItemChatMessageSentBinding
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.safeLet
import com.bumptech.glide.request.RequestOptions
import org.threeten.bp.format.DateTimeFormatter


class ChatMessagePagingAdapter : PagingDataAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ChatMessageStatus.RECEIVED.ordinal -> ViewHolder(
                ItemChatMessageReceivedBinding.inflate(LayoutInflater.from(parent.context)),
                parent.context
            )
            else -> ViewHolder(ItemChatMessageSentBinding.inflate(LayoutInflater.from(parent.context)), parent.context)
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

    private fun isOnSameTime(position: Int): Boolean {
        if (position >= (itemCount - 1)) return false
        val currentChatMessage = getItem(position)
        val nextChatMessage = getItem(position)
        if (currentChatMessage?.status != nextChatMessage?.status) return false

        safeLet(getItem(position), getItem(position + 1)) { currentChatMessage, nextChatMessage ->
            if (currentChatMessage.status != nextChatMessage.status) return@safeLet false
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
        private val binding: ViewBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindMessageSent(chatMessageDomain: ChatMessageDomain) {
            val sentBinding = binding as ItemChatMessageSentBinding
            sentBinding.tvChatMessageSentBody.text = chatMessageDomain.body
//            Glide.with(context).load(photoEndPoint).apply(glideRequestOptions()).into(binding.ivMatchProfilePicture)
            when (chatMessageDomain.status) {
                ChatMessageStatus.SENT -> {
                    sentBinding.tvChatMessageSentCreatedAt.text = chatMessageDomain.createdAt.toString()
                    showLayout(sentBinding, View.VISIBLE, View.GONE, View.GONE)
                }
                ChatMessageStatus.SENDING -> showLayout(sentBinding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(sentBinding, View.GONE, View.GONE, View.VISIBLE)
            }
        }

        fun bindMessageReceived(chatMessageDomain: ChatMessageDomain) {
            val receivedBinding = binding as ItemChatMessageReceivedBinding
            receivedBinding.tvChatMessageReceivedBody.text = chatMessageDomain.body
            receivedBinding.tvChatMessageReceivedCreatedAt.text = chatMessageDomain.createdAt?.toLocalTime()
                ?.format(DateTimeFormatter.ofPattern("h:mm a"))
        }

        companion object {
            private fun showLayout(
                sentBinding: ItemChatMessageSentBinding,
                createdAt: Int,
                loading: Int,
                errorOptions: Int
            ) {
                sentBinding.tvChatMessageSentCreatedAt.visibility = createdAt
                sentBinding.skvChatMessageSentLoading.visibility = loading
                sentBinding.llChatMessageSentErrorOptions.visibility = errorOptions
            }

            private fun glideRequestOptions(): RequestOptions {
                return RequestOptions().placeholder(R.drawable.ic_baseline_account_circle)
                    .error(R.drawable.ic_baseline_account_circle)
                    .circleCrop()
            }
        }
    }


}