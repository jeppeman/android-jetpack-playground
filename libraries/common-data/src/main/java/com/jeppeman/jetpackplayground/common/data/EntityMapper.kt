package com.jeppeman.jetpackplayground.common.data

interface EntityMapper<TEntity, TDomain> {
    fun toEntity(from: TDomain): TEntity
    fun toDomain(from: TEntity): TDomain

    fun toEntity(from: List<TDomain>): List<TEntity> = from.map(::toEntity)
    fun toDomain(from: List<TEntity>): List<TDomain> = from.map(::toDomain)
}