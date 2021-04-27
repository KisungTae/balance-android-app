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
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.match.MatchDomain
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import java.util.*


class ChatMessagePagingAdapter(
    private val chatMessageSentListener: ChatMessageSentListener
): PagingDataAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>(diffCallback) {

    private var repPhotoEndPoint: String? = null

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
                parent.context,
                chatMessageSentListener
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder.itemViewType) {
                ChatMessageStatus.SEPARATOR.ordinal -> {
                    (holder as SeparatorViewHolder).bind(
                        it,
                        marginTop(holder.itemViewType, position)
                    )
                }
                ChatMessageStatus.RECEIVED.ordinal -> {
                    (holder as ReceivedViewHolder).bind(
                        it,
                        marginTop(holder.itemViewType, position),
                        repPhotoEndPoint
                    )
                }
                else -> {
                    (holder as SentViewHolder).bind(
                        it,
                        marginTop(holder.itemViewType, position)
                    )
                }
            }
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

    fun onRepPhotoLoaded(repPhotoEndPoint: String?) {
        this.repPhotoEndPoint = repPhotoEndPoint
        notifyDataSetChanged()
    }

    private fun marginTop(itemViewType: Int, position: Int): Int {
        if (position == (itemCount - 1)) return MARGIN_LONG
        val nextViewType = getItemViewType(position + 1)

        return when {
            itemViewType == ChatMessageStatus.SEPARATOR.ordinal -> MARGIN_LONG
            nextViewType == ChatMessageStatus.SEPARATOR.ordinal -> MARGIN_LONG
            itemViewType == nextViewType -> MARGIN_SHORT
            else -> MARGIN_MEDIUM
        }
    }

    fun getChatMessage(position: Int): ChatMessageDomain? {
        return getItem(position)
    }

    interface ChatMessageSentListener {
        fun onResendChatMessage(position: Int)
        fun onDeleteChatMessage(position: Int)
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

        protected fun setMarginTop(root: LinearLayout, marginTop: Int, context: Context) {
            val marginLayoutParams = root.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = (marginTop * context.resources.displayMetrics.density).toInt()
            root.layoutParams = marginLayoutParams
        }

        protected fun formatTimeCreatedAt(timeCreatedAt: LocalTime?): String {
            return timeCreatedAt?.format(DateTimePattern.ofTimeWithMeridiem()) ?: ""
        }
    }

    class ReceivedViewHolder(
        private val binding: ItemChatMessageReceivedBinding,
        private val context: Context
    ) : ViewHolder(binding.root) {

        fun bind(
            chatMessage: ChatMessageDomain,
            marginTop: Int,
            repPhotoEndPoint: String?,
        ) {
            binding.tvChatMessageReceivedBody.text = chatMessage.body
            binding.ivChatMessageReceivedRepPhoto.visibility = if (chatMessage.showRepPhoto) View.VISIBLE else View.INVISIBLE
            binding.tvChatMessageReceivedCreatedAt.text = formatTimeCreatedAt(chatMessage.timeCreatedAt)
            setMarginTop(binding.root, marginTop, context)
            repPhotoEndPoint?.let {
                Glide.with(context).load(it).apply(glideRequestOptions()).into(binding.ivChatMessageReceivedRepPhoto)
            }
        }

        private fun glideRequestOptions(): RequestOptions {
            return RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.HIGH)
                .placeholder(R.drawable.ic_baseline_account_circle)
                .error(R.drawable.ic_baseline_account_circle)
                .circleCrop()
        }
    }

    class SentViewHolder(
        private val binding: ItemChatMessageSentBinding,
        private val context: Context,
        private val chatMessageSentListener: ChatMessageSentListener
    ) : ViewHolder(binding.root) {

        fun bind(
            chatMessage: ChatMessageDomain,
            marginTop: Int
        ) {
            binding.tvChatMessageSentBody.text = chatMessage.body
            binding.btnChatMessageSentResend.setOnClickListener {
                chatMessageSentListener.onResendChatMessage(absoluteAdapterPosition)
            }
            binding.btnChatMessageSentDelete.setOnClickListener {
                chatMessageSentListener.onDeleteChatMessage(absoluteAdapterPosition)
            }
            setMarginTop(binding.root, marginTop, context)
            when (chatMessage.status) {
                ChatMessageStatus.SENDING -> showLayout(binding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(binding, View.GONE, View.GONE, View.VISIBLE)
                else -> {
                    binding.tvChatMessageSentCreatedAt.text = formatTimeCreatedAt(chatMessage.timeCreatedAt)
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
        fun bind(
            chatMessage: ChatMessageDomain,
            marginTop: Int,
        ) {
            binding.tvChatSeparatorTitle.text = chatMessage.body
            setMarginTop(binding.root, marginTop, context)
        }
    }
}