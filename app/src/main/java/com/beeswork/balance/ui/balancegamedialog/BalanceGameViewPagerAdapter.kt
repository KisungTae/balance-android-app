package com.beeswork.balance.ui.balancegamedialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemBalanceGameBinding
import com.beeswork.balance.domain.uistate.balancegame.QuestionItemUIState
import com.beeswork.balance.internal.constant.BalanceGameOption
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide

class BalanceGameViewPagerAdapter(
    private val balanceGameListener: BalanceGameListener
) : RecyclerView.Adapter<BalanceGameViewPagerAdapter.ViewHolder>(), BalanceGameQuestionListener {

    private var profilePhotoURL: String? = null

    private val questionItemUIStates = mutableListOf<QuestionItemUIState>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBalanceGameBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            profilePhotoURL,
            this,
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questionItemUIStates[position], position + 1, itemCount)
    }

    override fun getItemCount(): Int {
        return questionItemUIStates.size
    }

    fun submit(newQuestionItemUIStates: List<QuestionItemUIState>) {
        questionItemUIStates.clear()
        questionItemUIStates.addAll(newQuestionItemUIStates)
        notifyDataSetChanged()
    }

    fun getQuestionIds(): List<Int> {
        return questionItemUIStates.map { questionItemUIState -> questionItemUIState.id }
    }

    fun replaceQuestion(position: Int, questionItemUIState: QuestionItemUIState) {
        questionItemUIStates[position] = questionItemUIState
        notifyItemChanged(position)
    }

    fun getAnswers(): Map<Int, Boolean> {
        val answers = mutableMapOf<Int, Boolean>()
        questionItemUIStates.forEach { questionItemUIState ->
            questionItemUIState.answer?.let { _answer ->
                answers[questionItemUIState.id] = _answer
            }
        }
        return answers
    }

    override fun onOptionSelected(position: Int, answer: Boolean) {
        questionItemUIStates[position].answer = answer
        balanceGameListener.onOptionSelected()
        notifyItemChanged(position)
    }

    fun setupProfilePhotoULR(url: String?) {
        profilePhotoURL = url
    }

    interface BalanceGameListener {
        fun onOptionSelected()
    }


    class ViewHolder(
        private val binding: ItemBalanceGameBinding,
        private val profilePhotoUrl: String?,
        private val balanceGameQuestionListener: BalanceGameQuestionListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(questionItemUIState: QuestionItemUIState, position: Int, totalCount: Int) {

            binding.tvBalanceGameDescription.text = questionItemUIState.description
            binding.btnBalanceGameTopOption.text = questionItemUIState.topOption
            binding.btnBalanceGameBottomOption.text = questionItemUIState.bottomOption
            binding.tvBalanceGameCurrentPosition.text = position.toString()
            binding.tvBalanceGameTotalCount.text = totalCount.toString()

            setOptionButton(binding.btnBalanceGameTopOption, questionItemUIState.answer == BalanceGameOption.TOP)
            setOptionButton(binding.btnBalanceGameBottomOption, questionItemUIState.answer == BalanceGameOption.BOTTOM)

            binding.btnBalanceGameTopOption.setOnClickListener {
                balanceGameQuestionListener.onOptionSelected(absoluteAdapterPosition, BalanceGameOption.TOP)
            }
            binding.btnBalanceGameBottomOption.setOnClickListener {
                balanceGameQuestionListener.onOptionSelected(absoluteAdapterPosition, BalanceGameOption.BOTTOM)
            }

            Glide.with(context)
                .load(profilePhotoUrl)
                .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
                .into(binding.ivBalanceGameProfilePhoto)
        }

        private fun setOptionButton(button: Button, selected: Boolean) {
            if (selected) {
                val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_baseline_check_circle_outline_24, null)
                button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                button.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.PrimaryLighter)
                button.compoundDrawableTintList = AppCompatResources.getColorStateList(context, R.color.Primary)
            } else {
                val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_baseline_radio_button_unchecked_24, null)
                button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                button.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.Grey)
                button.compoundDrawableTintList = AppCompatResources.getColorStateList(context, R.color.GreyDark)
            }
        }
    }


}