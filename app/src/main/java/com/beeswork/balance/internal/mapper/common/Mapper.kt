package com.beeswork.balance.internal.mapper.common

interface Mapper<DTO, ENTITY, DOMAIN> {
    fun fromDTOToEntity(dto: DTO): ENTITY
    fun fromEntityToDomain(entity: ENTITY): DOMAIN
}