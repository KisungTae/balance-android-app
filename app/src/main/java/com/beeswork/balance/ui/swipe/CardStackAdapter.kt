package com.beeswork.balance.ui.swipe


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.databinding.ItemCardStackBinding
import com.google.android.material.tabs.TabLayoutMediator


class CardStackAdapter : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private lateinit var cardImageViewPager: ViewPager2
    private val cardResponses: MutableList<CardResponse> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardStackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        cardImageViewPager = holder.itemView.vpCardImage
//
//        // TODO: change to photos
//        holder.itemView.vpCardImage.adapter =
//            CardImageViewPagerAdapter(
//                arrayListOf(
//                    R.drawable.person3,
//                    R.drawable.person2,
//                    R.drawable.person1
//                ), holder
//            )
//
//        TabLayoutMediator(holder.itemView.tlCardImage, holder.itemView.vpCardImage) { tab, pos -> }.attach()
    }

    fun addCards(newCardResponses: List<CardResponse>) {
        val startIndex = if (cardResponses.size > 0) cardResponses.size - 1 else 0
        cardResponses.addAll(startIndex, newCardResponses)
        notifyItemRangeInserted(startIndex, newCardResponses.size)
    }

    fun removeCard(): CardResponse? {

        if (cardResponses.size > 0) {
            val removedCard = cardResponses.removeAt(0)
            notifyItemRemoved(0)
            return removedCard
        }
        return null
    }

    class ViewHolder(private val binding: ItemCardStackBinding) : RecyclerView.ViewHolder(binding.root),
        CardImageViewPagerAdapter.CardImageListener {

        override fun onLeftButtonClick(position: Int) {
            println("onLeftButtonClick position: $position")
            binding.vpCardImage.currentItem = position - 1
        }

        override fun onRightButtonClick(position: Int) {
            println("onRightButtonClick position: $position")
            binding.vpCardImage.currentItem = position + 1
        }
    }

    override fun getItemCount(): Int {
        return cardResponses.size
    }


}
