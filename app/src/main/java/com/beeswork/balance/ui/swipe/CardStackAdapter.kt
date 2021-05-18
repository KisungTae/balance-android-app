package com.beeswork.balance.ui.swipe


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.databinding.ItemCardStackBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*


class CardStackAdapter : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private lateinit var cardImageViewPager: ViewPager2
    private val cardDomains: MutableList<CardDomain> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardStackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cardDomains[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return cardDomains.size
    }

    fun submitCards(newCardDomains: List<CardDomain>) {
        val startIndex = cardDomains.lastIndex + 1
        cardDomains.addAll(startIndex, newCardDomains)
        notifyItemRangeInserted(startIndex, newCardDomains.size)
    }

    fun removeCard(): CardDomain? {
        if (cardDomains.size > 0) {
            val removedCard = cardDomains.removeAt(0)
            notifyItemRemoved(0)
            return removedCard
        }
        return null
    }

    class ViewHolder(
        private val binding: ItemCardStackBinding
    ) : RecyclerView.ViewHolder(binding.root), CardImageViewPagerAdapter.CardImageListener {

        fun bind(cardDomain: CardDomain) {
//            cardImageViewPager = binding.vpCardImage
            binding.vpCardImage.adapter = CardImageViewPagerAdapter(cardDomain.accountId, cardDomain.photoKeys, this)
            TabLayoutMediator(binding.tlCardImage, binding.vpCardImage) { tab, pos -> }.attach()
            // TODO: change to photos
//            holder.binding.vpCardImage.adapter =
//                CardImageViewPagerAdapter(
//                    arrayListOf(
//                        R.drawable.person3,
//                        R.drawable.person2,
//                        R.drawable.person1
//                    ), holder
//                )


        }

        override fun onLeftButtonClick(position: Int) {
            println("onLeftButtonClick position: $position")
            binding.vpCardImage.currentItem = position - 1
        }

        override fun onRightButtonClick(position: Int) {
            println("onRightButtonClick position: $position")
            binding.vpCardImage.currentItem = position + 1
        }
    }


}
