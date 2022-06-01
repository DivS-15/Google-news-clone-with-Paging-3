package com.example.newsapppaging.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "news_articles")
@Parcelize
data class Article(
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source,
    val title: String?,
    @PrimaryKey
    val url: String,
    val urlToImage: String?,
    val created: Long? = System.currentTimeMillis()
):Parcelable