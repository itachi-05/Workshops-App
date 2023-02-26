package com.alpharays.workshops.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.alpharays.workshops.data.datasource.AppDatabase
import com.alpharays.workshops.data.entities.Workshop
import com.alpharays.workshops.repository.WorkshopRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkShopsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: WorkshopRepo
    val allWorkshops: LiveData<List<Workshop>>

    init {
        val dao = AppDatabase.getDatabase(application).workshopDao()
        repo = WorkshopRepo(dao)
        allWorkshops = repo.allWorkshops
    }

    fun insertWorkshop(workshop: Workshop) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(workshop)
    }

    suspend fun getWorkshopById(workshopId: Long): Workshop? = repo.getWorkshopById(workshopId)

}