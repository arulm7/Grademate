package com.app.grademate.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grademate.datastore.DataStoreManager
import com.app.grademate.datastore.HistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val historyItems: StateFlow<List<HistoryItem>> = dataStoreManager.getHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}
