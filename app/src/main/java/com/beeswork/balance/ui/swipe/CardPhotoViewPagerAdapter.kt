package com.beeswork.balance.ui.swipe

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemCardPhotoBinding
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide
import java.util.*

class CardPhotoViewPagerAdapter(
    private val accountId: UUID,
    private val photoKeys: List<String>,
    private val cardPhotoListener: CardPhotoListener
) : RecyclerView.Adapter<CardPhotoViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemCardPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            cardPhotoListener,
            parent.context
        )
    }

    override fun getItemCount(): Int {
        return photoKeys.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(EndPoint.ofPhoto(accountId, photoKeys[position]))
//        holder.itemView.imageView.setImageResource(images[position])
    }

    interface CardPhotoListener {
        fun onLeftButtonClick(position: Int)
        fun onRightButtonClick(position: Int)
    }

    class ViewHolder(
        private val binding: ItemCardPhotoBinding,
        private val cardPhotoListener: CardPhotoListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnCardImageLeft.setOnClickListener {
                println("btnCardImageLeft.setOnClickListener")
                cardPhotoListener.onLeftButtonClick(absoluteAdapterPosition)
//                viewPager2.currentItem = adapterPosition - 1
            }

            binding.btnCardImageRight.setOnClickListener {
                println("btnCardImageRight.setOnClickListener")
                cardPhotoListener.onRightButtonClick(absoluteAdapterPosition)
//                viewPager2.currentItem = adapterPosition + 1
            }
        }

        fun bind(photoKey: String?) {
            Glide.with(context)
                .load(R.drawable.person4)
                .apply(GlideHelper.cardPhotoGlideOptions())
                .centerCrop()
                .into(binding.civCardPhoto)
        }

    }
}