package com.example.newsapppaging.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapppaging.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArticlesDao {
    @Query(
        "SELECT * FROM news_articles ORDER BY created DESC"
    )
    fun getSavedArticlesByDateLatest(): Flow<List<Article>>

    @Query("SELECT * FROM news_articles WHERE title LIKE :queryString OR description LIKE :queryString ")
    fun getSavedArticlesByName(queryString: String): Flow<List<Article>>

    @Query("SELECT * FROM news_articles ORDER BY title Asc ")
    fun getSavedArticlesNameAsc(): Flow<List<Article>>

    @Query("SELECT * FROM news_articles ORDER BY source Asc ")
    fun getSavedArticlesSourceAsc(): Flow<List<Article>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNews(article: Article)

    @Query("DELETE FROM news_articles WHERE url LIKE :newsUrl")
    suspend fun deleteNewsArticle(newsUrl: String)
}