package com.alpharays.workshops.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "user_workshop_table",
    primaryKeys = ["userId", "workshopId"],
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(entity = Workshop::class, parentColumns = ["id"], childColumns = ["workshopId"])
    ]
)
data class UserWorkshop(
    val userId: Long,
    val workshopId: Long
)
