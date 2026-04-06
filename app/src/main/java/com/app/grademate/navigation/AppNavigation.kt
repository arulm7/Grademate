package com.app.grademate.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.grademate.datastore.DataStoreManager
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

            Scaffold(
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
                            pagerState.dispatchRawDelta(delta)
                        },
                        onDragStopped = {
                            coroutineScope.launch {
                                val offset = pagerState.currentPageOffsetFraction
                                val targetPage = when {
                                    offset > 0.2f -> pagerState.currentPage + 1
                                    offset < -0.2f -> pagerState.currentPage - 1
                                    else -> pagerState.currentPage
                                }
                                pagerState.animateScrollToPage(targetPage.coerceIn(0, 2))
                            }
                        }
                    )
                }
            ) { innerPadding ->

                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false,
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