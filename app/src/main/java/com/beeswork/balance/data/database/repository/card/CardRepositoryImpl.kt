package com.beeswork.balance.data.database.repository.card

import com.beeswork.balance.data.database.dao.CardFilterDAO
import com.beeswork.balance.data.database.dao.CardPageDAO
import com.beeswork.balance.data.database.entity.card.Card
import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.data.database.entity.card.CardPage
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
import kotlinx.coroutines.flow.Flow
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
                cardPageDAO.updateBy(accountId, 0, 0)
            }
            return@withContext Resource.success(EmptyResponse())
        }
    }

    override suspend fun fetchCards(resetPage: Boolean, isFirstFetch: Boolean): Resource<List<Card>> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val cardFilter = cardFilterDAO.getBy(accountId) ?: return@withContext Resource.error(CardFilterNotFoundException())
            val cardPage = cardPageDAO.getBy(accountId) ?: CardPage(accountId, 0, 0)

            if (resetPage) {
                cardPage.readByIndex = 0
                cardPage.currentIndex = 0
            }

            if (isFirstFetch) {
                cardPage.currentIndex = cardPage.readByIndex
            }

            val response = getResponse {
                cardRDS.fetchCards(cardFilter.minAge, cardFilter.maxAge, cardFilter.gender, cardFilter.distance, cardPage.currentIndex)
            }

            if (response.isSuccess() && !response.data.isNullOrEmpty()) {
                cardPage.currentIndex += response.data.size
                cardPageDAO.insert(cardPage)
            }

            return@withContext response.map { cardDTOs ->
                cardDTOs?.map { cardDTO ->
                    cardMapper.toCard(cardDTO)
                }
            }
        }
    }

    override suspend fun incrementReadByIndex() {
        withContext(ioDispatcher) {
            cardPageDAO.incrementReadByIndexBy(preferenceProvider.getAccountId(), 1)
        }
    }

    override fun getCardFilterInvalidationFlow(): Flow<Boolean> {
        return cardFilterDAO.getCardFilterInvalidationFlow(preferenceProvider.getAccountId())
    }


}