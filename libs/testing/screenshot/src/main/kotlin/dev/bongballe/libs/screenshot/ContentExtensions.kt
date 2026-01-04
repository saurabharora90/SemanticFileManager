package dev.bongballe.libs.screenshot

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bongballe.libs.theme.SemanticFileManagerTheme

@VisibleForTesting
fun ComposeContentTestRule.setContentWithTheme(
  drawBackground: Boolean = true,
  composable: @Composable () -> Unit,
) {
  setContent {
    CompositionLocalProvider(LocalInspectionMode provides true) {
      SemanticFileManagerTheme {
        Content(
          backgroundColor = if (drawBackground) MaterialTheme.colorScheme.primary else Color.Transparent,
        ) {
          composable()
        }
      }
    }
  }
}

@Composable
private fun Content(
  backgroundColor: Color,
  content: @Composable () -> Unit,
) {
  // The first box acts as a shield from ComposeView which forces the first layout node
  // to match it's size. This allows the content below to wrap as needed.
  Box {
    // The second box adds a border and background so we can easily see layout bounds in screenshots
    Box(
      Modifier
        .border(Dp.Hairline, Color.Magenta)
        .background(backgroundColor)
        .padding(1.dp), // Add padding for the border. Can't use Hairline as it takes up no space
    ) {
      content()
    }
  }
}
