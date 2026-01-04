package dev.bongballe.features.browser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bongballe.libs.theme.SemanticFileManagerTheme
import java.io.File

@Composable
fun FileItem(
  file: File,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
    modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(text = file.name)
  }
}

@Preview(showBackground = true)
@Composable
internal fun FileItemPreview() {
  SemanticFileManagerTheme {
    Column {
      FileItem(
        file = File("sample_file.txt"),
        onClick = {},
      )
      FileItem(
        file = object : File("sample_directory") {
          override fun isDirectory() = true
        },
        onClick = {},
      )
    }
  }
}
