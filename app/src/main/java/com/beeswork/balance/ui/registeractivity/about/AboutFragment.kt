package com.beeswork.balance.ui.registeractivity.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentAboutBinding
import com.beeswork.balance.ui.registeractivity.name.NameViewModel
import com.beeswork.balance.ui.registeractivity.name.NameViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AboutFragment: Fragment(), KodeinAware {

    override val kodein by closestKodein()
    private lateinit var binding: FragmentAboutBinding
    private lateinit var viewModel: AboutViewModel
    private val viewModelFactory: AboutViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAboutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AboutViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {

    }
}