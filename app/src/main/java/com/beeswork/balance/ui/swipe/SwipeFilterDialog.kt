package com.beeswork.balance.ui.swipe


import android.os.Bundle
import android.view.*
import androidx.preference.PreferenceManager
import com.beeswork.balance.R
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.constant.PreferencesDefault
import com.beeswork.balance.internal.constant.PreferencesKey
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_swipe_filter.*


class SwipeFilterDialog: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_swipe_filter, container, false)
    }

    override fun onStop() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        val editor = preferences.edit()

        editor.putBoolean(PreferencesKey.GENDER, (rgSwipeFilterGender.checkedRadioButtonId == R.id.rbSwipeFilterFemale))
        editor.putFloat(PreferencesKey.DISTANCE, sliderSwipeFilterDistance.value)

        editor.putFloat(PreferencesKey.MIN_AGE, rsSwipeFilterAge.values[0])
        editor.putFloat(PreferencesKey.MAX_AGE, rsSwipeFilterAge.values[1])
        editor.apply()
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

        val preferences = PreferenceManager.getDefaultSharedPreferences(context?.applicationContext)
        sliderSwipeFilterDistance.value = preferences?.getFloat(PreferencesKey.DISTANCE, PreferencesDefault.DISTANCE)!!

        val minAge = preferences.getFloat(PreferencesKey.MIN_AGE, PreferencesDefault.MIN_AGE)
        val maxAge = preferences.getFloat(PreferencesKey.MAX_AGE, PreferencesDefault.MAX_AGE)
        rsSwipeFilterAge.values = arrayListOf(minAge, maxAge)

        when (preferences.getBoolean(PreferencesKey.GENDER, PreferencesDefault.GENDER)) {
            Gender.FEMALE -> rbSwipeFilterFemale.isChecked = true
            Gender.MALE -> rbSwipeFilterMale.isChecked = true
        }
    }


}