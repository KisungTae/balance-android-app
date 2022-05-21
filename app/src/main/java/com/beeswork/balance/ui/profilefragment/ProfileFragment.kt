package com.beeswork.balance.ui.profilefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentProfileBinding
import com.beeswork.balance.domain.uistate.profile.ProfileUIState
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.exception.ServerException
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.internal.util.observeResource
import com.beeswork.balance.internal.util.observeUIState
import com.beeswork.balance.ui.balancegamedialog.BalanceGameListener
import com.beeswork.balance.ui.balancegamedialog.ProfileBalanceGameDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment
import com.beeswork.balance.ui.photofragment.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance


class ProfileFragment : BasePhotoFragment(),
    HeightOptionDialog.HeightOptionDialogListener,
    ErrorDialog.RetryListener,
    BalanceGameListener {

    private val viewModelFactory: ProfileViewModelFactory by instance()
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState, binding.profilePhotoPickerLayout)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        bindUI()
        viewModel.fetchProfile()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupListeners()
        observeFetchProfileUIStateLiveData()
        observeSaveBioLiveData()

    }

    private fun setupToolBar() {
        binding.btnProfileSave.setOnClickListener { saveBio() }
        binding.btnProfileBack.setOnClickListener { Navigator.popBackStack(activity, MainViewPagerFragment.TAG) }
    }

    private fun setupListeners() {
        binding.llProfileHeightWrapper.setOnClickListener {
            val height = binding.tvProfileHeight.text.toString().toIntOrNull()
            HeightOptionDialog(this, height).show(childFragmentManager, HeightOptionDialog.TAG)
        }
        binding.btnProfileRefresh.setOnClickListener {
            viewModel.fetchProfile()
        }
        binding.btnProfileEditBalanceGame.setOnClickListener {
            ProfileBalanceGameDialog(null, this@ProfileFragment).show(childFragmentManager, ProfileBalanceGameDialog.TAG)
        }
    }

    private fun observeFetchProfileUIStateLiveData() {
        viewModel.fetchProfileUIStateLiveData.observeUIState(viewLifecycleOwner, activity) { fetchProfileUIState ->
            when {
                fetchProfileUIState.profileUIState != null -> {
                    setupProfile(fetchProfileUIState.profileUIState)
                    binding.btnProfileRefresh.visibility = View.GONE
                    binding.skvProfileLoading.visibility = View.GONE
                    binding.btnProfileSave.isEnabled = true
                }
                fetchProfileUIState.showLoading -> {
                    binding.btnProfileRefresh.visibility = View.GONE
                    binding.skvProfileLoading.visibility = View.VISIBLE
                    binding.btnProfileSave.isEnabled = false
                }
                fetchProfileUIState.showError -> {
                    binding.btnProfileRefresh.visibility = View.VISIBLE
                    binding.skvProfileLoading.visibility = View.GONE
                    binding.btnProfileSave.isEnabled = false
                    val title = getString(R.string.error_title_fetch_profile)
                    val message = MessageSource.getMessage(requireContext(), fetchProfileUIState.exception)
                    ErrorDialog.show(title, message, RequestCode.FETCH_PROFILE, this, childFragmentManager)
                }
            }
        }
    }

    private fun setupProfile(profileUIState: ProfileUIState) {
        binding.tvProfileName.text = profileUIState.name
        binding.tvProfileDateOfBirth.text = profileUIState.birth?.format(DateTimePattern.ofDate())
        binding.tvProfileHeight.text = profileUIState.height?.toString() ?: ""
        binding.etProfileAbout.setText(profileUIState.about)
        when (profileUIState.gender) {
            Gender.FEMALE -> setupGender(binding.tvProfileGenderFemale, R.drawable.sh_radio_button_left_checked)
            else -> setupGender(binding.tvProfileGenderMale, R.drawable.sh_radio_button_right_checked)
        }
    }

    private fun observeSaveBioLiveData() {
        viewModel.saveBioLiveData.observeUIState(viewLifecycleOwner, activity) { saveBioUIState ->
            hideFieldErrors()
            when {
                saveBioUIState.saved -> {
                    Toast.makeText(requireContext(), getString(R.string.save_about_success_message), Toast.LENGTH_SHORT).show()
                    binding.btnProfileSave.visibility = View.VISIBLE
                    binding.llProfileSaveLoading.visibility = View.GONE
                }
                saveBioUIState.showLoading -> {
                    binding.btnProfileSave.visibility = View.GONE
                    binding.llProfileSaveLoading.visibility = View.VISIBLE
                }
                saveBioUIState.showError -> {
                    binding.btnProfileSave.visibility = View.VISIBLE
                    binding.llProfileSaveLoading.visibility = View.GONE
                    showSaveBioError(saveBioUIState.exception)
                }
            }
        }
    }

    private fun setupGender(textView: TextView, backgroundId: Int) {
        textView.background = ContextCompat.getDrawable(requireContext(), backgroundId)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary))
    }

    private fun showSaveBioError(exception: Throwable?) {
        if (exception is ServerException && !exception.fieldErrors.isNullOrEmpty()) {
            exception.fieldErrors.entries.forEach { entry ->
                val errorTextView = getErrorViewByTag(entry.key)
                errorTextView.text = entry.value
                errorTextView.visibility = View.VISIBLE
            }
        } else {
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

    override fun onHeightChanged(height: Int) {
        binding.tvProfileHeight.text = height.toString()
    }


    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.FETCH_PROFILE -> viewModel.fetchProfile()
        }
    }

    override fun onBalanceGameAnswersSaved() {
        Toast.makeText(requireContext(), getString(R.string.saved_balance_game), Toast.LENGTH_SHORT).show()
    }
}