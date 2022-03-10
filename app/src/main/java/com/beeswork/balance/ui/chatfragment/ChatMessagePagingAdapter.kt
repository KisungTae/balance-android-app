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
import com.beeswork.balance.databinding.ItemChatMessageReceivedBinding
import com.beeswork.balance.databinding.ItemChatMessageSentBinding
import com.beeswork.balance.databinding.ItemChatMessageSeparatorBinding
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide


class ChatMessagePagingAdapter(
    private val chatMessageSentListener: ChatMessageSentListener,
    displayDensity: Float
) : PagingDataAdapter<ChatMessageItemUIState, ChatMessagePagingAdapter.ViewHolder>(diffCallback) {

    private val topMarginShort: Int = (displayDensity * 5).toInt()
    private val topMarginMedium: Int = (displayDensity * 15).toInt()
    private val topMarginLong: Int = (displayDensity * 30).toInt()

    private var profilePhoto: Bitmap? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ChatMessageStatus.SEPARATOR.ordinal -> {
                SeparatorViewHolder(ItemChatMessageSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
                    chatMessageSentListener
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

    interface ChatMessageSentListener {
        fun onResendChatMessage(position: Int)
        fun onDeleteChatMessage(position: Int)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessageItemUIState>() {
            override fun areItemsTheSame(oldItem: ChatMessageItemUIState, newItem: ChatMessageItemUIState): Boolean = oldItem.tag == newItem.tag
            override fun areContentsTheSame(oldItem: ChatMessageItemUIState, newItem: ChatMessageItemUIState): Boolean = oldItem == newItem
        }
    }

    abstract class ViewHolder(
        val root: LinearLayout
    ) : RecyclerView.ViewHolder(root) {

        protected fun setTopMargin(root: LinearLayout, topMargin: Int) {
            val marginLayoutParams = root.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = topMargin
            root.layoutParams = marginLayoutParams
        }
    }

    class ReceivedViewHolder(
        private val binding: ItemChatMessageReceivedBinding,
        private val context: Context
    ) : ViewHolder(binding.root) {

        fun bind(
            chatMessageItemUIState: ChatMessageItemUIState,
            profilePhoto: Bitmap?,
        ) {
            binding.tvChatMessageReceivedBody.text = chatMessageItemUIState.body
            binding.ivChatMessageReceivedProfilePhoto.visibility = if (chatMessageItemUIState.showProfilePhoto) View.VISIBLE else View.INVISIBLE
            binding.tvChatMessageReceivedCreatedAt.text = chatMessageItemUIState.formatTimeCreatedAt()
            setTopMargin(binding.root, chatMessageItemUIState.topMargin)
            Glide.with(context)
                .load(profilePhoto)
                .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
                .into(binding.ivChatMessageReceivedProfilePhoto)
        }
    }

    class SentViewHolder(
        private val binding: ItemChatMessageSentBinding,
        private val chatMessageSentListener: ChatMessageSentListener
    ) : ViewHolder(binding.root) {

        fun bind(
            chatMessageItemUIState: ChatMessageItemUIState
        ) {
            binding.tvChatMessageSentBody.text = chatMessageItemUIState.body
            binding.btnChatMessageSentResend.setOnClickListener {
                chatMessageSentListener.onResendChatMessage(absoluteAdapterPosition)
            }
            binding.btnChatMessageSentDelete.setOnClickListener {
                chatMessageSentListener.onDeleteChatMessage(absoluteAdapterPosition)
            }
            setTopMargin(binding.root, chatMessageItemUIState.topMargin)
            when (chatMessageItemUIState.status) {
                ChatMessageStatus.SENDING -> showLayout(binding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(binding, View.GONE, View.GONE, View.VISIBLE)
                else -> {
                    binding.tvChatMessageSentCreatedAt.text = chatMessageItemUIState.formatTimeCreatedAt()
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
        private val binding: ItemChatMessageSeparatorBinding
    ) : ViewHolder(binding.root) {
        fun bind(chatMessageItemUIState: ChatMessageItemUIState) {
            binding.tvChatSeparatorTitle.text = chatMessageItemUIState.body
            setTopMargin(binding.root, chatMessageItemUIState.topMargin)
        }
    }
}