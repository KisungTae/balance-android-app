package com.beeswork.balance.ui.cardfragment.card


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemCardBinding
import com.beeswork.balance.domain.uistate.card.CardItemUIState
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.ui.cardfragment.CardListener
import com.google.android.material.tabs.TabLayoutMediator


class CardStackAdapter(
    private val cardListener: CardListener
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private val cardItemUIStates: MutableList<CardItemUIState> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false), cardListener, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cardItemUIStates[position])
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
        private val binding: ItemCardBinding,
        private val cardListener: CardListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root), CardPhotoViewPagerAdapter.CardPhotoListener {

        fun bind(cardItemUIState: CardItemUIState) {
            binding.tvCardName.text = cardItemUIState.name
            binding.tvCardAge.text = cardItemUIState.age.toString()
            binding.tvCardAboutSnippet.text = cardItemUIState.about
            binding.tvCardHeight.text = cardItemUIState.height.toString()
            binding.tvCardDistance.text = cardItemUIState.distance.toString()

            if (cardItemUIState.gender == Gender.FEMALE) {
                binding.tvCardGender.text = context.getString(R.string.female)
                binding.ivCardGenderIcon.setImageResource(R.drawable.ic_baseline_female_24)
            } else {
                binding.tvCardGender.text = context.getString(R.string.male)
                binding.ivCardGenderIcon.setImageResource(R.drawable.ic_baseline_male_24)
            }
            binding.tvCardAbout.text = cardItemUIState.about
            binding.vpCardPhoto.offscreenPageLimit = 1
            binding.vpCardPhoto.adapter = CardPhotoViewPagerAdapter(cardItemUIState.photoURLs, this)
            TabLayoutMediator(binding.tlCardImage, binding.vpCardPhoto) { _, _ -> }.attach()

            binding.btnCardReport.setOnClickListener {
                cardListener.onReportCard(cardItemUIState.accountId)
            }
        }

        override fun onLeftButtonClick(position: Int) {
            binding.vpCardPhoto.currentItem = position - 1
        }

        override fun onRightButtonClick(position: Int) {
            binding.vpCardPhoto.currentItem = position + 1
        }
    }


}
