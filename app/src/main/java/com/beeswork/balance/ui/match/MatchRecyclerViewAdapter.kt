package com.beeswork.balance.ui.match

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.entity.Match
import com.beeswork.balance.internal.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_match.view.*

class MatchRecyclerViewAdapter(
    private val onMatchListener: OnMatchListener
): RecyclerView.Adapter<MatchRecyclerViewAdapter.MatchHolder>() {

    private var matches: List<Match> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHolder {
        val view = parent.inflate(R.layout.item_match)
        return MatchHolder(view, onMatchListener)
    }

    override fun onBindViewHolder(holder: MatchHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    fun setMatches(matches: List<Match>) {
        this.matches = matches
        notifyDataSetChanged()
    }

    interface OnMatchListener {
        fun onMatchClick(view: View, chatId: Long)
    }

    class MatchHolder(
        itemView: View,
        private val onMatchListener: OnMatchListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private lateinit var match: Match

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(match: Match) {
            this.match = match
            Picasso.get().load(R.drawable.personsmall).into(itemView.ivMatch)
            itemView.tvMatchName.text = match.toString()
//            itemView.tvMatchRecentMessage.text = match.recentMessage
//            itemView.tvMatchUnmatch.text = match.unmatched.toString()
            itemView.tag = match.chatId
        }

        override fun onClick(view: View?) {
            if (view != null) onMatchListener.onMatchClick(view, view.tag.toString().toLong())
        }
    }

}