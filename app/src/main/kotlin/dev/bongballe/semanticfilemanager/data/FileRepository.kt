package dev.bongballe.semanticfilemanager.data

import dev.bongballe.libs.base.DispatcherType
import dev.bongballe.libs.base.qualifier.WithDispatcherType
import dev.zacsweers.metro.Inject
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext

@Inject
class FileRepository(@WithDispatcherType(DispatcherType.IO) private val ioContext: CoroutineContext) {
  suspend fun getFiles(path: String): List<File> =
    withContext(ioContext) {
      val directory = File(path)
      if (directory.exists() && directory.isDirectory) {
        directory.listFiles()?.toList()?.sortedBy { !it.isDirectory } ?: emptyList()
      } else {
        emptyList()
      }
    }
}
