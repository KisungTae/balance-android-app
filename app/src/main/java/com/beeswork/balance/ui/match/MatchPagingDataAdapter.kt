package com.beeswork.balance.ui.match

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemMatchBinding
import com.beeswork.balance.internal.constant.EndPoint
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class MatchPagingDataAdapter(
    private val matchListener: MatchListener
) : PagingDataAdapter<MatchDomain, MatchPagingDataAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            matchListener,
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    fun getMatch(position: Int): MatchDomain? {
        return getItem(position)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MatchDomain>() {
            override fun areItemsTheSame(oldItem: MatchDomain, newItem: MatchDomain): Boolean =
                oldItem.chatId == newItem.chatId

            override fun areContentsTheSame(oldItem: MatchDomain, newItem: MatchDomain): Boolean =
                oldItem == newItem
        }
    }

    interface MatchListener {
        fun onClick(position: Int)
    }

    class ViewHolder(
        private val binding: ItemMatchBinding,
        private val matchListener: MatchListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(matchDomain: MatchDomain) {
            binding.tvMatchName.text = matchDomain.name
            binding.tvMatchUpdatedAt.text = matchDomain.updatedAt.toLocalDate().toString()
            binding.tvMatchUnreadIndicator.visibility = if (matchDomain.unread) View.VISIBLE else View.GONE
            binding.tvMatchRecentChatMessage.text = getRecentChatMessage(matchDomain)
            setupProfilePictureBorder(matchDomain.active)
            setupProfilePicture(matchDomain.valid, matchDomain.matchedId, matchDomain.repPhotoKey)
            setupTextColor(matchDomain.valid)
        }

        private fun setupProfilePictureBorder(matchActive: Boolean) {
            binding.flMatchProfilePictureWrapper.background = if (matchActive) null else ContextCompat.getDrawable(
                context,
                R.drawable.sh_circle_border
            )
        }

        private fun setupProfilePicture(matchValid: Boolean, matchedId: UUID, repPhotoKey: String) {
            val photoEndPoint = if (matchValid) EndPoint.ofPhotoBucket(
                matchedId,
                repPhotoKey
            ) else R.drawable.ic_baseline_account_circle
            Glide.with(context).load(photoEndPoint).apply(glideRequestOptions()).into(binding.ivMatchProfilePicture)
        }

        private fun setupTextColor(matchValid: Boolean) {
            val colorCode = if (matchValid) R.color.TextBlack else R.color.TextGrey
            val textColor = context.getColor(colorCode)
            binding.tvMatchName.setTextColor(textColor)
            binding.tvMatchRecentChatMessage.setTextColor(textColor)
            binding.tvMatchUpdatedAt.setTextColor(textColor)
        }

        private fun getRecentChatMessage(matchDomain: MatchDomain): String {
            return if (matchDomain.deleted) context.getString(R.string.match_deleted_recent_chat_message)
            else if (matchDomain.unmatched) context.getString(R.string.match_unmatched_recent_chat_message)
            else if (!matchDomain.active) context.getString(R.string.match_new_recent_chat_message)
            else matchDomain.recentChatMessage
        }

        private fun glideRequestOptions(): RequestOptions {
            return RequestOptions().placeholder(R.drawable.ic_baseline_account_circle)
                .error(R.drawable.ic_baseline_account_circle)
                .circleCrop()
        }

        override fun onClick(view: View?) {
            matchListener.onClick(absoluteAdapterPosition)
        }

    }
}

