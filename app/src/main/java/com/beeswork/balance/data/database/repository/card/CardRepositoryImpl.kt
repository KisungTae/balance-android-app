package com.beeswork.balance.data.database.repository.card

import com.beeswork.balance.data.database.common.CallBackFlowListener
import com.beeswork.balance.data.database.dao.CardFilterDAO
import com.beeswork.balance.data.database.dao.CardPageDAO
import com.beeswork.balance.data.database.entity.card.Card
import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.data.database.repository.BaseRepository
import com.beeswork.balance.data.network.rds.card.CardRDS
import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.CardFilterNotFoundException
import com.beeswork.balance.internal.mapper.card.CardMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

@ExperimentalCoroutinesApi
class CardRepositoryImpl(
    private val cardFilterDAO: CardFilterDAO,
    private val cardPageDAO: CardPageDAO,
    loginRDS: LoginRDS,
    private val cardRDS: CardRDS,
    private val cardMapper: CardMapper,
    preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : BaseRepository(loginRDS, preferenceProvider), CardRepository {

    override suspend fun like(swipedId: UUID): Resource<FetchQuestionsDTO> {
        return withContext(ioDispatcher) {
            return@withContext cardRDS.like(swipedId)
        }
    }

    override suspend fun deleteCardFilter() {
        withContext(ioDispatcher) {
            cardFilterDAO.deleteAllBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun getCardFilter(): CardFilter? {
        return withContext(ioDispatcher) {
            return@withContext cardFilterDAO.getBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveCardFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val cardFilter = cardFilterDAO.getBy(accountId)
            if (cardFilter == null) {
                cardFilterDAO.insert(CardFilter(accountId, gender, minAge, maxAge, distance))
            } else if (!cardFilter.isEqualTo(gender, minAge, maxAge, distance)) {
                cardFilter.gender = gender
                cardFilter.minAge = minAge
                cardFilter.maxAge = maxAge
                cardFilter.distance = distance
                cardFilterDAO.insert(cardFilter)
            }
            return@withContext Resource.success(EmptyResponse())
        }
    }

    override suspend fun fetchCards(resetPage: Boolean): Resource<List<Card>> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            if (resetPage) {
                cardPageDAO.updateCurrentIndex(accountId, 0)
            }

            val cardFilter = cardFilterDAO.getBy(accountId) ?: return@withContext Resource.error(CardFilterNotFoundException())
            val currentCardPageIndex = cardPageDAO.getCurrentIndexBy(accountId) ?: 0

            val response = getResponse {
                cardRDS.fetchCards(cardFilter.minAge, cardFilter.maxAge, cardFilter.gender, cardFilter.distance, currentCardPageIndex)
            }

            if (response.isSuccess() && !response.data.isNullOrEmpty()) {
                cardPageDAO.updateCurrentIndex(cardFilter.accountId, currentCardPageIndex + response.data.size)
            }

            return@withContext response.map { cardDTOs ->
                val cards = cardDTOs?.map { cardDTO ->
                    cardMapper.toCard(cardDTO)
                }?.toMutableList()
                cards?.shuffle()
                cards
            }
        }
    }

    override fun getCardFilterInvalidationFlow(): Flow<Boolean> {
        return cardFilterDAO.getCardFilterInvalidationFlow(preferenceProvider.getAccountId())
    }


}