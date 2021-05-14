package com.beeswork.balance.ui.swipe


import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogSwipeFilterBinding
import com.beeswork.balance.internal.constant.Gender
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware

import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class SwipeFilterDialog(
    private val swipeFilterDialogListener: SwipeFilterDialogListener
) : BottomSheetDialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeFilterDialogViewModelFactory by instance()
    private lateinit var viewModel: SwipeFilterDialogViewModel
    private lateinit var binding: DialogSwipeFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSwipeFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SwipeFilterDialogViewModel::class.java)
        bind()
    }

    private fun bind() = lifecycleScope.launch {
        setupSliders()
        setupSwipeFilterLiveData()
        setupApplyButtonListener()
        setupSaveSwipeFilterLiveData()
        viewModel.fetchSwipeFilter()
    }

    private fun setupSaveSwipeFilterLiveData() {
        viewModel.saveSwipeFilterLiveData.observe(viewLifecycleOwner) {
            swipeFilterDialogListener.onApplySwipeFilter()
            dismiss()
        }
    }

    private fun setupApplyButtonListener() {
        binding.btnSwipeFilterApply.setOnClickListener {
            val minAge = binding.rsSwipeFilterAge.values[0].toInt()
            val maxAge = binding.rsSwipeFilterAge.values[1].toInt()
            val distance = binding.sliderSwipeFilterDistance.value.toInt()
            viewModel.saveSwipeFilter(getGender(), minAge, maxAge, distance)
        }
    }

    private fun getGender(): Gender {
        return when (binding.rgSwipeFilterGender.checkedRadioButtonId) {
            R.id.rbSwipeFilterMale -> Gender.MALE
            else -> Gender.FEMALE
        }
    }

    private fun setupSliders() {
        binding.rsSwipeFilterAge.addOnChangeListener { rangeSlider, _, _ ->
            binding.tvSwipeFilterMinAge.text = rangeSlider.values[0].toInt().toString()
            binding.tvSwipeFilterMaxAge.text = rangeSlider.values[1].toInt().toString()
        }
        binding.sliderSwipeFilterDistance.addOnChangeListener { _, value, _ ->
            binding.tvSwipeFilterDistance.text = value.toInt().toString()
        }
    }

    private fun setupSwipeFilterLiveData() {
        viewModel.swipeFilterLiveData.observe(viewLifecycleOwner) {
            binding.rbSwipeFilterFemale.isChecked = it.gender == Gender.FEMALE
            binding.rbSwipeFilterMale.isChecked = it.gender == Gender.MALE
            binding.sliderSwipeFilterDistance.value = it.distance.toFloat()
            binding.rsSwipeFilterAge.values = arrayListOf(it.minAge.toFloat(), it.maxAge.toFloat())
        }
    }

    interface SwipeFilterDialogListener {
        fun onApplySwipeFilter()
    }

    companion object {
        const val TAG = "swipeFilterDialog"
    }

}