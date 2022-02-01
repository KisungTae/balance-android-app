package com.beeswork.balance.ui.click

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.internal.mapper.click.ClickMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map


class ClickViewModel(
    private val clickRepository: ClickRepository,
    private val clickMapper: ClickMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val clickPageInvalidationLiveData by viewModelLazyDeferred {
        clickRepository.getClickPageInvalidationFlow().asLiveData()
    }

    @ExperimentalPagingApi
    fun initClickPagingData(): LiveData<PagingData<ClickDomain>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = ClickRemoteMediator(clickRepository)
        ) {
            ClickPagingSource(clickRepository)
        }.flow.cachedIn(viewModelScope)
            .map { pagingData ->
                pagingData.map { clickMapper.toClickDomain(it) }
            }
            .map { pagingData ->
                pagingData.insertHeaderItem(TerminalSeparatorType.FULLY_COMPLETE, ClickDomain.header())
            }
            .asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun test() {
        clickRepository.test()
    }

    companion object {
        private const val CLICK_PAGE_SIZE = 30
        private const val CLICK_PAGE_PREFETCH_DISTANCE = CLICK_PAGE_SIZE
        private const val CLICK_MAX_PAGE_SIZE = CLICK_PAGE_PREFETCH_DISTANCE * 3 + CLICK_PAGE_SIZE
        private val pagingConfig = PagingConfig(
            CLICK_PAGE_SIZE,
            CLICK_PAGE_PREFETCH_DISTANCE,
            false,
            CLICK_PAGE_SIZE,
            CLICK_MAX_PAGE_SIZE
        )
    }

}