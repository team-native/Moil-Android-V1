package com.example.moil.core.di

import com.example.moil.feature.auth.data.AuthRepositoryImpl
import com.example.moil.feature.auth.data.local.DefaultCurrentUserProfileStore
import com.example.moil.feature.auth.domain.AuthRepository
import com.example.moil.feature.auth.domain.CurrentUserProfileStore
import com.example.moil.feature.event.data.EventRepositoryImpl
import com.example.moil.feature.event.domain.EventRepository
import com.example.moil.feature.group.data.GroupRepositoryImpl
import com.example.moil.feature.group.domain.GroupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {
    @Binds
    abstract fun bindAuthRepository(implementation: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindCurrentUserProfileStore(
        implementation: DefaultCurrentUserProfileStore,
    ): CurrentUserProfileStore

    @Binds
    abstract fun bindGroupRepository(implementation: GroupRepositoryImpl): GroupRepository

    @Binds
    abstract fun bindEventRepository(implementation: EventRepositoryImpl): EventRepository
}
