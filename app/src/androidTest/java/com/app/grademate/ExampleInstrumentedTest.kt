package com.app.grademate

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.datastore.preferences.core.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.app.grademate.datastore.dataStore
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupScreenTest {

    // Rule to launch the MainActivity and load its Compose content
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        runBlocking {
            // Clear preferences to guarantee that isFirstTime defaults to true (Setup screen opens)
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    private fun completeSetupFlow(name: String = "John Doe", department: String = "CSE") {
        // Wait for the Splash Screen delay and animations to complete and the Setup Screen to load
        composeTestRule.waitUntil(timeoutMillis = 6000) {
            composeTestRule.onAllNodesWithText("Welcome to GradeMate").fetchSemanticsNodes().isNotEmpty()
        }

        // Fill out profile details
        composeTestRule.onNodeWithText("Name (Optional)").performTextInput(name)
        composeTestRule.onNodeWithText("Department").performClick()
        composeTestRule.onNodeWithText(department).performClick()
        composeTestRule.onNodeWithText("Continue").performClick()

        // Wait for the Home screen to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Attendance Tracker").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testSetupScreenFlow() {
        completeSetupFlow("John Doe", "CSE")

        // Verify we successfully navigate to the Home screen
        composeTestRule.onNodeWithText("Attendance Tracker").assertExists()
    }

    @Test
    fun testSetupScreenValidationFailure() {
        // Wait for the Splash Screen delay and animations to complete and the Setup Screen to load
        composeTestRule.waitUntil(timeoutMillis = 6000) {
            composeTestRule.onAllNodesWithText("Welcome to GradeMate").fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Verify that the welcome texts are shown
        composeTestRule.onNodeWithText("Welcome to GradeMate").assertExists()

        // 2. Locate the Optional Name text field and type a name
        composeTestRule.onNodeWithText("Name (Optional)").performTextInput("Jane Doe")

        // 3. Do not select a department. Verify that the Continue button remains disabled
        composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()

        // 4. Verify that the Home screen (Attendance Tracker) does not exist since we haven't continued
        composeTestRule.onNodeWithText("Attendance Tracker").assertDoesNotExist()
    }

    @Test
    fun testCgpaCalculatorFlow() {
        // 1. Complete setup flow to reach Home screen
        completeSetupFlow("Alex", "CSE")

        // 2. Navigate to CGPA Calculator screen
        composeTestRule.onNodeWithText("CGPA Calculator").performClick()

        // Verify we are on CGPA screen
        composeTestRule.onNodeWithText("CGPA Calculator").assertExists()
        composeTestRule.onNodeWithText("No subjects added yet").assertExists()

        // 3. Add an "S" grade subject (which is worth 10 points)
        composeTestRule.onNodeWithText("S").performClick()

        // Verify that the empty state text is gone
        composeTestRule.onNodeWithText("No subjects added yet").assertDoesNotExist()

        // 4. Click Calculate
        composeTestRule.onNodeWithText("Calculate").performClick()

        // 5. Verify estimated CGPA is shown and it is 10.00 (since it's one S grade subject with default credits)
        composeTestRule.onNodeWithText("Estimated CGPA").assertExists()
        composeTestRule.onNodeWithText("10.00").assertExists()
        composeTestRule.onNodeWithText("Outstanding").assertExists()

        // 6. Test Reset Button
        composeTestRule.onNodeWithText("Reset").performClick()
        composeTestRule.onNodeWithText("No subjects added yet").assertExists()
        composeTestRule.onNodeWithText("Estimated CGPA").assertDoesNotExist()
    }

    @Test
    fun testAttendanceTrackerFlow() {
        // 1. Complete setup flow to reach Home screen
        completeSetupFlow("Alex", "CSE")

        // 2. Navigate to Attendance screen
        composeTestRule.onNodeWithText("Attendance Tracker").performClick()

        // Verify we are on Attendance screen
        composeTestRule.onNodeWithText("Attendance Tracker").assertExists()

        // 3. Set Total Classes to 5 by clicking the add button 5 times
        for (i in 1..5) {
            composeTestRule.onNodeWithTag("Total Classes_add").performClick()
        }
        composeTestRule.onNodeWithTag("Total Classes_value").assertExists()

        // 4. Set Attended Classes to 4 by clicking the add button 4 times
        for (i in 1..4) {
            composeTestRule.onNodeWithTag("Attended_add").performClick()
        }
        composeTestRule.onNodeWithTag("Attended_value").assertExists()

        // 5. Verify attendance percentage is 80% and the status is "Safe"
        composeTestRule.onNodeWithText("80%").assertExists()
        composeTestRule.onNodeWithText("Safe").assertExists()
        composeTestRule.onNodeWithText("Great job! Your attendance is solid.").assertExists()

        // 6. Save Progress
        composeTestRule.onNodeWithText("Save Progress").performClick()
    }

    @Test
    fun testProfileScreenFlow() {
        // 1. Complete setup flow to reach Home screen
        completeSetupFlow("Alex", "CSE")

        // 2. Navigate to Profile tab
        composeTestRule.onNodeWithContentDescription("Profile").performClick()

        // Verify current profile details
        composeTestRule.onNodeWithText("Alex").assertExists()
        composeTestRule.onNodeWithText("Department: CSE").assertExists()

        // 3. Edit Profile
        composeTestRule.onNodeWithText("Edit Profile").performClick()

        // Modify name and department
        composeTestRule.onNodeWithText("Name").performTextReplacement("Bob")
        composeTestRule.onNodeWithText("Department").performClick()
        composeTestRule.onNodeWithText("IT").performClick()

        // 4. Save profile
        composeTestRule.onNodeWithText("Save").performClick()

        // Verify updated profile details
        composeTestRule.onNodeWithText("Bob").assertExists()
        composeTestRule.onNodeWithText("Department: IT").assertExists()
    }

    @Test
    fun testHistoryScreenFlow() {
        // 1. Complete setup flow to reach Home screen
        completeSetupFlow("Alex", "CSE")

        // 2. Add CGPA to history
        composeTestRule.onNodeWithText("CGPA Calculator").performClick()
        composeTestRule.onNodeWithText("S").performClick()
        composeTestRule.onNodeWithText("Calculate").performClick()
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // 3. Add Attendance to history
        composeTestRule.onNodeWithText("Attendance Tracker").performClick()
        composeTestRule.onNodeWithTag("Total Classes_add").performClick()
        composeTestRule.onNodeWithTag("Attended_add").performClick()
        composeTestRule.onNodeWithText("Save Progress").performClick()
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // 4. Navigate to History tab
        composeTestRule.onNodeWithContentDescription("History").performClick()

        // 5. Verify the saved entries exist in the history screen list
        composeTestRule.onNodeWithText("CGPA Calculation").assertExists()
        composeTestRule.onNodeWithText("Attendance Tracked").assertExists()
    }

    @Test
    fun testIntentionalFailureForReport() {
        // 1. Complete setup flow
        completeSetupFlow("Alex", "CSE")

        // 2. Navigate to Profile
        composeTestRule.onNodeWithContentDescription("Profile").performClick()

        // 3. INTENTIONAL FAILURE: Assert that a non-existent name is displayed to trigger report failure
        composeTestRule.onNodeWithText("Non Existent User").assertExists()
    }
}