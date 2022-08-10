package com.beeswork.balance.ui.cardfragment.filter


import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogCardFilterBinding
import com.beeswork.balance.internal.constant.CardFilterConstant
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.observeUIState
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware

import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class CardFilterDialog(
    private val showGenderTip: Boolean
) : BottomSheetDialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: CardFilterDialogViewModelFactory by instance()
    private lateinit var viewModel: CardFilterDialogViewModel
    private lateinit var binding: DialogCardFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCardFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CardFilterDialogViewModel::class.java)
        bind()
    }

    private fun bind() = lifecycleScope.launch {
        binding.llGenderTipWrapper.visibility = if (showGenderTip) {
            View.VISIBLE
        } else {
            View.GONE
        }
        setupSliders()
        setupApplyButtonListener()
        observeCardFilterUIStateLiveData()
        observeSaveCardFilterUIStateLiveData()
        viewModel.fetchCardFilter()
    }

    private fun observeSaveCardFilterUIStateLiveData() {
        viewModel.saveCardFilterUIStateLiveData.observeUIState(viewLifecycleOwner, requireActivity()) { saveCardFilterUIstate ->
            if (saveCardFilterUIstate.saved) {
                dismiss()
            } else if (saveCardFilterUIstate.showError) {
                val title = getString(R.string.error_title_save_card_filter)
                val message = MessageSource.getMessage(saveCardFilterUIstate.exception)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }

    private fun setupApplyButtonListener() {
        binding.btnCardFilterApply.setOnClickListener {
            val gender = when (binding.rgCardFilterGender.checkedRadioButtonId) {
                R.id.rbCardFilterMale -> Gender.MALE
                R.id.rbCardFilterFemale -> Gender.FEMALE
                else -> null
            }

            if (gender == null) {
                binding.tvGenderError.visibility = View.VISIBLE
            } else {
                val minAge = binding.rsCardFilterAge.values[0].toInt()
                val maxAge = binding.rsCardFilterAge.values[1].toInt()
                val distance = binding.sliderCardFilterDistance.value.toInt()
                viewModel.saveCardFilter(gender, minAge, maxAge, distance)
            }
        }
    }

    private fun setupSliders() {
        binding.rsCardFilterAge.valueFrom = CardFilterConstant.MIN_AGE.toFloat()
        binding.rsCardFilterAge.valueTo = CardFilterConstant.MAX_AGE.toFloat()
        binding.rsCardFilterAge.addOnChangeListener { rangeSlider, _, _ ->
            binding.tvCardFilterMinAge.text = rangeSlider.values[0].toInt().toString()
            binding.tvCardFilterMaxAge.text = rangeSlider.values[1].toInt().toString()
        }

        binding.sliderCardFilterDistance.valueFrom = CardFilterConstant.MIN_DISTANCE.toFloat()
        binding.sliderCardFilterDistance.valueTo = CardFilterConstant.MAX_DISTANCE.toFloat()
        binding.sliderCardFilterDistance.addOnChangeListener { _, value, _ ->
            binding.tvCardFilterDistance.text = value.toInt().toString()
        }
    }

    private fun observeCardFilterUIStateLiveData() {
        viewModel.cardFilterUIStateLiveData.observe(viewLifecycleOwner) {
            binding.rbCardFilterFemale.isChecked = it.gender == Gender.FEMALE
            binding.rbCardFilterMale.isChecked = it.gender == Gender.MALE
            binding.sliderCardFilterDistance.value = it.distance.toFloat()
            binding.rsCardFilterAge.values = arrayListOf(it.minAge.toFloat(), it.maxAge.toFloat())
        }
    }

    companion object {
        const val TAG = "cardFilterDialog"
    }

}