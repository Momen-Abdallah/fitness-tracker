package com.example.fitnesstracker.ui.homeScreen

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.Utilts
import com.example.fitnesstracker.data.repo.StepsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(val repository: StepsRepository): ViewModel()  {


    val stepsWeeksData = MutableLiveData<Map<String,Any>>()
     fun getDaysData() {
        viewModelScope.launch {
            stepsWeeksData.value = repository.getDaysData().data
        }
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