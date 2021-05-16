package com.beeswork.balance.ui.swipe


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.data.network.response.swipe.CardDTO
import com.beeswork.balance.databinding.ItemCardStackBinding


class CardStackAdapter : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private lateinit var cardImageViewPager: ViewPager2
    private val cardRespons: MutableList<CardDTO> = arrayListOf()


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

    fun addCards(newCardRespons: List<CardDTO>) {
        val startIndex = if (cardRespons.size > 0) cardRespons.size - 1 else 0
        cardRespons.addAll(startIndex, newCardRespons)
        notifyItemRangeInserted(startIndex, newCardRespons.size)
    }

    fun removeCard(): CardDTO? {

        if (cardRespons.size > 0) {
            val removedCard = cardRespons.removeAt(0)
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
        return cardRespons.size
    }


}
