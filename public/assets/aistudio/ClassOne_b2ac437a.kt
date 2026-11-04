// --- START OF FILE ClassOne.kt ---
package `in`.sandeep.odiabhasa

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

// Data model for School Books
data class SchoolBookData(val id: String, val title: String, val url: String)

private fun convertEngToOdiaSchoolBookText(text: String): String {
    val odiaNumerals = listOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return text.map { char ->
        if (char.isDigit()) odiaNumerals[char.toString().toInt()] else char
    }.joinToString("")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ClassOneScreen(
    onBack: () -> Unit,
    onReadBook: (title: String, path: String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedBook by remember { mutableStateOf<SchoolBookData?>(null) }
    // A trigger to refresh the list items (checkmarks) when a file is downloaded or deleted
    var refreshListTrigger by remember { mutableStateOf(0) }

    val books = listOf(
        SchoolBookData("class1_jhulana2", "ଝୁଲଣା - ୧ (ସଂଶୋଧିତ)", "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Jhulana_Odia_Cl-I-P_1-184_Web_DRAFT_1_11zon.pdf"),
        SchoolBookData("class1_ganitakhela2", "ଗଣିତ ଖେଳ (ସଂଶୋଧିତ)", "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Math_Class_1_Web(1)_11zon.pdf"),
        SchoolBookData("class1_jhulana1", "ଝୁଲଣା - ୧ (ନୂତନ)", "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.I.-.Jhulana.pdf"),
        SchoolBookData("class1_ganitakhela", "ଗଣିତ ଖେଳ (ନୂତନ)", "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.I.-.Math.pdf"),
        SchoolBookData("class1_hasakhela_1", "ହସଖେଳ (ପ୍ରଥମ ଭାଗ)", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-I/Hasakhela-P-I-(Cl-I)-P-1-104.pdf"),
        SchoolBookData("class1_hasakhela_2", "ହସଖେଳ (ଦ୍ଵିତୀୟ ଭାଗ)", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-I/Haso-Khelo-Part-II-112.pdf"),
        SchoolBookData("class1_sikhara_1", "ଶିଖର - ୧", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-I/Sikhara-1.pdf")
    )

    if (selectedBook != null) {
        BookDownloadDialog(
            book = selectedBook!!,
            onDismiss = { selectedBook = null },
            onRead = { title, filePath ->
                selectedBook = null
                onReadBook(title, filePath) // Passes both Title and Path!
            },
            onFileChanged = {
                refreshListTrigger++ // Triggers re-composition for checkmarks
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // SHARED BOUNDS ANIMATION APPLIED HERE
                    Text(
                        text = "ପ୍ରଥମ ଶ୍ରେଣୀ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "class_title_ପ୍ରଥମ ଶ୍ରେଣୀ"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp)
        ) {
            items(books.size) { index ->
                val book = books[index]
                val context = LocalContext.current
                val file = File(context.filesDir, "school_books/${book.id}.pdf")

                // Real-time check if the book is downloaded
                val isDownloaded = remember(book, refreshListTrigger) {
                    file.exists() && file.length() > 0
                }

                // Identify if book is "New" or "Revised"
                val isNew = book.title.contains("(ନୂତନ)")
                val isRevised = book.title.contains("(ସଂଶୋଧିତ)")

                // Styling logic based on the tag
                val containerBg = when {
                    isNew -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    isRevised -> Color(0xFF4CAF50).copy(alpha = 0.15f) // Light Green background
                    else -> MaterialTheme.colorScheme.surface
                }

                val outlineColor = when {
                    isNew -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    isRevised -> Color(0xFF4CAF50).copy(alpha = 0.6f) // Green Outline
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                }

                val outlineWidth = if (isNew || isRevised) 2.dp else 1.dp

                val iconTint = when {
                    isNew -> MaterialTheme.colorScheme.primary
                    isRevised -> Color(0xFF2E7D32) // Dark Green Icon
                    else -> MaterialTheme.colorScheme.secondary
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .border(outlineWidth, outlineColor, RoundedCornerShape(15.dp))
                        .background(containerBg)
                        .bouncingClickable {
                            selectedBook = book
                        }
                        .padding(16.dp, 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = book.title,
                        fontSize = 18.sp,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // Display Checkmark if downloaded, otherwise normal arrow
                    if (isDownloaded) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "ଡାଉନଲୋଡ୍ ହୋଇଛି", tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }
        }
    }
}

sealed class SchoolBookDownloadState {
    object Idle : SchoolBookDownloadState()
    object FetchingSize : SchoolBookDownloadState()
    data class Ready(val sizeMb: String) : SchoolBookDownloadState()
    data class Downloading(val progress: Int) : SchoolBookDownloadState()
    data class Downloaded(val file: File) : SchoolBookDownloadState()
    data class Error(val message: String) : SchoolBookDownloadState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDownloadDialog(
    book: SchoolBookData,
    onDismiss: () -> Unit,
    onRead: (String, String) -> Unit,
    onFileChanged: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var state by remember { mutableStateOf<SchoolBookDownloadState>(SchoolBookDownloadState.Idle) }

    val dir = File(context.filesDir, "school_books")
    if (!dir.exists()) dir.mkdirs()
    val targetFile = File(dir, "${book.id}.pdf")

    fun checkFileSize() {
        state = SchoolBookDownloadState.FetchingSize
        scope.launch(Dispatchers.IO) {
            try {
                val connection = URL(book.url).openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD"
                connection.connect()
                val fileLength = connection.contentLength
                val sizeMb = if (fileLength > 0) String.format(Locale.US, "%.1f", fileLength / (1024.0 * 1024.0)) else "ଅଜଣା"
                withContext(Dispatchers.Main) { state = SchoolBookDownloadState.Ready(sizeMb) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { state = SchoolBookDownloadState.Ready("ଅଜଣା") }
            }
        }
    }

    LaunchedEffect(book) {
        if (targetFile.exists() && targetFile.length() > 0) {
            state = SchoolBookDownloadState.Downloaded(targetFile)
        } else {
            checkFileSize()
        }
    }

    fun startDownload() {
        state = SchoolBookDownloadState.Downloading(0)
        scope.launch(Dispatchers.IO) {
            try {
                val connection = URL(book.url).openConnection() as HttpURLConnection
                connection.connect()
                val fileLength = connection.contentLength
                val input = connection.inputStream
                val output = FileOutputStream(targetFile)
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    total += count
                    if (fileLength > 0) {
                        val progress = (total * 100 / fileLength).toInt()
                        withContext(Dispatchers.Main) { state = SchoolBookDownloadState.Downloading(progress) }
                    }
                    output.write(data, 0, count)
                }
                output.flush(); output.close(); input.close()
                withContext(Dispatchers.Main) {
                    state = SchoolBookDownloadState.Downloaded(targetFile)
                    onFileChanged()
                }
            } catch (e: Exception) {
                if (targetFile.exists()) targetFile.delete()
                withContext(Dispatchers.Main) { state = SchoolBookDownloadState.Error("ଡାଉନଲୋଡ୍ ବିଫଳ ହେଲା") }
            }
        }
    }

    fun deleteBook() {
        if (targetFile.exists()) {
            targetFile.delete()
        }
        Toast.makeText(context, "ବହିଟି ସଫଳତାର ସହ ଡିଲିଟ୍ ହୋଇଗଲା", Toast.LENGTH_SHORT).show()
        onFileChanged()
        checkFileSize() // Resetting to download state again
    }

    ModalBottomSheet(
        onDismissRequest = { if (state !is SchoolBookDownloadState.Downloading) onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ବହିର ବିବରଣୀ",
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            Text("ବହିର ନାମ : ${book.title}", fontFamily = OdiaFont, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("ଉତ୍ସ : ଓଡ଼ିଶା ବିଦ୍ୟାଳୟ ଶିକ୍ଷା କାର୍ଯ୍ୟକ୍ରମ ପ୍ରାଧିକରଣ", fontFamily = OdiaFont, fontSize = 14.sp)
            Text("ଓଡ଼ିଶା ସରକାର", fontFamily = OdiaFont, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))

            when (val s = state) {
                is SchoolBookDownloadState.FetchingSize -> Text("ଆକାର ଯାଞ୍ଚ ହେଉଛି...", fontFamily = OdiaFont, fontSize = 14.sp, color = Color.Gray)
                is SchoolBookDownloadState.Ready -> Text("ବହିର ଆକାର : ${if (s.sizeMb != "ଅଜଣା") convertEngToOdiaSchoolBookText(s.sizeMb) else "ଅଜଣା"} ଏମବି", fontFamily = OdiaFont, fontSize = 14.sp)
                is SchoolBookDownloadState.Downloading -> {
                    Text("ଡାଉନଲୋଡ୍ ହେଉଛି: ${convertEngToOdiaSchoolBookText(s.progress.toString())}%", fontFamily = OdiaFont, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    LinearProgressIndicator(progress = s.progress / 100f, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)))
                }
                is SchoolBookDownloadState.Downloaded -> Text("ବହିଟି ପଢିବା ପାଇଁ ପ୍ରସ୍ତୁତ ଅଛି ।", fontFamily = OdiaFont, fontSize = 14.sp, color = Color(0xFF2E7D32))
                is SchoolBookDownloadState.Error -> Text(s.message, fontFamily = OdiaFont, fontSize = 14.sp, color = Color.Red)
                else -> {}
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state is SchoolBookDownloadState.Downloaded) {
                    OutlinedButton(
                        onClick = { deleteBook() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ଡିଲିଟ୍ କରନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { onRead(book.title, (state as SchoolBookDownloadState.Downloaded).file.absolutePath) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("ପଢନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
                    }
                } else {
                    if (state !is SchoolBookDownloadState.Downloading) {
                        TextButton(onClick = onDismiss) {
                            Text("ବନ୍ଦ କରନ୍ତୁ", fontFamily = OdiaFont, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (state is SchoolBookDownloadState.Downloading) {
                        Button(onClick = { }, enabled = false, modifier = Modifier.fillMaxWidth()) {
                            Text("ଅପେକ୍ଷା କରନ୍ତୁ", fontFamily = OdiaFont)
                        }
                    } else {
                        Button(onClick = { startDownload() }) {
                            Text("ଡାଉନଲୋଡ୍ କରନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
// --- END OF FILE ClassOne.kt ---