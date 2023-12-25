package com.example.fitnesstracker.data.repo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class StepsRepository (val context: Context,val googleSignInAccount: GoogleSignInAccount) :
    SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var step_sensor : Sensor
    var steps  =  MutableLiveData<Int>(0)

    val lastWeekStepsData : MutableLiveData<List<Int>> = MutableLiveData<List<Int>>()
    val days : MutableLiveData<List<String>> = MutableLiveData<List<String>>()
    var distance  =  MutableLiveData<String>("0.0")
    var cal  =  MutableLiveData<String>("0.0")
    var min  =  MutableLiveData<String>("0.0")
    var weekAverage  =  MutableLiveData<Int>(0)

    suspend fun getStepsData(){

//        val fitnessOptions = FitnessOptions.builder()
//            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.TYPE_MOVE_MINUTES,FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
//            .build()

//        val googleSignInAccount = GoogleSignIn.getAccountForExtension(context,fitnessOptions)

//        val date = LocalDate.now().minusDays(i.toLong()).toString()
//        val day =
//            LocalDate.now().minusDays(i.toLong()).dayOfWeek.toString().first().toString()
//            val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
//            val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())


        val stepsData = Array<Int>(7){0}
        val daysName = Array<String>(7){""}
        var avg = 0
        for (i in 0..6){
            var startTime : ZonedDateTime
            var endTime : ZonedDateTime
            if (i == 0){
                startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                endTime =  LocalDateTime.of(LocalDate.now(), LocalTime.now()).atZone(ZoneId.systemDefault())
            }else{
                startTime = LocalDateTime.of(LocalDate.now().minusDays(i.toLong()), LocalTime.MIDNIGHT).atZone(
                    ZoneId.systemDefault())
                endTime =  LocalDateTime.of(LocalDate.now().minusDays((i-1).toLong()), LocalTime.MIDNIGHT).atZone(
                    ZoneId.systemDefault())
            }



            daysName[i] = LocalDate.now().minusDays(i.toLong()).dayOfWeek.toString().first().toString()
//            val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
//            val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
            val datasource = DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build()

            val request = DataReadRequest.Builder()
                .aggregate(datasource)
                .bucketByTime(1, TimeUnit.DAYS)
//                .setTimeRange(startTime.toEpochSecond(ZoneOffset.MIN), endTime.toEpochSecond(ZoneOffset.MIN), TimeUnit.SECONDS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

            Fitness.getHistoryClient(context, googleSignInAccount)
                .readData(request)
                .addOnSuccessListener { response ->
                    val totalSteps = response.buckets
                        .flatMap { it.dataSets }
                        .flatMap { it.dataPoints }
                        .sumBy { it.getValue(Field.FIELD_STEPS).asInt() }

                    stepsData[i] = totalSteps
                    avg += totalSteps

                }.await()
        }

        lastWeekStepsData.value = stepsData.toList()
        days.value = daysName.toList()
        weekAverage.value = avg / 7
    }

    suspend fun getTodayDistance(){
        val historyClient = Fitness.getHistoryClient(context, googleSignInAccount)
        historyClient.readDailyTotal(DataType.TYPE_DISTANCE_DELTA).addOnSuccessListener {
            if (it.dataPoints.isNotEmpty()){
                val data = (it.dataPoints[0]?.getValue(Field.FIELD_DISTANCE)?.toString()!!.toDouble()/1000 * 1.609).toString()
                if (data!!.length - data.indexOf('.') > 3){
                    distance.value = data.removeRange(data.indexOf('.')+2,data.length-1)
                }
                else{
                    distance.value =  data
                }
            }


        }.addOnFailureListener {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()

        }.await()
    }

    suspend fun getTodayCal(){
        val historyClient = Fitness.getHistoryClient(context, googleSignInAccount)
        historyClient.readDailyTotal(DataType.TYPE_CALORIES_EXPENDED).addOnSuccessListener {
//                Toast.makeText(requireContext(), "Success3 ${(it.dataPoints.size)}", Toast.LENGTH_SHORT).show()
//                binding.kcal.amountText.text = (it.dataPoints[0]?.getValue(Field.FIELD_CALORIES)?.toString() ?: 0).toString()
            if (it.dataPoints.isNotEmpty()){
                val data = it.dataPoints[0]?.getValue(Field.FIELD_CALORIES)?.toString()
                if (data!!.length - data.indexOf('.') > 3){
                    cal.value = data.removeRange(data.indexOf('.')+2,data.length-1)
                }
                else{
                    cal.value =  data!!
                }
            }



        }.addOnFailureListener {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()

        }.await()
    }
    suspend fun getTodayMin(){
        val historyClient = Fitness.getHistoryClient(context, googleSignInAccount)
        historyClient.readDailyTotal(DataType.TYPE_MOVE_MINUTES).addOnSuccessListener {


//                Toast.makeText(requireContext(), "Success2 ${(it.dataPoints.size)}", Toast.LENGTH_SHORT).show()
            if (it.dataPoints.isNotEmpty()){
                val data = it.dataPoints[0]?.getValue(Field.FIELD_DURATION)?.toString()
                if (data!!.length - data.indexOf('.') > 3){
                    min.value = data.removeRange(data.indexOf('.')+2,data.length-1)
                }
                else{
                    min.value =  data!!
                }
            }


        }.addOnFailureListener {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()

        }.await()
    }

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