package com.svadev.eca.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.svadev.eca.models.ContractModel
import com.svadev.eca.models.SavedContractModel

@Database(entities = [(ContractModel::class),(SavedContractModel::class)],version = 1)
abstract class ContractsDatabase : RoomDatabase() {
    abstract fun contractsDao() : ContractsDao

    companion object{
        private var instance: ContractsDatabase? = null

        fun getInstance(context: Context): ContractsDatabase{
            if (instance==null){
                instance = Room.databaseBuilder(context,ContractsDatabase::class.java,"contracts").allowMainThreadQueries().build()
            }
            return instance as ContractsDatabase
        }
    }
}