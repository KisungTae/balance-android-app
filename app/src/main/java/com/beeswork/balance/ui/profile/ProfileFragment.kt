package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ProfileFragment : BaseFragment(), KodeinAware {

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
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupProfileLiveDataObserver()
        setupPhotoPickerRecyclerView()
    }

    private fun setupProfileLiveDataObserver() {
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            binding.tvProfileName.text = it.name
            binding.tvProfileDateOfBirth.text = it.birth.format(DateTimePattern.ofDate())
            binding.tvProfileHeight.text = it.height.toString()
            binding.etProfileAbout.setText(it.about)

            if (it.gender == Gender.FEMALE) {
                binding.tvProfileGenderFemale.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.sh_left_round_box_border_black
                )
                binding.tvProfileGenderMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
            } else {
                binding.tvProfileGenderMale.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.sh_right_round_box_border_black
                )
                binding.tvProfileGenderFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
            }
        }
    }


    private fun setupPhotoPickerRecyclerView() {

    }

    private fun setupToolBar() {
        binding.tbProfile.inflateMenu(R.menu.profile_tool_bar)
        binding.tbProfile.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miProfileSave -> {
//                  TODO: remove me
//                    viewModel.test()
                    true
                }
                else -> false
            }
        }
        binding.btnProfileBack.setOnClickListener { popBackStack() }
    }

    private fun popBackStack() {
        requireActivity().supportFragmentManager.popBackStack(
            MainViewPagerFragment.TAG,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}