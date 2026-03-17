package com.app.grademate.ui.screens.attendance

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
import kotlin.math.ceil

class AttendanceViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val savedAttendance: StateFlow<Float> = dataStoreManager.getAttendance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    private val _totalClasses = MutableStateFlow(0)
    val totalClasses: StateFlow<Int> = _totalClasses.asStateFlow()

    private val _attendedClasses = MutableStateFlow(0)
    val attendedClasses: StateFlow<Int> = _attendedClasses.asStateFlow()

    private val _futureClasses = MutableStateFlow(0)
    val futureClasses: StateFlow<Int> = _futureClasses.asStateFlow()

    private val _attendancePercentage = MutableStateFlow<Float?>(null)
    val attendancePercentage: StateFlow<Float?> = _attendancePercentage.asStateFlow()

    private val _classesNeeded = MutableStateFlow(0)
    val classesNeeded: StateFlow<Int> = _classesNeeded.asStateFlow()

    private val _predictionIfAttend = MutableStateFlow<Float?>(null)
    val predictionIfAttend: StateFlow<Float?> = _predictionIfAttend.asStateFlow()

    private val _predictionIfMiss = MutableStateFlow<Float?>(null)
    val predictionIfMiss: StateFlow<Float?> = _predictionIfMiss.asStateFlow()

    fun updateTotalClasses(value: Int) {
        _totalClasses.value = value
        if (_attendedClasses.value > value) {
            _attendedClasses.value = value
        }
        calculatePredictions()
    }

    fun updateAttendedClasses(value: Int) {
        // cannot attend more classes than total
        _attendedClasses.value = value.coerceAtMost(_totalClasses.value)
        calculatePredictions()
    }

    fun updateFutureClasses(value: Int) {
        _futureClasses.value = value
        calculatePredictions()
    }

    fun saveHistory() {
        val percentage = _attendancePercentage.value ?: return
        viewModelScope.launch {
            dataStoreManager.saveAttendance(percentage)
            dataStoreManager.saveHistory(
                HistoryItem(
                    type = "ATTENDANCE",
                    value = percentage,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private fun calculatePredictions() {
        val total = _totalClasses.value
        val attended = _attendedClasses.value
        val future = _futureClasses.value

        if (total > 0 && attended >= 0 && attended <= total) {
            val percentage = (attended.toFloat() / total.toFloat()) * 100f
            _attendancePercentage.value = percentage

            if (percentage < 80f) {
                val required = ceil((0.8 * total - attended) / (1 - 0.8)).toInt()
                _classesNeeded.value = if (required > 0) required else 0
            } else {
                _classesNeeded.value = 0
            }

            if (future > 0) {
                val ifAttend = ((attended + future).toFloat() / (total + future).toFloat()) * 100f
                val ifMiss = (attended.toFloat() / (total + future).toFloat()) * 100f

                _predictionIfAttend.value = ifAttend
                _predictionIfMiss.value = ifMiss
            } else {
                _predictionIfAttend.value = null
                _predictionIfMiss.value = null
            }
        } else {
            _attendancePercentage.value = null
            _classesNeeded.value = 0
            _predictionIfAttend.value = null
            _predictionIfMiss.value = null
        }
    }
}
