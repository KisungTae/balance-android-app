package com.beeswork.balance.ui.swipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemCardImageBinding
import com.bumptech.glide.Glide

class CardImageViewPagerAdapter(
    private val images: List<Int>,
    private val cardImageListener: CardImageListener
) : RecyclerView.Adapter<CardImageViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            ItemCardImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            cardImageListener
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.itemView.imageView.setImageResource(images[position])
    }

    interface CardImageListener {
        fun onLeftButtonClick(position: Int)
        fun onRightButtonClick(position: Int)
    }

    class ViewHolder(
        private val binding: ItemCardImageBinding,
        private val cardImageListener: CardImageListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnCardImageLeft.setOnClickListener {
                println("btnCardImageLeft.setOnClickListener")
                cardImageListener.onLeftButtonClick(adapterPosition)
//                viewPager2.currentItem = adapterPosition - 1
            }

            binding.btnCardImageRight.setOnClickListener {
                println("btnCardImageRight.setOnClickListener")
                cardImageListener.onRightButtonClick(adapterPosition)
//                viewPager2.currentItem = adapterPosition + 1
            }
        }

    }
}