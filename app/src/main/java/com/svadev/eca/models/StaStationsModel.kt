package com.svadev.eca.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staStations")
class StaStationsModel (
    @PrimaryKey
    var stationID: Long,
    var security: Double?,
    var stationName: String?
)