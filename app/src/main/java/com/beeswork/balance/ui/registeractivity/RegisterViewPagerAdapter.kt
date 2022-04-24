package com.beeswork.balance.ui.registeractivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beeswork.balance.ui.common.LocationRequestListener
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.registeractivity.about.AboutFragment
import com.beeswork.balance.ui.registeractivity.balancegame.BalanceGameFragment
import com.beeswork.balance.ui.registeractivity.birthdate.BirthDateFragment
import com.beeswork.balance.ui.registeractivity.gender.GenderFragment
import com.beeswork.balance.ui.registeractivity.height.HeightFragment
import com.beeswork.balance.ui.registeractivity.location.LocationFragment
import com.beeswork.balance.ui.registeractivity.name.NameFragment
import com.beeswork.balance.ui.registeractivity.photo.PhotoFragment
import com.beeswork.balance.ui.registeractivity.registerfinish.RegisterFinishFragment

class RegisterViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle,
    private val registerStepListener: RegisterStepListener,
    private val locationRequestListener: LocationRequestListener
): FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int {
        return RegisterViewPagerTabPosition.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            RegisterViewPagerTabPosition.LOCATION.ordinal -> LocationFragment(registerStepListener, locationRequestListener)
            RegisterViewPagerTabPosition.NAME.ordinal -> NameFragment(registerStepListener)
            RegisterViewPagerTabPosition.GENDER.ordinal -> GenderFragment(registerStepListener)
            RegisterViewPagerTabPosition.BIRTH_DATE.ordinal -> BirthDateFragment(registerStepListener)
            RegisterViewPagerTabPosition.HEIGHT.ordinal -> HeightFragment(registerStepListener)
            RegisterViewPagerTabPosition.ABOUT.ordinal -> AboutFragment(registerStepListener)
            RegisterViewPagerTabPosition.PHOTO.ordinal -> PhotoFragment(registerStepListener)
            RegisterViewPagerTabPosition.BALANCE_GAME.ordinal -> BalanceGameFragment(registerStepListener)
            RegisterViewPagerTabPosition.FINISH.ordinal -> RegisterFinishFragment()
            else -> NameFragment(registerStepListener)
        }
    }

}