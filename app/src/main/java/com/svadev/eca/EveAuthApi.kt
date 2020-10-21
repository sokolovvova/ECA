package com.svadev.eca

import com.svadev.eca.models.AccessTokenResponseModel
import com.svadev.eca.models.AuthRequestBodyModel
import com.svadev.eca.models.CharacterAuthResponseModel
import retrofit2.Call
import retrofit2.http.*

interface EveAuthApi {

    @Headers ("Content-Type:application/json")
    @POST("/oauth/token")
    fun getAccessToken(@Header("Authorization") authToken: String,@Body req : AuthRequestBodyModel) : Call<AccessTokenResponseModel>

    @GET("/oauth/verify")
    fun getAuthCharInfo(@Header("Authorization") accessToken: String) :Call<CharacterAuthResponseModel>
}