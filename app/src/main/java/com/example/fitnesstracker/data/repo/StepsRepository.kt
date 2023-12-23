package com.example.fitnesstracker.data.repo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.util.LocaleData
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.fitnesstracker.Utilts
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.firestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class StepsRepository (val context: Context) :
    SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var step_sensor : Sensor
    var steps  =  MutableLiveData<Int>(0)

    suspend fun getDaysData() =

        Firebase.firestore.collection("users").document(Firebase.auth.uid!!)
            .collection("StepsData")
            .document("DailySteps")
            .get().await()


    suspend fun insertTodaySteps(steps : Int){

        Firebase.firestore.collection("users").document(Firebase.auth.uid!!)
            .collection("StepsData")
            .document("DailySteps")
            .set(mapOf(LocalDate.now().minusDays(1).toString() to steps), SetOptions.merge())
            .await()

    }
    fun registerSensors(){
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
            step_sensor = it
        }
        sensorManager.registerListener(this,step_sensor,SensorManager.SENSOR_DELAY_NORMAL)

        steps.value = 0
    }
    fun unregisterSensors(){
        sensorManager.unregisterListener(this)
    }
    override fun onSensorChanged(p0: SensorEvent?) {


        Toast.makeText(context, "sensor changed", Toast.LENGTH_SHORT).show()
        val allSteps = context.getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("all_steps",0)

//        val initialStepCount =
//        if (Utilts.initialStepCount == 0){
//
//        }

        if (p0?.sensor?.type == Sensor.TYPE_STEP_COUNTER){

            val pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE)

            pref.edit().putInt("today_steps" , allSteps - pref.getInt("today_steps",0))
                .apply()
//            step_sensor.
//            steps.value = p0.values[0].toInt() //- allSteps
            steps.value = pref.getInt("today_steps",0)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


}