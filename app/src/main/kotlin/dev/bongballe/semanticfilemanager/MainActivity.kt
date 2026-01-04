package dev.bongballe.semanticfilemanager

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.bongballe.semanticfilemanager.ui.FileManagerViewModel
import dev.bongballe.semanticfilemanager.ui.theme.SemanticFileManagerTheme
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.metroViewModel
import java.io.File

data object RouteA
data object RouteB

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(private val viewModelFactory: MetroViewModelFactory) : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val backStack = remember { mutableStateListOf<Any>(RouteA) }
      CompositionLocalProvider(LocalMetroViewModelFactory provides viewModelFactory) {
        SemanticFileManagerTheme {
          NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
              rememberSaveableStateHolderNavEntryDecorator(),
              rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
              entry<RouteA> {
                Text(
                  text = "Welcome to Nav 3",
                  modifier = Modifier
                    .statusBarsPadding()
                    .clickable {
                      backStack.add(RouteB)
                    },
                )
              }
              entry<RouteB> {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                  FileManagerScreen(modifier = Modifier.padding(innerPadding))
                }
              }
            },
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FileManagerScreen(
  modifier: Modifier = Modifier,
  viewModel: FileManagerViewModel = metroViewModel(),
) {
  val currentPath by viewModel.currentPath.collectAsState()
  val files by viewModel.files.collectAsState()

  val permissions =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      listOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO,
      )
    } else {
      listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

  val permissionState = rememberMultiplePermissionsState(permissions = permissions)

  LaunchedEffect(key1 = permissionState.allPermissionsGranted) {
    if (permissionState.allPermissionsGranted) {
      viewModel.loadFiles(currentPath)
    } else {
      permissionState.launchMultiplePermissionRequest()
    }
  }

  if (permissionState.allPermissionsGranted) {
    FileBrowser(
      currentPath = currentPath,
      files = files,
      onFileClick = viewModel::navigateTo,
      onUpClick = viewModel::navigateUp,
      modifier = modifier,
    )
  } else {
    PermissionRationale(modifier)
  }
}

@Composable
fun FileBrowser(
  currentPath: String,
  files: List<File>,
  onFileClick: (File) -> Unit,
  onUpClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxSize()) {
    Text(
      text = currentPath,
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier.padding(8.dp),
    )
    if (currentPath != Environment.getExternalStorageDirectory().absolutePath) {
      Text(
        text = "..",
        modifier =
        Modifier
          .clickable(onClick = onUpClick)
          .padding(16.dp)
          .fillMaxWidth(),
      )
    }
    LazyColumn {
      items(files) { file ->
        FileItem(file = file, onClick = { onFileClick(file) })
      }
    }
  }
}

@Composable
fun PermissionRationale(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Permission required to access files.")
  }
}

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
