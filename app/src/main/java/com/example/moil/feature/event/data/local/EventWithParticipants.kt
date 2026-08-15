package com.example.moil.feature.event.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithParticipants(
    @Embedded
    val event: EventEntity,
    @Relation(
        parentColumn = "event_id",
        entityColumn = "event_id",
    )
    val participants: List<EventParticipantEntity>,
)
