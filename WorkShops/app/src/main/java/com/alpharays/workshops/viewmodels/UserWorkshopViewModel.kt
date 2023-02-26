package com.alpharays.workshops.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.alpharays.workshops.data.datasource.AppDatabase
import com.alpharays.workshops.data.entities.UserWorkshop
import com.alpharays.workshops.repository.UserDetailsRepo
import com.alpharays.workshops.repository.UserWorkshopRepository
import kotlinx.coroutines.launch

class UserWorkshopViewModel(application: Application) : AndroidViewModel(application) {

    private val userWorkshopRepository: UserWorkshopRepository
    private val _userWorkshops = MutableLiveData<List<UserWorkshop>>()
    val userWorkshops: LiveData<List<UserWorkshop>>
        get() = _userWorkshops

    init {
        val dao = AppDatabase.getDatabase(application).userWorkshopDao()
        userWorkshopRepository = UserWorkshopRepository(dao)
    }

    fun insertUserWorkshop(userId: Long, workshopId: Long) = viewModelScope.launch {
        userWorkshopRepository.linkUserToWorkshop(userId, workshopId)
    }

    fun deleteUserWorkshop(userWorkshop: UserWorkshop) = viewModelScope.launch {
        userWorkshopRepository.deleteUserWorkshop(userWorkshop)
    }

    fun getWorkshopsForUser(userId: Long) = viewModelScope.launch {
        _userWorkshops.value = userWorkshopRepository.getWorkshopsForUser(userId)
    }

    fun deleteByUserIdAndWorkshopId(userId: Long, workshopId: Long) = viewModelScope.launch {
        userWorkshopRepository.deleteByUserIdAndWorkshopId(userId, workshopId)
    }

//    fun getUsersForWorkshop(workshopId: Long) = viewModelScope.launch {
//        _userWorkshops.value = userWorkshopRepository.getUsersForWorkshop(workshopId)
//    }
}