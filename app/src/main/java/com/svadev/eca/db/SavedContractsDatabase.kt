package com.svadev.eca.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.svadev.eca.models.ContractModel

@Database(entities = [(ContractModel::class)],version = 2)
abstract class SavedContractsDatabase : RoomDatabase() {
    abstract fun contractsDao() : ContractsDao

    companion object{
        private var instance: SavedContractsDatabase? = null

        fun getInstance(context: Context): SavedContractsDatabase{
            if (instance==null){
                instance = Room.databaseBuilder(context,SavedContractsDatabase::class.java,"saved_contracts").allowMainThreadQueries().build()
            }
            return instance as SavedContractsDatabase
        }
    }
}