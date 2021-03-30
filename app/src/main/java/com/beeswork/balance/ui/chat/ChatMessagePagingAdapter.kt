package com.beeswork.balance.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemChatMessageReceivedBinding
import com.beeswork.balance.databinding.ItemChatMessageSentBinding
import com.beeswork.balance.databinding.ItemChatMessageSeparatorBinding
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.util.safeLet
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.*


class ChatMessagePagingAdapter(
    repPhotoEndPoint: String?
) : PagingDataAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>(diffCallback) {

    private val repPhotoEndPoint = repPhotoKey?.let {
        EndPoint.ofPhotoBucket(matchedId, repPhotoKey)
    } ?: R.drawable.ic_baseline_account_circle


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ChatMessageStatus.SEPARATOR.ordinal -> SeparatorViewHolder(
                ItemChatMessageSeparatorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                parent.context
            )
            ChatMessageStatus.RECEIVED.ordinal -> ReceivedViewHolder(
                ItemChatMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                parent.context
            )
            else -> SentViewHolder(
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

            holder.bind(
                it,
                isSameAsPrev(it, position),
                isSameAsNext(it, position),
                marginTop(position),
                repPhotoEndPoint
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            return when (it.status) {
                ChatMessageStatus.SEPARATOR -> ChatMessageStatus.SEPARATOR.ordinal
                ChatMessageStatus.RECEIVED -> ChatMessageStatus.RECEIVED.ordinal
                else -> ChatMessageStatus.SENT.ordinal
            }
        } ?: return ChatMessageStatus.SENT.ordinal
    }

    private fun marginTop(position: Int): Int {
        if (position == (itemCount - 1)) return MARGIN_LONG
        val currentViewType = getItemViewType(position)
        val nextViewType = getItemViewType(position + 1)

        return when {
            currentViewType == ChatMessageStatus.SEPARATOR.ordinal -> MARGIN_LONG
            nextViewType == ChatMessageStatus.SEPARATOR.ordinal -> MARGIN_LONG
            currentViewType == nextViewType -> MARGIN_SHORT
            else -> MARGIN_MEDIUM
        }
    }

    private fun isSameAsPrev(currentChatMessage: ChatMessageDomain, position: Int): Boolean {
        var sameAsPrev = false
        if (position > 0)
            sameAsPrev = isOnSameTime(currentChatMessage, position - 1)
        return sameAsPrev
    }

    private fun isSameAsNext(currentChatMessage: ChatMessageDomain, position: Int): Boolean {
        var sameAsNext = false
        if (position < (itemCount - 1))
            sameAsNext = isOnSameTime(currentChatMessage, position + 1)
        return sameAsNext
    }

    private fun isOnSameTime(
        currentChatMessage: ChatMessageDomain,
        targetPosition: Int
    ): Boolean {
        var onSameTime = false
        getItem(targetPosition)?.let { targetChatMessage ->
            if (currentChatMessage.status == targetChatMessage.status) {
                safeLet(currentChatMessage.createdAt, targetChatMessage.createdAt) { c, t ->
                    if (c.truncatedTo(ChronoUnit.MINUTES).equals(t.truncatedTo(ChronoUnit.MINUTES)))
                        onSameTime = true
                }
            }
        }
        return onSameTime
    }

    companion object {
        private const val MARGIN_SHORT = 5
        private const val MARGIN_MEDIUM = 15
        private const val MARGIN_LONG = 30

        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessageDomain>() {
            override fun areItemsTheSame(oldItem: ChatMessageDomain, newItem: ChatMessageDomain): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ChatMessageDomain, newItem: ChatMessageDomain): Boolean =
                oldItem == newItem
        }
    }

    abstract class ViewHolder(
        val root: LinearLayout
    ) : RecyclerView.ViewHolder(root) {

        abstract fun bind(
            chatMessage: ChatMessageDomain,
            sameAsPrev: Boolean,
            sameAsNext: Boolean,
            marginTop: Int,
            repPhotoEndPoint: Any
        )

        protected fun glideRequestOptions(): RequestOptions {
            return RequestOptions().placeholder(R.drawable.ic_baseline_account_circle)
                .error(R.drawable.ic_baseline_account_circle)
                .circleCrop()
        }

        protected fun setMarginTop(root: LinearLayout, marginTop: Int, context: Context) {
            val marginLayoutParams = root.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = (marginTop * context.resources.displayMetrics.density).toInt()
            root.layoutParams = marginLayoutParams
        }

        protected fun truncateToMinute(createdAt: OffsetDateTime?, onSameTime: Boolean): String {
            return if (onSameTime) ""
            else createdAt?.toLocalTime()?.format(DateTimePattern.ofTimeWithMeridiem(Locale.getDefault())) ?: ""
        }
    }

    class ReceivedViewHolder(
        private val binding: ItemChatMessageReceivedBinding,
        private val context: Context
    ) : ViewHolder(binding.root) {

        override fun bind(
            chatMessage: ChatMessageDomain,
            sameAsPrev: Boolean,
            sameAsNext: Boolean,
            marginTop: Int,
            repPhotoEndPoint: Any
        ) {
            binding.tvChatMessageReceivedBody.text = chatMessage.body
            binding.ivChatMessageReceivedProfile.visibility = if (sameAsNext) View.INVISIBLE else View.VISIBLE
            binding.tvChatMessageReceivedCreatedAt.text = truncateToMinute(chatMessage.createdAt, sameAsPrev)
            setMarginTop(binding.root, marginTop, context)
            Glide.with(context)
                .load(repPhotoEndPoint)
                .apply(glideRequestOptions())
                .into(binding.ivChatMessageReceivedProfile)
        }
    }

    class SentViewHolder(
        private val binding: ItemChatMessageSentBinding,
        private val context: Context
    ) : ViewHolder(binding.root) {

        override fun bind(
            chatMessage: ChatMessageDomain,
            sameAsPrev: Boolean,
            sameAsNext: Boolean,
            marginTop: Int,
            repPhotoEndPoint: Any
        ) {
            binding.tvChatMessageSentBody.text = chatMessage.body
            setMarginTop(binding.root, marginTop, context)

            when (chatMessage.status) {
                ChatMessageStatus.SENDING -> showLayout(binding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(binding, View.GONE, View.GONE, View.VISIBLE)
                else -> {
                    binding.tvChatMessageSentCreatedAt.text = truncateToMinute(chatMessage.createdAt, sameAsPrev)
                    showLayout(binding, View.VISIBLE, View.GONE, View.GONE)
                }
            }
        }

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
    }

    class SeparatorViewHolder(
        private val binding: ItemChatMessageSeparatorBinding,
        private val context: Context
    ) : ViewHolder(binding.root) {
        override fun bind(
            chatMessage: ChatMessageDomain,
            sameAsPrev: Boolean,
            sameAsNext: Boolean,
            marginTop: Int,
            repPhotoEndPoint: Any
        ) {
            binding.tvChatSeparatorTitle.text = chatMessage.body
            setMarginTop(binding.root, marginTop, context)
        }
    }
}