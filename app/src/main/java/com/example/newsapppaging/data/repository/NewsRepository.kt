package com.example.newsapppaging.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapppaging.api.NewsApiInterface
import com.example.newsapppaging.data.model.Article
import com.example.newsapppaging.db.SavedArticlesDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val service: NewsApiInterface,
    private val savedArticlesDao: SavedArticlesDao
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultStream(
        query: String?,
        sortBy: String?,
        country: String?,
        language: String?,
        category: String?
    ): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NewsPagingSource(
                    service,
                    query,
                    sortBy,
                    country,
                    language,
                    category
                )
            }
        ).flow

    }

    val getNewsFromDatabaseFlow: Flow<List<Article>> = savedArticlesDao.getSavedArticlesByDateLatest()

    suspend fun insertArticle(article: Article) {
        savedArticlesDao.insertAllNews(article)
    }

    suspend fun deleteArticle(article: Article) {
        savedArticlesDao.deleteNewsArticle(article.url)
    }

}