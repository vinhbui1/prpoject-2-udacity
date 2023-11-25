package com.udacity.asteroidradar.main

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
    private val filter = MutableLiveData(AsteroidApiFilter.SHOW_SAVED)
    private var asteroidsSource: LiveData<List<Asteroid>> = MutableLiveData()
    val asteroids: LiveData<List<Asteroid>> get() = asteroidsSource

    private  fun updateAsteroids() {
        asteroidsSource = when (filter.value) {
            AsteroidApiFilter.SHOW_TODAY -> asteroidsRepository.asteroidsToday
            AsteroidApiFilter.SHOW_WEEK -> asteroidsRepository.asteroidsWeek
            else -> asteroidsRepository.asteroidsSaved
        }
        Log.e("TAG1ASTEROIDSRE",filter.value.toString());
        val today = Utils.convertDateStringToFormattedString(Calendar.getInstance().time, Constants.API_QUERY_DATE_FORMAT)
        Log.e("today",today.toString());

        Log.e("TAG1ASTEROIDSRE", asteroidsSource.value.toString());

    }
    init {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
            picturesOfDayRepository.refreshPictureOfDay()
        }
    }

     fun setFilter(newFilter: AsteroidApiFilter) {
        filter.value = newFilter
         viewModelScope.launch {
             updateAsteroids()
         }
    }
    val picOfDay = picturesOfDayRepository.pictureOfDay


}