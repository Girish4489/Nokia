package com.example.nokia.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nokia.data.DummyDataLoader
import com.example.nokia.databinding.ActivityTestDashboardBinding
import org.json.JSONArray
import org.json.JSONObject
import com.example.nokia.utils.Constants

class TestExecuteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedEnvironment = intent.getStringExtra(Constants.EXTRA_SELECTED_ENVIRONMENT) ?: "demo"
        "@string/selected_environment $selectedEnvironment".also { binding.tvSelectedEnvironment.text = it }

        val jsonString = DummyDataLoader.loadJSONFromAsset(this, "dummy_response.json")
        try {
            val jsonObject = JSONObject(jsonString ?: "")
            val outputObject = jsonObject.getJSONObject("output")

            // Populate single-select spinners
            setUpSpinner(binding.spinnerUEProfiles, outputObject.getJSONArray("ue_profiles"))
            setUpSpinner(binding.spinnerEnvProfiles, outputObject.getJSONArray("env_profiles"))

            // Set up multi-select dropdowns
            setUpMultiSelectDropdown(binding.tvSelectTestServices, outputObject.getJSONArray("test_services"))
            setUpMultiSelectDropdown(binding.tvSelectTestSuites, outputObject.getJSONArray("test_suites"))
            setUpMultiSelectDropdown(binding.tvSelectTestCases, outputObject.getJSONArray("test_cases"))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.btnExecute.setOnClickListener {
            Toast.makeText(this, "Executing tests in $selectedEnvironment environment", Toast.LENGTH_SHORT).show()
            val executeJsonString = DummyDataLoader.loadJSONFromAsset(this, "dummy_execute.json")
            val ntspSessionId: String?

            try {
                val executeJsonObject = JSONObject(executeJsonString ?: "")
                val outputObject = executeJsonObject.getJSONObject("output")
                ntspSessionId = outputObject.getString("ntsp_session_id")

                // Navigate to DashboardTestListActivity with the session ID
                val intent = Intent(this, DashboardTestListActivity::class.java)
                intent.putExtra("SESSION_ID", ntspSessionId)
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Execution failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Single-select spinner setup
    private fun setUpSpinner(spinner: Spinner, jsonArray: JSONArray) {
        val items = List(jsonArray.length()) { index -> jsonArray.getString(index) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // Multi-select dropdown setup using TextView
    private fun setUpMultiSelectDropdown(textView: TextView, jsonArray: JSONArray) {
        val items = List(jsonArray.length()) { index -> jsonArray.getString(index) }
        val selectedItems = BooleanArray(items.size)

        textView.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Options")
                .setMultiChoiceItems(items.toTypedArray(), selectedItems) { _, which, isChecked ->
                    selectedItems[which] = isChecked
                }
                .setPositiveButton("OK") { dialog, _ ->
                    val selected = items.filterIndexed { index, _ -> selectedItems[index] }
                    textView.text = selected.joinToString(", ")
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}
