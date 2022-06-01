package com.example.newsapppaging.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapppaging.api.NewsApiInterface
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

const val STARTING_PAGE_INDEX = 1
const val NETWORK_PAGE_SIZE = 10

class NewsPagingSource @Inject constructor(
    private val service: NewsApiInterface,
    private val query: String?,
    private val sort: String?,
    private val country: String?,
    private val language: String?,
    private val category: String?
) :
    PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: STARTING_PAGE_INDEX
        val apiQuery = query
        return if (apiQuery != null && sort != null) {
            try {
                val response =
                    service.getSearchNewsArticles(apiQuery, sort, position, params.loadSize)
                val repos = response.articles
                val nextKey = if (repos.isEmpty()) {
                    null
                } else {
                    // initial load size = 3 * NETWORK_PAGE_SIZE
                    // ensure we're not requesting duplicating items, at the 2nd request
                     if(
                         params.loadSize == 3* NETWORK_PAGE_SIZE
                     )   {
                         position + 1
                     }
                    else {
                         position + (params.loadSize / NETWORK_PAGE_SIZE)
                     }
                }
                LoadResult.Page(
                    data = repos,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = nextKey
                )
            } catch (exception: IOException) {
                return LoadResult.Error(exception)
            } catch (exception: HttpException) {
                return LoadResult.Error(exception)
            }
        } else {
            try {
                val response =
                    service.getTopHeadlines(
                        position,
                        params.loadSize,
                        country.toString(),
                        language.toString(),
                        category.toString()
                    )
                val repos = response.articles
                val nextKey = if (repos.isEmpty()) {
                    null
                } else {
                    // initial load size = 3 * NETWORK_PAGE_SIZE
                    // ensure we're not requesting duplicating items, at the 2nd request
                    position + (params.loadSize / NETWORK_PAGE_SIZE)
                }
                LoadResult.Page(
                    data = repos,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = nextKey
                )
            } catch (exception: IOException) {
                return LoadResult.Error(exception)
            } catch (exception: HttpException) {
                return LoadResult.Error(exception)
            }
        }


    }
}