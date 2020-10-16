package com.svadev.eca.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eveIcons")
class EveIconsModel (
    @PrimaryKey
    var iconID: Int,
    var iconFile: String?
)