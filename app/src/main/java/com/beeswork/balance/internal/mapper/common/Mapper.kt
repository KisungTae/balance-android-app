package com.beeswork.balance.internal.mapper.common

interface Mapper<DTO, ENTITY, DOMAIN> {
    fun toEntity(dto: DTO): ENTITY?
    fun toDomain(entity: ENTITY): DOMAIN
}