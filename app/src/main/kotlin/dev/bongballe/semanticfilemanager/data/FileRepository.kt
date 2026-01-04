package dev.bongballe.semanticfilemanager.data

import dev.zacsweers.metro.Inject
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Inject
class FileRepository {
  suspend fun getFiles(path: String): List<File> =
    withContext(Dispatchers.IO) {
      val directory = File(path)
      if (directory.exists() && directory.isDirectory) {
        directory.listFiles()?.toList()?.sortedBy { !it.isDirectory } ?: emptyList()
      } else {
        emptyList()
      }
    }
}
