package com.udacity.asteroidradar.main

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.Utils
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.repository.PictureOfDayRepository
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AsteroidApiFilter(val value: String) { SHOW_SAVED("saved"), SHOW_TODAY("today"), SHOW_WEEK("week") }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)
    private val picturesOfDayRepository = PictureOfDayRepository(database)
    private val filter = MutableLiveData(AsteroidApiFilter.SHOW_TODAY)
    private var _asteroidsList = MutableLiveData<List<Asteroid>>()

    private val _asteroids = MediatorLiveData<List<Asteroid>>()
    val asteroidsdata: LiveData<List<Asteroid>> = _asteroids
//    val asteroids =
//        when (filter.value) {
//            AsteroidApiFilter.SHOW_TODAY -> asteroidsRepository.asteroidsToday
//            AsteroidApiFilter.SHOW_WEEK -> asteroidsRepository.asteroidsWeek
//            else -> asteroidsRepository.asteroidsSaved
//
//}

    init {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
            picturesOfDayRepository.refreshPictureOfDay()
        }
        filter.observeForever { newFilter ->
            _asteroids.removeSource(asteroidsRepository.asteroidsToday)
            _asteroids.removeSource(asteroidsRepository.asteroidsWeek)
            _asteroids.removeSource(asteroidsRepository.asteroidsSaved)

            when (newFilter) {
                AsteroidApiFilter.SHOW_TODAY -> _asteroids.addSource(asteroidsRepository.asteroidsToday) {
                    _asteroids.value = it
                    Log.e("today", asteroidsdata.value.toString())

                }
                AsteroidApiFilter.SHOW_WEEK -> _asteroids.addSource(asteroidsRepository.asteroidsWeek) {
                    _asteroids.value = it
                    Log.e("week", asteroidsdata.value.toString())

                }
                else -> _asteroids.addSource(asteroidsRepository.asteroidsSaved) {
                    _asteroids.value = it
                    Log.e("saved", asteroidsdata.value.toString())

                }
            }
        }

    }

     @SuppressLint("SuspiciousIndentation")
     fun setFilter(newFilter: AsteroidApiFilter) {
        filter.value = newFilter


    }
    val picOfDay = picturesOfDayRepository.pictureOfDay


}