package com.beeswork.balance.ui.swipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.databinding.ItemCardImageBinding
import com.beeswork.balance.internal.constant.EndPoint
import java.util.*

class CardImageViewPagerAdapter(
    private val accountId: UUID,
    private val photoKeys: List<String>,
    private val cardImageListener: CardImageListener
) : RecyclerView.Adapter<CardImageViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemCardImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            cardImageListener
        )
    }

    override fun getItemCount(): Int {
        return photoKeys.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(EndPoint.ofPhoto(accountId, photoKeys[position]))
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
                cardImageListener.onLeftButtonClick(absoluteAdapterPosition)
//                viewPager2.currentItem = adapterPosition - 1
            }

            binding.btnCardImageRight.setOnClickListener {
                println("btnCardImageRight.setOnClickListener")
                cardImageListener.onRightButtonClick(absoluteAdapterPosition)
//                viewPager2.currentItem = adapterPosition + 1
            }
        }

        fun bind(photoKey: String?) {

        }

    }
}