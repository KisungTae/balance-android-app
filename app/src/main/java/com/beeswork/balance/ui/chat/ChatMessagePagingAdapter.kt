package com.beeswork.balance.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemChatMessageReceivedBinding
import com.beeswork.balance.databinding.ItemChatMessageSentBinding
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.safeLet
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit


class ChatMessagePagingAdapter : PagingDataAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ChatMessageStatus.RECEIVED.ordinal -> ViewHolder(
                ItemChatMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                parent.context
            )
            else -> ViewHolder(
                ItemChatMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                parent.context
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            println("position: $position - key: ${it.key} - size: $itemCount - viewType: ${holder.itemViewType}")
            when (holder.itemViewType) {
                ChatMessageStatus.RECEIVED.ordinal -> holder.bindMessageReceived(
                    it,
                    isSameAsPrev(it, position, false),
                    isSameAsNext(it, position),
                    marginTop(position)
                )
                ChatMessageStatus.SENT.ordinal -> holder.bindMessageSent(
                    it,
                    isSameAsPrev(it, position, true),
                    isSameAsNext(it, position),
                    marginTop(position)
                )
            }
        }
    }

    private fun marginTop(position: Int): Int {
        return when {
            position == (itemCount - 1) -> MARGIN_LONG
            getItemViewType(position) == getItemViewType(position + 1) -> MARGIN_SHORT
            else -> MARGIN_LONG
        }
    }

    private fun isSameAsPrev(currentChatMessage: ChatMessageDomain, position: Int, isSent: Boolean): Boolean {
        var sameAsPrev = false
        if (position > 0)
            sameAsPrev = isOnSameTime(currentChatMessage, position, position - 1, isSent)
        return sameAsPrev
    }

    private fun isSameAsNext(currentChatMessage: ChatMessageDomain, position: Int): Boolean {
        var sameAsNext = false
        if (position < (itemCount - 1))
            sameAsNext = isOnSameTime(currentChatMessage, position, position + 1, false)
        return sameAsNext
    }

    private fun isOnSameTime(
        currentChatMessage: ChatMessageDomain,
        position: Int,
        targetPosition: Int,
        isSent: Boolean
    ): Boolean {
        var onSameTime = false
        if (getItemViewType(position) != getItemViewType(targetPosition)) return onSameTime
        getItem(targetPosition)?.let { targetChatMessage ->
            if (isSent && currentChatMessage.status != targetChatMessage.status) return onSameTime
            safeLet(currentChatMessage.createdAt, targetChatMessage.createdAt) { c, t ->
                if (c.truncatedTo(ChronoUnit.MINUTES).equals(t.truncatedTo(ChronoUnit.MINUTES)))
                    onSameTime = true
            }
        }
        return onSameTime
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            return if (it.status == ChatMessageStatus.RECEIVED) ChatMessageStatus.RECEIVED.ordinal
            else ChatMessageStatus.SENT.ordinal
        } ?: kotlin.run {
            return ChatMessageStatus.SENT.ordinal
        }
    }

    companion object {

        private const val MARGIN_SHORT = 5
        private const val MARGIN_LONG = 50

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

        fun bindMessageSent(chatMessage: ChatMessageDomain, sameAsPrev: Boolean, sameAsNext: Boolean, marginTop: Int) {
            val sentBinding = binding as ItemChatMessageSentBinding
            sentBinding.tvChatMessageSentBody.text = "${chatMessage.key} - ${chatMessage.status} - ${chatMessage.body}"
            setMarginTop(binding.root, sameAsNext, context)

            when (chatMessage.status) {
                ChatMessageStatus.SENT -> {
                    binding.tvChatMessageSentCreatedAt.text = truncateToMinute(chatMessage.createdAt, sameAsPrev)
                    showLayout(sentBinding, View.VISIBLE, View.GONE, View.GONE)
                }
                ChatMessageStatus.SENDING -> showLayout(sentBinding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(sentBinding, View.GONE, View.GONE, View.VISIBLE)
            }
        }

        fun bindMessageReceived(
            chatMessage: ChatMessageDomain,
            sameAsPrev: Boolean,
            sameAsNext: Boolean,
            marginTop: Int
        ) {
            val receivedBinding = binding as ItemChatMessageReceivedBinding
            receivedBinding.tvChatMessageReceivedBody.text = "${chatMessage.key} - ${chatMessage.status} - ${chatMessage.body}"
//            receivedBinding.tvChatMessageReceivedBody.text = chatMessageDomain.body

            if (sameAsNext) binding.ivChatMessageReceivedProfile.visibility = View.INVISIBLE
            else binding.ivChatMessageReceivedProfile.visibility = View.VISIBLE

            binding.tvChatMessageReceivedCreatedAt.text = truncateToMinute(chatMessage.createdAt, sameAsPrev)
            setMarginTop(binding.root, sameAsNext, context)
            Glide.with(context)
                .load(R.drawable.person3)
                .apply(glideRequestOptions())
                .into(binding.ivChatMessageReceivedProfile)
        }

        companion object {

            private const val MARGIN_SHORT = 5
            private const val MARGIN_LONG = 50

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

            private fun truncateToMinute(createdAt: OffsetDateTime?, onSameTime: Boolean): String {
                return if (onSameTime) ""
                else createdAt?.toLocalTime()?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: ""
            }

            private fun setMarginTop(root: LinearLayout, sameAsNext: Boolean, context: Context) {
                val marginLayoutParams = root.layoutParams as ViewGroup.MarginLayoutParams
                val margin = if (sameAsNext) MARGIN_SHORT else MARGIN_LONG
                val marginBottom = margin * context.resources.displayMetrics.density
                marginLayoutParams.topMargin = marginBottom.toInt()
                root.layoutParams = marginLayoutParams
            }
        }
    }


}