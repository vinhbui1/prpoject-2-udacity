package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Utils
import com.udacity.asteroidradar.database.NasaDatabase
import java.util.Calendar
import androidx.lifecycle.map
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: NasaDatabase) {
    val today = Utils.convertDateStringToFormattedString(Calendar.getInstance().time, Constants.API_QUERY_DATE_FORMAT)

    val week = Utils.convertDateStringToFormattedString(
        Utils.addDaysToDate(Calendar.getInstance().time, 7),
        Constants.API_QUERY_DATE_FORMAT)

    val asteroidsSaved: LiveData<List<Asteroid>> =
       database.asteroidDao.getAllAsteroids().map {
           Log.e("TAGAll", it.toString());

           it.asDomainModel()
       }

    val asteroidsWeek: LiveData<List<Asteroid>> =
       database.asteroidDao.getWeeklyAsteroids(today,week).map {
           Log.e("TAGWeek", it.toString());

           it.asDomainModel()
        }
    val asteroidsToday: LiveData<List<Asteroid>> =
    database.asteroidDao.getTodayAsteroids(today).map {
        Log.e("TAGToday", it.toString());

        it.asDomainModel()
        }

    suspend fun refreshAsteroids(){
        withContext(Dispatchers.IO){
            try {
                Log.e("TAG1START", "find");

                val asteroids = AsteroidApi.asteroidRadarService.getAsteroidRadar(Constants.API_KEY).await()
                Log.e("TAG1ASTEROIDSRE", asteroids);

                database.asteroidDao.insertAll(*parseAsteroidsJsonResult(JSONObject(asteroids)).asDatabaseModel())
            }catch (e:Exception){
                Log.e("TAGError" +
                        "", e.toString());

            }
        }
    }
}