package com.example.fitnesstracker.ui.homeScreen

import android.R.attr.delay
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fitnesstracker.databinding.HomeScreenBinding
import com.google.type.DateTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class HomeScreen : Fragment()//, SensorEventListener
{
    private val viewModel: HomeScreenViewModel by viewModels()

    private lateinit var binding: HomeScreenBinding
    var t = false
    lateinit var sensorManager: SensorManager
    lateinit var alarmManager: AlarmManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  HomeScreenBinding.inflate(inflater, container, false)
       // sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        //sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)

//        if (viewModel.steps.value == 0)
//            viewModel.steps.value = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("today_steps",0)
//
//        if (!t){
//            viewLifecycleOwner.lifecycleScope.launch {
//                val steps = viewModel.steps.value?:0
////                viewModel.steps.value = 0
//
//                val editor = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).edit()
//                val oldSteps = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("all_steps",0)
//                Log.d("all_steps",oldSteps.toString())
//                Log.d("toady_steps",steps.toString())
//                Log.d("viewModel_steps",viewModel.steps.value.toString())
//
//                editor.putInt("initial_steps",steps + oldSteps)
//                editor.apply()
////                withContext(Dispatchers.IO){
////                    viewModel.insertTodaySteps(steps)
////                }
//            }
//            t = false
//        }

        scheduleResetAlarm()
        scheduleWork()
        viewModel.registerSensors()
        viewModel.steps.observe(viewLifecycleOwner){
            binding.stepsText.text = viewModel.steps.value.toString()
        }




        binding.first.dayText.text = "S"
        binding.second.dayText.text = "M"
        binding.third.dayText.text = "T"
        binding.forth.dayText.text = "W"
        binding.fifth.dayText.text = "T"
        binding.sixth.dayText.text = "F"
        binding.seventh.dayText.text = "S"

        binding.progressBar.max = 1000
        binding.progressBar.progress=300


        binding.first.progressCircular.max = 1000
        binding.first.progressCircular.progress = 700
        binding.second.progressCircular.max = 1000
        binding.second.progressCircular.progress = 300

//        if (stepSensor != null){
//        }

         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.button.setOnClickListener {
//            findNavController().navigate(R.id.action_homeScreen_to_mapsFragment)
//        }
    }

    //    override fun onSensorChanged(event: SensorEvent?) {
//
//        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
//            val currentStepCount = event.values[0].toInt()
//            binding.stepsText.text = currentStepCount.toString()
//        }
//        Toast.makeText(requireContext(), "onSensorChanged", Toast.LENGTH_SHORT).show()
////        val total = event!!.values[0]
////        binding.stepsText.text = (binding.stepsText.text.toString().toInt() + 1).toString()
//    }
//
//    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        Toast.makeText(requireContext(), "onAccuracyChanged", Toast.LENGTH_SHORT).show()
//
//    }
    override fun onDestroyView() {
        super.onDestroyView()
//        viewModel.unregisterSensors()
    }
    private fun scheduleResetAlarm(){
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY,19)
        calendar.set(Calendar.MINUTE,54)
        calendar.set(Calendar.SECOND,0)

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(),StepCountResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )



    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleWork() {

//        val workRequest = PeriodicWorkRequest.Builder(
//            StepResetWorker::class.java,
//            15,
//            TimeUnit.MINUTES,
//            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
//            TimeUnit.MILLISECONDS
//        )
//            .setInitialDelay(delay.toLong(), TimeUnit.MINUTES)
//            .addTag("send_reminder_periodic")
//            .build()
//
//
//        WorkManager.getInstance(requireContext().applicationContext)
//            .enqueueUniquePeriodicWork(
//                "send_reminder_periodic",
//                ExistingPeriodicWorkPolicy.KEEP,
//                workRequest
//            )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<StepResetWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInitialDelay(0, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(requireContext().applicationContext).enqueueUniquePeriodicWork(
            "my_unique_work_name",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}