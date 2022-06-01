package com.example.newsapppaging.utils

import java.text.SimpleDateFormat
import java.util.*

fun getDateTimeDifference(before: String): DateDifference {

    val parser =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()) // the initial pattern

    // Get the current time (in millis)
    val now = Date().time
    // Put the time (in millis) in our formatter
    val formattedNow = parser.format(now)

    //val before = "2022-02-11T19:22:41Z"

    // Create a parser and formatter with its pattern respectively


    val fromParse = parser.parse(before)!!
    val toParse = parser.parse(formattedNow)!!

    var diff = fromParse.time - toParse.time

    // Validation
    if(diff < 0) diff *= -1

    // Because the difference is in the form of milliseconds, we should divide it first.
    val days = diff / (24 * 60 * 60 * 1000)
    // Get the remainder
    diff %= (24 * 60 * 60 * 1000)

    // Do this again as necessary.
    val hours = diff / (60 * 60 * 1000)
    diff %= (60 * 60 * 1000)
    val minutes = diff / (60 * 1000)
    diff %= (60 * 1000)
    val seconds = diff / 1000


    return DateDifference(
        days = days,
        hours = hours,
        minutes,
        seconds
    )

}


data class DateDifference(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
)