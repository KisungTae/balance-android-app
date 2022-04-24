package com.beeswork.balance.ui.registeractivity.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentPhotoBinding
import com.beeswork.balance.ui.common.RegisterStepListener
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.photofragment.BasePhotoFragment
import com.beeswork.balance.ui.photofragment.PhotoViewModel
import com.beeswork.balance.ui.photofragment.PhotoViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class PhotoFragment(
    private val registerStepListener: RegisterStepListener
) : BasePhotoFragment(), KodeinAware {
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
        super.onViewCreated(viewModel, binding.layoutPhotoPicker)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupNextBtnListener()
    }

    private fun setupNextBtnListener() {
        binding.btnRegisterPhotoNext.setOnClickListener {
            if (photoPickerRecyclerViewAdapter.hasOccupiedPhoto()) {
                registerStepListener.onMoveToNextStep()
            } else {
                val title = getString(R.string.error_title_fail_to_next_register_step)
                val message = getString(R.string.error_message_upload_photo)
                ErrorDialog.show(title, message, childFragmentManager)
            }
        }
    }
}