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
    fun getFirstPageOfContracts(@Path("regionId") link: String?,@Query("token") token: String="") : Call<List<ContractResponseModel>>

    @GET("{regionId}/?datasource=tranquility")
    fun getContractsByPage(@Path("regionId") link: String?, @Query("page") page: Int,@Query("token") token: String="") : Call<List<ContractResponseModel>>

    @GET("{contractId}/?datasource=tranquility&page=1")
    fun getItemListByContractId(@Path("contractId") link: String?,@Query("token") token: String="") : Call<List<ContractItemModel>>

}