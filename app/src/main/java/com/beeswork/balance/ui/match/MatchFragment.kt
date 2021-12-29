package com.beeswork.balance.ui.match

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.match.MatchProfileTuple
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.databinding.SnackBarNewMatchBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.internal.util.SnackBarHelper
import com.beeswork.balance.ui.chat.ChatFragment
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class MatchFragment : BaseFragment(), KodeinAware, MatchPagingDataAdapter.MatchListener,
    ErrorDialog.OnRetryListener, ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagingDataAdapter: MatchPagingDataAdapter
    private lateinit var matchPagingRefreshAdapter: PagingRefreshAdapter<MatchDomain, MatchPagingDataAdapter.ViewHolder>
    private lateinit var binding: FragmentMatchBinding
    private var searchJob: Job? = null
    private var newMatchSnackBar: Snackbar? = null
    private val preferenceProvider: PreferenceProvider by instance()

    private var fetchMatchesStatus = Resource.Status.SUCCESS
    private var fetchChatMessagesStatus = Resource.Status.SUCCESS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchViewModel::class.java)
        bindUI()
        viewModel.fetchMatches()
//        viewModel.fetchChatMessages()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupMatchRecyclerView()
        setupToolBars()
        observeMatchInvalidation()
        observeFetchMatchesLiveData()
        observeNewMatchLiveData()
        observeFetchChatMessagesLiveData()
        search("")
    }


    private suspend fun observeNewMatchLiveData() {
        viewModel.newMatchLiveData.await().observe(viewLifecycleOwner) {
            showNewMatchSnackBar(it)
        }
    }

    private suspend fun observeMatchInvalidation() {
        viewModel.matchInvalidation.await().observe(viewLifecycleOwner) {
            matchPagingRefreshAdapter.refresh()
        }
    }

    private fun showNewMatchSnackBar(matchProfileTuple: MatchProfileTuple) {
        val binding = SnackBarNewMatchBinding.inflate(layoutInflater)
        val topPadding = resources.getDimension(R.dimen.snack_bar_top_padding).toInt()
        val snackBar = SnackBarHelper.make(requireView(), Gravity.TOP, topPadding, 0, binding.root)
        snackBar.view.setOnClickListener { newMatchSnackBar?.dismiss() }

//        val swiperProfilePhoto = EndPoint.ofPhoto(
//            preferenceProvider.getAccountId(),
//            preferenceProvider.getProfilePhotoKey()
//        )
//        val swipedProfilePhoto = EndPoint.ofPhoto(matchProfileTuple.swipedId, matchProfileTuple.profilePhotoKey)

        val swiperProfilePhoto = R.drawable.person2
        val swipedProfilePhoto = R.drawable.person1

        Glide.with(requireContext())
            .load(swiperProfilePhoto)
            .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
            .into(binding.ivNewMatchSnackBarSwiper)

        Glide.with(requireContext())
            .load(swipedProfilePhoto)
            .apply(GlideHelper.profilePhotoGlideOptions().circleCrop())
            .into(binding.ivNewMatchSnackBarSwiped)

        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (transientBottomBar === newMatchSnackBar) newMatchSnackBar = null
            }
        })

        newMatchSnackBar?.dismiss()
        newMatchSnackBar = snackBar
        snackBar.show()
    }

    private fun setupMatchRecyclerView() {
        matchPagingDataAdapter = MatchPagingDataAdapter(this@MatchFragment)
        binding.rvMatch.adapter = matchPagingDataAdapter
        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatch.itemAnimator = null
        matchPagingRefreshAdapter = PagingRefreshAdapter(binding.rvMatch, matchPagingDataAdapter)
    }

    private fun setupToolBars() {
        binding.tbMatch.inflateMenu(R.menu.match_tool_bar)
        binding.tbMatch.setOnMenuItemClickListener {
            //TODO: remove me
            println("ddd")
            viewModel.testFunction()
            false
//            when (it.itemId) {
//                R.id.miMatchSearch -> showSearchToolBar()
//                else -> false
//            }
        }
        binding.btnMatchSearchClose.setOnClickListener { hideSearchToolBar() }
        binding.etMatchSearch.addTextChangedListener { search(it.toString()) }
        binding.btnMatchRefresh.setOnClickListener {
            if (fetchMatchesStatus == Resource.Status.ERROR) viewModel.fetchMatches()
            if (fetchChatMessagesStatus == Resource.Status.ERROR) viewModel.fetchChatMessages()
        }
    }

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.initMatchPagingData(keyword.trim()).observe(viewLifecycleOwner, {
                matchPagingRefreshAdapter.reset()
                lifecycleScope.launch { matchPagingDataAdapter.submitData(it) }
            })
        }
    }

    private fun hideSearchToolBar() {
        binding.tbMatchSearch.visibility = View.GONE
        binding.tbMatch.visibility = View.VISIBLE
        binding.etMatchSearch.setText("")
    }

    private fun showSearchToolBar(): Boolean {
        binding.tbMatch.visibility = View.GONE
        binding.tbMatchSearch.visibility = View.VISIBLE
        return true
    }

    private fun updateRefreshBtn() {
        if (fetchMatchesStatus == Resource.Status.LOADING || fetchChatMessagesStatus == Resource.Status.LOADING) {
            binding.btnMatchRefresh.visibility = View.GONE
            binding.skvMatchLoading.visibility = View.VISIBLE
        } else if (fetchMatchesStatus == Resource.Status.ERROR || fetchChatMessagesStatus == Resource.Status.ERROR) {
            binding.btnMatchRefresh.visibility = View.VISIBLE
            binding.skvMatchLoading.visibility = View.GONE
        } else {
            binding.btnMatchRefresh.visibility = View.GONE
            binding.skvMatchLoading.visibility = View.INVISIBLE
        }
    }

    private fun observeFetchChatMessagesLiveData() {
        viewModel.fetchChatMessagesLiveData.observe(viewLifecycleOwner) { resource ->
            fetchChatMessagesStatus = resource.status
            updateRefreshBtn()
            if (resource.isError() && validateLogin(resource))
                showFetchChatMessagesError(resource.error, resource.errorMessage)
        }
    }

    private fun showFetchChatMessagesError(error: String?, errorMessage: String?) {
        val errorTitle = getString(R.string.error_title_fetch_chat_messages)
        ErrorDialog.show(error, errorTitle, errorMessage, RequestCode.FETCH_CHAT_MESSAGES, this, childFragmentManager)
    }

    private fun observeFetchMatchesLiveData() {
        viewModel.fetchMatchesLiveData.observe(viewLifecycleOwner, { resource ->
            fetchMatchesStatus = resource.status
            updateRefreshBtn()
            if (resource.isError() && validateLogin(resource))
                showFetchMatchesError(resource.error, resource.errorMessage)
        })
    }

    private fun showFetchMatchesError(error: String?, errorMessage: String?) {
        val errorTitle = getString(R.string.error_title_fetch_matches)
        ErrorDialog.show(error, errorTitle, errorMessage, RequestCode.FETCH_MATCHES, this, childFragmentManager)
    }

    override fun onClick(position: Int) {
        val chatFragment = ChatFragment()
        val arguments = Bundle()

        matchPagingDataAdapter.getMatch(position)?.let {
            arguments.putLong(BundleKey.CHAT_ID, it.chatId)
            arguments.putString(BundleKey.SWIPED_ID, it.swipedId.toString())
            arguments.putString(BundleKey.SWIPED_NAME, it.name)
            arguments.putBoolean(BundleKey.UNMATCHED, it.unmatched)
            arguments.putString(BundleKey.SWIPED_PROFILE_PHOTO_KEY, it.profilePhotoKey.toString())
            chatFragment.arguments = arguments
        }
        moveToFragment(chatFragment, R.id.fcvMain, MainViewPagerFragment.TAG)
    }

    override fun onRetry(requestCode: Int?) {
        requestCode?.let {
            when (it) {
                RequestCode.FETCH_MATCHES -> viewModel.fetchMatches()
                RequestCode.FETCH_CHAT_MESSAGES -> viewModel.fetchChatMessages()
            }
        }

    }

    override fun onFragmentSelected() {
//        viewModel.fetchMatches()
//        viewModel.fetchChatMessages()
    }
}


