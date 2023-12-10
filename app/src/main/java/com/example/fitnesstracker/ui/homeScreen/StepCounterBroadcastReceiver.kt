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
import javax.inject.Inject


@AndroidEntryPoint
class StepCountResetReceiver : BroadcastReceiver() {

    @Inject
     lateinit var repository: StepsRepository
    @Inject
    lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(p0: Context?, p1: Intent?) {
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
            val steps = repository.steps.value?:0
//            GlobalScope.launch(Dispatchers.IO) {
//                repository.insertTodaySteps(steps)
//            }

            Log.d("broadcast1234567","broadcast1234567")

            repository.steps.value = 0

            // save the data of the previous day
            //
            val editor = context.getSharedPreferences("pref",MODE_PRIVATE).edit()
            val oldSteps = context.getSharedPreferences("pref",MODE_PRIVATE).getInt("initial_steps",0)
            editor.putInt("initial_steps",steps + oldSteps)
            editor.apply()


        }
    }
}