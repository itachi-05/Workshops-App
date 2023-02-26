package com.alpharays.workshops.repository

import com.alpharays.workshops.data.datasource.UserWorkshopDao
import com.alpharays.workshops.data.entities.UserWorkshop

class UserWorkshopRepository(private val userWorkshopDao: UserWorkshopDao) {

    suspend fun linkUserToWorkshop(userId: Long, workshopId: Long) {
        val userWorkshop = UserWorkshop(userId, workshopId)
        userWorkshopDao.insert(userWorkshop)
    }

    suspend fun deleteUserWorkshop(userWorkshop: UserWorkshop) {
        userWorkshopDao.delete(userWorkshop)
    }

    suspend fun getWorkshopsForUser(userId: Long): List<UserWorkshop> {
        return userWorkshopDao.getWorkshopsForUser(userId)
    }

    suspend fun deleteByUserIdAndWorkshopId(userId: Long, workshopId: Long) {
        return userWorkshopDao.deleteByUserIdAndWorkshopId(userId, workshopId)
    }

//    suspend fun getUsersForWorkshop(workshopId: Long): List<UserWorkshop> {
//        return userWorkshopDao.getUsersForWorkshop(workshopId)
//    }

}

