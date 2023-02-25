package com.alpharays.workshops.repository

import androidx.lifecycle.LiveData
import com.alpharays.workshops.data.datasource.UserDao
import com.alpharays.workshops.data.entities.User

class UserDetailsRepo(private val userDao: UserDao) {
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User): Boolean {
        val existingUser = userDao.getUserByEmail(user.email)
        return if (existingUser == null) {
            userDao.insert(user)
            true
        } else {
            false
        }
    }

    suspend fun check(email: String): Boolean {
        val existingUser = userDao.getUserByEmail(email)
        return existingUser == null
    }
}