package com.svadev.eca

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.svadev.eca.models.AuthCharacterModel
import com.svadev.eca.models.AuthRequestBodyModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EveAuthRepository(context: Context) {
    private val eveApiUrl = "https://login.eveonline.com/"
    private val authBase64String =context.getString(R.string.eveSSOBase64)
    private val appContext = context
    private val prefProv = PreferenceProvider(context)
    private val scope = CoroutineScope(newFixedThreadPoolContext(4, "Background_Threads"))
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .baseUrl(eveApiUrl)
        .build()

    private val eveAuthApi = retrofit.create(EveAuthApi::class.java)

    fun updateToken(){
        scope.launch {
            val aT = "Basic $authBase64String"
            val refToken = prefProv.getRefreshToken()
            val request = AuthRequestBodyModel("refresh_token",refToken!!)
            val response = eveAuthApi.getAccessToken(aT, request).execute()
            if (response.code() == 200) {
                val firstResponse = response.body()
                prefProv.setAccessToken(firstResponse?.access_token!!)
                prefProv.setExpTime(System.currentTimeMillis()+1_050_000)
            }
        }
    }


    fun auth(authCode :String) {
        Log.d("Retrofit2","avoke")
        scope.launch {
            val aT = "Basic $authBase64String"
            val request = AuthRequestBodyModel("authorization_code",authCode)
            val response = eveAuthApi.getAccessToken(aT, request).execute()
            if (response.code() == 200) {
                val firstResponse = response.body()
                val accessToken = "Bearer ${firstResponse?.access_token}"
                val response2 = eveAuthApi.getAuthCharInfo(accessToken!!).execute()
                if(response2.code()==200){
                    Log.d("Retrofit2", response2.body()?.CharacterID.toString())
                    val secondResponse = response2.body()
                    prefProv.saveAuthCharacter(AuthCharacterModel(
                        secondResponse!!.CharacterID,
                        secondResponse.CharacterName,
                        secondResponse.ExpiresOn,
                        secondResponse.Scopes,
                        secondResponse.TokenType,
                        secondResponse.CharacterOwnerHash,
                        secondResponse.IntellectualProperty,
                        firstResponse!!.access_token,
                        firstResponse.token_type,
                        System.currentTimeMillis()+1_050_000,
                        firstResponse.refresh_token
                    ))
                }
            }
        }
    }
}