package dev.bongballe.semanticfilemanager.ui

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.bongballe.libs.base.qualifier.WithScope
import dev.bongballe.semanticfilemanager.data.FileRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(FileManagerViewModel::class)
@Inject
class FileManagerViewModel(
  private val repository: FileRepository,
  @WithScope(AppScope::class) private val appScope: CoroutineScope,
) : ViewModel() {
  private val _currentPath = MutableStateFlow(Environment.getExternalStorageDirectory().absolutePath)
  val currentPath: StateFlow<String> = _currentPath.asStateFlow()

  private val _files = MutableStateFlow<List<File>>(emptyList())
  val files: StateFlow<List<File>> = _files.asStateFlow()

  init {
    loadFiles(_currentPath.value)
  }

  fun loadFiles(path: String) {
    viewModelScope.launch {
      _currentPath.value = path
      _files.value = repository.getFiles(path)
    }
  }

  fun navigateUp() {
    val parent = File(_currentPath.value).parentFile
    if (parent != null && parent.canRead()) {
      loadFiles(parent.absolutePath)
    }
  }

  fun navigateTo(file: File) {
    if (file.isDirectory) {
      loadFiles(file.absolutePath)
    }
  }

  override fun onCleared() {
    super.onCleared()
    Log.i("Saurabh", "VM Cleared")
  }
}
