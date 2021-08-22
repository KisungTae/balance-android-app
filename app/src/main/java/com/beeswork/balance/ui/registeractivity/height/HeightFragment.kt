package com.beeswork.balance.ui.registeractivity.height

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentGenderBinding
import com.beeswork.balance.databinding.FragmentHeightBinding
import com.beeswork.balance.ui.registeractivity.gender.GenderViewModel
import com.beeswork.balance.ui.registeractivity.gender.GenderViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class HeightFragment: Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentHeightBinding
    private lateinit var viewModel: HeightViewModel
    private val viewModelFactory: HeightViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHeightBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HeightViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {

    }

}