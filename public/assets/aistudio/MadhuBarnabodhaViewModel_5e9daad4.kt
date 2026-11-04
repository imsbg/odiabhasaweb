// MadhuBarnabodhaViewModel.kt
package `in`.sandeep.odiabhasa

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL

// <<< FIX: Renamed to avoid conflict with your existing class
sealed class BookDownloadState {
    object NotDownloaded : BookDownloadState()
    data class Downloading(val progress: Int, val total: Int) : BookDownloadState()
    object Downloaded : BookDownloadState()
    data class Error(val message: String) : BookDownloadState()
}

class MadhuBarnabodhaViewModel(application: Application) : AndroidViewModel(application) {

    // <<< FIX: Use the new renamed class
    private val _downloadState = MutableStateFlow<BookDownloadState>(BookDownloadState.NotDownloaded)
    val downloadState: StateFlow<BookDownloadState> = _downloadState

    private val sharedPreferences = application.getSharedPreferences("book_downloads", Context.MODE_PRIVATE)
    private val imagesDir = File(application.filesDir, "barnabodha_images")

    companion object {
        private const val IS_DOWNLOADED_KEY = "is_barnabodha_downloaded"
        const val TOTAL_PAGES = 44
    }

    init {
        checkDownloadStatus()
    }

    private fun checkDownloadStatus() {
        val isDownloaded = sharedPreferences.getBoolean(IS_DOWNLOADED_KEY, false)
        if (isDownloaded && imagesDir.exists() && (imagesDir.listFiles()?.size ?: 0) >= TOTAL_PAGES) {
            _downloadState.value = BookDownloadState.Downloaded
        } else {
            _downloadState.value = BookDownloadState.NotDownloaded
        }
    }

    fun downloadBook() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_downloadState.value is BookDownloadState.Downloaded) return@launch

            try {
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }

                for (i in 1..TOTAL_PAGES) {
                    _downloadState.value = BookDownloadState.Downloading(i, TOTAL_PAGES)

                    val url = URL("https://imsbg.github.io/odiabhasa/bahi/barnabodha/$i.jpg")
                    val file = File(imagesDir, "$i.jpg")

                    url.openStream().use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                sharedPreferences.edit().putBoolean(IS_DOWNLOADED_KEY, true).apply()
                _downloadState.value = BookDownloadState.Downloaded

            } catch (e: Exception) {
                e.printStackTrace()
                _downloadState.value = BookDownloadState.Error("ଡାଉନଲୋଡ କରିବାରେ ଅସଫଳ ହେଲା")
                imagesDir.deleteRecursively()
            }
        }
    }

    fun getDownloadedImageFiles(): List<File> {
        if (!imagesDir.exists()) return emptyList()
        return imagesDir.listFiles()
            ?.filter { it.isFile && it.extension == "jpg" }
            ?.sortedBy { it.nameWithoutExtension.toInt() }
            ?: emptyList()
    }
}