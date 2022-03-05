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
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide


class ChatMessagePagingAdapter(
    private val chatMessageSentListener: ChatMessageSentListener,
    displayDensity: Float
) : PagingDataAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>(diffCallback) {

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

    private fun calculateTopMargin(chatMessageDomain: ChatMessageDomain, position: Int): Int {
        val nextPosition = position + 1
        if (nextPosition == itemCount) {
            return topMarginLong
        }
        val nextChatMessageDomain = getItem(nextPosition)
        return when {
            chatMessageDomain.status == ChatMessageStatus.SEPARATOR -> topMarginLong
            nextChatMessageDomain?.status == ChatMessageStatus.SEPARATOR -> topMarginLong
            chatMessageDomain.status == nextChatMessageDomain?.status -> topMarginShort
            else -> topMarginMedium
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
        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessageDomain>() {
            override fun areItemsTheSame(oldItem: ChatMessageDomain, newItem: ChatMessageDomain): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ChatMessageDomain, newItem: ChatMessageDomain): Boolean = oldItem == newItem
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
            chatMessageDomain: ChatMessageDomain,
            profilePhoto: Bitmap?,
        ) {
            binding.tvChatMessageReceivedBody.text = chatMessageDomain.body
            binding.ivChatMessageReceivedProfilePhoto.visibility = if (chatMessageDomain.showProfilePhoto) View.VISIBLE else View.INVISIBLE
            binding.tvChatMessageReceivedCreatedAt.text = chatMessageDomain.formatTimeCreatedAt()
            setTopMargin(binding.root, chatMessageDomain.topMargin)
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
            chatMessageDomain: ChatMessageDomain
        ) {
            binding.tvChatMessageSentBody.text = chatMessageDomain.body
            binding.btnChatMessageSentResend.setOnClickListener {
                chatMessageSentListener.onResendChatMessage(absoluteAdapterPosition)
            }
            binding.btnChatMessageSentDelete.setOnClickListener {
                chatMessageSentListener.onDeleteChatMessage(absoluteAdapterPosition)
            }
            setTopMargin(binding.root, chatMessageDomain.topMargin)
            when (chatMessageDomain.status) {
                ChatMessageStatus.SENDING -> showLayout(binding, View.GONE, View.VISIBLE, View.GONE)
                ChatMessageStatus.ERROR -> showLayout(binding, View.GONE, View.GONE, View.VISIBLE)
                else -> {
                    binding.tvChatMessageSentCreatedAt.text = chatMessageDomain.formatTimeCreatedAt()
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
        fun bind(chatMessageDomain: ChatMessageDomain) {
            binding.tvChatSeparatorTitle.text = chatMessageDomain.body
            setTopMargin(binding.root, chatMessageDomain.topMargin)
        }
    }
}