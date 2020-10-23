package com.svadev.eca

import com.svadev.eca.models.AccessTokenResponseModel
import com.svadev.eca.models.CharacterAuthResponseModel
import retrofit2.Call
import retrofit2.http.*

interface EveAuthApi {


    @FormUrlEncoded
    @Headers ("Content-Type: application/x-www-form-urlencoded","Host: login.eveonline.com")
    @POST("/v2/oauth/token")
    fun getAccessToken(
        @Field("grant_type") grantType : String="",
        @Field("code") code: String="",
        @Field("client_id") clientId: String="",
        @Field("code_verifier") codeVerifier: String="",
        @Field("refresh_token") refreshToken:String=""

    ) : Call<AccessTokenResponseModel>

    @GET("/oauth/verify")
    fun getAuthCharInfo(@Header("Authorization") accessToken: String) :Call<CharacterAuthResponseModel>
}