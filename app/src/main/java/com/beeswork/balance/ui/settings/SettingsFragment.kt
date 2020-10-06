package com.beeswork.balance.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.beeswork.balance.R

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }


}