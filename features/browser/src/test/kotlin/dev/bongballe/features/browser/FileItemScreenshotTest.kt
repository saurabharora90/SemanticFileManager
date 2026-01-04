package dev.bongballe.features.browser

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import dev.bongballe.libs.screenshot.BongBalleRoborazziRule
import dev.bongballe.libs.screenshot.setContentWithTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class FileItemScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @get:Rule
  val xRoborazziRule = BongBalleRoborazziRule()

  @Test
  fun file_item() {
    composeTestRule.setContentWithTheme {
      FileItemPreview()
    }
    composeTestRule.onRoot().captureRoboImage()
  }
}
