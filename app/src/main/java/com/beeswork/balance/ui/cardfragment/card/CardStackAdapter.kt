package com.beeswork.balance.ui.cardfragment.card


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.databinding.ItemCardStackBinding
import com.beeswork.balance.domain.uistate.card.CardItemUIState
import com.google.android.material.tabs.TabLayoutMediator


class CardStackAdapter : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private val cardItemUIStates: MutableList<CardItemUIState> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardStackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cardItemUIStates[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return cardItemUIStates.size
    }

    fun submitCards(newCardItemUIStates: List<CardItemUIState>) {
        val startIndex = cardItemUIStates.size
        cardItemUIStates.addAll(startIndex, newCardItemUIStates)
        notifyItemRangeInserted(startIndex, newCardItemUIStates.size)
    }

    fun removeCard(): CardItemUIState? {
        if (cardItemUIStates.size > 0) {
            val removedCard = cardItemUIStates.removeAt(0)
            notifyItemRemoved(0)
            return removedCard
        }
        return null
    }

    fun clearCards() {
        cardItemUIStates.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemCardStackBinding
    ) : RecyclerView.ViewHolder(binding.root), CardPhotoViewPagerAdapter.CardPhotoListener {

        fun bind(cardItemUIState: CardItemUIState) {


            binding.vpCardPhoto.adapter = CardPhotoViewPagerAdapter(cardItemUIState.accountId, cardItemUIState.photoURLs, this)
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
