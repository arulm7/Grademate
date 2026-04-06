package com.app.grademate.ui.screens.cgpa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grademate.datastore.DataStoreManager
import com.app.grademate.datastore.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class Subject(
    val id: String = UUID.randomUUID().toString(),
    val grade: String,
    val credits: Int = 4
)

class CgpaViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val lastCgpa: StateFlow<Float> = dataStoreManager.getCGPA()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    val gradeS = _subjects.map { list -> list.count { it.grade == "S" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val gradeA = _subjects.map { list -> list.count { it.grade == "A" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val gradeB = _subjects.map { list -> list.count { it.grade == "B" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val gradeC = _subjects.map { list -> list.count { it.grade == "C" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val gradeD = _subjects.map { list -> list.count { it.grade == "D" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val gradeE = _subjects.map { list -> list.count { it.grade == "E" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _calculatedCgpa = MutableStateFlow<Float?>(null)
    val calculatedCgpa: StateFlow<Float?> = _calculatedCgpa.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val totalSubjects = _subjects.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCredits = _subjects.map { it.sumOf { s -> s.credits } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    fun addSubject(grade: String) {
        val newList = _subjects.value.toMutableList()
        newList.add(Subject(grade = grade, credits = 4))
        _subjects.value = newList
        _errorMessage.value = null
    }

    fun removeSubject(subjectId: String) {
        val newList = _subjects.value.toMutableList()
        newList.removeAll { it.id == subjectId }
        _subjects.value = newList
        _errorMessage.value = null
    }

    fun updateSubjectCredits(subjectId: String, newCredits: Int) {
        _subjects.value = _subjects.value.map {
            if (it.id == subjectId) it.copy(credits = newCredits) else it
        }
    }

    fun calculateCgpa() {
        val gradePoints = mapOf("S" to 10, "A" to 9, "B" to 8, "C" to 7, "D" to 6, "E" to 5)
        
        var totalWeightedPoints = 0f
        var totalCredits = 0f

        _subjects.value.forEach { subject ->
            val points = gradePoints[subject.grade] ?: 0
            totalWeightedPoints += points * subject.credits
            totalCredits += subject.credits
        }

        if (totalCredits > 0) {
            val cgpa = totalWeightedPoints / totalCredits
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
        _subjects.value = emptyList()
        _calculatedCgpa.value = null
        _errorMessage.value = null
    }
}
