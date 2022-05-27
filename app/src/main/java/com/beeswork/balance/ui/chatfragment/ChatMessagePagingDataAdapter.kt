package com.beeswork.balance.ui.chatfragment

import android.content.Context
import android.graphics.Bitmap
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
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.toDP
import com.bumptech.glide.Glide
import org.threeten.bp.format.DateTimeFormatter


class ChatMessagePagingDataAdapter(
    private val chatMessageListener: ChatMessageListener
) : PagingDataAdapter<ChatMessageItemUIState, ChatMessagePagingDataAdapter.ViewHolder>(diffCallback) {

    private val topMarginShort: Int = 5.toDP()
    private val topMarginMedium: Int = 15.toDP()
    private val topMarginLong: Int = 30.toDP()

    private var profilePhoto: Bitmap? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ChatMessageStatus.SEPARATOR.ordinal -> {
                SeparatorViewHolder(
                    ItemChatMessageSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    parent.context
                )
            }
            ChatMessageStatus.RECEIVED.ordinal -> {
                ReceivedViewHolder(
                    ItemChatMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    parent.context
                )
            }
            else -> {
                SentViewHolder(
                    ItemChatMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    chatMessageListener,
                    parent.context
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { chatMessageDomain ->
            chatMessageDomain.topMargin = calculateTopMargin(chatMessageDomain, position)
            when (holder) {
                is SeparatorViewHolder -> {
                    holder.bind(chatMessageDomain)
                }
                is ReceivedViewHolder -> {
                    holder.bind(chatMessageDomain, profilePhoto)
                }
                is SentViewHolder -> {
                    holder.bind(chatMessageDomain)
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
        } ?: return ChatMessageStatus.SEPARATOR.ordinal
    }

    fun setProfilePhoto(newProfilePhoto: Bitmap?) {
        if ((profilePhoto == null && newProfilePhoto != null) || (profilePhoto != null && newProfilePhoto == null)) {
            this.profilePhoto = newProfilePhoto
            notifyDataSetChanged()
        }
    }

    private fun calculateTopMargin(chatMessageItemUIState: ChatMessageItemUIState, position: Int): Int {
        val nextPosition = position + 1
        if (nextPosition == itemCount) {
            return topMarginLong
        }
        val nextChatMessageDomain = getItem(nextPosition)
        return when {
            chatMessageItemUIState.status == ChatMessageStatus.SEPARATOR -> topMarginLong
            nextChatMessageDomain?.status == ChatMessageStatus.SEPARATOR -> topMarginLong
            chatMessageItemUIState.status == nextChatMessageDomain?.status -> topMarginShort
            else -> topMarginMedium
        }
    }

    fun getChatMessage(position: Int): ChatMessageItemUIState? {
        return getItem(position)
    }

    interface ChatMessageListener {
        fun onResendChatMessage(position: Int)
        fun onDeleteChatMessage(position: Int)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessageItemUIState>() {
            override fun areItemsTheSame(oldItem: ChatMessageItemUIState, newItem: ChatMessageItemUIState): Boolean =
                oldItem.tag == newItem.tag

            override fun areContentsTheSame(oldItem: ChatMessageItemUIState, newItem: ChatMessageItemUIState): Boolean = oldItem == newItem
        }
    }

    abstract class ViewHolder(
        val root: LinearLayout,
        val context: Context
    ) : RecyclerView.ViewHolder(root) {

        protected fun setTopMargin(root: LinearLayout, topMargin: Int) {
            val marginLayoutParams = root.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = topMargin
            root.layoutParams = marginLayoutParams
        }

        protected fun getFormattedTime(chatMessageItemUIState: ChatMessageItemUIState): String {
            return if (chatMessageItemUIState.showTime) {
                val dateTimeFormat = DateTimeFormatter.ofPattern(context.getString(R.string.date_time_pattern_time_with_meridiem))
                chatMessageItemUIState.timeCreatedAt?.format(dateTimeFormat) ?: ""
            } else {
                ""
            }
        }
    }

    class ReceivedViewHolder(
        private val binding: ItemChatMessageReceivedBinding,
        context: Context
    ) : ViewHolder(binding.root, context) {

        fun bind(
            chatMessageItemUIState: ChatMessageItemUIState,
            profilePhoto: Bitmap?,
        ) {
            binding.tvChatMessageReceivedBody.text = chatMessageItemUIState.body
            binding.ivChatMessageReceivedProfilePhoto.visibility = if (chatMessageItemUIState.showProfilePhoto) View.VISIBLE else View.INVISIBLE
            binding.tvChatMessageReceivedCreatedAt.text = getFormattedTime(chatMessageItemUIState)
            setTopMargin(binding.root, chatMessageItemUIState.topMargin)
            Glide.with(context)
                .load(profilePhoto)
                .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
                .into(binding.ivChatMessageReceivedProfilePhoto)
        }
    }

    class SentViewHolder(
        private val binding: ItemChatMessageSentBinding,
        private val chatMessageListener: ChatMessageListener,
        context: Context
    ) : ViewHolder(binding.root, context) {

        fun bind(
            chatMessageItemUIState: ChatMessageItemUIState
        ) {
            binding.tvChatMessageSentBody.text = chatMessageItemUIState.body
            binding.btnChatMessageSentResend.setOnClickListener {
                chatMessageListener.onResendChatMessage(absoluteAdapterPosition)
            }
            binding.btnChatMessageSentDelete.setOnClickListener {
                chatMessageListener.onDeleteChatMessage(absoluteAdapterPosition)
            }
            setTopMargin(binding.root, chatMessageItemUIState.topMargin)
            when (chatMessageItemUIState.status) {
                ChatMessageStatus.SENDING -> showLayout(binding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(binding, View.GONE, View.GONE, View.VISIBLE)
                else -> {
                    binding.tvChatMessageSentCreatedAt.text = getFormattedTime(chatMessageItemUIState)
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
        context: Context
    ) : ViewHolder(binding.root, context) {

        fun bind(chatMessageItemUIState: ChatMessageItemUIState) {
            val dateTimeFormat = DateTimeFormatter.ofPattern(context.getString(R.string.date_time_pattern_date_with_day_of_week))
            binding.tvChatSeparatorTitle.text = chatMessageItemUIState.dateCreatedAt?.format(dateTimeFormat)
            setTopMargin(binding.root, chatMessageItemUIState.topMargin)
        }
    }
}