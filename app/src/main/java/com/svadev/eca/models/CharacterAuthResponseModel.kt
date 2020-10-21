package com.svadev.eca.models

class CharacterAuthResponseModel (
    val CharacterID :Long,
    val CharacterName: String,
    val ExpiresOn: String,
    val Scopes: String,
    val TokenType: String,
    val CharacterOwnerHash: String,
    val IntellectualProperty: String
)