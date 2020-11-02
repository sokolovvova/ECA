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
    private val eveAuthToken = MutableLiveData<String>("")
    private val prefProv = PreferenceProvider(application)
    val authCharacterLd = MutableLiveData(AuthCharacterModel())
    var savedDatabase = SavedContractsDatabase.getInstance(application)
    var database = ContractsDatabase.getInstance(application)
    var contractListLD = database.contractsDao().getAllContracts()
    private val contractsRepository = ContractsRepository(application)
    private val eveAuthRepository = EveAuthRepository(application)

    val currentContractItems = contractsRepository.getCi()
    var savedContractListLD = savedDatabase.contractsDao().getAllContracts()

    fun properlyChangeFragment(fragmentId: Int) {
        when (fragmentId) {
            1 -> {
                changeDataSource(1)
                vmFragmentChanger(1)
                updateContractList()
            }
            2 -> {
                changeDataSource(2)
                vmFragmentChanger(1)
                updateSavedDatabase()
            }
            3 -> {
                changeDataSource(3)
                vmFragmentChanger(1)
                updateContractList()
            }
            4 -> {
                changeDataSource(4)
                vmFragmentChanger(1)
                updateContractList()
            }
        }
    }

    fun updateAuthCharacter() {
        authCharacterLd.postValue(prefProv.getAuthCharacter())
    }


    fun setEveAuthToken(str: String) {
        eveAuthToken.postValue(str)
        eveAuthRepository.auth(str)
    }

    private fun changeDataSource(sourceNumber: Int) {
        when (sourceNumber) {
            1 -> {
                contractListLD = database.contractsDao().getAllContracts()
                datasource.value = 1
            }
            2 -> {
                contractListLD = savedDatabase.contractsDao().getAllContracts()
                datasource.value = 2
            }
            3 -> {
                contractListLD = database.contractsDao().getAllContracts()
                datasource.value = 3
            }
            4 -> {
                contractListLD = database.contractsDao().getAllContracts()
                datasource.value = 4
            }
        }
    }

    fun changeTitle(str: String) {
        title.postValue(str)
    }

    fun setNewSortOrder(n: Int) {
        sortOrder.postValue(n)
    }

    fun setNewRegion(n: Long) {
        selectedRegion.postValue(n)
    }

    fun vmFragmentChanger(num: Int) {
        currentFragment.postValue(num)
    }

    fun updateContractList() {
        if (System.currentTimeMillis() > prefProv.getExpTime()) {
            eveAuthRepository.updateToken()
        }
        when (datasource.value) {
            1 -> contractsRepository.getContractList(
                selectedRegion.value,
                status = 1,
                token = prefProv.getAuthCharacter().access_token!!
            )
            2 -> updateSavedDatabase()
            3 -> contractsRepository.getContractList(
                characterId = prefProv.getAuthCharacter().CharacterID,
                token = prefProv.getAuthCharacter().access_token!!,
                status = 3
            )
            4 -> contractsRepository.getContractList(
                characterId = prefProv.getAuthCharacter().CharacterID,
                token = prefProv.getAuthCharacter().access_token!!,
                status = 4
            )
        }
    }

    fun destroyThread() {
        contractsRepository.stopMultiThreadWork()
    }

    fun setNewCurrentContractId(i: Int) {
        selectedId.postValue(i)
    }

    fun getContractData() {
        if (System.currentTimeMillis() > prefProv.getExpTime()) {
            eveAuthRepository.updateToken()
        }
        contractsRepository.getContractItems(
            selectedId.value,
            characterId = prefProv.getAuthCharacter().CharacterID,
            token = prefProv.getAuthCharacter().access_token!!
        )
    }

    fun clearCurrentContractItems() {
        currentContractItems.postValue(emptyList())
    }

    fun saveContract(contract: ContractModel) {
        savedDatabase.contractsDao().saveContract(contract)
    }

    fun removeSavedContract(id: Int) {
        savedDatabase.contractsDao().deleteContractById(id)
    }

    fun updateSavedDatabase() {
        savedDatabase.contractsDao().saveContract(
            ContractModel(
                contractId = 99999,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        )
        savedDatabase.contractsDao().deleteContractById(99999)
    }
}