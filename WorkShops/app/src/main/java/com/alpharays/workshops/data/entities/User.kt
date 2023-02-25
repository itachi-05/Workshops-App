package com.alpharays.workshops.data.entities

import androidx.room.*


@Entity(
    tableName = "user_table",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String
)