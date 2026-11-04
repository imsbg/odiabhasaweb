package `in`.sandeep.odiabhasa

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.annotation.Keep
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.File
import java.net.URL

// --- Data Models (Unchanged) ---
@Keep data class BookDetails(val title: String, val author: String, val source: String, val coverpage: String, val pages: List<BookPage>)
@Keep data class BookPage(val pageNumber: Int, val content: List<ContentBlock>, val audio: String? = null, val lyrics: String? = null)
@Keep data class ContentBlock(val type: String, val text: String?, val spans: List<TextSpan>?)
@Keep data class TextSpan(val text: String, val style: String)
@Keep data class BookMetadata(val destination: String, val version: Int, val new: String? = null)
@Keep data class LocalBookWrapper(val metadata: BookMetadata, val details: BookDetails)

// --- Shared File Utilities & User Preferences (Unchanged) ---
object BahiFileUtils {
    private const val MASTER_LIST_CACHE_FILE = "sabu_master_list.json"
    fun getBookFile(context: Context, destinationUrl: String): File { val fileName = destinationUrl.hashCode().toString() + ".json"; return File(context.filesDir, fileName) }
    fun getBookCacheFile(context: Context, destinationUrl: String): File { val fileName = destinationUrl.hashCode().toString() + ".json"; return File(context.cacheDir, fileName) }
    fun getMasterListCacheFile(context: Context): File = File(context.cacheDir, MASTER_LIST_CACHE_FILE)
}
class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("bahi_user_prefs", Context.MODE_PRIVATE)
    private val PINNED_BOOKS_KEY = "pinned_books"; private val HIDDEN_BOOKS_KEY = "hidden_books"
    fun getPinnedBooks(): Set<String> = prefs.getStringSet(PINNED_BOOKS_KEY, emptySet()) ?: emptySet()
    fun getHiddenBooks(): Set<String> = prefs.getStringSet(HIDDEN_BOOKS_KEY, emptySet()) ?: emptySet()
    fun togglePinStatus(destinationUrl: String) { val current = getPinnedBooks().toMutableSet(); if (current.contains(destinationUrl)) current.remove(destinationUrl) else current.add(destinationUrl); prefs.edit().putStringSet(PINNED_BOOKS_KEY, current).apply() }
    fun toggleHideStatus(destinationUrl: String) { val current = getHiddenBooks().toMutableSet(); if (current.contains(destinationUrl)) current.remove(destinationUrl) else current.add(destinationUrl); prefs.edit().putStringSet(HIDDEN_BOOKS_KEY, current).apply(); if (getPinnedBooks().contains(destinationUrl)) { togglePinStatus(destinationUrl) } }
}

// --- UI Models & State (Unchanged) ---
enum class DownloadState { NOT_DOWNLOADED, DOWNLOADING, DOWNLOADED }
data class BookUiModel(val details: BookDetails, val downloadState: DownloadState, val metadata: BookMetadata, val isPinned: Boolean = false, val isHidden: Boolean = false)
sealed interface BookScreenState {
    object Loading : BookScreenState
    data class Success(val books: List<BookUiModel>, val hiddenBooks: List<BookUiModel>, val showHidden: Boolean = false) : BookScreenState
    data class Error(val message: String) : BookScreenState
}

// --- ViewModelFactory (Unchanged) ---
class BahiViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BahiViewModel::class.java)) { @Suppress("UNCHECKED_CAST") return BahiViewModel(application) as T }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// --- ViewModel (Unchanged) ---
class BahiViewModel(application: Application) : AndroidViewModel(application) {
    val booksState = mutableStateOf<BookScreenState>(BookScreenState.Loading)
    private val booksListJsonUrl = "https://sbgb.pages.dev/ବହି/ସବୁ.json"
    private val gson = Gson()
    private val userPrefs = UserPreferences(application)
    private var allBooksMasterList: List<BookUiModel> = emptyList()

    init { loadBooks() }
    fun refreshBooks() { booksState.value = BookScreenState.Loading; loadBooks() }
    fun onBackNavigation(): Boolean { val currentState = booksState.value; if (currentState is BookScreenState.Success && currentState.showHidden) { toggleHiddenView(); return true }; return false }
    fun resetViewStateOnEntry() { val currentState = booksState.value; if (currentState is BookScreenState.Success && currentState.showHidden) { booksState.value = currentState.copy(showHidden = false) } }

    private fun loadBooks() {
        viewModelScope.launch {
            val masterList: List<BookMetadata>? = try {
                fetchAndCacheMasterList()
            } catch (e: Exception) {
                loadMasterListFromCache()
            }

            if (masterList == null) {
                booksState.value = BookScreenState.Error("ବହି ତାଲିକା ପାଇବା ପାଇଁ \nଦୟାକରି ଇଣ୍ଟରନେଟ୍ ସଂଯୋଗ କରନ୍ତୁ")
                return@launch
            }

            val bookUiModels = processMasterList(masterList)
            allBooksMasterList = bookUiModels
            updateVisibleBooks()
        }
    }

    private suspend fun fetchAndCacheMasterList(): List<BookMetadata> = withContext(Dispatchers.IO) {
        val noCacheUrl = "$booksListJsonUrl?t=${System.currentTimeMillis()}"
        val jsonString = URL(noCacheUrl).readText()
        BahiFileUtils.getMasterListCacheFile(getApplication()).writeText(jsonString)
        gson.fromJson(jsonString, Array<BookMetadata>::class.java).toList()
    }

    private suspend fun loadMasterListFromCache(): List<BookMetadata>? = withContext(Dispatchers.IO) {
        try {
            val cacheFile = BahiFileUtils.getMasterListCacheFile(getApplication())
            if (!cacheFile.exists()) return@withContext null
            val jsonString = cacheFile.readText()
            gson.fromJson(jsonString, Array<BookMetadata>::class.java).toList()
        } catch (e: Exception) { null }
    }

    private suspend fun processMasterList(masterList: List<BookMetadata>): List<BookUiModel> = coroutineScope {
        val context = getApplication<Application>()
        masterList.map { serverMeta ->
            async(Dispatchers.IO) {
                val permFile = BahiFileUtils.getBookFile(context, serverMeta.destination)
                if (permFile.exists()) {
                    val localWrapper = try { gson.fromJson(permFile.readText(), LocalBookWrapper::class.java) } catch (e: Exception) { null }

                    if (localWrapper != null && localWrapper.metadata.version >= serverMeta.version) {
                        return@async BookUiModel(localWrapper.details, DownloadState.DOWNLOADED, serverMeta)
                    } else {
                        val newDetails = downloadBookDetails(serverMeta)
                        if (newDetails != null) {
                            permFile.writeText(gson.toJson(LocalBookWrapper(serverMeta, newDetails)))
                            return@async BookUiModel(newDetails, DownloadState.DOWNLOADED, serverMeta)
                        } else {
                            return@async localWrapper?.let { BookUiModel(it.details, DownloadState.DOWNLOADED, serverMeta) }
                        }
                    }
                }

                val cacheFile = BahiFileUtils.getBookCacheFile(context, serverMeta.destination)
                if (cacheFile.exists()) {
                    val cachedWrapper = try { gson.fromJson(cacheFile.readText(), LocalBookWrapper::class.java) } catch (e: Exception) { null }
                    if (cachedWrapper != null && cachedWrapper.metadata.version >= serverMeta.version) {
                        return@async BookUiModel(cachedWrapper.details, DownloadState.NOT_DOWNLOADED, serverMeta)
                    }
                }
                val details = downloadBookDetails(serverMeta)
                if (details != null) {
                    val newWrapper = LocalBookWrapper(serverMeta, details)
                    cacheFile.writeText(gson.toJson(newWrapper))
                    return@async BookUiModel(details, DownloadState.NOT_DOWNLOADED, serverMeta)
                }
                return@async null
            }
        }.awaitAll().filterNotNull()
    }

    private fun updateVisibleBooks() {
        val pinned = userPrefs.getPinnedBooks(); val hidden = userPrefs.getHiddenBooks()
        val updatedList = allBooksMasterList.map { it.copy(isPinned = pinned.contains(it.metadata.destination), isHidden = hidden.contains(it.metadata.destination)) }
        val hiddenBooks = updatedList.filter { it.isHidden }; val visibleBooks = updatedList.filter { !it.isHidden }.sortedByDescending { it.isPinned }
        val showHidden = (booksState.value as? BookScreenState.Success)?.showHidden ?: false
        booksState.value = BookScreenState.Success(visibleBooks, hiddenBooks, showHidden)
    }

    fun getBookForReading(book: BookUiModel, onResult: (BookDetails) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val permFile = BahiFileUtils.getBookFile(context, book.metadata.destination)
            if (!permFile.exists()) {
                val cacheFile = BahiFileUtils.getBookCacheFile(context, book.metadata.destination)
                if (cacheFile.exists()) {
                    val json = cacheFile.readText(); permFile.writeText(json); cacheFile.delete()
                } else {
                    downloadBookDetails(book.metadata)?.let {
                        permFile.writeText(gson.toJson(LocalBookWrapper(book.metadata, it)))
                    }
                }
            }
            withContext(Dispatchers.Main) { onResult(book.details) }
        }
    }

    private fun downloadBookDetails(meta: BookMetadata): BookDetails? { try { return gson.fromJson(URL(meta.destination).readText(), BookDetails::class.java) } catch (e: Exception) { return null } }
    fun togglePin(book: BookUiModel) { userPrefs.togglePinStatus(book.metadata.destination); updateVisibleBooks() }
    fun toggleHide(book: BookUiModel) { userPrefs.toggleHideStatus(book.metadata.destination); updateVisibleBooks() }
    fun toggleHiddenView() { val state = booksState.value; if (state is BookScreenState.Success) { booksState.value = state.copy(showHidden = !state.showHidden) } }

    fun findBookByIdentifier(identifier: String): BookUiModel? {
        return allBooksMasterList.find {
            val bookIdentifier = it.metadata.destination.substringAfterLast('/').removeSuffix(".json")
            bookIdentifier.equals(identifier, ignoreCase = true)
        }
    }

    fun findBookByIndex(index: Int): BookUiModel? {
        if (index >= 0 && index < allBooksMasterList.size) {
            return allBooksMasterList[index]
        }
        return null
    }
}

// --- POP COMIC CONSTANTS ---
private val PopYellow = Color(0xFFFFEB3B)
private val PopCyan = Color(0xFF00E5FF)
private val PopPink = Color(0xFFFF4081)
private val PopGreen = Color(0xFFC6FF00)
private val PopWhite = Color(0xFFFFFFFF)
private val PopBlack = Color(0xFF000000)
private val PopBg = Color(0xFFFFFDE7) // Very light yellow bg

// --- Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BahiScreen(onBack: () -> Unit, onBookClick: (BookDetails, String, Int) -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val viewModel: BahiViewModel = viewModel(factory = BahiViewModelFactory(context.applicationContext as Application))
    val state = viewModel.booksState.value
    var searchQuery by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // State for the new BottomSheet Menu
    var bookForOptions by remember { mutableStateOf<BookUiModel?>(null) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) { viewModel.resetViewStateOnEntry() }
    BackHandler(enabled = state is BookScreenState.Success && state.showHidden) { viewModel.onBackNavigation() }

    Scaffold(
        containerColor = PopBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PopYellow,
                    titleContentColor = PopBlack,
                    actionIconContentColor = PopBlack,
                    navigationIconContentColor = PopBlack
                ),
                title = { val titleText = if (state is BookScreenState.Success && state.showHidden) "ଲୁଚିଥିବା ବହି" else "ଓଡ଼ିଆ ପାଠାଗାର"; Text(titleText, fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 24.sp) },
                navigationIcon = { IconButton(onClick = { if (!viewModel.onBackNavigation()) { onBack() } }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(32.dp)) } },
                actions = {
                    if (state is BookScreenState.Success && state.hiddenBooks.isNotEmpty()) { IconButton(onClick = { viewModel.toggleHiddenView() }) { Icon(if (state.showHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff, "ଲୁଚିଥିବା ବହି ଦେଖାନ୍ତୁ", modifier = Modifier.size(28.dp)) } }
                    IconButton(onClick = { viewModel.refreshBooks() }) { Icon(Icons.Default.Refresh, "Refresh Books", modifier = Modifier.size(28.dp)) }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (state !is BookScreenState.Error) {
                // COMIC STYLE SEARCH BAR
                Box(modifier = Modifier.padding(16.dp)) {
                    // Shadow for search
                    Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(PopBlack, RoundedCornerShape(50)))

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().border(2.dp, PopBlack, RoundedCornerShape(50)),
                        placeholder = { Text("ବହି ଖୋଜନ୍ତୁ...", fontFamily = OdiaFont, color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, "Search Icon", tint = PopBlack) },
                        trailingIcon = { if (searchQuery.isNotEmpty()) { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, "ସବୁ ଲିଭାନ୍ତୁ", tint = PopBlack) } } },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = PopWhite,
                            unfocusedContainerColor = PopWhite,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = PopBlack
                        )
                    )
                }
            }
            Box(Modifier.fillMaxSize()) {
                when (state) {
                    is BookScreenState.Loading -> ShimmerBooksGrid()
                    is BookScreenState.Success -> {
                        val listToDisplay = if (state.showHidden) state.hiddenBooks else state.books
                        val booksToShow = listToDisplay.filter { it.details.title.contains(searchQuery, ignoreCase = true) || it.details.author.contains(searchQuery, ignoreCase = true) }
                        if (booksToShow.isEmpty()) {
                            val msg = when { listToDisplay.isNotEmpty() -> "କୌଣସି ବହି ମିଳିଲା ନାହିଁ"; state.showHidden -> "ଆପଣ କୌଣସି ବହି ଲୁଚାଇ ନାହାଁନ୍ତି"; else -> "କୌଣସି ସଂରକ୍ଷିତ ବହି ମିଳିଲା ନାହିଁ" }
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(32.dp)) { Text(msg, fontFamily = OdiaFont, textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PopBlack) }
                        } else {
                            BooksGrid(
                                books = booksToShow,
                                onBookClick = { book -> viewModel.getBookForReading(book) { bookDetails -> onBookClick(bookDetails, book.metadata.destination, book.metadata.version) } },
                                onBookLongClick = { book ->
                                    bookForOptions = book
                                    showBottomSheet.value = true
                                },
                                isHiddenView = state.showHidden
                            )
                        }
                    }
                    is BookScreenState.Error -> Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(32.dp)) { Text(state.message, fontFamily = OdiaFont, textAlign = TextAlign.Center, lineHeight = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red) }
                }
            }
        }

        // --- EYE CATCHY BOTTOM SHEET ---
        if (showBottomSheet.value && bookForOptions != null) {
            val book = bookForOptions!!
            val isHiddenView = (state as? BookScreenState.Success)?.showHidden ?: false

            ModalBottomSheet(
                onDismissRequest = { showBottomSheet.value = false },
                sheetState = sheetState,
                containerColor = PopWhite,
                tonalElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = null
            ) {
                // Decor element
                Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(PopYellow))

                BookOptionsSheetContent(
                    book = book,
                    isHiddenView = isHiddenView,
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet.value = false
                        }
                    },
                    onShare = {
                        val bookIdentifier = book.metadata.destination.substringAfterLast('/').removeSuffix(".json")
                        val shareableLink = "https://odiabhasa.pages.dev/open?Bahi=$bookIdentifier"
                        val shareText = "ଓଡ଼ିଆ ଭାଷା ଆପ୍ ରେ *${book.details.title}* ବହିଟି ଦେଖନ୍ତୁ \n ${book.details.author}'ଙ୍କ ଦ୍ବାରା\n$shareableLink"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                    onCopyLink = {
                        val bookIdentifier = book.metadata.destination.substringAfterLast('/').removeSuffix(".json")
                        val shareableLink = "https://odiabhasa.pages.dev/open?Bahi=$bookIdentifier"
                        clipboardManager.setText(AnnotatedString(shareableLink))
                    },
                    onTogglePin = { viewModel.togglePin(book) },
                    onToggleHide = { viewModel.toggleHide(book) }
                )
            }
        }
    }
}

// --- New Eye-Catchy Sheet Content UI ---
@Composable
fun BookOptionsSheetContent(
    book: BookUiModel,
    isHiddenView: Boolean,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onCopyLink: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleHide: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {

        // --- 1. Header with Big Cover ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(80.dp, 100.dp)) {
                Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(PopBlack, RoundedCornerShape(8.dp)))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, PopBlack),
                    modifier = Modifier.fillMaxSize()
                ) {
                    SubcomposeAsyncImage(model = book.details.coverpage, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(), loading = { ImagePlaceholder() })
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                AutoFitText(
                    text = book.details.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = PopBlack),
                    minFontSize = 14.sp
                )
                Text(book.details.author, fontFamily = OdiaFont, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }

        // --- 2. Action Cards Grid ---

        // Share (Cyan)
        ComicOptionCard(
            text = "ସେୟାର କରନ୍ତୁ",
            subText = "(କେବଳ ପିତାମାତା)",
            icon = Icons.Default.Share,
            baseColor = PopCyan,
            onClick = { onShare(); onDismiss() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Copy (Yellow)
        ComicOptionCard(
            text = "ଲିଙ୍କ୍ କପି କରନ୍ତୁ",
            subText = "(କେବଳ ପିତାମାତା)",
            icon = Icons.Default.ContentCopy,
            baseColor = PopYellow,
            onClick = { onCopyLink(); onDismiss() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pin (Pink)
        if (!isHiddenView) {
            val pinText = if (book.isPinned) "ପିନ୍ ହଟାନ୍ତୁ" else "ପିନ୍ କରନ୍ତୁ"
            val pinIcon = if (book.isPinned) Icons.Default.DoNotDisturbOn else Icons.Default.PushPin
            ComicOptionCard(
                text = pinText,
                subText = null,
                icon = pinIcon,
                baseColor = PopPink,
                onClick = { onTogglePin(); onDismiss() }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Hide (Green)
        val hideText = if (isHiddenView) "ପୁଣି ଦେଖାନ୍ତୁ" else "ଲୁଚାନ୍ତୁ"
        val hideIcon = if (isHiddenView) Icons.Default.Visibility else Icons.Default.VisibilityOff
        ComicOptionCard(
            text = hideText,
            subText = null,
            icon = hideIcon,
            baseColor = PopGreen,
            onClick = { onToggleHide(); onDismiss() }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- Reusable "Eye Catchy" Action Button ---
@Composable
fun ComicOptionCard(
    text: String,
    subText: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    baseColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Press logic: Push down to cover shadow
    val offset by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp, label = "offset")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        // Hard Shadow (Static)
        Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(PopBlack, RoundedCornerShape(12.dp)))

        // Main Card (Animated)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(offset, offset) // Moves down when pressed
                .background(baseColor, RoundedCornerShape(12.dp))
                .border(2.dp, PopBlack, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(PopWhite, CircleShape).border(2.dp, PopBlack, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = PopBlack)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text, fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 16.sp, color = PopBlack)
                    if (subText != null) {
                        Text(subText, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PopBlack.copy(alpha = 0.7f))
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = PopBlack,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}


@Composable
fun BooksGrid(books: List<BookUiModel>, onBookClick: (BookUiModel) -> Unit, onBookLongClick: (BookUiModel) -> Unit, isHiddenView: Boolean) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(books, key = { it.metadata.destination }) { book ->
            BookItemCard(book = book, onClick = { onBookClick(book) }, onLongClick = { onBookLongClick(book) })
        }
    }
}

@Composable
private fun ImagePlaceholder() {
    Box(modifier = Modifier.fillMaxSize().background(PopWhite), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.logo_monochrome), contentDescription = "App Logo Placeholder", modifier = Modifier.size(64.dp), alpha = 0.5f)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookItemCard(book: BookUiModel, onClick: () -> Unit, onLongClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // --- BUTTON PRESS LOGIC ---
    // When pressed, offset goes to 6.dp (matching shadow position), hiding the shadow.
    val offset by animateDpAsState(targetValue = if (isPressed) 6.dp else 0.dp, label = "press_offset")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp) // Increased height to ensure text fits
            .padding(4.dp)
    ) {
        // 1. Hard Shadow (Static at 6,6)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 6.dp, y = 6.dp)
                .background(PopBlack, RoundedCornerShape(16.dp))
        )

        // 2. Main Card Content (Moves when pressed)
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(3.dp, PopBlack),
            colors = CardDefaults.cardColors(containerColor = PopWhite),
            modifier = Modifier
                .fillMaxSize()
                .offset(x = offset, y = offset) // <--- ANIMATION HERE
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null, // Disable ripple, we use physical movement
                    onClick = onClick,
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                )
        ) {
            Column {
                // COVER IMAGE AREA - FIXED RATIO (3:4)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f)
                        .border(
                            BorderStroke(3.dp, PopBlack),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                ) {
                    SubcomposeAsyncImage(
                        model = book.details.coverpage,
                        contentDescription = book.details.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = { ImagePlaceholder() },
                        error = { ImagePlaceholder() }
                    )

                    // "New" Badge
                    if (book.metadata.new == "yes") {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .background(PopPink, RoundedCornerShape(8.dp))
                                .border(2.dp, PopBlack, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "ନୂତନ ", fontFamily = OdiaFont, color = PopBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Pinned Badge
                    if (book.isPinned) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                                .background(PopYellow, CircleShape)
                                .border(2.dp, PopBlack, CircleShape)
                                .padding(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PushPin, contentDescription = "Pinned", tint = PopBlack, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Text Area - FIXED to show Author Name Properly
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Takes remaining space
                        .background(PopWhite)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween // <<< MODIFIED HERE
                ) {
                    // Title
                    AutoFitText(
                        text = book.details.title,
                        style = TextStyle(
                            fontFamily = OdiaFont,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = PopBlack,
                            lineHeight = 20.sp // Added for better spacing in 2 lines
                        ),
                        minFontSize = 14.sp
                    )

                    // Spacer(modifier = Modifier.height(4.dp)) // <<< REMOVED SPACER

                    // Author - Standard Text with Ellipsis
                    Text(
                        text = book.details.author,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PopPink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start, // <<< Ensure alignment
                        modifier = Modifier.fillMaxWidth() // <<< Ensure it takes full width
                    )
                }
            }
        }
    }
}

// Improved AutoFitText
@Composable
fun AutoFitText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minFontSize: TextUnit
) {
    var fontSize by remember { mutableStateOf(style.fontSize) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        style = style.copy(fontSize = fontSize),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowHeight || textLayoutResult.didOverflowWidth) {
                val newSize = fontSize * 0.9f
                if (newSize.value >= minFontSize.value) {
                    fontSize = newSize
                } else {
                    readyToDraw = true
                }
            } else {
                readyToDraw = true
            }
        }
    )
}

// --- Comic Shimmer ---
@Composable fun ShimmerBooksGrid() { LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 150.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp), userScrollEnabled = false) { items(10) { ShimmerBookItemCard() } } }
@Composable fun ShimmerBookItemCard() {
    Box(modifier = Modifier.height(320.dp).padding(4.dp)) {
        Box(modifier = Modifier.fillMaxSize().offset(6.dp, 6.dp).background(Color.Gray.copy(alpha=0.3f), RoundedCornerShape(16.dp)))
        Card(shape = RoundedCornerShape(16.dp), border = BorderStroke(3.dp, Color.Gray.copy(alpha=0.5f)), colors = CardDefaults.cardColors(containerColor = PopWhite)) {
            Column {
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.75f).background(shimmerBrush()))
                Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                    Spacer(modifier = Modifier.fillMaxWidth(0.8f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush()))
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(modifier = Modifier.fillMaxWidth(0.5f).height(16.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush()))
                }
            }
        }
    }
}
@Composable fun shimmerBrush(targetValue: Float = 1000f): Brush { val shimmerColors = listOf(PopYellow.copy(alpha = 0.3f), PopPink.copy(alpha = 0.3f), PopCyan.copy(alpha = 0.3f)); val transition = rememberInfiniteTransition(label = "shimmer_transition"); val translateAnimation = transition.animateFloat(initialValue = 0f, targetValue = targetValue, animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse), label = "shimmer_animation"); return Brush.linearGradient(colors = shimmerColors, start = Offset.Zero, end = Offset(x = translateAnimation.value, y = translateAnimation.value)) }