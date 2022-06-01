package com.example.newsapppaging.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ActivityComponent::class)
object CoroutineScopesModule {
    @Provides
    @ActivityScoped
    fun provideCoroutineScope(): CoroutineScope{
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}