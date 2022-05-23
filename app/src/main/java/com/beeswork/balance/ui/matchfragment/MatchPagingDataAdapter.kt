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
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide
import kotlin.random.Random

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
            //todo: remove me
            val image = when (Random.nextInt(4)) {
                0 -> R.drawable.person1
                1 -> R.drawable.person2
                2 -> R.drawable.person3
                3 -> R.drawable.person4
                4 -> R.drawable.person5
                else -> R.drawable.person5
            }
            Glide.with(context)
                .load(image)
                .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
                .into(binding.ivMatchProfilePhoto)
//            Glide.with(context)
//                .load(matchItemUIState.swipedProfilePhotoUrl)
//                .apply(GlideHelper.profilePhotoGlideOptions())
//                .into(binding.ivMatchProfilePhoto)

            binding.tvMatchName.text = matchItemUIState.swipedName ?: context.getString(R.string.unknown_user_name)

            binding.vMatchNewMessageFlag.visibility = if (matchItemUIState.unread) {
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.tvMatchLastChatMessageBody.text = if (matchItemUIState.active || matchItemUIState.unmatched) {
                matchItemUIState.lastChatMessageBody
            } else {
                context.getString(R.string.recent_chat_message_new_match)
            }

            if (matchItemUIState.active || matchItemUIState.unmatched) {
                binding.flMatchProfilePhotoWrapper.background = null
                binding.tvMatchNewTag.visibility = View.GONE
            } else {
                binding.flMatchProfilePhotoWrapper.background = ContextCompat.getDrawable(context, R.drawable.sh_new_match_circle_boarder)
                binding.tvMatchNewTag.visibility = View.VISIBLE
            }

            if (matchItemUIState.unmatched) {
                binding.tvMatchName.setTextColor(context.getColor(R.color.TextGrey))
            } else {
                binding.tvMatchName.setTextColor(context.getColor(R.color.TextBlack))
            }
        }

        override fun onClick(view: View?) {
            matchListener.onClick(absoluteAdapterPosition)
        }

    }
}

