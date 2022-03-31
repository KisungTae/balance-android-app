package com.beeswork.balance.ui.registeractivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beeswork.balance.ui.registeractivity.about.AboutFragment
import com.beeswork.balance.ui.registeractivity.birthdate.BirthDateFragment
import com.beeswork.balance.ui.registeractivity.gender.GenderFragment
import com.beeswork.balance.ui.registeractivity.height.HeightFragment
import com.beeswork.balance.ui.registeractivity.name.NameFragment
import com.beeswork.balance.ui.registeractivity.photo.PhotoFragment
import com.beeswork.balance.ui.registeractivity.registerfinish.RegisterFinishFragment

class RegisterViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int {
        return RegisterViewPagerTabPosition.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            RegisterViewPagerTabPosition.NAME.ordinal -> NameFragment()
            RegisterViewPagerTabPosition.GENDER.ordinal -> GenderFragment()
            RegisterViewPagerTabPosition.BIRTH_DATE.ordinal -> BirthDateFragment()
            RegisterViewPagerTabPosition.HEIGHT.ordinal -> HeightFragment()
            RegisterViewPagerTabPosition.ABOUT.ordinal -> AboutFragment()
            RegisterViewPagerTabPosition.PHOTO.ordinal -> PhotoFragment()
            RegisterViewPagerTabPosition.FINISH.ordinal -> RegisterFinishFragment()
            else -> NameFragment()
        }
    }

}