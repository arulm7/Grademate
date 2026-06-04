package com.app.grademate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.grademate.datastore.DataStoreManager
import com.app.grademate.navigation.AppNavigation
import com.app.grademate.ui.theme.GradeMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val dataStoreManager = DataStoreManager(applicationContext)
        
        enableEdgeToEdge()
        setContent {
            val isSystemDark = isSystemInDarkTheme()
            val isDarkMode by dataStoreManager.isDarkMode(isSystemDark).collectAsState(initial = isSystemDark)

            GradeMateTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    dataStoreManager = dataStoreManager,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}