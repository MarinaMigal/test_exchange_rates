package com.example.test_exchangerate.room

import com.example.test_exchangerate.data.model.Rates
import com.example.test_exchangerate.util.EntityMapper


class CacheMapper : EntityMapper<CurrencyRatesCacheEntity, Rates> {

    override fun mapFromEntity(entity: CurrencyRatesCacheEntity): Rates {
        val cacheRates: LinkedHashMap<String, Double> = LinkedHashMap()
        cacheRates[entity.currency] = entity.rate
        return Rates(
            cacheRates, entity.timestamp, entity.lastRequestTS
        )
    }


    fun mapFromEntityList(entities: List<CurrencyRatesCacheEntity?>?): Rates {
        val cacheRates: LinkedHashMap<String, Double> = LinkedHashMap()
        val timestamp : Long = entities!![entities.lastIndex]!!.timestamp
        val lastRequestTS : Long = entities!![entities.lastIndex]!!.lastRequestTS
        for (item in entities) {
            cacheRates[item!!.currency] = item.rate
        }
        return Rates(cacheRates, timestamp, lastRequestTS)
    }

}
