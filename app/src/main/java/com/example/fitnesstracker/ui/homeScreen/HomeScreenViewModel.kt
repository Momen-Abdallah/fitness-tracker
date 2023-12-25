package com.example.fitnesstracker.ui.homeScreen

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.repo.StepsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(val repository: StepsRepository,@ApplicationContext val context: Context): ViewModel()  {


    val stepsWeeksData = MutableLiveData<Map<String,Any>>()

    val lastWeekStepsData : MutableLiveData<List<Int>>
        get() = repository.lastWeekStepsData

    val days : MutableLiveData<List<String>>
        get() = repository.days
    val distance : MutableLiveData<String>
        get() = repository.distance

    val cal : MutableLiveData<String>
        get() = repository.cal

    val min : MutableLiveData<String>
        get() = repository.min

    val weekAverage : MutableLiveData<Int>
        get() = repository.weekAverage

//     fun getDaysData() {
//        viewModelScope.launch {
//            stepsWeeksData.value = repository.getDaysData().data
//        }
//    }
    fun getStepsData() {
        viewModelScope.launch{
            repository.getStepsData()
        }
    }
    fun getDistance() {
        viewModelScope.launch {
            repository.getTodayDistance()
        }
    }
    fun getCal() {
        viewModelScope.launch {
            repository.getTodayCal()
        }
    }
    fun getMin() {
        viewModelScope.launch {
            repository.getTodayMin()
        }
    }

    fun getGoal() : Int{
        return context.getSharedPreferences("pref",Context.MODE_PRIVATE).getInt("goal",6000)
    }

//    private lateinit var sensorManager: SensorManager
//    private lateinit var step_sensor : Sensor
    var steps  =  repository.steps
//    inner class StepCounter : LiveData<Int>() , SensorEventListener{
//        override fun onSensorChanged(p0: SensorEvent?) {
//
//        }
//
//        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//
//        }
//
//        override fun onActive() {
//            super.onActive()
//
//        }
//
//        override fun onInactive() {
//            super.onInactive()
//        }
//    }


    fun registerSensors(){
        repository.registerSensors()
    }
    fun unregisterSensors(){
        repository.unregisterSensors()
    }
}