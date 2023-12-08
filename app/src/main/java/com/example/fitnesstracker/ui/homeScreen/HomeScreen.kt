package com.example.fitnesstracker.ui.homeScreen

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.HomeScreenBinding


class HomeScreen : Fragment(), SensorEventListener {

    private lateinit var binding: HomeScreenBinding

    lateinit var sensorManager: SensorManager
    lateinit var alarmManager: AlarmManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  HomeScreenBinding.inflate(inflater, container, false)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)



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
        binding.first.progressCircular.progress = 300

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

    override fun onSensorChanged(event: SensorEvent?) {

        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val currentStepCount = event.values[0].toInt()
            binding.stepsText.text = currentStepCount.toString()
        }
        Toast.makeText(requireContext(), "onSensorChanged", Toast.LENGTH_SHORT).show()
//        val total = event!!.values[0]
//        binding.stepsText.text = (binding.stepsText.text.toString().toInt() + 1).toString()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Toast.makeText(requireContext(), "onAccuracyChanged", Toast.LENGTH_SHORT).show()

    }
}