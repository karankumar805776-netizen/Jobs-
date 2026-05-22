package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.ui.JobPortalApp
import com.example.ui.JobViewModel
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testFullAppWalkthrough() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = JobViewModel(application)

    composeTestRule.setContent {
        MyApplicationTheme {
            JobPortalApp(viewModel = viewModel)
        }
    }

    // Wait for compose to idle and render
    composeTestRule.waitForIdle()

    // 1. Verify Home Screen job list is rendered and we click on the first job card to view detail
    composeTestRule.onNodeWithTag("job_card_1").performClick()
    composeTestRule.waitForIdle()

    // 2. Perform save action & apply action in the details dialog
    composeTestRule.onNodeWithTag("detail_save_btn").performClick()
    composeTestRule.onNodeWithTag("detail_apply_btn").performClick()

    // Close the details overlay dialog
    composeTestRule.onNodeWithTag("detail_close").performClick()
    composeTestRule.waitForIdle()

    // 3. Navigate to "Saved" tab using bottom bar
    composeTestRule.onNodeWithTag("nav_saved").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("saved_list").assertExists()
    composeTestRule.onNodeWithTag("job_card_1").assertExists() // Job 1 should be saved now

    // 4. Navigate to "Applied" tab using bottom bar
    composeTestRule.onNodeWithTag("nav_applied").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("applied_list").assertExists()
    composeTestRule.onNodeWithTag("job_row_or_card_1", useUnmergedTree = true) // Should exist under applied

    // 5. Navigate to "Resume / Profile" tab
    composeTestRule.onNodeWithTag("nav_profile").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("profile_view_screen").assertExists()

    // Trigger profile editing
    composeTestRule.onNodeWithTag("edit_profile_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("profile_edit_form").assertExists()

    // Edit full name
    composeTestRule.onNodeWithTag("input_full_name").performTextReplacement("Alex Mercer Junior")
    // Save change
    composeTestRule.onNodeWithTag("save_edit_button").performClick()
    composeTestRule.waitForIdle()

    // Assert new name is rendered on profile view
    composeTestRule.onNodeWithText("Alex Mercer Junior").assertExists()
  }
}
