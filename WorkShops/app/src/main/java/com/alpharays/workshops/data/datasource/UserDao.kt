package com.alpharays.workshops.data.datasource

import androidx.lifecycle.LiveData
import androidx.room.*
import com.alpharays.workshops.data.entities.User
import com.alpharays.workshops.data.entities.Workshop

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<User>>
}
