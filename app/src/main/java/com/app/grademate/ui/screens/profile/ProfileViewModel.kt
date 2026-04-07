package com.app.grademate.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grademate.datastore.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val userName: StateFlow<String> = dataStoreManager.getUserName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val department: StateFlow<String> = dataStoreManager.getDepartment()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val isDarkMode: StateFlow<Boolean> = dataStoreManager.isDarkMode(false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.saveDarkMode(enabled)
        }
    }

    fun updateProfile(name: String, dept: String) {
        viewModelScope.launch {
            dataStoreManager.saveUser(name, dept)
        }
    }
}
