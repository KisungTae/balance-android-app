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
import com.bumptech.glide.request.RequestOptions

class MatchPagingDataAdapter(
    private val onMatchClickListener: OnMatchClickListener
) : PagingDataAdapter<MatchDomain, MatchPagingDataAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onMatchClickListener,
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

    interface OnMatchClickListener {
        fun onClick(view: View)
    }

    class ViewHolder(
        private val binding: ItemMatchBinding,
        private val onMatchClickListener: OnMatchClickListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(matchDomain: MatchDomain) {
            binding.root.tag = matchDomain.chatId
            binding.tvMatchName.text = matchDomain.name
            binding.tvMatchUpdatedAt.text = matchDomain.updatedAt.toLocalDate().toString()
            binding.tvMatchUnreadIndicator.visibility = if (matchDomain.unread) View.VISIBLE else View.GONE
            binding.tvMatchRecentChatMessage.text = getRecentChatMessage(matchDomain, context)
            resetProfilePictureCircleBorder(matchDomain.active)

            if (matchDomain.unmatched || matchDomain.deleted) {
                binding.ivMatchProfilePicture.setImageResource(R.drawable.ic_baseline_account_circle)
                changeTextColor(context.getColor(R.color.LightGrey))
            } else {
                val photoEndPoint = EndPoint.ofPhotoBucket(matchDomain.matchedId, matchDomain.repPhotoKey)
//                Glide.with(context).load(photoEndPoint).apply(glideRequestOptions()).into(binding.ivMatchProfilePicture)
                changeTextColor(context.getColor(R.color.TextBlack))
            }
        }

        private fun resetProfilePictureCircleBorder(active: Boolean) {
            if (active) binding.flMatchProfilePictureWrapper.background = null
            else {
                binding.flMatchProfilePictureWrapper.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.sh_circle_border
                )
            }
        }

        private fun changeTextColor(textColor: Int) {
            binding.tvMatchName.setTextColor(textColor)
            binding.tvMatchRecentChatMessage.setTextColor(textColor)
            binding.tvMatchUpdatedAt.setTextColor(textColor)
        }


        override fun onClick(view: View) {
            onMatchClickListener.onClick(view)
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

