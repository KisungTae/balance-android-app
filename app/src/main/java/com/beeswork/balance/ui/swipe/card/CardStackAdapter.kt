package com.beeswork.balance.ui.swipe.card


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.databinding.ItemCardStackBinding
import com.google.android.material.tabs.TabLayoutMediator


class CardStackAdapter : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

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
        val startIndex = cardDomains.size
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
    ) : RecyclerView.ViewHolder(binding.root), CardPhotoViewPagerAdapter.CardPhotoListener {

        fun bind(cardDomain: CardDomain) {
            binding.vpCardPhoto.adapter = CardPhotoViewPagerAdapter(cardDomain.accountId, cardDomain.photoKeys, this)
            TabLayoutMediator(binding.tlCardImage, binding.vpCardPhoto) { tab, pos -> }.attach()
        }

        override fun onLeftButtonClick(position: Int) {
            binding.vpCardPhoto.currentItem = position - 1
        }

        override fun onRightButtonClick(position: Int) {
            binding.vpCardPhoto.currentItem = position + 1
        }
    }


}
