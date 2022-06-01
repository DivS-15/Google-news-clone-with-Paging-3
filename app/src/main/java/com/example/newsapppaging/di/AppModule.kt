package com.example.newsapppaging.di

import android.content.Context
import androidx.room.Room
import com.example.newsapppaging.api.NewsApiInterface
import com.example.newsapppaging.data.HeadlinesCategory
import com.example.newsapppaging.db.SavedArticlesDao
import com.example.newsapppaging.db.SavedArticlesDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(NewsApiInterface.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApiInterface =
        retrofit.create(NewsApiInterface::class.java)

    @Provides
    fun provideSavedArticlesDao(database: SavedArticlesDatabase): SavedArticlesDao {
        return database.savedArticlesDao()
    }


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            SavedArticlesDatabase::class.java,
            "savedNewsArticles.db"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideHeadlinesPreferences(): HeadlinesCategory{
        return HeadlinesCategory(
            "business",
        "entertainment",
        "general",
        "health",
        "science",
        "sports",
        "technology"
        )
    }
}
