package com.example.fitnesstracker.ui.settingsScreen

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.example.fitnesstracker.databinding.SettingsScreenBinding
import com.example.fitnesstracker.databinding.StepGoalDialogBinding


class settingsScreen : Fragment() {

    private lateinit var binding: SettingsScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  SettingsScreenBinding .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textInputLayout.suffixText = requireContext().getSharedPreferences("pref",Context.MODE_PRIVATE).getInt("goal",6000).toString()

        binding.linearLayout2.setOnClickListener {
//            StepGoalDialog().show(parentFragmentManager,"")
//            findNavController().navigate(R.id.action_settingsScreen_to_stepGoalDialog)

            val dialog = Dialog(requireContext())
            val dialogBinding = StepGoalDialogBinding.inflate(LayoutInflater.from(requireContext()))
            dialog.setContentView(dialogBinding.root)
            dialog.window!!.setGravity(Gravity.BOTTOM)
            dialog.window!!.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            val numberPicker = dialogBinding.numberPicker
            dialogBinding.cancel.setOnClickListener {
                dialog.dismiss()
            }

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
              dialogBinding.numberPicker.textColor = Color.WHITE
            numberPicker.wrapSelectorWheel = false
            numberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


            val stepSize = 500
            val arr =  (1000..40000 step stepSize).map { it.toString() }.toTypedArray()
            numberPicker.maxValue = arr.size-1
            numberPicker.minValue = 0
            numberPicker.displayedValues =arr
            numberPicker.value = arr.indexOf(requireContext().getSharedPreferences("pref",Context.MODE_PRIVATE).getInt("goal",6000).toString())
//            numberPicker.focusedChild.background = AppCompatResources.getDrawable(requireContext(),R.drawable.shape_for_background)
            dialogBinding.saveButton.setOnClickListener {
                requireContext().getSharedPreferences("pref",Context.MODE_PRIVATE).edit().putInt("goal",arr[numberPicker.value].toInt()).apply()
                binding.textInputLayout.suffixText = arr[numberPicker.value]
                dialog.dismiss()
            }
//            numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
//
//            }

            dialog.show()



        }


    }

}