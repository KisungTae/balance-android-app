package com.beeswork.balance.ui.profilefragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.databinding.FragmentProfileBinding
import com.beeswork.balance.domain.uistate.profile.ProfileUIState
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.exception.ServerException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.internal.util.observeResource
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment
import com.beeswork.balance.ui.photofragment.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.io.File


class ProfileFragment : BasePhotoFragment(),
    HeightOptionDialog.HeightOptionDialogListener,
    ErrorDialog.RetryListener{

    private val viewModelFactory: ProfileViewModelFactory by instance()
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState, binding.layoutPhotoPicker)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        bindUI()
        viewModel.fetchProfile()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        observeFetchProfileLiveData()
        observeSaveBioLiveData()
        setupListeners()
    }

    private fun setupListeners() {
        binding.llProfileHeightWrapper.setOnClickListener {
            val height = binding.tvProfileHeight.text.toString().toIntOrNull()
            HeightOptionDialog(this, height).show(childFragmentManager, HeightOptionDialog.TAG)
        }
//        binding.btnProfileEditBalanceGame.setOnClickListener {
//            ProfileBalanceGameDialog().show(childFragmentManager, ProfileBalanceGameDialog.TAG)
//        }

    }



    private fun setupToolBar() {
        binding.btnProfileSave.setOnClickListener { saveBio() }
        binding.btnProfileBack.setOnClickListener { Navigator.popBackStack(activity, MainViewPagerFragment.TAG) }
    }

    override fun onHeightChanged(height: Int) {
        binding.tvProfileHeight.text = height.toString()
    }


    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.FETCH_PROFILE -> viewModel.fetchProfile()
        }
    }





    private fun observeFetchProfileLiveData() {
        viewModel.fetchProfileLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            updateRefreshBtn()
            when {
                resource.isSuccess() -> showFetchProfileSuccess(resource.data)
                resource.isLoading() -> {
                    disableProfileEdit()
                    setupProfile(resource.data)
                }
                resource.isError() -> showFetchProfileError(resource.exception)
            }
        }
    }

    private fun showFetchProfileSuccess(profileUIState: ProfileUIState?) {
        enableProfileEdit()
        setupProfile(profileUIState)
    }

    private fun showFetchProfileError(exception: Throwable?) {
        disableProfileEdit()
        val title = getString(R.string.error_title_fetch_profile)
        val message = MessageSource.getMessage(requireContext(), exception)
        ErrorDialog.show(title, message, RequestCode.FETCH_PROFILE, this, childFragmentManager)
    }

    private fun setupProfile(profileUIState: ProfileUIState?) {
        profileUIState?.let { _profileDomain ->
            binding.tvProfileName.text = _profileDomain.name
            binding.tvProfileDateOfBirth.text = _profileDomain.birth?.format(DateTimePattern.ofDate())
            binding.tvProfileHeight.text = _profileDomain.height?.toString() ?: ""
            binding.etProfileAbout.setText(_profileDomain.about)
            when (_profileDomain.gender) {
                Gender.FEMALE -> setupGender(binding.tvProfileGenderFemale, R.drawable.sh_radio_button_left_checked)
                else -> setupGender(binding.tvProfileGenderMale, R.drawable.sh_radio_button_right_checked)
            }
        }
    }

    private fun observeSaveBioLiveData() {
        viewModel.saveBioLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isLoading() -> {
                    hideFieldErrors()
                    disableProfileEdit()
                    showLoading()
                }
                resource.isSuccess() -> showSaveBioSuccess()
                resource.isError() -> {
                    showSaveBioError(resource.data, resource.exception)
                }
            }
        }
    }

    private fun setupGender(textView: TextView, backgroundId: Int) {
        textView.background = ContextCompat.getDrawable(requireContext(), backgroundId)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary))
    }

    private fun showSaveBioSuccess() {
        hideLoadingAndRefreshBtn()
        hideFieldErrors()
        enableProfileEdit()
        Toast.makeText(requireContext(), getString(R.string.save_about_success_message), Toast.LENGTH_SHORT).show()
    }

    private fun showSaveBioError(
        profileUIState: ProfileUIState?,
        exception: Throwable?
    ) {
        enableProfileEdit()
        hideLoadingAndRefreshBtn()

        if (exception is ServerException && !exception.fieldErrors.isNullOrEmpty()) {
            exception.fieldErrors.entries.forEach { entry ->
                val errorTextView = getErrorViewByTag(entry.key)
                errorTextView.text = entry.value
                errorTextView.visibility = View.VISIBLE
            }
        } else {
            setupProfile(profileUIState)
            hideFieldErrors()
            val title = getString(R.string.error_title_save_about)
            val message = MessageSource.getMessage(requireContext(), exception)
            ErrorDialog.show(title, message, childFragmentManager)
        }
    }

    private fun hideFieldErrors() {
        binding.tvAboutError.visibility = View.GONE
    }

    private fun getErrorViewByTag(tag: String): TextView {
        return binding.root.findViewWithTag("${tag}Error")
    }

    private fun saveBio(): Boolean {
        val height = binding.tvProfileHeight.text.toString().toIntOrNull()
        val about = binding.etProfileAbout.text.toString()
        viewModel.saveBio(height, about)
        return true
    }

    private fun disableProfileEdit() {
        binding.btnProfileSave.isEnabled = false
        binding.etProfileAbout.isEnabled = false
        binding.llProfileHeightWrapper.isClickable = false
    }

    private fun enableProfileEdit() {
        binding.btnProfileSave.isEnabled = true
        binding.etProfileAbout.isEnabled = true
        binding.llProfileHeightWrapper.isClickable = true
    }


    private fun showLoading() {
//        binding.btnProfileRefresh.visibility = View.GONE
//        binding.skvProfileLoading.visibility = View.VISIBLE
    }

    private fun showRefreshBtn() {
//        binding.btnProfileRefresh.visibility = View.VISIBLE
//        binding.skvProfileLoading.visibility = View.GONE
    }

    private fun hideLoadingAndRefreshBtn() {
//        binding.btnProfileRefresh.visibility = View.INVISIBLE
//        binding.skvProfileLoading.visibility = View.GONE
    }

    private fun updateRefreshBtn() {
//        if (fetchProfileStatus == Resource.Status.LOADING || fetchPhotosStatus == Resource.Status.LOADING) showLoading()
//        else if (fetchProfileStatus == Resource.Status.ERROR || fetchPhotosStatus == Resource.Status.ERROR) showRefreshBtn()
//        else hideLoadingAndRefreshBtn()
    }
}