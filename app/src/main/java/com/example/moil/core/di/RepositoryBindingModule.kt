package com.example.moil.core.di

import com.example.moil.feature.auth.module.data.repository.AuthRepositoryImpl
import com.example.moil.feature.auth.module.data.repository.DefaultCurrentUserProfileStore
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import com.example.moil.feature.event.module.data.repository.EventRepositoryImpl
import com.example.moil.feature.event.module.domain.repository.EventRepository
import com.example.moil.feature.group.module.data.repository.GroupRepositoryImpl
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import com.example.moil.feature.image.module.data.repository.ImageRepositoryImpl
import com.example.moil.feature.image.module.domain.repository.ImageRepository
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

    @Binds
    abstract fun bindImageRepository(implementation: ImageRepositoryImpl): ImageRepository
}
