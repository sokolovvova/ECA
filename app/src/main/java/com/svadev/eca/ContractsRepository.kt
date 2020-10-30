package com.svadev.eca

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.svadev.eca.db.ContractsDatabase
import com.svadev.eca.db.EveSdaDatabase
import com.svadev.eca.models.ContractItemModel
import com.svadev.eca.models.ContractResponseModel
import com.svadev.eca.models.StaStationsModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ObsoleteCoroutinesApi
@Suppress("BlockingMethodInNonBlockingContext")
class ContractsRepository(context: Context) {
    private val eveApiUrl = "https://esi.evetech.net/latest/"
    private val appContext = context
    private val scope = CoroutineScope(newFixedThreadPoolContext(4, "Background_Threads"))
    private var contractItems = MutableLiveData<List<ContractItemModel>>()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .baseUrl(eveApiUrl)
        .build()
    private val eveEsiApi = retrofit.create(EveEsiApi::class.java)

    fun getCi(): MutableLiveData<List<ContractItemModel>> {
        return contractItems
    }

    fun getContractItems(contractId: Int?, characterId: Long?, token: String = "99") {
        var link: String = ""
        if (contractId != 99) {
            link = "contracts/public/items/$contractId"
        }
        eveEsiApi.getItemListByContractId(link, token)
            .enqueue(object : Callback<List<ContractItemModel>> {
                override fun onResponse(
                    call: Call<List<ContractItemModel>>,
                    response: Response<List<ContractItemModel>>
                ) {
                    scope.launch {
                        when (response.code()) {
                            200 -> {
                                contractItems.postValue(response.body())
                            }
                            400 -> {
                                contractItems.postValue(listOf(ContractItemModel(type_id = 1090001)))
                            }
                            403 -> {
                                val link2 = "characters/$characterId/contracts/$contractId/items"
                                val secondTry =
                                    eveEsiApi.getItemListByContractId(link2, token).execute()
                                if (secondTry.code() == 200) contractItems.postValue(secondTry.body())
                                else {
                                    val corpId =
                                        eveEsiApi.getCharacterContractId(characterId).execute()
                                            .body()?.corporation_id
                                    val link3 = "corporations/$corpId/contracts/$contractId/items"
                                    val thirdTry =
                                        eveEsiApi.getItemListByContractId(link3, token).execute()
                                    if (thirdTry.code() == 200) contractItems.postValue(thirdTry.body())
                                    else contractItems.postValue(listOf(ContractItemModel(type_id = 1090003)))
                                }
                            }
                            else -> {
                                contractItems.postValue(listOf(ContractItemModel(type_id = 1090000)))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<ContractItemModel>>, t: Throwable) {
                    scope.launch {
                        contractItems.postValue(listOf(ContractItemModel(type_id = 1090000)))
                    }
                }
            })

    }


    fun getContractList(
        regionId: Long? = 99,
        status: Int = 0,
        characterId: Long = 99,
        token: String = "99"
    ) {
        scope.launch {
            var link: String = ""
            when (status) {
                1 -> link = "contracts/public/$regionId"
                3 -> {
                    link = "characters/$characterId/contracts"
                }
                4 -> {
                    val corpId = eveEsiApi.getCharacterContractId(characterId).execute()
                        .body()?.corporation_id
                    link = "corporations/$corpId/contracts"
                }
            }
            val array = arrayListOf<ContractResponseModel>()
            try {
                val response = eveEsiApi.getFirstPageOfContracts(link, token).execute()
                if (response.code() == 200) {
                    val pages = response.headers().get("x-pages")
                    array.addAll(response.body() as ArrayList<ContractResponseModel>)
                    pages?.let {
                        if (it.toInt() > 1) {
                            for (i in 2..it.toInt()) {
                                val result = eveEsiApi.getContractsByPage(link, i, token).execute()
                                array.addAll(result.body() as ArrayList<ContractResponseModel>)
                            }
                        }
                    }
                    if (array.isEmpty()) array.add(
                        ContractResponseModel(
                            title = "Contracts not found!",
                            contract_id = 0,
                            type = "item_exchange"
                        )
                    )
                } else {
                    array.add(
                        ContractResponseModel(
                            title = "[Error ${response.code()} ] (403-> Try to login again)",
                            contract_id = 0,
                            type = "item_exchange"
                        )
                    )
                }
            } catch (e: Exception) {
                array.clear()
                array.add(
                    ContractResponseModel(
                        title = "An error occurred",
                        contract_id = 0,
                        type = "item_exchange"
                    )
                )
            } finally {
                val dbDao = EveSdaDatabase.getInstance(appContext).eveSdaDao()
                val filledArray = dbDao.getAllIds() as ArrayList
                for (n in array) {
                    if (n.start_location_id != null && n.start_location_id > 10000000000 && n.start_location_id !in filledArray) {
                        n.start_location_id?.let {
                            val sta =
                                eveEsiApi.getStructureNameById(n.start_location_id, token).execute()
                            filledArray.add(n.start_location_id)
                            if (sta.code() == 200) {
                                dbDao.putStation(
                                    StaStationsModel(n.start_location_id, 0.0, sta.body()?.name)
                                )
                            }
                        }
                    }
                }
                ContractsDatabase.getInstance(appContext).contractsDao().deleteAllContracts()
                ContractsDatabase.getInstance(appContext).contractsDao()
                    .insertAllContracts(convertCRMLtoCML(array.toList()))
            }
        }
    }

    fun stopMultiThreadWork() {
        scope.cancel()
    }
}