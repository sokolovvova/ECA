package com.svadev.eca

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.svadev.eca.models.AuthCharacterModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EveAuthRepository(context: Context) {
    private val eveApiUrl = "https://login.eveonline.com"
    private val clientId = context.getString(R.string.clientId)
    private val codeVer = context.getString(R.string.codeVerify)
    private val prefProv = PreferenceProvider(context)
    private val scope = CoroutineScope(newFixedThreadPoolContext(4, "Background_Threads"))
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .baseUrl(eveApiUrl)
        .build()
    private val eveAuthApi = retrofit.create(EveAuthApi::class.java)

    fun updateToken(){
        scope.launch {
            val refToken = prefProv.getRefreshToken()
            val response = eveAuthApi.getAccessToken(grantType = "refresh_token",refreshToken = refToken!!,clientId = clientId).execute()
            if (response.code() == 200) {
                val firstResponse = response.body()
                prefProv.setAccessToken(firstResponse?.access_token!!)
                prefProv.setExpTime(System.currentTimeMillis()+1_050_000)
                prefProv.setRefreshToken(firstResponse.refresh_token!!)
            }
        }
    }

    fun auth(authCode :String) {
        Log.d("Retrofit2","avoke")
        scope.launch {
            val response = eveAuthApi.getAccessToken(grantType = "authorization_code",code = authCode,clientId = clientId,codeVerifier = codeVer).execute()
            if (response.code() == 200) {
                val firstResponse = response.body()
                val accessToken = "Bearer ${firstResponse?.access_token}"
                val response2 = eveAuthApi.getAuthCharInfo(accessToken!!).execute()
                if(response2.code()==200){
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