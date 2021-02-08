package com.beeswork.balance.data.database.repository.match

interface MatchRepository {
    suspend fun fetchMatches()
}