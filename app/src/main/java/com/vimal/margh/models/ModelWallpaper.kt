package com.vimal.margh.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "table_wallpaper")
class ModelWallpaper(
    @field:PrimaryKey
    var id: Int,
    var previewURL: String,
    var webformatURL: String,
    var largeImageURL: String
) : Serializable 