package com.beeswork.balance.ui.swipe


import android.os.Bundle
import android.view.*
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogSwipeFilterBinding
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware

import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class SwipeFilterDialog : BottomSheetDialogFragment(), KodeinAware {

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
    }

    override fun onStop() {

//        val gender = binding.rgSwipeFilterGender.checkedRadioButtonId == R.id.rbSwipeFilterFemale
//        val minAge = binding.rsSwipeFilterAge.values[0]
//        val maxAge = binding.rsSwipeFilterAge.values[1]
//        val distance = binding.sliderSwipeFilterDistance.value
//
//        preferenceProvider.putSwipeFilterValues(gender, minAge, maxAge, distance)
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
//
//        binding.rsSwipeFilterAge.addOnChangeListener { rangeSlider, value, fromUser ->
//            binding.tvSwipeFilterMinAge.text = rangeSlider.values[0].toInt().toString()
//            binding.tvSwipeFilterMaxAge.text = rangeSlider.values[1].toInt().toString()
//        }
//
//        binding.sliderSwipeFilterDistance.addOnChangeListener { rangeSlider, value, fromUser ->
//            binding.tvSwipeFilterDistance.text = value.toInt().toString()
//        }
//
//        binding.sliderSwipeFilterDistance.value = preferenceProvider.getDistance()
//
//        binding.rsSwipeFilterAge.values = arrayListOf(preferenceProvider.getMinAge(), preferenceProvider.getMaxAge())
//
//        when (preferenceProvider.getGender()) {
//            Gender.FEMALE -> binding.rbSwipeFilterFemale.isChecked = true
//            Gender.MALE -> binding.rbSwipeFilterMale.isChecked = true
//        }
    }

    companion object {
        const val TAG = "swipeFilterDialog"

    }

}