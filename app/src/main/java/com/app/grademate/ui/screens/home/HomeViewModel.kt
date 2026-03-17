package com.app.grademate.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grademate.datastore.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(dataStoreManager: DataStoreManager) : ViewModel() {
    val userName: StateFlow<String> = dataStoreManager.getUserName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
}
