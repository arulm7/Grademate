package com.app.grademate.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Setup : Screen("setup")
    object Home : Screen("home")
    object History : Screen("history")
    object Cgpa : Screen("cgpa")
    object Attendance : Screen("attendance")
    object Profile : Screen("profile")
}
