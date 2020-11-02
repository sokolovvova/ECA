package com.svadev.eca.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "contracts")
data class ContractModel (

    @ColumnInfo(name = "contract_id")
    var contractId : Int?,
    var buyout : Double?,
    @ColumnInfo(name = "date_expired")
    var dateExpired : String?,
    @ColumnInfo(name = "date_issued")
    var dateIssued: String?,
    @ColumnInfo(name = "days_to_complete")
    var daysToComplete: Int?,
    @ColumnInfo(name = "is_for_corp")
    var isForCorporation: Boolean?,
    @ColumnInfo(name = "issuer_corp_id")
    var issuerCorpId: Int?,
    @ColumnInfo(name = "issuer_id")
    var issuerId: Int?,
    var price: Double?,
    @ColumnInfo(name = "start_location_id")
    var startLocationId: Long?,
    var title: String?,
    var type: String?,
    var volume: Double?,
    var status: String?,
    var availability: String?
){
    @PrimaryKey(autoGenerate = true)
    var id: Long =0
}