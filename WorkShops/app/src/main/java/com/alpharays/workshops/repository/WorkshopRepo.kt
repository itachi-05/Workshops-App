package com.alpharays.workshops.repository

import androidx.lifecycle.LiveData
import com.alpharays.workshops.data.datasource.WorkshopDao
import com.alpharays.workshops.data.entities.Workshop

class WorkshopRepo(private val workshopDao: WorkshopDao) {
    val allWorkshops: LiveData<List<Workshop>> = workshopDao.getAllWorkshops()

    suspend fun getWorkshopById(workshopId: Long): Workshop? = workshopDao.getWorkshopById(workshopId)

    suspend fun insert(workshop: Workshop) = workshopDao.insert(workshop)

}