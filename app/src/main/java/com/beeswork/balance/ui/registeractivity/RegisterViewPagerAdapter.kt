package com.beeswork.balance.ui.registeractivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.registeractivity.about.AboutStepFragment
import com.beeswork.balance.ui.registeractivity.balancegame.BalanceGameStepFragment
import com.beeswork.balance.ui.registeractivity.birthdate.BirthDateStepFragment
import com.beeswork.balance.ui.registeractivity.gender.GenderStepFragment
import com.beeswork.balance.ui.registeractivity.height.HeightStepFragment
import com.beeswork.balance.ui.registeractivity.location.LocationStepFragment
import com.beeswork.balance.ui.registeractivity.name.NameStepFragment
import com.beeswork.balance.ui.registeractivity.photo.PhotoStepFragment
import com.beeswork.balance.ui.registeractivity.registerfinish.RegisterFinishFragment

class RegisterViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle,
    private val registerStepListener: RegisterStepListener
): FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int {
        return RegisterViewPagerTabPosition.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            RegisterViewPagerTabPosition.LOCATION.ordinal -> LocationStepFragment(registerStepListener)
            RegisterViewPagerTabPosition.NAME.ordinal -> NameStepFragment(registerStepListener)
            RegisterViewPagerTabPosition.GENDER.ordinal -> GenderStepFragment(registerStepListener)
            RegisterViewPagerTabPosition.BIRTH_DATE.ordinal -> BirthDateStepFragment(registerStepListener)
            RegisterViewPagerTabPosition.HEIGHT.ordinal -> HeightStepFragment(registerStepListener)
            RegisterViewPagerTabPosition.ABOUT.ordinal -> AboutStepFragment(registerStepListener)
            RegisterViewPagerTabPosition.PHOTO.ordinal -> PhotoStepFragment(registerStepListener)
            RegisterViewPagerTabPosition.BALANCE_GAME.ordinal -> BalanceGameStepFragment(registerStepListener)
            RegisterViewPagerTabPosition.FINISH.ordinal -> RegisterFinishFragment()
            else -> NameStepFragment(registerStepListener)
        }
    }

}