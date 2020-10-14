package com.beeswork.balance.ui.swipe


import android.os.Bundle
import android.view.*
import androidx.preference.PreferenceManager
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.constant.PreferencesDefault
import com.beeswork.balance.internal.constant.PreferencesKey
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_swipe_filter.*


class SwipeFilterDialog(
    private val preferenceProvider: PreferenceProvider
): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_swipe_filter, container, false)
    }

    override fun onStop() {

        val gender = rgSwipeFilterGender.checkedRadioButtonId == R.id.rbSwipeFilterFemale
        val minAge = rsSwipeFilterAge.values[0]
        val maxAge = rsSwipeFilterAge.values[1]
        val distance = sliderSwipeFilterDistance.value

        preferenceProvider.putSwipeFilterValues(gender, minAge, maxAge, distance)
        super.onStop()
    }

    override fun onStart() {
        super.onStart()

        rsSwipeFilterAge.addOnChangeListener { rangeSlider, value, fromUser ->
            tvSwipeFilterMinAge.text = rangeSlider.values[0].toInt().toString()
            tvSwipeFilterMaxAge.text = rangeSlider.values[1].toInt().toString()
        }

        sliderSwipeFilterDistance.addOnChangeListener { rangeSlider, value, fromUser ->
            tvSwipeFilterDistance.text = value.toInt().toString()
        }

        sliderSwipeFilterDistance.value = preferenceProvider.getDistance()

        rsSwipeFilterAge.values = arrayListOf(preferenceProvider.getMinAge(), preferenceProvider.getMaxAge())

        when (preferenceProvider.getGender()) {
            Gender.FEMALE -> rbSwipeFilterFemale.isChecked = true
            Gender.MALE -> rbSwipeFilterMale.isChecked = true
        }
    }


}