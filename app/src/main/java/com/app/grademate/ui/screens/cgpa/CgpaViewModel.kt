package com.app.grademate.ui.screens.cgpa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grademate.datastore.DataStoreManager
import com.app.grademate.datastore.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CgpaViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val lastCgpa: StateFlow<Float> = dataStoreManager.getCGPA()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // Grade counter values
    private val _gradeS = MutableStateFlow(0)
    val gradeS: StateFlow<Int> = _gradeS.asStateFlow()

    private val _gradeA = MutableStateFlow(0)
    val gradeA: StateFlow<Int> = _gradeA.asStateFlow()

    private val _gradeB = MutableStateFlow(0)
    val gradeB: StateFlow<Int> = _gradeB.asStateFlow()

    private val _gradeC = MutableStateFlow(0)
    val gradeC: StateFlow<Int> = _gradeC.asStateFlow()

    private val _gradeD = MutableStateFlow(0)
    val gradeD: StateFlow<Int> = _gradeD.asStateFlow()

    private val _gradeE = MutableStateFlow(0)
    val gradeE: StateFlow<Int> = _gradeE.asStateFlow()

    private val _calculatedCgpa = MutableStateFlow<Float?>(null)
    val calculatedCgpa: StateFlow<Float?> = _calculatedCgpa.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _totalSubjects = MutableStateFlow(0)
    val totalSubjects: StateFlow<Int> = _totalSubjects.asStateFlow()


    fun updateGradeCount(grade: String, change: Int) {
        val currentValue = when (grade) {
            "S" -> _gradeS.value
            "A" -> _gradeA.value
            "B" -> _gradeB.value
            "C" -> _gradeC.value
            "D" -> _gradeD.value
            "E" -> _gradeE.value
            else -> 0
        }

        val newValue = (currentValue + change).coerceAtLeast(0)

        when (grade) {
            "S" -> _gradeS.value = newValue
            "A" -> _gradeA.value = newValue
            "B" -> _gradeB.value = newValue
            "C" -> _gradeC.value = newValue
            "D" -> _gradeD.value = newValue
            "E" -> _gradeE.value = newValue
        }

        updateTotalSubjects()
        _errorMessage.value = null // clear error when modifying values
    }

    private fun updateTotalSubjects() {
        _totalSubjects.value = _gradeS.value + _gradeA.value + _gradeB.value +
                _gradeC.value + _gradeD.value + _gradeE.value
    }

    fun calculateCgpa() {
        val totalPoints = (_gradeS.value * 10) + (_gradeA.value * 9) + (_gradeB.value * 8) +
                (_gradeC.value * 7) + (_gradeD.value * 6) + (_gradeE.value * 5)

        val totalSubs = _totalSubjects.value

        if (totalSubs > 0) {
            val cgpa = totalPoints.toFloat() / totalSubs
            _calculatedCgpa.value = cgpa
            _errorMessage.value = null

            viewModelScope.launch {
                dataStoreManager.saveCGPA(cgpa)
                dataStoreManager.saveHistory(
                    HistoryItem(
                        type = "CGPA",
                        value = cgpa,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        } else {
            _calculatedCgpa.value = null
            _errorMessage.value = "Add at least one subject to calculate"
        }
    }
    
    fun reset() {
        _gradeS.value = 0
        _gradeA.value = 0
        _gradeB.value = 0
        _gradeC.value = 0
        _gradeD.value = 0
        _gradeE.value = 0
        updateTotalSubjects()
        _calculatedCgpa.value = null
        _errorMessage.value = null
    }
}
