package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentProfileBinding
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : BaseFragment(), KodeinAware, HeightOptionDialog.HeightOptionDialogListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ProfileViewModelFactory by instance()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        bindUI()
        viewModel.fetchProfile()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupProfileLiveDataObserver()
        setupPhotoPickerRecyclerView()
        setupSaveAboutLiveDataObserver()
        setupListeners()
    }

    private fun setupSaveAboutLiveDataObserver() {
        viewModel.saveAboutLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading()
                it.isError() -> {
                    hideLoading()
                    val errorTitle = getString(R.string.error_title_save_about)
                    showErrorDialog(it.error, errorTitle, it.errorMessage)
                }
                it.isSuccess() -> popBackStack()
            }
        }
    }

    private fun showLoading() {
        binding.btnProfileSave.visibility = View.GONE
        binding.spvProfileLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.btnProfileSave.visibility = View.VISIBLE
        binding.spvProfileLoading.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.llProfileHeightWrapper.setOnClickListener {
            val height = binding.tvProfileHeight.text.toString().toIntOrNull()
            HeightOptionDialog(this, height).show(childFragmentManager, HeightOptionDialog.TAG)
        }
        binding.btnProfileEditBalanceGame.setOnClickListener {  }
    }

    private fun setupProfileLiveDataObserver() {
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            binding.tvProfileName.text = it.name
            binding.tvProfileDateOfBirth.text = it.birth.format(DateTimePattern.ofDate())
            binding.tvProfileHeight.text = it.height?.toString() ?: ""
            binding.etProfileAbout.setText(it.about)
            when (it.gender) {
                Gender.FEMALE -> setupGender(binding.tvProfileGenderFemale, R.drawable.sh_radio_button_left_checked)
                else -> setupGender(binding.tvProfileGenderMale, R.drawable.sh_radio_button_right_checked)
            }
        }
    }

    private fun setupGender(textView: TextView, backgroundId: Int) {
        textView.background = ContextCompat.getDrawable(requireContext(), backgroundId)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary))
    }

    private fun setupPhotoPickerRecyclerView() {

    }

    private fun setupToolBar() {
        binding.btnProfileSave.setOnClickListener { saveProfile() }
        binding.btnProfileBack.setOnClickListener { popBackStack() }
    }

    private fun saveProfile(): Boolean {
        val height = binding.tvProfileHeight.text.toString().toIntOrNull()
        val about = binding.etProfileAbout.text.toString()
        viewModel.saveAbout(height, about)
        return true
    }

    private fun popBackStack() {
        requireActivity().supportFragmentManager.popBackStack(
            MainViewPagerFragment.TAG,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    override fun onValueChanged(height: Int) {
        binding.tvProfileHeight.text = height.toString()
    }
}