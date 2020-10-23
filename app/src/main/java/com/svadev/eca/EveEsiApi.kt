package com.svadev.eca

import com.svadev.eca.models.CharacterInfoResponseModel
import com.svadev.eca.models.ContractItemModel
import com.svadev.eca.models.ContractResponseModel
import com.svadev.eca.models.StructureResponseModel
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

    @GET("characters/{characterId}/?datasource=tranquility")
    fun getCharacterContractId(@Path("characterId") characterId: Long?) :Call<CharacterInfoResponseModel>

    @GET("universe/structures/{structureId}/?datasource=tranquility")
    fun getStructureNameById(@Path("structureId") structureId: Long?,@Query("token") token: String="") : Call<StructureResponseModel>

}