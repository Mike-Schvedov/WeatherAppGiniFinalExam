package com.mikeschvedov.weatherappginifinalexam.di

import com.mikeschvedov.weatherappginifinalexam.data.database.repository.Repository
import com.mikeschvedov.weatherappginifinalexam.data.database.repository.RepositoryIMPL
import com.mikeschvedov.weatherappginifinalexam.data.mediator.ContentMediator
import com.mikeschvedov.weatherappginifinalexam.data.mediator.ContentMediatorIMPL
import com.mikeschvedov.weatherappginifinalexam.data.network.ApiManager
import com.mikeschvedov.weatherappginifinalexam.data.network.ApiManagerIMPL
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {

    @Binds
    @Singleton
    abstract fun bindRepository(repository: RepositoryIMPL): Repository

    @Binds
    @Singleton
    abstract fun bindRemoteApi(apiManager: ApiManagerIMPL): ApiManager

    @Binds
    @Singleton
    abstract fun bindContentMediator(contentMediator: ContentMediatorIMPL): ContentMediator
}
