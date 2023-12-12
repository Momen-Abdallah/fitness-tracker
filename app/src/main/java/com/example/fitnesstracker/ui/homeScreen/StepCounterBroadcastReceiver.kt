package com.example.fitnesstracker.ui.homeScreen

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fitnesstracker.data.repo.StepsRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class StepCountResetReceiver : BroadcastReceiver() {


//    @Inject
//     lateinit var repository: StepsRepository
//    @Inject
//    lateinit var context: Context

    override fun onReceive(context: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED){

//            with(NotificationManagerCompat.from(this)) {
//                // notificationId is a unique int for each notification that you must define.
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.POST_NOTIFICATIONS
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return
//                }
//                notify("@", builder.build())
//            }
//            val steps = repository.steps.value?:0
//            GlobalScope.launch(Dispatchers.IO) {
//                repository.insertTodaySteps(steps)
//            }

            if (context!=null)
            GlobalScope.launch {
                Log.d("StepBroadcast123","Succeed")

                val todaySteps = context.getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("today_steps",0)
                val allSteps = context.getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("all_steps",0)

                withContext(Dispatchers.IO) {
                    StepsRepository(context)
                        .insertTodaySteps(steps = todaySteps)
                }

                val editor = context.getSharedPreferences("pref", Context.MODE_PRIVATE).edit()
                editor.putInt("all_steps",todaySteps + allSteps)
                editor.putInt("today_steps",0)
                editor.apply()
            }
            else{
                Log.d("StepBroadcast123","Failed")

            }



        }
    }
}