package com.example.fitnesstracker.ui.homeScreen

import android.R.attr.delay
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.HomeScreenBinding
import com.example.fitnesstracker.services.NotificationWorker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notify
import okio.utf8Size
import org.joda.time.DateTime
import org.joda.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeScreen : Fragment()//, SensorEventListener
{
    private val viewModel: HomeScreenViewModel by viewModels()

    private lateinit var binding: HomeScreenBinding
    var t = false
    lateinit var sensorManager: SensorManager
    lateinit var alarmManager: AlarmManager


    override fun onResume() {
        super.onResume()
//        val t_steps = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
//            .getInt("today_steps", 0)
//        binding.min.amountText.text =
//            (t_steps / 6000.0).roundToInt().toString().apply { if (length > 4) substring(3) }
//        binding.kcal.amountText.text =
//            (t_steps * 0.04).toString().apply { if (length > 4) this.substring(3) }
//        binding.km.amountText.text =
//            (t_steps * 75 / 100000.0).toString().apply { if (length > 4) substring(3) }

        val goal = viewModel.getGoal()
        binding.first.progressCircular.max = goal
        binding.second.progressCircular.max = goal
        binding.third.progressCircular.max = goal
        binding.forth.progressCircular.max = goal
        binding.fifth.progressCircular.max = goal
        binding.sixth.progressCircular.max = goal
        binding.seventh.progressCircular.max = goal
        binding.progressBar.max = goal
        binding.goalText.text = "/${goal} Steps"

        viewModel.getStepsData()
        viewModel.getDistance()
        viewModel.getMin()
        viewModel.getCal()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = HomeScreenBinding.inflate(inflater, container, false)
        // sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val goal = requireContext().getSharedPreferences("pref",Context.MODE_PRIVATE).getInt("goal",6000)
//            binding.first.progressCircular.max = goal
//            binding.second.progressCircular.max = goal
//            binding.third.progressCircular.max = goal
//            binding.forth.progressCircular.max = goal
//            binding.fifth.progressCircular.max = goal
//            binding.sixth.progressCircular.max = goal
//            binding.seventh.progressCircular.max = goal
//            binding.progressBar.max = goal
//            binding.goalText.text = goal.toString()


        if (requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
                .getBoolean("isNotificationWorkerNotScheduled", true)
        ) {
            requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).edit()
                .putBoolean("isNotificationWorkerScheduled", false).apply()
            scheduleNotificationWorker()
        }

        binding.min.unit.text = "Min"
        binding.kcal.unit.text = "Cal"
        binding.km.unit.text = "Km"


        viewModel.lastWeekStepsData.observe(viewLifecycleOwner) {
            binding.stepsText.text = it[0].toString()
            binding.second.progressCircular.progress = it[1]
            binding.third.progressCircular.progress = it[2]
            binding.forth.progressCircular.progress = it[3]
            binding.fifth.progressCircular.progress = it[4]
            binding.sixth.progressCircular.progress = it[5]
            binding.seventh.progressCircular.progress = it[6]

            binding.first.progressCircular.progress = it[0]
            binding.progressBar.progress = it[0]
        }

        viewModel.days.observe(viewLifecycleOwner) {
            binding.first.dayText.text = it[0]
            binding.second.dayText.text = it[1]
            binding.third.dayText.text = it[2]
            binding.forth.dayText.text = it[3]
            binding.fifth.dayText.text = it[4]
            binding.sixth.dayText.text = it[5]
            binding.seventh.dayText.text = it[6]

            binding.first.dayText.setTextColor(requireContext().getColor(R.color.light_red))
        }

        viewModel.distance.observe(viewLifecycleOwner) {
            binding.km.amountText.text = it
        }
        viewModel.cal.observe(viewLifecycleOwner) {
            binding.kcal.amountText.text = it
        }
        viewModel.min.observe(viewLifecycleOwner) {
            binding.min.amountText.text = it
        }

        viewModel.weekAverage.observe(viewLifecycleOwner) {
            binding.dailyAverage.text = "Week average: ${it}"
        }


//        binding.stepsText.text = requireContext().getSharedPreferences("pref",Context.MODE_PRIVATE).getInt("today_steps",0).toString()
//        WorkManager.getInstance(requireContext()).cancelAllWork()

//        scheduleResetAlarm()
//        scheduleWork()
//        viewModel.registerSensors()
//        binding.progressBar.max = 1000

//        viewModel.steps.observe(viewLifecycleOwner) {
////            binding.stepsText.text = viewModel.steps.value.toString()
//            binding.stepsText.text = requireContext().getSharedPreferences("pref",Context.MODE_PRIVATE).getInt("today_steps",0).toString()
//
////            binding.stepsText.text = it.toString()
//            binding.first.progressCircular.progress = it
//            binding.progressBar.progress = it
//
//            binding.min.amountText.text = (it / 6000.0).roundToInt().toString().apply { if (length > 4) substring(3) }
////             (it * 0.04).toString().let { binding.kcal.amountText.text = if (it.length>4) it.substring(3) else it }
//            binding.kcal.amountText.text = (it * 0.04).toString().apply {  if (length>4) this.substring(3)  }
//
//            binding.km.amountText.text =  (it * 75 / 100000.0).toString().apply { if (length > 4) substring(3) }
//
//            binding.min.unit.text = "Min"
//            binding.kcal.unit.text = "Kcal"
//            binding.km.unit.text =  "Km"
//
//        }

//        viewModel.stepsWeeksData.observe(viewLifecycleOwner) { data ->
//            binding.first.progressCircular.progress = 0
//            binding.second.progressCircular.progress = 0
//            binding.third.progressCircular.progress = 0
//            binding.forth.progressCircular.progress = 0
//            binding.fifth.progressCircular.progress = 0
//            binding.sixth.progressCircular.progress = 0
//            binding.seventh.progressCircular.progress = 0
//            for (i in 0..6) {
//
//                val date = LocalDate.now().minusDays(i.toLong()).toString()
//                val day =
//                    LocalDate.now().minusDays(i.toLong()).dayOfWeek.toString().first().toString()
//
//
//                when (i) {
//                    0 -> {
//                        binding.first.dayText.text = day
//                        binding.first.dayText.setTextColor(Color.RED)
////                        data[date]?.let { binding.first.progressCircular.progress = (it as Long).toInt() }
//                    }
//
//                    1 -> {
//                        binding.second.dayText.text = day
//                        data[date]?.let {
//                            binding.second.progressCircular.progress = (it as Long).toInt()
//                        }
//                    }
//
//                    2 -> {
//                        binding.third.dayText.text = day
//                        data[date]?.let {
//                            binding.third.progressCircular.progress = (it as Long).toInt()
//                        }
//                    }
//
//                    3 -> {
//                        binding.forth.dayText.text = day
//                        data[date]?.let {
//                            binding.forth.progressCircular.progress = (it as Long).toInt()
//                        }
//                    }
//
//                    4 -> {
//                        binding.fifth.dayText.text = day
//                        data[date]?.let {
//                            binding.fifth.progressCircular.progress = (it as Long).toInt()
//                        }
//                    }
//
//                    5 -> {
//                        binding.sixth.dayText.text = day
//                        data[date]?.let {
//                            binding.sixth.progressCircular.progress = (it as Long).toInt()
//                        }
//                    }
//
//                    6 -> {
//                        binding.seventh.dayText.text = day
//                        data[date]?.let {
//                            binding.seventh.progressCircular.progress = (it as Long).toInt()
//                        }
//                    }
//                }
//
//
//            }
//        }
//        viewModel.getDaysData()

        /* val fitnessOptions = FitnessOptions.builder()
             .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
             .addDataType(DataType.TYPE_DISTANCE_DELTA,FitnessOptions.ACCESS_READ)
             .addDataType(DataType.TYPE_MOVE_MINUTES,FitnessOptions.ACCESS_READ)
             .addDataType(DataType.TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
             .build()

         val googleSignInAccount = GoogleSignIn.getAccountForExtension(requireContext(),fitnessOptions)
 //        Fitness.getRecordingClient(requireContext(),googleSignInAccount)

         try {

 //            Fitness.getRecordingClient(requireContext(), GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions))
 //                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
 //                .addOnSuccessListener {
 //                    Log.i(TAG,"Subscription was successful!")
 //                }
 //                .addOnFailureListener { e ->
 //                    Log.w(TAG, "There was a problem subscribing ", e)
 //                }


 //            val historyClient = Fitness.getHistoryClient(requireContext(), googleSignInAccount)

 //            val startTime = LocalDateTime.of(2023, 12, 21, 0, 0).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
 //            val endTime = startTime + DateUtils.DAY_IN_MILLIS

 //            val readRequest = DataReadRequest.Builder()
 //                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
 //                .bucketByTime(1, TimeUnit.DAYS)
 //                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
 //                .build()
 //
 //            val readRequest2 =SessionReadRequest.Builder()
 //                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
 //                .read(DataType.TYPE_STEP_COUNT_DELTA)
 //                .build();


 //            val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
 //            val endTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT)
             if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
                 GoogleSignIn.requestPermissions(
                     this, // Activity
                     1,
                     googleSignInAccount,
                     fitnessOptions
                 )
             }

             for (i in 0..6){
                 var startTime : ZonedDateTime
                 var endTime : ZonedDateTime
                 if (i == 0){
                      startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                      endTime =  LocalDateTime.of(LocalDate.now(), LocalTime.now()).atZone(ZoneId.systemDefault())
                 }else{
                      startTime = LocalDateTime.of(LocalDate.now().minusDays(i.toLong()), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                      endTime =  LocalDateTime.of(LocalDate.now().minusDays((i-1).toLong()), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                 }

                 val date = LocalDate.now().minusDays(i.toLong()).toString()
                 val day =
                     LocalDate.now().minusDays(i.toLong()).dayOfWeek.toString().first().toString()
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

                 Fitness.getHistoryClient(requireContext(), GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions))
                     .readData(request)
                     .addOnSuccessListener { response ->
                         val totalSteps = response.buckets
                             .flatMap { it.dataSets }
                             .flatMap { it.dataPoints }
                             .sumBy { it.getValue(Field.FIELD_STEPS).asInt() }

                         when(i){
                             0->{
                                 binding.stepsText.text = totalSteps.toString()
                                 binding.first.progressCircular.progress = totalSteps
                                 binding.progressBar.progress = totalSteps
                                 binding.first.dayText.text = day
                                 binding.first.dayText.setTextColor(Color.RED)
 //                                binding.min.amountText.text =
 //                                    (totalSteps / 6000.0).roundToInt().toString().apply { if (length > 4) substring(3) }
 //                                binding.kcal.amountText.text =
 //                                    (totalSteps * 0.04).toString().apply { if (length > 4) this.substring(3) }
 //                                binding.km.amountText.text =
 //                                    (totalSteps * 75 / 100000.0).toString().apply { if (length > 4) substring(3) }

                             }
                             1->{
                                 binding.second.progressCircular.progress = totalSteps
                                 binding.second.dayText.text = day

                             }
                             2->{
                                 binding.third.progressCircular.progress = totalSteps
                                 binding.third.dayText.text = day

                             }
                             3->{
                                 binding.forth.progressCircular.progress = totalSteps
                                 binding.forth.dayText.text = day

                             }
                             4->{
                                 binding.fifth.progressCircular.progress = totalSteps
                                 binding.fifth.dayText.text = day


                             }
                             5->{
                                 binding.sixth.progressCircular.progress = totalSteps
                                 binding.sixth.dayText.text = day

                             }
                             6->{
                                 binding.seventh.progressCircular.progress = totalSteps
                                 binding.seventh.dayText.text = day

                             }
                         }
                     }
             }




             val historyClient = Fitness.getHistoryClient(requireContext(), googleSignInAccount)
             historyClient.readDailyTotal(DataType.TYPE_DISTANCE_DELTA).addOnSuccessListener {
 //                Toast.makeText(requireContext(), "Success1 ${(it.dataPoints.size)}", Toast.LENGTH_SHORT).show()
 //                binding.km.amountText.text = (it.dataPoints[0]?.getValue(Field.FIELD_DISTANCE)?.toString() ?: 0).toString()
                 if (it.dataPoints.isNotEmpty()){
                     val data = (it.dataPoints[0]?.getValue(Field.FIELD_DISTANCE)?.toString()!!.toDouble()/1000 * 1.609).toString()
                     if (data!!.length - data.indexOf('.') > 3){
                         binding.km.amountText.text = data.removeRange(data.indexOf('.')+2,data.length-1)
                     }
                     else{
                         binding.km.amountText.text =  data
                     }
                 }

 //                if (it.dataPoints.firstOrNull()?.getValue(Field.FIELD_DISTANCE) != null
 //                ) {
 ////                    Toast.makeText(requireContext(), "null1 ${(it.dataPoints.size)}", Toast.LENGTH_SHORT).show()
 //                    Toast.makeText(requireContext(), "null1 ${it.dataPoints.firstOrNull()?.getValue(Field.FIELD_DISTANCE)}", Toast.LENGTH_SHORT).show()
 //                }

             }.addOnFailureListener {
                 Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()

             }
             historyClient.readDailyTotal(DataType.TYPE_MOVE_MINUTES).addOnSuccessListener {


 //                Toast.makeText(requireContext(), "Success2 ${(it.dataPoints.size)}", Toast.LENGTH_SHORT).show()
                 if (it.dataPoints.isNotEmpty()){
                     val data = it.dataPoints[0]?.getValue(Field.FIELD_DURATION)?.toString()
                     if (data!!.length - data.indexOf('.') > 3){
                         binding.min.amountText.text = data.removeRange(data.indexOf('.')+2,data.length-1)
                     }
                     else{
                         binding.min.amountText.text =  data
                     }
                 }


             }.addOnFailureListener {
                 Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()

             }
             historyClient.readDailyTotal(DataType.TYPE_CALORIES_EXPENDED).addOnSuccessListener {
 //                Toast.makeText(requireContext(), "Success3 ${(it.dataPoints.size)}", Toast.LENGTH_SHORT).show()
 //                binding.kcal.amountText.text = (it.dataPoints[0]?.getValue(Field.FIELD_CALORIES)?.toString() ?: 0).toString()
                 if (it.dataPoints.isNotEmpty()){
                     val data = it.dataPoints[0]?.getValue(Field.FIELD_CALORIES)?.toString()
                     if (data!!.length - data.indexOf('.') > 3){
                         binding.kcal.amountText.text = data.removeRange(data.indexOf('.')+2,data.length-1)
                     }
                     else{
                         binding.kcal.amountText.text =  data
                     }
                 }



             }.addOnFailureListener {
                 Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()

             }


         } catch (e: Exception) {
             Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
         }*/


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

    private fun scheduleResetAlarm() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 45)
        calendar.set(Calendar.SECOND, 0)

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + 10 * 1000 // 1 hour from now
        val intent = Intent(requireContext(), StepCountResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 5, intent,
            PendingIntent.FLAG_MUTABLE
        )

//        val windowStart = calendar.timeInMillis
//        calendar.set(Calendar.MINUTE,5)
//        val windowEnd = calendar.timeInMillis

//        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,windowStart,windowEnd,pendingIntent)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
//        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerTime, pendingIntent), pendingIntent)

//        }else{
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
//        }

//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )
//        alarmManager.setAlarmClock(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )

//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )

//        alarmManager.set
//        alarmManager.setExactAndAllowWhileIdle()

    }

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

        //work manager is used for schedule the notification to be send everyday without care about the time
        // or execute an operation at such constraints
        //WorkManager is for deferrable tasks,

        val SELF_REMINDER_HOUR = 0

        val delay = if (DateTime.now().getHourOfDay() < SELF_REMINDER_HOUR) {
            Duration(
                DateTime.now(),
                DateTime.now().withTimeAtStartOfDay().plusHours(SELF_REMINDER_HOUR)
            ).getStandardMinutes()
        } else {
            Duration(
                DateTime.now(),
                DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(SELF_REMINDER_HOUR)
            ).getStandardMinutes()
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val currentTime = System.currentTimeMillis()
        val timeDiff = calendar.timeInMillis - currentTime
        //
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
//            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<StepResetWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(requireContext().applicationContext).enqueueUniquePeriodicWork(
            "worker_1",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun scheduleNotificationWorker() {

        val SELF_REMINDER_HOUR = 10

        val delay = if (DateTime.now().hourOfDay < SELF_REMINDER_HOUR) {
            Duration(
                DateTime.now(),
                DateTime.now().withTimeAtStartOfDay().plusHours(SELF_REMINDER_HOUR)
            ).standardMinutes
        } else {
            Duration(
                DateTime.now(),
                DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(SELF_REMINDER_HOUR)
            ).standardMinutes
        }

        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24,TimeUnit.HOURS)
            .setInitialDelay(delay,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext().applicationContext).enqueueUniquePeriodicWork(
            "notificationWorker"
            ,ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun getDayOfWeek(dateString: String): String {
        val date = LocalDate.parse(dateString)
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        return dayOfWeek
    }
}