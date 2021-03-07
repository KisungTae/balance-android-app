package com.beeswork.balance.ui.match

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemMatchBinding
import com.beeswork.balance.internal.constant.EndPoint
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class MatchPagedListAdapter(
    private val onMatchListener: OnMatchListener
) : PagedListAdapter<MatchDomain, MatchPagedListAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onMatchListener,
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MatchDomain>() {
            override fun areItemsTheSame(oldItem: MatchDomain, newItem: MatchDomain): Boolean =
                oldItem.chatId == newItem.chatId

            override fun areContentsTheSame(oldItem: MatchDomain, newItem: MatchDomain): Boolean =
                oldItem == newItem
        }
    }

    interface OnMatchListener {
        fun onMatchClick(view: View, position: Int)
    }

    class ViewHolder(
        private val binding: ItemMatchBinding,
        private val onMatchListener: OnMatchListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(matchDomain: MatchDomain) {
            binding.tvMatchName.text = matchDomain.name
            binding.tvMatchUnreadIndicator.visibility = if (matchDomain.unread) View.VISIBLE else View.GONE
            binding.tvMatchRecentChatMessage.text = getRecentChatMessage(matchDomain, context)

            if (matchDomain.unmatched || matchDomain.deleted) {
                val colorTextLightGrey = context.getColor(R.color.textLightGrey)
                binding.ivMatchProfilePicture.setImageResource(R.drawable.ic_baseline_account_circle)
                binding.tvMatchName.setTextColor(colorTextLightGrey)
                binding.tvMatchRecentChatMessage.setTextColor(colorTextLightGrey)
                binding.tvMatchUpdatedAt.text = ""
            } else {
                val photoEndPoint = EndPoint.ofPhotoBucket(matchDomain.matchedId, matchDomain.repPhotoKey)
                Glide.with(context).load(photoEndPoint).apply(glideRequestOptions()).into(binding.ivMatchProfilePicture)
                val circleBorderShape = ContextCompat.getDrawable(context, R.drawable.sh_circle_border)
                if (!matchDomain.active) binding.flMatchProfilePictureWrapper.background = circleBorderShape
                binding.tvMatchUpdatedAt.text = matchDomain.updatedAt.toLocalDate().toString()
            }
        }

        override fun onClick(view: View) {
            onMatchListener.onMatchClick(view, layoutPosition)
        }

        companion object {
            private fun getRecentChatMessage(matchDomain: MatchDomain, context: Context): String {
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
        }
    }
}

