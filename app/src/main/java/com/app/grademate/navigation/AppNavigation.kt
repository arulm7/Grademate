package com.app.grademate.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.grademate.ui.components.FloatingBottomBar
import com.app.grademate.datastore.DataStoreManager
import com.app.grademate.ui.screens.attendance.AttendanceScreen
import com.app.grademate.ui.screens.attendance.AttendanceViewModel
import com.app.grademate.ui.screens.cgpa.CgpaScreen
import com.app.grademate.ui.screens.cgpa.CgpaViewModel
import com.app.grademate.ui.screens.history.HistoryScreen
import com.app.grademate.ui.screens.history.HistoryViewModel
import com.app.grademate.ui.screens.home.HomeScreen
import com.app.grademate.ui.screens.home.HomeViewModel
import com.app.grademate.ui.screens.profile.ProfileScreen
import com.app.grademate.ui.screens.profile.ProfileViewModel
import com.app.grademate.ui.screens.setup.SetupScreen
import com.app.grademate.ui.screens.setup.SetupViewModel
import com.app.grademate.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation(
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var isScrollingDown by remember { mutableStateOf(false) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -10) {
                    isScrollingDown = true
                } else if (available.y > 10) {
                    isScrollingDown = false
                }
                return Offset.Zero
            }
        }
    }

    val bottomBarRoutes = listOf(Screen.Home.route, Screen.History.route, Screen.Profile.route)
    val isBottomBarVisible = currentRoute in bottomBarRoutes && !isScrollingDown

    Scaffold(
        modifier = modifier.nestedScroll(nestedScrollConnection),
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                if (currentRoute in bottomBarRoutes) {
                    FloatingBottomBar(
                        currentRoute = currentRoute ?: Screen.Home.route,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    navController = navController,
                    dataStoreManager = dataStoreManager
                )
            }

            composable(Screen.Setup.route) {
                val setupViewModel = SetupViewModel(dataStoreManager)
                SetupScreen(
                    navController = navController,
                    viewModel = setupViewModel
                )
            }

            composable(Screen.Home.route) {
                val homeViewModel = HomeViewModel(dataStoreManager)
                HomeScreen(
                    navController = navController,
                    viewModel = homeViewModel
                )
            }

            composable(Screen.History.route) {
                val historyViewModel = HistoryViewModel(dataStoreManager)
                HistoryScreen(
                    navController = navController,
                    viewModel = historyViewModel
                )
            }

            composable(Screen.Cgpa.route) {
                val cgpaViewModel = CgpaViewModel(dataStoreManager)
                CgpaScreen(
                    navController = navController,
                    viewModel = cgpaViewModel
                )
            }

            composable(Screen.Attendance.route) {
                val attendanceViewModel = AttendanceViewModel(dataStoreManager)
                AttendanceScreen(
                    navController = navController,
                    viewModel = attendanceViewModel
                )
            }

            composable(Screen.Profile.route) {
                val profileViewModel = ProfileViewModel(dataStoreManager)
                ProfileScreen(
                    navController = navController,
                    viewModel = profileViewModel
                )
            }
        }
    }
}
