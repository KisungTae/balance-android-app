package com.beeswork.balance.ui.registeractivity.gender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentBirthdateBinding
import com.beeswork.balance.databinding.FragmentGenderBinding
import com.beeswork.balance.ui.registeractivity.about.AboutViewModel
import com.beeswork.balance.ui.registeractivity.about.AboutViewModelFactory
import com.beeswork.balance.ui.registeractivity.birthdate.BirthDateViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class GenderFragment: Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentGenderBinding
    private lateinit var viewModel: GenderViewModel
    private val viewModelFactory: GenderViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGenderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(GenderViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {

    }

}