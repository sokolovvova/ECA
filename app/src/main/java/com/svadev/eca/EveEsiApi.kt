package com.svadev.eca

import com.svadev.eca.models.ContractItemModel
import com.svadev.eca.models.ContractResponseModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EveEsiApi {

    @GET("{regionId}/?datasource=tranquility&page=1")
    fun getFirstPageOfContracts(@Path("regionId") regionId: Long?) : Call<List<ContractResponseModel>>

    @GET("{regionId}/?datasource=tranquility")
    fun getContractsByPage(@Path("regionId") regionId: Long?, @Query("page") page: Int) : Call<List<ContractResponseModel>>

    @GET("items/{contractId}/?datasource=tranquility&page=1")
    fun getItemListByContractId(@Path("contractId") contractId: Int?) : Call<List<ContractItemModel>>

}