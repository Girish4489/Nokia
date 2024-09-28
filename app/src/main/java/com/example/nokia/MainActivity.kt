package com.example.nokia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.nokia.databinding.ActivityMainBinding
import com.example.nokia.ui.TestExecuteActivity
import com.example.nokia.data.DummyDataLoader
import com.example.nokia.utils.Constants
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use ViewBinding to bind the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the dummy job data
        val jsonString = DummyDataLoader.loadJSONFromAsset(this, "dummy_jobs.json")
        if (jsonString == null) {
            Log.e("MainActivity", "Error loading JSON data")
            // Show an error message to the user or handle the error gracefully
            return
        }
        println(jsonString)
        val isValidSession = isValidSessionFromJson(jsonString.toString())
        println(isValidSession) // Output: true
        when (isValidSession) {
            true -> {
                // Extract job names from the JSON data
                val jobNames: List<String> = getJobNamesFromJson(jsonString)
                println(jobNames)

                // Set up the environment dropdown spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jobNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerEnvironment.adapter = adapter

                // Enable the "Proceed" button when an environment is selected
                if (jobNames.isNotEmpty()) {
                    binding.spinnerEnvironment.setSelection(0)
                    binding.btnProceed.isEnabled = false
                }
                if (!binding.btnProceed.isEnabled){
                    binding.btnProceed.isEnabled = true
                }

                // Handle the "Proceed" button click
                binding.btnProceed.setOnClickListener {
                    val selectedEnvironment = binding.spinnerEnvironment.selectedItem.toString()

                    // Pass the selected environment to the TestDashboardActivity
                    val intent = Intent(this, TestExecuteActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ENVIRONMENT, selectedEnvironment)
                    startActivity(intent)
                    Toast.makeText(this, "Proceed button clicked with $selectedEnvironment", Toast.LENGTH_SHORT).show()
                }
                Log.d("MainActivity", "Valid session")
            }
            false -> {
                val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, emptyList())
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.btnProceed.isEnabled = false
                binding.spinnerEnvironment.adapter = adapter
                // Show an error message to the user or handle the error gracefully
                Log.d("MainActivity", "Invalid session")
            }
        }
    }

    // Utility method to extract valid session status from JSON data
    private fun isValidSessionFromJson(jsonString: String): Boolean {
        try {
            val jsonObject = JSONObject(jsonString)
            return jsonObject.getBoolean("valid_session")
        } catch (e: Exception) {
            e.printStackTrace() // Handle JSON parsing errors
            return false
        }
    }

    // Utility method to extract job names from JSON data
    private fun getJobNamesFromJson(jsonString: String): MutableList<String> {
        val jobNames = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonString)
            val outputArray = jsonObject.getJSONArray("output")
            for (i in 0 until outputArray.length()) {
                val jobObject = outputArray.getJSONObject(i)
                val jobName = jobObject.getString("job_name")
                jobNames.add(jobName)
            }
        } catch (e: Exception) {
            e.printStackTrace() // Handle JSON parsing errors
        }
        return jobNames
    }
}


