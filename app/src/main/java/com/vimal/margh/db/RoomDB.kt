package com.vimal.margh.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vimal.margh.models.ModelWallpaper

@Database(entities = [ModelWallpaper::class], version = 1)
abstract class RoomDB : RoomDatabase() {

    abstract fun getFavoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var instance: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "Database.db"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}