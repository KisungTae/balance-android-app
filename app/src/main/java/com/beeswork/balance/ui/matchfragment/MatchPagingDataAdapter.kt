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
import com.beeswork.balance.internal.constant.DateTimePattern
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
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
//            TODO: remove me
//            binding.tvMatchName.text = "${matchDomain.chatId}"
            binding.tvMatchName.text = matchDomain.swipedName ?: context.getString(R.string.unknown_user_name)
            binding.tvMatchUnreadIndicator.visibility = if (matchDomain.unread) View.VISIBLE else View.GONE
            binding.tvMatchLastChatMessageBody.text = getLastChatMessageBody(matchDomain)
            setupProfilePictureBorder(matchDomain.active)
            setupProfilePicture(matchDomain.swipedId, matchDomain.swipedProfilePhotoKey)
            setupTextColor(matchDomain)
        }

        private fun formatUpdatedAt(updatedAt: ZonedDateTime?): String {
            updatedAt?.let {
                val now = LocalDateTime.now()
                return if (updatedAt.year != now.year) updatedAt.toLocalDate().toString()
                else if (updatedAt.month != now.month || updatedAt.dayOfMonth != now.dayOfMonth)
                    updatedAt.toLocalDate().format(DateTimePattern.ofDateWithShortYear())
                else updatedAt.format(DateTimePattern.ofTimeWithMeridiem())
            } ?: return ""
        }

        private fun setupProfilePictureBorder(matchActive: Boolean) {
            binding.flMatchProfilePictureWrapper.background = if (matchActive) null else ContextCompat.getDrawable(
                context,
                R.drawable.sh_circle_primary_border
            )
        }

        private fun setupProfilePicture(swipedId: UUID, profilePhotoKey: String?) {
//            val photoEndPoint = profilePhotoKey?.let {
//                EndPoint.ofPhotoBucket(matchedId, profilePhotoKey)
//            } ?: R.drawable.ic_baseline_account_circle
//            Glide.with(context)
//                .load(photoEndPoint)
//                .apply(GlideHelper.profilePhotoGlideOptions())
//                .into(binding.ivMatchProfilePicture)
        }

        private fun setupTextColor(matchDomain: MatchDomain) {
            val colorCode = if (matchDomain.unmatched) R.color.TextGrey else R.color.TextBlack
            val textColor = context.getColor(colorCode)
            binding.tvMatchName.setTextColor(textColor)
            binding.tvMatchLastChatMessageBody.setTextColor(textColor)
        }

        private fun getLastChatMessageBody(matchDomain: MatchDomain): String {
            return if (!matchDomain.active) {
                context.getString(R.string.recent_chat_message_new_match)
            } else {
                matchDomain.lastChatMessageBody
            }
        }

        override fun onClick(view: View?) {
            matchListener.onClick(absoluteAdapterPosition)
        }

    }
}

