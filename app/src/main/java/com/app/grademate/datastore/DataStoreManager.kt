package com.app.grademate.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "grademate_prefs")

data class HistoryItem(
    val type: String, // "CGPA" or "ATTENDANCE"
    val value: Float,
    val timestamp: Long
)

class DataStoreManager(private val context: Context) {

    private val gson = Gson()

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val DEPARTMENT = stringPreferencesKey("department")
        val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
        val LAST_CGPA = floatPreferencesKey("last_cgpa")
        val ATTENDANCE = floatPreferencesKey("attendance")
        val HISTORY = stringPreferencesKey("history")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }

    fun isDarkMode(systemDefault: Boolean): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: systemDefault
    }

    suspend fun saveUser(name: String, department: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[DEPARTMENT] = department
            preferences[IS_FIRST_TIME] = false
        }
    }

    fun getUserName(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }

    fun getDepartment(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEPARTMENT] ?: ""
    }

    fun isFirstTime(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_FIRST_TIME] ?: true
    }

    suspend fun saveCGPA(cgpa: Float) {
        context.dataStore.edit { preferences ->
            preferences[LAST_CGPA] = cgpa
        }
    }

    fun getCGPA(): Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[LAST_CGPA] ?: 0f
    }
    
    suspend fun saveAttendance(attendancePct: Float) {
        context.dataStore.edit { preferences ->
            preferences[ATTENDANCE] = attendancePct
        }
    }

    fun getAttendance(): Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[ATTENDANCE] ?: 0f
    }

    suspend fun saveHistory(item: HistoryItem) {
        context.dataStore.edit { preferences ->
            val jsonHistory = preferences[HISTORY] ?: "[]"
            val type = object : TypeToken<List<HistoryItem>>() {}.type
            val historyList: MutableList<HistoryItem> = gson.fromJson(jsonHistory, type)
            historyList.add(0, item) // prepend new item
            preferences[HISTORY] = gson.toJson(historyList)
        }
    }

    suspend fun deleteHistoryItem(item: HistoryItem) {
        context.dataStore.edit { preferences ->
            val jsonHistory = preferences[HISTORY] ?: "[]"
            val type = object : TypeToken<List<HistoryItem>>() {}.type
            val historyList: MutableList<HistoryItem> = gson.fromJson(jsonHistory, type)
            historyList.removeAll { it.timestamp == item.timestamp }
            preferences[HISTORY] = gson.toJson(historyList)
        }
    }

    fun getHistory(): Flow<List<HistoryItem>> = context.dataStore.data.map { preferences ->
        val jsonHistory = preferences[HISTORY] ?: "[]"
        val type = object : TypeToken<List<HistoryItem>>() {}.type
        gson.fromJson(jsonHistory, type)
    }
}
