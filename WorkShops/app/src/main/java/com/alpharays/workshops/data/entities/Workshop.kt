package com.alpharays.workshops.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workshops_table")
data class Workshop(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String
)