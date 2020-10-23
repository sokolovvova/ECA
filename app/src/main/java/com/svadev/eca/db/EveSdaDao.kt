package com.svadev.eca.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.svadev.eca.models.StaStationsModel

@Dao
interface EveSdaDao {

    @Query("SELECT stationName FROM staStations WHERE stationID = :id")
    fun getStationNameById(id: Long): String
    @Query("SELECT typeName FROM invTypes WHERE typeID = :id")
    fun getItemNameByTypeId(id: Int): String

    @Query("SELECT volume FROM invTypes WHERE typeID = :id")
    fun getItemVolumeByTypeId(id: Int): Double

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun putStation(sta:StaStationsModel)

    @Query("SELECT stationID FROM staStations")
    fun getAllIds():List<Long>

}