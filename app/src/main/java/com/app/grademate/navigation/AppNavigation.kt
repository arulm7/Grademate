package com.app.grademate.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.grademate.datastore.DataStoreManager
import com.app.grademate.ui.components.AppTopBarWrapper
import com.app.grademate.ui.components.FloatingBottomBar
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavigation(
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {

        // 🔹 Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                dataStoreManager = dataStoreManager
            )
        }

        // 🔹 Setup
        composable(Screen.Setup.route) {
            val setupViewModel = remember { SetupViewModel(dataStoreManager) }
            SetupScreen(
                navController = navController,
                viewModel = setupViewModel
            )
        }

        // 🔥 MAIN SCREEN (Pager + Bottom Nav)
        composable(Screen.Home.route) {

            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { 3 }
            )

            val coroutineScope = rememberCoroutineScope()

            val homeViewModel = remember { HomeViewModel(dataStoreManager) }
            val historyViewModel = remember { HistoryViewModel(dataStoreManager) }
            val profileViewModel = remember { ProfileViewModel(dataStoreManager) }

            val currentTitle = when (pagerState.targetPage) {
                0 -> "GradeMate"
                1 -> "History"
                2 -> "Profile"
                else -> ""
            }

            Scaffold(
                topBar = {
                    AppTopBarWrapper(title = currentTitle)
                },
                bottomBar = {
                    FloatingBottomBar(
                        selectedIndex = pagerState.currentPage,
                        pagerOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction,
                        onItemSelected = { page ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page)
                            }
                        },
                        onDrag = { delta ->
                            coroutineScope.launch {
                                // Apply a multiplier to make dragging more responsive
                                // Pager width is much larger than the bottom bar
                                val multiplier = 3f 
                                pagerState.scroll {
                                    scrollBy(delta * multiplier)
                                }
                            }
                        },
                        onDragStopped = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.targetPage)
                            }
                        }
                    )
                }
            ) { innerPadding ->

                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) { page ->

                    when (page) {
                        0 -> HomeScreen(
                            navController = navController,
                            viewModel = homeViewModel
                        )

                        1 -> HistoryScreen(
                            navController = navController,
                            viewModel = historyViewModel
                        )

                        2 -> ProfileScreen(
                            navController = navController,
                            viewModel = profileViewModel
                        )
                    }
                }
            }
        }

        // 🔹 CGPA Screen
        composable(Screen.Cgpa.route) {
            val cgpaViewModel = remember { CgpaViewModel(dataStoreManager) }
            CgpaScreen(
                navController = navController,
                viewModel = cgpaViewModel
            )
        }

        // 🔹 Attendance Screen
        composable(Screen.Attendance.route) {
            val attendanceViewModel = remember { AttendanceViewModel(dataStoreManager) }
            AttendanceScreen(
                navController = navController,
                viewModel = attendanceViewModel
            )
        }
    }
}