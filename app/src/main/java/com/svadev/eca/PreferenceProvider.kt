package com.svadev.eca

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.svadev.eca.models.AuthCharacterModel

class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext
    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveAuthCharacter(character: AuthCharacterModel) {
        preference.edit()
            .putLong("CharacterID", character.CharacterID)
            .putString("CharacterName", character.CharacterName)
            .putString("ExpiresOn", character.ExpiresOn)
            .putString("Scopes", character.Scopes)
            .putString("TokenType", character.TokenType)
            .putString("CharacterOwnerHash", character.CharacterOwnerHash)
            .putString("IntellectualProperty", character.IntellectualProperty)
            .putString("access_token", character.access_token)
            .putString("token_type", character.token_type)
            .putInt("expires_in", character.expires_in)
            .putString("refresh_token", character.refresh_token)
            .apply()
    }

    fun getAuthCharacter(): AuthCharacterModel {
        return AuthCharacterModel(
            preference.getLong("CharacterID",0),
            preference.getString("CharacterName",""),
            preference.getString("ExpiresOn",""),
            preference.getString("Scopes",""),
            preference.getString("TokenType",""),
            preference.getString("CharacterOwnerHash",""),
            preference.getString("IntellectualProperty",""),
            preference.getString("access_token",""),
            preference.getString("token_type",""),
            preference.getInt("expires_in",0),
            preference.getString("refresh_token",""),
        )
    }
}