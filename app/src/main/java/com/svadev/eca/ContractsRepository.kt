package com.svadev.eca

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.svadev.eca.db.ContractsDatabase
import com.svadev.eca.models.ContractItemModel
import com.svadev.eca.models.ContractResponseModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@ObsoleteCoroutinesApi
@Suppress("BlockingMethodInNonBlockingContext")
class ContractsRepository(context: Context) {
    private val eveApiUrl = "https://esi.evetech.net/latest/contracts/public/"
    private val appContext = context
    private val scope = CoroutineScope(newFixedThreadPoolContext(4, "Background_Threads"))
    private var contractItems = MutableLiveData<List<ContractItemModel>>()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(eveApiUrl)
        .build()
    private val eveEsiApi = retrofit.create(EveEsiApi::class.java)

    fun getCi(): MutableLiveData<List<ContractItemModel>> {
        return contractItems
    }

    fun getContractItems(contractId: Int?) {
        eveEsiApi.getItemListByContractId(contractId)
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
                                contractItems.postValue(listOf(ContractItemModel(type_id = 1090001 )))
                            }
                            403 -> {
                                contractItems.postValue(listOf(ContractItemModel(type_id = 1090003)))
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


    fun getContractList(regionId: Long?) {
        scope.launch {
            val array = arrayListOf<ContractResponseModel>()
            try {
                val response = eveEsiApi.getFirstPageOfContracts(regionId).execute()
                if (response.code() == 200) {
                    val pages = response.headers().get("x-pages")
                    array.addAll(response.body() as ArrayList<ContractResponseModel>)
                    pages?.let {
                        if (it.toInt() > 1) {
                            for (i in 2..it.toInt()) {
                                val result = eveEsiApi.getContractsByPage(regionId, i).execute()
                                array.addAll(result.body() as ArrayList<ContractResponseModel>)
                            }
                        }
                    }
                    if (array.isEmpty()) array.add(
                        ContractResponseModel(
                            title = "Contracts not found!",
                            contract_id = 0,
                            type="item_exchange"
                        )
                    )
                } else {
                    array.add(
                        ContractResponseModel(
                            title = "An error occurred",
                            contract_id = 0,
                            type="item_exchange"
                        )
                    )
                }
            } catch (e: Exception) {
                array.clear()
                array.add(
                    ContractResponseModel(
                        title = "An error occurred",
                        contract_id = 0,
                        type="item_exchange"
                    )
                )
            } finally {
                ContractsDatabase.getInstance(appContext).contractsDao().deleteAllContracts()
                ContractsDatabase.getInstance(appContext).contractsDao()
                    .insertAllContracts(convertCRMLtoCML(array.toList()))
                Log.d("Retrofit", "Final Array Size ${array.size}")
            }
        }
    }

    fun stopMultiThreadWork() {
        scope.cancel()
    }
}