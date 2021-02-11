package com.beeswork.balance.internal.mapper.common

interface Mapper<RI, EO, EI, DO> {
    fun fromDTOToEntity(input: RI): EO
    fun fromEntityToDomain(input: EI): DO
}