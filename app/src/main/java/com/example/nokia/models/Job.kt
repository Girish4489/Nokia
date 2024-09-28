package com.example.nokia.models

data class Job(
    var testName: String = "",
    var jobName: String = ""
)

data class JobItem(
    val jobName: String,
    val execDate: Long,
    val username: String,
    val total: Int,
    val executed: Int,
    val pass: Int,
    val fail: Int,
    val status: String
)

