package com.beeswork.balance.ui.registeractivity.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.databinding.FragmentGenderBinding
import com.beeswork.balance.databinding.FragmentPhotoBinding
import com.beeswork.balance.ui.registeractivity.RegisterActivity
import com.beeswork.balance.ui.registeractivity.height.HeightViewModel
import com.beeswork.balance.ui.registeractivity.height.HeightViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class PhotoFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentPhotoBinding
    private lateinit var viewModel: PhotoViewModel
    private val viewModelFactory: PhotoViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPhotoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PhotoViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        binding.btnRegisterPhotoNext.setOnClickListener {
            activity?.let { _activity ->
                (_activity as RegisterActivity).moveToNextTab()
            }
        }
    }

}