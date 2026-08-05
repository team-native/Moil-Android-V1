package com.example.moil.feature.event.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "events",
    indices = [
        Index(value = ["group_id", "date"]),
    ],
)
data class EventEntity(
    @androidx.room.PrimaryKey
    @ColumnInfo(name = "event_id")
    val eventId: Long,
    @ColumnInfo(name = "group_id")
    val groupId: Long,
    val title: String,
    val date: String,
    @ColumnInfo(name = "is_all_day")
    val isAllDay: Boolean,
    @ColumnInfo(name = "start_time")
    val startTime: String?,
    @ColumnInfo(name = "end_time")
    val endTime: String?,
    val location: String?,
)

@Entity(
    tableName = "event_participants",
    primaryKeys = ["event_id", "user_id"],
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["event_id"],
            childColumns = ["event_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["event_id"]),
    ],
)
data class EventParticipantEntity(
    @ColumnInfo(name = "event_id")
    val eventId: Long,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    val nickname: String,
    val color: String,
)
