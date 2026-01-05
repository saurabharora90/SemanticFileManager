package dev.bongballe.features.browser

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BrowserScreen(
  modifier: Modifier = Modifier,
  viewModel: BrowserViewModel = metroViewModel(),
) {
  val context = LocalContext.current
  var hasManageExternalStoragePermission by remember {
    mutableStateOf(
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
      } else {
        true
      },
    )
  }

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      hasManageExternalStoragePermission = Environment.isExternalStorageManager()
    }
  }

  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    if (hasManageExternalStoragePermission) {
      BrowserContent(viewModel = viewModel, modifier = modifier)
    } else {
      Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Text("Permission required to access all files.")
        Button(
          onClick = {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = "package:${context.packageName}".toUri()
            context.startActivity(intent)
          },
        ) {
          Text("Grant Permission")
        }
      }
    }
  } else {
    val permissions =
      listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
      )

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    LaunchedEffect(key1 = permissionState.allPermissionsGranted) {
      if (!permissionState.allPermissionsGranted) {
        permissionState.launchMultiplePermissionRequest()
      }
    }

    if (permissionState.allPermissionsGranted) {
      BrowserContent(viewModel = viewModel, modifier = modifier)
    } else {
      PermissionRationale(modifier)
    }
  }
}

@Composable
fun BrowserContent(
  viewModel: BrowserViewModel,
  modifier: Modifier = Modifier,
) {
  val currentPath by viewModel.currentPath.collectAsState()
  val files by viewModel.files.collectAsState()
  val context = LocalContext.current
  val rootPath = remember { Environment.getExternalStorageDirectory().absolutePath }

  BackHandler(enabled = currentPath != rootPath) {
    viewModel.navigateUp()
  }

  FileBrowser(
    currentPath = currentPath,
    files = files,
    onFileClick = { file ->
      if (file.isDirectory) {
        viewModel.navigateTo(file)
      } else {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
          setDataAndType(uri, context.contentResolver.getType(uri))
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Open with"))
      }
    },
    onUpClick = viewModel::navigateUp,
    onDeleteClick = viewModel::deleteFile,
    onRenameClick = viewModel::renameFile,
    modifier = modifier,
  )
}

@Composable
fun FileBrowser(
  currentPath: String,
  files: List<File>,
  onFileClick: (File) -> Unit,
  onUpClick: () -> Unit,
  onDeleteClick: (File) -> Unit,
  onRenameClick: (File, String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var fileToDelete by remember { mutableStateOf<File?>(null) }
  var fileToRename by remember { mutableStateOf<File?>(null) }

  if (fileToDelete != null) {
    AlertDialog(
      onDismissRequest = { fileToDelete = null },
      title = { Text("Delete File") },
      text = { Text("Are you sure you want to delete ${fileToDelete?.name}?") },
      confirmButton = {
        Button(
          onClick = {
            fileToDelete?.let { onDeleteClick(it) }
            fileToDelete = null
          },
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        Button(onClick = { fileToDelete = null }) {
          Text("Cancel")
        }
      },
    )
  }

  if (fileToRename != null) {
    var newName by remember { mutableStateOf(fileToRename?.name ?: "") }
    AlertDialog(
      onDismissRequest = { fileToRename = null },
      title = { Text("Rename File") },
      text = {
        TextField(
          value = newName,
          onValueChange = { newName = it },
          label = { Text("New Name") },
        )
      },
      confirmButton = {
        Button(
          onClick = {
            fileToRename?.let { onRenameClick(it, newName) }
            fileToRename = null
          },
        ) {
          Text("Rename")
        }
      },
      dismissButton = {
        Button(onClick = { fileToRename = null }) {
          Text("Cancel")
        }
      },
    )
  }

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
        FileItem(
          file = file,
          onClick = { onFileClick(file) },
          trailingContent = {
            IconButton(onClick = { fileToRename = file }) {
              Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Rename")
            }
            IconButton(onClick = { fileToDelete = file }) {
              Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
            }
          },
        )
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
