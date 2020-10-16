package com.svadev.eca.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.svadev.eca.models.ContractModel
import com.svadev.eca.models.SavedContractModel

@Dao
interface ContractsDao {
    @Query("SELECT * FROM contracts")
    fun getAllContracts(): LiveData<List<ContractModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContracts(contractsList: List<ContractModel>)

    @Query("DELETE FROM contracts")
    fun deleteAllContracts()

    @Query("SELECT * FROM contracts WHERE contract_id = :id")
    fun getContractById(id: Int) :ContractModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveContract(contractModel: ContractModel)

    @Query("DELETE FROM contracts WHERE contract_id = :id")
    fun deleteContractById(id: Int)
}