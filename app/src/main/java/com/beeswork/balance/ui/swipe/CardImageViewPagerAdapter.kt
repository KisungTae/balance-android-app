package com.beeswork.balance.ui.swipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_card_image.view.*

class CardImageViewPagerAdapter(
    private val images: List<Int>,
    private val cardImageListener: CardImageListener
): RecyclerView.Adapter<CardImageViewPagerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_card_image, parent, false), cardImageListener)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.imageView.setImageResource(images[position])
    }

    interface CardImageListener {
        fun onLeftButtonClick(position: Int)
        fun onRightButtonClick(position: Int)
    }

    class ViewHolder(
        itemView: View,
        private val cardImageListener: CardImageListener
    ): RecyclerView.ViewHolder(itemView) {

        init {
            itemView.btnCardImageLeft.setOnClickListener {
                println("btnCardImageLeft.setOnClickListener")
                cardImageListener.onLeftButtonClick(adapterPosition)
//                viewPager2.currentItem = adapterPosition - 1
            }

            itemView.btnCardImageRight.setOnClickListener {
                println("btnCardImageRight.setOnClickListener")
                cardImageListener.onRightButtonClick(adapterPosition)
//                viewPager2.currentItem = adapterPosition + 1
            }
        }
    }
}