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
import com.example.fitnesstracker.Utilts
import com.example.fitnesstracker.data.repo.StepsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(val repository: StepsRepository): ViewModel()  {


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