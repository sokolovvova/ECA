package com.svadev.eca.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface EveSdaDao {

    @Query("SELECT stationName FROM staStations WHERE stationID = :id")
    fun getStationNameById(id: Long): String
    @Query("SELECT typeName FROM invTypes WHERE typeID = :id")
    fun getItemNameByTypeId(id: Int): String

    @Query("SELECT volume FROM invTypes WHERE typeID = :id")
    fun getItemVolumeByTypeId(id: Int): Double


}