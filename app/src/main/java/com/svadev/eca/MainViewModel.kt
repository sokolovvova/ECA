package com.svadev.eca

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.svadev.eca.db.ContractsDatabase
import com.svadev.eca.db.SavedContractsDatabase
import com.svadev.eca.models.AuthCharacterModel
import com.svadev.eca.models.ContractModel
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
class MainViewModel(application: Application) : AndroidViewModel(application) {
    var selectedRegion = MutableLiveData<Long>(10000043)
    var currentFragment = MutableLiveData(1)
    var selectedId = MutableLiveData<Int>(0)
    var datasource = MutableLiveData(1)
    var sortOrder = MutableLiveData<Int>(5)
    var title = MutableLiveData("Public Contracts")
    val eveAuthToken = MutableLiveData<String>("")
    private val prefProv = PreferenceProvider(application)
    val authCharacterLd = MutableLiveData(AuthCharacterModel())
    var savedDatabase = SavedContractsDatabase.getInstance(application)
    var database = ContractsDatabase.getInstance(application)
    var contractListLD = database.contractsDao().getAllContracts()
    private val contractsRepository = ContractsRepository(application)
    private val eveAuthRepository = EveAuthRepository(application)

    val currentContractItems = contractsRepository.getCi()
    var savedContractListLD = savedDatabase.contractsDao().getAllContracts()

    fun updateAuthCharacter(){
        authCharacterLd.postValue(prefProv.getAuthCharacter())
    }


    fun setEveAuthToken(str: String){
        eveAuthToken.postValue(str)
        eveAuthRepository.auth(str)
    }

    fun changeDataSource(sourceNumber: Int){
        when(sourceNumber){
            1-> {contractListLD = database.contractsDao().getAllContracts()
                datasource.postValue(1)
            }
            2-> {contractListLD = savedDatabase.contractsDao().getAllContracts()
                datasource.postValue(2)}
        }
    }

    fun changeTitle(str:String){
        title.postValue(str)
    }

    fun setNewSortOrder(n: Int){
        sortOrder.postValue(n)
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
        contractsRepository.stopMultiThreadWork()
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