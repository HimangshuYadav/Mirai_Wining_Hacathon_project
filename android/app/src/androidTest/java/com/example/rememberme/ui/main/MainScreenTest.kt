package com.example.rememberme.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent {
      MainScreen(onNavigateToEnroll = {})
    }
  }

  @Test
  fun testTitle_exists() {
    composeTestRule.onNodeWithText("See someone you know.").assertExists()
  }
}
