package com.example.newsapppaging.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapppaging.data.model.Article

@Database(entities = [Article::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SavedArticlesDatabase : RoomDatabase() {
    abstract fun savedArticlesDao(): SavedArticlesDao
}