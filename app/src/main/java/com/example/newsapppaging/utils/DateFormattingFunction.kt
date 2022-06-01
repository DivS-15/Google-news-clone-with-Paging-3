package com.example.newsapppaging.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DateFormattingFunction"
fun getSimpleDate(date: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    val result = formatter.format(parser.parse(date)!!)
    Log.d(TAG, result)

    return result
}