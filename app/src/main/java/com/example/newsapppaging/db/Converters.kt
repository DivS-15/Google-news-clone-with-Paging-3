package com.example.newsapppaging.db

import androidx.room.TypeConverter
import com.example.newsapppaging.data.Source

class Converters {
    @TypeConverter
    fun fromSource(value: String?): Source?{
        return value?.let { Source(it) }
    }

    @TypeConverter
    fun sourceToString(source: Source?): String {
        return source?.name.toString()
    }
}