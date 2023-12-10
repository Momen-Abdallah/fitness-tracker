package com.example.fitnesstracker.ui.homeScreen

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fitnesstracker.data.repo.StepsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StepResetWorker(val context: Context, val workerParams: WorkerParameters) : Worker(context, workerParams) {

//    @Inject
//    lateinit var repository: StepsRepository
//    @Inject
//    lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {

        GlobalScope.launch {
            val todaySteps = context.getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("today_steps",0)
            val allSteps = context.getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("all_steps",0)
            val editor = context.getSharedPreferences("pref", Context.MODE_PRIVATE).edit()
            editor.putInt("all_steps",todaySteps + allSteps)
            editor.putInt("today_steps",0)
            editor.apply()
//            withContext(Dispatchers.IO){
//                repository.insertTodaySteps(steps)
//            }
        }

        // save the data of the previous day
        //


//        GlobalScope.launch(Dispatchers.IO) {
//            repository.insertTodaySteps(steps)
//        }

        return Result.success()


    }

}