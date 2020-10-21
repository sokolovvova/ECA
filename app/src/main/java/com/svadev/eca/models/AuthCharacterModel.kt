package com.svadev.eca.models

class AuthCharacterModel (
    val CharacterID :Long=0,
    val CharacterName: String?="",
    val ExpiresOn: String?="",
    val Scopes: String?="",
    val TokenType: String?="",
    val CharacterOwnerHash: String?="",
    val IntellectualProperty: String?="",
    val access_token: String?="",
    val token_type: String?="",
    val expires_in: Int=0,
    val refresh_token: String?=""
)