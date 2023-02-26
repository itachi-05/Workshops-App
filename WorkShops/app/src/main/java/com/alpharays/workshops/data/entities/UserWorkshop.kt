package com.alpharays.workshops.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_workshop_table",
    indices = [Index(value = ["userId", "workshopId"], unique = true)],
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(entity = Workshop::class, parentColumns = ["id"], childColumns = ["workshopId"])
    ]
)
data class UserWorkshop(
    val userId: Long,
    val workshopId: Long,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)
