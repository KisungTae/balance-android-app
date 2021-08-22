package com.beeswork.balance.ui.registeractivity.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentGenderBinding
import com.beeswork.balance.databinding.FragmentNameBinding
import com.beeswork.balance.ui.registeractivity.RegisterActivity
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class NameFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentNameBinding
    private lateinit var viewModel: NameViewModel
    private val viewModelFactory: NameViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NameViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        binding.btnRegisterNameNext.setOnClickListener {
            activity?.let { _activity -> (_activity as RegisterActivity).moveToNextTab() }
        }
    }

}