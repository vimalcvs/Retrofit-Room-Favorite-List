package com.vimal.margh.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.vimal.margh.models.ModelWallpaper


@Dao
interface FavoriteDao {

    @Query("Select * from table_wallpaper")
    fun getAllFavorite(): LiveData<List<ModelWallpaper>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavorite(modelWallpaper: ModelWallpaper)

    @Delete
    fun deleteFavorite(modelWallpaper: ModelWallpaper)

    @Query("DELETE FROM table_wallpaper")
    fun deleteAllFavorite()

    @Query("SELECT EXISTS (SELECT 1 FROM table_wallpaper WHERE id = :id)")
    fun isFavorite(id: Int): Boolean

}