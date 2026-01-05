package dev.bongballe.features.browser

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(BrowserViewModel::class)
@Inject
class BrowserViewModel(private val repository: FileRepository) : ViewModel() {
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

  fun deleteFile(file: File) {
    viewModelScope.launch {
      if (repository.deleteFile(file)) {
        loadFiles(_currentPath.value)
      }
    }
  }

  fun renameFile(file: File, newName: String) {
    viewModelScope.launch {
      if (repository.renameFile(file, newName)) {
        loadFiles(_currentPath.value)
      }
    }
  }
}
