package com.svadev.eca.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invTypes")
class InvTypesModel (
    @PrimaryKey
    var typeID: Int,
    var groupID: Int?,
    var typeName: String?,
    var volume: Double?,
    var iconID: Int?
)