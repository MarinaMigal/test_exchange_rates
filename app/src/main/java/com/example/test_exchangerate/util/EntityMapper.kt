package com.example.test_exchangerate.util

interface EntityMapper<Entity, DomainModel> {

    fun mapFromEntityList(entities: List<Entity?>?): DomainModel

}