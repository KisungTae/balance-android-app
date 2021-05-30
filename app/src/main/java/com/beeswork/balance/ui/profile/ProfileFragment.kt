package com.beeswork.balance.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentProfileBinding
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : BaseFragment(),
    KodeinAware,
    HeightOptionDialog.HeightOptionDialogListener,
    PhotoPickerRecyclerViewAdapter.PhotoPickerListener,
    ErrorDialog.OnRetryListener,
    PhotoPickerOptionDialog.PhotoPickerOptionListener {

    override val kodein by closestKodein()
    private val preferenceProvider: PreferenceProvider by instance()
    private val viewModelFactory: ProfileViewModelFactory by instance()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var photoPickerRecyclerViewAdapter: PhotoPickerRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        bindUI()
        viewModel.fetchProfile()
        viewModel.fetchPhotos()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupFetchProfileLiveDataObserver()
        setupPhotoPickerRecyclerView()
        setupSaveAboutLiveDataObserver()
        setupFetchPhotosLiveDataObserver()
        setupListeners()
    }

    private fun setupFetchPhotosLiveDataObserver() {
        viewModel.fetchPhotosLiveData.observe(viewLifecycleOwner) {
            when {
                it.isSuccess() -> it.data?.let { photos -> photoPickerRecyclerViewAdapter.initPhotoPicker(photos) }
                it.isError() && validateAccount(it.error, it.errorMessage) -> {
                    binding.btnProfileRefresh.visibility = View.VISIBLE
                    showErrorDialog(
                        it.error,
                        getString(R.string.error_title_fetch_photos),
                        it.errorMessage,
                        RequestCode.FETCH_PHOTOS,
                        this
                    )
                }
            }
        }
    }

    private fun setupSaveAboutLiveDataObserver() {
        viewModel.saveAboutLiveData.observe(viewLifecycleOwner) {
            when {
                it.isLoading() -> showLoading()
                it.isError() && validateAccount(it.error, it.errorMessage) -> {
                    hideLoading()
                    showErrorDialog(
                        it.error,
                        getString(R.string.error_title_save_about),
                        it.errorMessage,
                        RequestCode.SAVE_ABOUT,
                        this
                    )
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
        binding.btnProfileEditBalanceGame.setOnClickListener {
            ProfileBalanceGameDialog().show(childFragmentManager, ProfileBalanceGameDialog.TAG)
        }
        binding.btnProfileRefresh.setOnClickListener {
            if (it.visibility == View.VISIBLE) {
                viewModel.fetchPhotos()
                it.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupFetchProfileLiveDataObserver() {
        viewModel.fetchProfileLiveData.observe(viewLifecycleOwner) {
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
        photoPickerRecyclerViewAdapter = PhotoPickerRecyclerViewAdapter(
            requireContext(),
            this,
            preferenceProvider.getAccountId()
        )
        binding.rvPhotoPicker.adapter = photoPickerRecyclerViewAdapter
        binding.rvPhotoPicker.layoutManager = object : GridLayoutManager(
            requireContext(),
            ProfileDialog.PHOTO_PICKER_GALLERY_COLUMN_NUM
        ) {
            override fun canScrollVertically(): Boolean = false
            override fun canScrollHorizontally(): Boolean = false
        }
    }

    private fun setupToolBar() {
        binding.btnProfileSave.setOnClickListener { saveAbout() }
        binding.btnProfileBack.setOnClickListener { popBackStack() }
    }

    private fun saveAbout(): Boolean {
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

    override fun onHeightChanged(height: Int) {
        binding.tvProfileHeight.text = height.toString()
    }


    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.SAVE_ABOUT -> saveAbout()
            RequestCode.FETCH_PHOTOS -> viewModel.fetchPhotos()
        }
    }

    override fun onClickPhotoPicker(position: Int) {
        val photoPicker = photoPickerRecyclerViewAdapter.getPhotoPicker(position)
        PhotoPickerOptionDialog(photoPicker.key, photoPicker.status, this).show(
            childFragmentManager,
            PhotoPickerOptionDialog.TAG
        )
    }

    override fun onDeletePhoto(photoKey: String, photoPickerStatus: PhotoPicker.Status) {
        TODO("Not yet implemented")
    }

    override fun onReuploadPhoto(photoKey: String) {
        TODO("Not yet implemented")
    }

    override fun onRedownloadPhoto(photoKey: String) {
        TODO("Not yet implemented")
    }

    override fun onUploadPhotoFromGallery() {
        TODO("Not yet implemented")
    }

    override fun onUploadPhotoFromCapture() {
        TODO("Not yet implemented")
    }
}