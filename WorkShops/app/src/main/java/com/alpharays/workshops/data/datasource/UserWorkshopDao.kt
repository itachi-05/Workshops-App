package com.alpharays.workshops.data.datasource

import androidx.room.*
import com.alpharays.workshops.data.entities.UserWorkshop

@Dao
interface UserWorkshopDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userWorkshop: UserWorkshop)

    @Delete
    suspend fun delete(userWorkshop: UserWorkshop)

    @Query("SELECT * FROM user_workshop_table WHERE userId = :userId")
    suspend fun getWorkshopsForUser(userId: Long): List<UserWorkshop>

    @Query("SELECT * FROM user_workshop_table WHERE workshopId = :workshopId")
    suspend fun getUsersForWorkshop(workshopId: Long): List<UserWorkshop>
}
