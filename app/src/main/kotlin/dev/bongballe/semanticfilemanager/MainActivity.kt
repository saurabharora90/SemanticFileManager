package dev.bongballe.semanticfilemanager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.bongballe.features.browser.BrowserScreen
import dev.bongballe.libs.theme.SemanticFileManagerTheme
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

data object RouteMain

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(private val viewModelFactory: MetroViewModelFactory) : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val backStack = remember { mutableStateListOf<Any>(RouteMain) }
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
              entry<RouteMain> {
                MainScreen()
              }
            },
          )
        }
      }
    }
  }
}

enum class AppTab {
  Home,
  Browse,
  Settings,
}

@Composable
fun MainScreen() {
  var selectedTab by remember { mutableStateOf(AppTab.Home) }

  Scaffold(
    bottomBar = {
      NavigationBar {
        NavigationBarItem(
          selected = selectedTab == AppTab.Home,
          onClick = { selectedTab = AppTab.Home },
          icon = { Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = "Home") },
          label = { Text("Home") },
        )
        NavigationBarItem(
          selected = selectedTab == AppTab.Browse,
          onClick = { selectedTab = AppTab.Browse },
          icon = { Icon(painter = painterResource(id = R.drawable.ic_folder), contentDescription = "Browse") },
          label = { Text("Browse") },
        )
        NavigationBarItem(
          selected = selectedTab == AppTab.Settings,
          onClick = { selectedTab = AppTab.Settings },
          icon = { Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = "Settings") },
          label = { Text("Settings") },
        )
      }
    },
  ) { innerPadding ->
    val modifier = Modifier.padding(innerPadding).fillMaxSize()
    when (selectedTab) {
      AppTab.Home -> HomeScreen(modifier)
      AppTab.Browse -> FileManagerScreen(modifier)
      AppTab.Settings -> SettingsScreen(modifier)
    }
  }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Home Screen")
  }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Settings Screen")
  }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FileManagerScreen(
  modifier: Modifier = Modifier,
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
      BrowserScreen(modifier = modifier)
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
      BrowserScreen(modifier = modifier)
    } else {
      PermissionRationale(modifier)
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
