package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.DatabasePictureOfDay
import com.udacity.asteroidradar.database.NasaDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class PictureOfDayRepository(private val database: NasaDatabase) {
    val pictureOfDay: LiveData<DatabasePictureOfDay> = database.pictureOfDayDao.getPictureOfDay()



    suspend fun refreshPictureOfDay(): DatabasePictureOfDay? {
        withContext(Dispatchers.IO) {
            try {

                val pictureOfDay = AsteroidApi.asteroidRadarService.getPictureOfDay(Constants.API_KEY).await()
               val data = pictureOfDay.asDatabaseModel()
                database.pictureOfDayDao.insertPictureOfDay(data)
                return@withContext data
            }catch (e: Exception){

            }
        }
        return null

    }
}


