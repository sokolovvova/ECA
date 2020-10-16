package com.svadev.eca.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_contracts")
class SavedContractModel (
    @ColumnInfo(name = "contract_id")
    var contractId : Int?,
    var buyout : Double?,
    @ColumnInfo(name = "date_expired")
    var dateExpired : String?,
    @ColumnInfo(name = "date_issued")
    var dateIssued: String?,
    @ColumnInfo(name = "days_to_complete")
    var daysToComplete: Int?,
    var price: Double?,
    @ColumnInfo(name = "start_location_id")
    var startLocationId: Long?,
    var title: String?,
    var type: String?,
    var volume: Double?
){
    @PrimaryKey(autoGenerate = true)
    var id: Long =0
}