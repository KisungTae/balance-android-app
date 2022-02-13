package com.beeswork.balance.ui.cardfragment.filter


import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogCardFilterBinding
import com.beeswork.balance.internal.constant.Gender
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware

import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class CardFilterDialog(
    private val cardFilterDialogListener: CardFilterDialogListener
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
        setupSliders()
        setupCardFilterLiveData()
        setupApplyButtonListener()
        setupSaveCardFilterLiveData()
        viewModel.fetchCardFilter()
    }

    private fun setupSaveCardFilterLiveData() {
        viewModel.saveCardFilterLiveData.observe(viewLifecycleOwner) {
            cardFilterDialogListener.onApplyCardFilter()
            dismiss()
        }
    }

    private fun setupApplyButtonListener() {
        binding.btnCardFilterApply.setOnClickListener {
            val minAge = binding.rsCardFilterAge.values[0].toInt()
            val maxAge = binding.rsCardFilterAge.values[1].toInt()
            val distance = binding.sliderCardFilterDistance.value.toInt()
            viewModel.saveCardFilter(getGender(), minAge, maxAge, distance)
            dismiss()
        }
    }

    private fun getGender(): Boolean {
        return when (binding.rgCardFilterGender.checkedRadioButtonId) {
            R.id.rbCardFilterMale -> Gender.MALE
            else -> Gender.FEMALE
        }
    }

    private fun setupSliders() {
        binding.rsCardFilterAge.addOnChangeListener { rangeSlider, _, _ ->
            binding.tvCardFilterMinAge.text = rangeSlider.values[0].toInt().toString()
            binding.tvCardFilterMaxAge.text = rangeSlider.values[1].toInt().toString()
        }
        binding.sliderCardFilterDistance.addOnChangeListener { _, value, _ ->
            binding.tvCardFilterDistance.text = value.toInt().toString()
        }
    }

    private fun setupCardFilterLiveData() {
        viewModel.cardFilterLiveData.observe(viewLifecycleOwner) {
            binding.rbCardFilterFemale.isChecked = it.gender == Gender.FEMALE
            binding.rbCardFilterMale.isChecked = it.gender == Gender.MALE
            binding.sliderCardFilterDistance.value = it.distance.toFloat()
            binding.rsCardFilterAge.values = arrayListOf(it.minAge.toFloat(), it.maxAge.toFloat())
        }
    }

    interface CardFilterDialogListener {
        fun onApplyCardFilter()
    }

    companion object {
        const val TAG = "cardFilterDialog"
    }

}