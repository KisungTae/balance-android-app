package com.beeswork.balance.ui.matchfragment

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

class MatchPagingDataAdapter(
    private val matchListener: MatchListener
) : PagingDataAdapter<MatchItemUIState, MatchPagingDataAdapter.ViewHolder>(diffCallback) {

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

    fun getMatch(position: Int): MatchItemUIState? {
        return getItem(position)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MatchItemUIState>() {
            override fun areItemsTheSame(oldItem: MatchItemUIState, newItem: MatchItemUIState): Boolean =
                oldItem.chatId == newItem.chatId

            override fun areContentsTheSame(oldItem: MatchItemUIState, newItem: MatchItemUIState): Boolean =
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

        fun bind(matchItemUIState: MatchItemUIState) {
//            Glide.with(context)
//                .load(matchItemUIState.swipedProfilePhotoUrl)
//                .apply(GlideHelper.profilePhotoGlideOptions())
//                .into(binding.ivMatchProfilePicture)
            binding.tvMatchName.text = matchItemUIState.swipedName ?: context.getString(R.string.unknown_user_name)
            binding.tvMatchUnreadIndicator.visibility = if (matchItemUIState.unread) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.tvMatchLastChatMessageBody.text = if (matchItemUIState.active || matchItemUIState.unmatched) {
                matchItemUIState.lastChatMessageBody
            } else {
                context.getString(R.string.recent_chat_message_new_match)
            }
            binding.flMatchProfilePhotoWrapper.background = if (matchItemUIState.active || matchItemUIState.unmatched) {
                null
            } else {
                ContextCompat.getDrawable(context, R.drawable.sh_circle_primary_border)
            }
            val colorCode = if (matchItemUIState.unmatched) {
                R.color.TextGrey
            } else {
                R.color.TextBlack
            }
            val textColor = context.getColor(colorCode)
            binding.tvMatchName.setTextColor(textColor)
            binding.tvMatchLastChatMessageBody.setTextColor(textColor)
        }

        override fun onClick(view: View?) {
            matchListener.onClick(absoluteAdapterPosition)
        }

    }
}

