package com.vimal.margh.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Repository(context: Context?) {

    private val favoriteDao: FavoriteDao
    private val favoriteLiveData: LiveData<List<ModelWallpaper>>

    init {
        val database = RoomDB.getDatabase(context!!)

        favoriteDao = database.getFavoriteDao()
        favoriteLiveData = favoriteDao.getAllFavorite()
    }


    fun allFavorite(): LiveData<List<ModelWallpaper>> {
        return favoriteLiveData
    }

    fun deleteFavorite(model: ModelWallpaper?) {
        try {
            object : Thread(Runnable { favoriteDao.deleteFavorite(model!!) }) {
            }.start()
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }


    fun insertFavorite(model: ModelWallpaper?) {
        try {
            object : Thread(Runnable { favoriteDao.insertFavorite(model!!) }) {
            }.start()
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }


    fun deleteAllFavorite() {
        try {
            object : Thread(Runnable { favoriteDao.deleteAllFavorite() }) {
            }.start()
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }

    fun isFavorite(id: Int): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                favoriteDao.isFavorite(id)
            }
        }
    }

    companion object {
        private var repository: Repository? = null
        fun getInstance(context: Context?): Repository? {
            if (repository == null) {
                repository = Repository(context)
            }
            return repository
        }
    }
}