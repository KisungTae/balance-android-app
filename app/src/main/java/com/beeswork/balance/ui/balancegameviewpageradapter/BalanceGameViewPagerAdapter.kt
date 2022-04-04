package com.beeswork.balance.ui.balancegameviewpageradapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemBalanceGameBinding
import com.beeswork.balance.domain.uistate.balancegame.BalanceGameQuestionItemUIState
import com.beeswork.balance.internal.constant.BalanceGameOption
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide

class BalanceGameViewPagerAdapter(
    private val balanceGameQuestionItemUIStates: MutableList<BalanceGameQuestionItemUIState>,
    private val balanceGameListener: BalanceGameListener
): RecyclerView.Adapter<BalanceGameViewPagerAdapter.ViewHolder>(), BalanceGameItemListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBalanceGameBinding.inflate(LayoutInflater.from(parent.context), parent, false), this, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(balanceGameQuestionItemUIStates[position])
    }

    override fun getItemCount(): Int {
        return balanceGameQuestionItemUIStates.size
    }


    override fun onOptionSelected(position: Int, answer: Boolean) {
        balanceGameQuestionItemUIStates[position].answer = answer
        notifyItemChanged(position)
    }


    class ViewHolder(
        private val binding: ItemBalanceGameBinding,
        private val balanceGameItemListener: BalanceGameItemListener,
        private val context: Context
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(balanceGameQuestionItemUIState: BalanceGameQuestionItemUIState) {
//            binding.btnBalanceGameTopOption.text = balanceGameQuestionItemUIState.topOption
//            binding.btnBalanceGameBottomOption.text = balanceGameQuestionItemUIState.bottomOption

            Glide.with(context)
                .load(R.drawable.person1)
                .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
                .into(binding.ivBalanceGameProfilePhoto)


            if (balanceGameQuestionItemUIState.answer != null) {

            }

//            binding.btnBalanceGameTopOption.setOnClickListener {
//                balanceGameItemListener.onOptionSelected(absoluteAdapterPosition, BalanceGameOption.TOP)
//            }
//
//            binding.btnBalanceGameBottomOption.setOnClickListener {
//                balanceGameItemListener.onOptionSelected(absoluteAdapterPosition, BalanceGameOption.BOTTOM)
//            }
        }
    }


}