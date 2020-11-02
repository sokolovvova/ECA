package com.svadev.eca

import android.content.Context
import com.svadev.eca.db.EveSdaDatabase
import com.svadev.eca.models.ContractModel
import com.svadev.eca.models.ContractResponseModel

fun convertCRMLtoCML(list :List<ContractResponseModel>?): List<ContractModel>{
    val newList = mutableListOf<ContractModel>()
    if(list==null) return newList
    else{
        for(i in 0..list.lastIndex){
            if(list[i].type=="item_exchange" && list[i].status!="deleted"){
                newList.add(
                    ContractModel(
                        contractId = list[i].contract_id,
                        buyout = list[i].buyout,
                        dateExpired = list[i].date_expired,
                        dateIssued = list[i].date_issued,
                        daysToComplete = list[i].days_to_complete,
                        isForCorporation = list[i].for_corporation,
                        issuerCorpId = list[i].issuer_corporation_id,
                        issuerId = list[i].issuer_id,
                        price = list[i].price,
                        startLocationId = list[i].start_location_id,
                        title = list[i].title,
                        type = list[i].type,
                        volume = list[i].volume,
                        status = list[i].status,
                        availability = list[i].availability
                    ))
            }
        }
        return newList
    }
}
fun stationIdToName(id : Long?,context:Context) : String{
    if(id==null) return "null"
    val str = EveSdaDatabase.getInstance(context).eveSdaDao().getStationNameById(id!!)
    if(str==null) return "Unknown Player-Owned Station"
    else return str
}
fun priceToString(price: Double?) : String{
    if(price==null) return "null"
    val pr = price!!/1000_000
    return String.format("%,.2f",pr)+"M ISK"
}
fun volumeToString(volume: Double?) : String{
    if(volume==null) return "null"
    return String.format("%,.2f",volume) +" m3"
}