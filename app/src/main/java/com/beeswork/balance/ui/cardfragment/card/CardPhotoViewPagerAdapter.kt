package com.beeswork.balance.ui.cardfragment.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemCardPhotoBinding
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide

class CardPhotoViewPagerAdapter(
    private val photoURLs: List<String>,
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
        return photoURLs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind("")
        println("onBindViewHolder: ${photoURLs[position]}")
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
                cardPhotoListener.onLeftButtonClick(absoluteAdapterPosition)
            }

            binding.btnCardImageRight.setOnClickListener {
                cardPhotoListener.onRightButtonClick(absoluteAdapterPosition)
            }
        }

        fun bind(photoKey: String?) {
            Glide.with(context)
                .load(R.drawable.person1)
                .apply(GlideHelper.cardPhotoGlideOptions())
                .centerCrop()
                .into(binding.civCardPhoto)
        }

    }
}