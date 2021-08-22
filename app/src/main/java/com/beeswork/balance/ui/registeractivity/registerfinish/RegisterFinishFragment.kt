package com.beeswork.balance.ui.registeractivity.registerfinish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentGenderBinding
import com.beeswork.balance.databinding.FragmentRegisterFinishBinding
import com.beeswork.balance.ui.registeractivity.RegisterViewPagerAdapter
import com.beeswork.balance.ui.registeractivity.photo.PhotoViewModel
import com.beeswork.balance.ui.registeractivity.photo.PhotoViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class RegisterFinishFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentRegisterFinishBinding
    private lateinit var viewModel: RegisterFinishViewModel
    private val viewModelFactory: RegisterFinishViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterFinishBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RegisterFinishViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {

    }


}