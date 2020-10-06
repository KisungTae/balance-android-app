package com.beeswork.balance.ui.swipe


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Card
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.item_card_stack.view.*


class CardStackAdapter: RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private lateinit var cardImageViewPager: ViewPager2
    private val cards: MutableList<Card> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_card_stack, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cardImageViewPager = holder.itemView.vpCardImage

        // TODO: change to photos
        holder.itemView.vpCardImage.adapter =
            CardImageViewPagerAdapter(arrayListOf(R.drawable.person3,
                                                  R.drawable.person2,
                                                  R.drawable.person1), holder)

        TabLayoutMediator(holder.itemView.tlCardImage, holder.itemView.vpCardImage) { tab, pos -> }.attach()
        println("onBindViewHolder")
        holder.itemView.tvCardStackName.text = "position: $position | size: ${cards.size}"
    }

    fun addCards(newCards: List<Card>) {
        val startIndex = if (cards.size > 0) cards.size - 1 else 0
        cards.addAll(startIndex, newCards)
        notifyItemRangeInserted(startIndex, newCards.size)
    }

    fun removeCard(): Card? {

        if (cards.size > 0) {
            val removedCard = cards.removeAt(0)
            notifyItemRemoved(0)
            return removedCard
        }
        return null
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        CardImageViewPagerAdapter.CardImageListener {

        override fun onLeftButtonClick(position: Int) {
            println("onLeftButtonClick position: $position")
            itemView.vpCardImage.currentItem = position - 1
        }

        override fun onRightButtonClick(position: Int) {
            println("onRightButtonClick position: $position")
            itemView.vpCardImage.currentItem = position + 1
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }


}
