package com.example.moil.core.di

import com.example.moil.core.network.DefaultSessionManager
import com.example.moil.core.network.SessionManager
import com.example.moil.feature.auth.data.remote.AuthRemoteDataSource
import com.example.moil.feature.auth.data.remote.AuthRemoteDataSourceImpl
import com.example.moil.feature.event.module.data.remote.EventRemoteDataSource
import com.example.moil.feature.event.module.data.remote.EventRemoteDataSourceImpl
import com.example.moil.feature.group.data.remote.GroupRemoteDataSource
import com.example.moil.feature.group.data.remote.GroupRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindingModule {
    @Binds
    abstract fun bindSessionManager(implementation: DefaultSessionManager): SessionManager

    @Binds
    abstract fun bindAuthRemoteDataSource(implementation: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds
    abstract fun bindGroupRemoteDataSource(implementation: GroupRemoteDataSourceImpl): GroupRemoteDataSource

    @Binds
    abstract fun bindEventRemoteDataSource(implementation: EventRemoteDataSourceImpl): EventRemoteDataSource
}
