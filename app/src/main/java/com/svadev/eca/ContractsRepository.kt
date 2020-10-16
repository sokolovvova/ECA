package com.svadev.eca

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.svadev.eca.db.ContractsDatabase
import com.svadev.eca.models.ContractItemModel
import com.svadev.eca.models.ContractResponseModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

class ContractsRepository(context: Context) {
    val BASE_URL = "https://esi.evetech.net/latest/contracts/public/"
    val appContext = context

    var contractItems = MutableLiveData<List<ContractItemModel>>()

    val executor = Executors.newSingleThreadExecutor()
    val gson = Gson()
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    val eveEsiApi = retrofit.create<EveEsiApi>(EveEsiApi::class.java)

    fun getCi() : MutableLiveData<List<ContractItemModel>>{
        return contractItems
    }

    fun getContractItems(contractId : Int?){
        eveEsiApi.getItemListByContractId(contractId).enqueue(object: Callback<List<ContractItemModel>>{
            override fun onResponse(
                call: Call<List<ContractItemModel>>,
                response: Response<List<ContractItemModel>>
            ) {
                when(response.code()){
                    200->{
                        executor.execute {
                            contractItems.postValue(response.body())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ContractItemModel>>, t: Throwable) {
                Log.d("Retrofit","SomeError")
            }
        })
    }


    fun getContractList(regionId: Long?){

        eveEsiApi.getFirstPageOfContracts(regionId).enqueue(object: Callback<List<ContractResponseModel>>{
            override fun onResponse(
                call: Call<List<ContractResponseModel>>,
                response: Response<List<ContractResponseModel>>
            ) {
                when(response.code()){
                    200->{
                        executor.execute {
                            val array = arrayListOf<ContractResponseModel>()
                            val pages = response.headers().get("x-pages")
                            array.addAll(response.body() as ArrayList<ContractResponseModel>)
                            if (pages != null) {
                                if(pages.toInt()>1){
                                    var pagesList = (1..pages.toInt()).toList()
                                    Observable.fromIterable(pagesList)
                                        .flatMap { it ->
                                            eveEsiApi.getContractsByPage(regionId, it)
                                        }
                                        .subscribeOn(Schedulers.single())
                                        .observeOn(Schedulers.single())
                                        .subscribe({
                                                result->array.addAll(result)
                                        },{
                                                error->Log.d("Retrofit","RxError")
                                        },{
                                            ContractsDatabase.getInstance(appContext).contractsDao().deleteAllContracts()
                                            ContractsDatabase.getInstance(appContext).contractsDao().insertAllContracts(convertCRMLtoCML(array.toList()))
                                        })
                                }
                                else{
                                    if(array.isEmpty()) array.add(ContractResponseModel(title = "Contracts not found!",contract_id = 0))
                                    ContractsDatabase.getInstance(appContext).contractsDao().deleteAllContracts()
                                    ContractsDatabase.getInstance(appContext).contractsDao().insertAllContracts(convertCRMLtoCML(array.toList()))
                                }
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<ContractResponseModel>>, t: Throwable) {
                Log.d("Retrofit","SomeError")
            }
        })

    }
    fun stopMultithreadWork(){
        executor.shutdown()
    }
}