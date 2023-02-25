package com.alpharays.workshops.viewmodels

import android.app.Application
import android.view.View
import android.widget.PopupWindow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alpharays.workshops.data.datasource.AppDatabase
import com.alpharays.workshops.data.entities.User
import com.alpharays.workshops.data.entities.Workshop
import com.alpharays.workshops.repository.UserDetailsRepo
import com.alpharays.workshops.repository.WorkshopRepo
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: UserDetailsRepo
    private val response = MutableLiveData<Boolean>()
    val statusResponse: LiveData<Boolean> get() = response
    val allUsers: LiveData<List<User>>
    private val response1 = MutableLiveData<Boolean>()
    val statusResponse1: LiveData<Boolean> get() = response1

    init {
        val dao = AppDatabase.getDatabase(application).userDao()
        repo = UserDetailsRepo(dao)
        allUsers = repo.allUsers
    }

    fun insertUser(user: User) =
        viewModelScope.launch(Dispatchers.IO) {
            val success = repo.insert(user)
            if (success) {
                response.postValue(true)
            } else {
                response.postValue(false)
            }
        }

    fun checkUser(email: String) = viewModelScope.launch(Dispatchers.IO) {
        val success = repo.check(email)
        if (success) response1.postValue(true)
        else response1.postValue(false)
    }

}