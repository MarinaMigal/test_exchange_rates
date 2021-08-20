package com.example.test_exchangerate.room

import com.example.test_exchangerate.data.model.Rates
import com.example.test_exchangerate.util.EntityMapper


class CacheMapper : EntityMapper<CurrencyRatesCacheEntity, Rates> {

    //Convert cached data from room to Rates for Recycler Adapter
    override fun mapFromEntityList(entities: List<CurrencyRatesCacheEntity?>?): Rates {
        val cacheRates: LinkedHashMap<String, Double> = LinkedHashMap()
        val timestamp: Long = entities?.get(entities.lastIndex)!!.timestamp
        val lastRequestTS: Long = entities[entities.lastIndex]!!.lastRequestTS
        for (item in entities) {
            cacheRates[item!!.currency] = item.rate
        }
        return Rates(cacheRates, timestamp, lastRequestTS)
    }

}
