package com.app.grademate.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grademate.datastore.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetupViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _department = MutableStateFlow("")
    val department: StateFlow<String> = _department.asStateFlow()

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateDepartment(newDept: String) {
        _department.value = newDept
    }

    fun saveSetupData(onComplete: () -> Unit) {
        viewModelScope.launch {
            dataStoreManager.saveUser(_name.value, _department.value)
            onComplete()
        }
    }
}
