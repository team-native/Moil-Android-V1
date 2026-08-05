package com.example.moil.core.di

import android.content.Context
import androidx.room.Room
import com.example.moil.feature.event.data.local.EventDao
import com.example.moil.feature.event.data.local.MoilDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMoilDatabase(
        @ApplicationContext context: Context,
    ): MoilDatabase = Room.databaseBuilder(
        context,
        MoilDatabase::class.java,
        "moil.db",
    ).build()

    @Provides
    fun provideEventDao(database: MoilDatabase): EventDao = database.eventDao()
}
