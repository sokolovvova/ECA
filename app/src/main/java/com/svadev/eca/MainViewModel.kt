package com.svadev.eca

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.svadev.eca.db.ContractsDatabase
import com.svadev.eca.db.EveSdaDatabase
import com.svadev.eca.db.SavedContractsDatabase
import com.svadev.eca.models.ContractItemModel
import com.svadev.eca.models.ContractModel
import com.svadev.eca.models.SavedContractModel

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var selectedRegion = MutableLiveData<Long>(10000043)
    var currentFragment = MutableLiveData(1)
    var selectedId = MutableLiveData<Int>(0)
    var datasource = 1


    var savedDatabase = SavedContractsDatabase.getInstance(application)
    var database = ContractsDatabase.getInstance(application)
    var contractListLD = database.contractsDao().getAllContracts()
    val contractsRepository = ContractsRepository(application)

    val currentContractItems = contractsRepository.getCi()
    var savedContractListLD = savedDatabase.contractsDao().getAllContracts()
    var currentDatabase =database

    fun changeDataSource(sourceNumber: Int){
        when(sourceNumber){
            1-> {contractListLD = database.contractsDao().getAllContracts()
                datasource=1}
            2-> {contractListLD = savedDatabase.contractsDao().getAllContracts()
                datasource=2}
        }
    }

    fun setNewRegion(n : Long){
        selectedRegion.postValue(n)
    }
    fun vmFragmentChanger(num : Int){
        currentFragment.postValue(num)
    }

    fun updateContractList(){
        contractsRepository.getContractList(selectedRegion.value)
    }
    fun destroyThread(){
        contractsRepository.stopMultithreadWork()
    }
    fun setNewCurrentContractId(i: Int){
        selectedId.postValue(i)
    }

    fun getContractData(){
        contractsRepository.getContractItems(selectedId.value)
    }
    fun clearCurrentContractItems(){
        currentContractItems.postValue(emptyList())
    }

    fun saveContract(contract: ContractModel){
       savedDatabase.contractsDao().saveContract(contract)
    }

    fun removeSavedContract(id: Int){
        savedDatabase.contractsDao().deleteContractById(id)
    }
    fun updateSavedDatabase(){
        savedDatabase.contractsDao().saveContract(ContractModel(contractId = 99999,null,null,null,null,null,null,null,null,null,null,null,null))
        savedDatabase.contractsDao().deleteContractById(99999)
    }
}