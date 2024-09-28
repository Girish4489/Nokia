package com.example.nokia.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import com.example.nokia.R
import android.view.Gravity
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nokia.databinding.ActivityDashboardTestListBinding
import org.json.JSONObject
import com.example.nokia.models.JobItem
import com.example.nokia.data.DummyDataLoader
import java.util.Date
import java.util.Locale

class DashboardTestListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardTestListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardTestListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load dummy results data
        val jsonString = DummyDataLoader.loadJSONFromAsset(this, "dummy_results.json")
        Log.d("DashboardTestList", "JSON String: $jsonString")

        val testJobsList = mutableListOf<JobItem>()

        try {
            val jsonObject = JSONObject(jsonString ?: "")
            val outputObject = jsonObject.getJSONObject("output")

            // Parse the TESTJOB array from JSON
            val testJobArray = outputObject.getJSONArray("TESTJOB")

            // Filter results based on session ID
            for (i in 0 until testJobArray.length()) {
                val jobObject = testJobArray.getJSONObject(i)

                // Create a JobItem object for the RecyclerView
                val jobItem = JobItem(
                    jobObject.getString("testrun_name"),
                    jobObject.getLong("ExecDate"),
                    jobObject.getString("UserName"),
                    jobObject.getString("CatTot").toInt(),
                    jobObject.getString("ExecCases").toInt(),
                    jobObject.getString("Pass").toInt(),
                    jobObject.getString("Fail").toInt(),
                    jobObject.getString("status")
                )

                // Log each job item to confirm parsing
                Log.d("DashboardTestList", "Parsed Job Item: $jobItem")

                testJobsList.add(jobItem)

                // Add job item to table layout
                addJobItemRow(jobItem)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Log the size of the list after population
        Log.d("DashboardTestList", "Number of items after parsing: ${testJobsList.size}")
    }

    private fun addJobItemRow(jobItem: JobItem) {
        // Create a new TableRow
        val tableRow = TableRow(this)

        // Create TextViews for each property of JobItem
        val jobNameTextView = TextView(this).apply {
            text = jobItem.jobName
            layoutParams = TableRow.LayoutParams(150, TableRow.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val executedDateTextView = TextView(this).apply {
            text = dateFormat.format(Date(jobItem.execDate))
            layoutParams = TableRow.LayoutParams(200, TableRow.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        val usernameTextView = TextView(this).apply {
            text = jobItem.username
            layoutParams = TableRow.LayoutParams(150, TableRow.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        val totalTextView = TextView(this).apply {
            text = jobItem.total.toString()
            layoutParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        val executedTextView = TextView(this).apply {
            text = jobItem.executed.toString()
            layoutParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        val passTextView = TextView(this).apply {
            text = jobItem.pass.toString()
            layoutParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        val failTextView = TextView(this).apply {
            text = jobItem.fail.toString()
            layoutParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        val statusTextView = TextView(this).apply {
            text = jobItem.status
            layoutParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
        }

        // Create an ImageView for the status icon
        val statusIconImageView = ImageView(this).apply {
            setImageResource(
                when (jobItem.status) {
                    "inprogress" -> R.drawable.ic_in_progress
                    "success" -> R.drawable.ic_success
                    "failed" -> R.drawable.ic_failed
                    else -> R.drawable.ic_aborted
                }
            )
            layoutParams = TableRow.LayoutParams(50, TableRow.LayoutParams.WRAP_CONTENT)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(8, 8, 8, 8)
        }

        // Add all TextViews and ImageView to the TableRow
        tableRow.addView(jobNameTextView)
        tableRow.addView(executedDateTextView)
        tableRow.addView(usernameTextView)
        tableRow.addView(totalTextView)
        tableRow.addView(executedTextView)
        tableRow.addView(passTextView)
        tableRow.addView(failTextView)
        tableRow.addView(statusTextView)
        tableRow.addView(statusIconImageView)

        // Add the TableRow to the TableLayout
        binding.tblLayout.addView(tableRow)
    }
}
