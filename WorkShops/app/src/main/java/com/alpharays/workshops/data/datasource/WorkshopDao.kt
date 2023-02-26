package com.alpharays.workshops.data.datasource

import androidx.lifecycle.LiveData
import androidx.room.*
import com.alpharays.workshops.data.entities.Workshop

@Dao
interface WorkshopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workshop: Workshop)

    @Query("SELECT * FROM workshops_table WHERE id = :workshopId")
    fun getWorkshopById(workshopId: Long): Workshop?


    @Query("Select * from workshops_table order by id ASC")
    fun getAllWorkshops(): LiveData<List<Workshop>>

}