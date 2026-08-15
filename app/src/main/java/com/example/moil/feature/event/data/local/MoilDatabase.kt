package com.example.moil.feature.event.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        EventEntity::class,
        EventParticipantEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class MoilDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}
