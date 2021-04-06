package com.beeswork.balance.ui.splash

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.beeswork.balance.R


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.Primary)
        //TODO: check login

//        Handler().postDelayed({findNavController().navigate(R.id.action_splashFragment_to_mainViewPagerFragment)}, 3000)
        findNavController().navigate(R.id.action_splashFragment_to_mainViewPagerFragment)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}