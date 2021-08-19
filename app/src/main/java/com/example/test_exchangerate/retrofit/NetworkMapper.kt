package com.example.test_exchangerate.retrofit

import com.example.test_exchangerate.data.model.Rates
import com.example.test_exchangerate.util.EntityMapper
import javax.inject.Inject



class NetworkMapper
@Inject
constructor():
    EntityMapper<CurrencyRatesNetworkEntity, Rates> {

    override fun mapFromEntity(entity: CurrencyRatesNetworkEntity): Rates {
        return Rates(rates = entity.rates, timestamp = entity.timestamp, 0)
    }

    override fun mapToEntity(domainModel: Rates): CurrencyRatesNetworkEntity {
        return CurrencyRatesNetworkEntity(
            "","","",domainModel.rates,domainModel.timestamp
        )
    }



}


