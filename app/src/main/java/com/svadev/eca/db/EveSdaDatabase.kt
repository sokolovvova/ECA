package com.svadev.eca.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.svadev.eca.models.*

@Database(entities = [(InvTypesModel::class),(MapRegionsModel::class),(StaStationsModel::class)],version = 2)
abstract class EveSdaDatabase: RoomDatabase() {
    abstract fun eveSdaDao() : EveSdaDao

    companion object{
        private var instance: EveSdaDatabase? = null

        fun getInstance(context: Context): EveSdaDatabase{
            if (instance==null){
                instance = Room.databaseBuilder(context,EveSdaDatabase::class.java,"eve_small.db").createFromAsset("databases/eve_small.db").allowMainThreadQueries().build()
            }
            return instance as EveSdaDatabase
        }
    }
}