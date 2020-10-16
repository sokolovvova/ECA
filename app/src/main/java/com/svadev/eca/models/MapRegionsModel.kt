package com.svadev.eca.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mapRegions")
class MapRegionsModel (
    @PrimaryKey
    var regionID: Long,
    var regionName: String?
)