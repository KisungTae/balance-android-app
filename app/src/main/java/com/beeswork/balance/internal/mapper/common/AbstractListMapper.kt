package com.beeswork.balance.internal.mapper.common

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.match.MatchResponse

abstract class AbstractListMapper<RI, EO, EI, DO>(
    private val mapper: Mapper<RI, EO, EI, DO>
): ListMapper<RI, EO, EI, DO> {

    override fun fromResponseToEntity(input: List<RI>?): List<EO> {
        return input?.map { mapper.fromResponseToEntity(it) }.orEmpty()
    }

    override fun fromEntityToDomain(input: List<EI>?): List<DO> {
        return input?.map { mapper.fromEntityToDomain(it) }.orEmpty()
    }
}