// --- START OF FILE PurunaBahi.kt ---
package `in`.sandeep.odiabhasa

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

// Custom Odia Font Declaration
val OdiaFonti = FontFamily(Font(R.font.odia2))

// Data model for Puruna Bahi
data class PurunaBahiData(
    val id: String,
    val title: String,
    val author: String,
    val year: String,
    val category: String,
    val source: String,
    val publisher: String,
    val url: String,
    val version: Int
)

private fun convertEngToOdiaPurunaBahiText(text: String): String {
    val odiaNumerals = listOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return text.map { char ->
        if (char.isDigit()) odiaNumerals[char.toString().toInt()] else char
    }.joinToString("")
}

private fun convertOdiaToEngNumerals(text: String): String {
    val odiaNumerals = listOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return text.map { char ->
        val index = odiaNumerals.indexOf(char)
        if (index != -1) index.toString() else char
    }.joinToString("")
}

private fun extractYearAsInt(yearStr: String): Int {
    val cleanStr = yearStr.split(",").firstOrNull()?.trim() ?: ""
    val engStr = convertOdiaToEngNumerals(cleanStr).replace(Regex("[^0-9]"), "")
    return engStr.toIntOrNull() ?: 9999
}

// Shimmer Animation for Skeleton Loader
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )
    return Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.4f),
            Color.LightGray.copy(alpha = 0.1f),
            Color.LightGray.copy(alpha = 0.4f)
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

@Composable
fun SkeletonBookItem() {
    val brush = shimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(45.dp).clip(CircleShape).background(brush))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.height(18.dp).fillMaxWidth(0.7f).clip(RoundedCornerShape(4.dp)).background(brush))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.5f).clip(RoundedCornerShape(4.dp)).background(brush))
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.height(13.dp).fillMaxWidth(0.4f).clip(RoundedCornerShape(4.dp)).background(brush))
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurunaBahiScreen(onBack: () -> Unit, onReadBook: (title: String, path: String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    var selectedBook by remember { mutableStateOf<PurunaBahiData?>(null) }
    var downloadedFiles by remember { mutableStateOf<Set<String>>(emptySet()) }

    var allBooks by remember { mutableStateOf<List<PurunaBahiData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var isOffline by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var filteredBooks by remember { mutableStateOf<List<PurunaBahiData>>(emptyList()) }
    var isFiltering by remember { mutableStateOf(false) }

    // Toggle state for All Books vs Downloaded Books
    var showOnlyDownloaded by remember { mutableStateOf(false) }

    var showSortSheet by remember { mutableStateOf(false) }
    val sortSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sortOption by remember { mutableStateOf("କୌଣସିଟି ନୁହେଁ") }
    val sortOptionsList = listOf("କୌଣସିଟି ନୁହେଁ", "ଅକ୍ଷର ଅନୁଯାୟୀ", "ପୁରୁଣା ରୁ ନୂଆ", "ନୂଆ ରୁ ପୁରୁଣା")

    var selectedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedAuthors by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedPublishers by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedYears by remember { mutableStateOf<Set<String>>(emptySet()) }

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val cacheFile = File(context.filesDir, "bahi_cache.json")

    // Scroll Tracking for hiding/showing Top Header
    var isHeaderVisible by remember { mutableStateOf(true) }
    var previousIndex by remember { mutableIntStateOf(0) }
    var previousScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow { Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (index, offset) ->
                if (index == 0 && offset == 0) {
                    isHeaderVisible = true
                } else if (index > previousIndex) {
                    isHeaderVisible = false
                } else if (index < previousIndex) {
                    isHeaderVisible = true
                } else {
                    if (offset > previousScrollOffset + 15) {
                        isHeaderVisible = false
                    } else if (offset < previousScrollOffset - 15) {
                        isHeaderVisible = true
                    }
                }
                previousIndex = index
                previousScrollOffset = offset
            }
    }

    fun parseBooksJson(jsonString: String): List<PurunaBahiData> {
        val jsonArray = JSONArray(jsonString)
        val fetchedBooks = mutableListOf<PurunaBahiData>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            fetchedBooks.add(
                PurunaBahiData(
                    id = obj.optString("id", ""),
                    title = obj.optString("title", ""),
                    author = obj.optString("author", "ଅଜ୍ଞାତ"),
                    year = obj.optString("year", "ଅଜ୍ଞାତ"),
                    category = obj.optString("category", "ଅନ୍ୟାନ୍ୟ"),
                    source = obj.optString("source", "ଅଜ୍ଞାତ"),
                    publisher = obj.optString("publisher", ""),
                    url = obj.optString("url", ""),
                    version = obj.optInt("version", 1)
                )
            )
        }
        return fetchedBooks
    }

    fun refreshDownloadedFiles() {
        scope.launch(Dispatchers.IO) {
            val dir = File(context.filesDir, "puruna_books")
            val files = if (dir.exists()) dir.list()?.toSet() ?: emptySet() else emptySet()
            withContext(Dispatchers.Main) { downloadedFiles = files }
        }
    }

    fun fetchBooks() {
        if (allBooks.isEmpty()) isLoading = true
        isError = false
        isOffline = false

        scope.launch(Dispatchers.IO) {
            var cachedJsonString: String? = null
            try {
                if (cacheFile.exists()) {
                    cachedJsonString = cacheFile.readText()
                    val fetchedBooks = parseBooksJson(cachedJsonString)
                    if (fetchedBooks.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            if (allBooks.isEmpty()) {
                                allBooks = fetchedBooks
                                isLoading = false
                            }
                        }
                    }
                }
            } catch (e: Exception) { }

            try {
                val url = URL("https://odiabhasa.pages.dev/puruna-bahi/bahi.json")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    if (response != cachedJsonString) {
                        cacheFile.writeText(response)
                        val fetchedBooks = parseBooksJson(response)
                        withContext(Dispatchers.Main) {
                            allBooks = fetchedBooks
                            isLoading = false
                        }
                    } else {
                        withContext(Dispatchers.Main) { isLoading = false }
                    }
                } else {
                    throw Exception("Failed to connect")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (allBooks.isEmpty()) {
                        isError = true
                        isLoading = false
                    } else {
                        isOffline = true
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshDownloadedFiles()
        fetchBooks()
    }

    LaunchedEffect(allBooks, searchQuery, selectedCategories, selectedAuthors, selectedPublishers, selectedYears, showOnlyDownloaded, sortOption, downloadedFiles) {
        if (allBooks.isEmpty()) {
            filteredBooks = emptyList()
            return@LaunchedEffect
        }

        isFiltering = true
        delay(300)

        val result = withContext(Dispatchers.Default) {
            val queryEng = convertOdiaToEngNumerals(searchQuery).replace(Regex("[^0-9]"), "")
            val isJustPrefix = searchQuery.trim().lowercase() in listOf("puruna", "puruna_", "ପୁରୁଣା", "ପୁରୁଣା_")

            var filtered = allBooks.filter { book ->
                val numId = book.id.replace(Regex("[^0-9]"), "")

                val matchesSearch = if (searchQuery.isBlank()) true else {
                    if (isJustPrefix) false else {
                        book.title.contains(searchQuery, ignoreCase = true) ||
                                book.author.contains(searchQuery, ignoreCase = true) ||
                                book.publisher.contains(searchQuery, ignoreCase = true) ||
                                book.category.contains(searchQuery, ignoreCase = true) ||
                                (queryEng.isNotEmpty() && numId == queryEng)
                    }
                }

                if (!matchesSearch) return@filter false

                val bookCategories = book.category.split(",").map { it.trim() }
                val matchesCategory = selectedCategories.isEmpty() || bookCategories.any { selectedCategories.contains(it) }
                if (!matchesCategory) return@filter false

                val bookAuthors = book.author.split(",").map { it.trim() }
                val matchesAuthor = selectedAuthors.isEmpty() || bookAuthors.any { selectedAuthors.contains(it) }
                if (!matchesAuthor) return@filter false

                val bookPublishers = book.publisher.split(",").map { it.trim() }
                val matchesPublisher = selectedPublishers.isEmpty() || bookPublishers.any { selectedPublishers.contains(it) }
                if (!matchesPublisher) return@filter false

                val bookYears = book.year.split(",").map { it.trim() }
                val matchesYear = selectedYears.isEmpty() || bookYears.any { selectedYears.contains(it) }
                if (!matchesYear) return@filter false

                val fileName = "${book.id}_v${book.version}.pdf"
                val matchesDownloaded = !showOnlyDownloaded || downloadedFiles.contains(fileName)
                if (!matchesDownloaded) return@filter false

                true
            }

            if (sortOption != "କୌଣସିଟି ନୁହେଁ") {
                filtered = filtered.sortedWith { b1, b2 ->
                    when (sortOption) {
                        "ଅକ୍ଷର ଅନୁଯାୟୀ" -> b1.title.compareTo(b2.title)
                        "ପୁରୁଣା ରୁ ନୂଆ" -> extractYearAsInt(b1.year).compareTo(extractYearAsInt(b2.year))
                        "ନୂଆ ରୁ ପୁରୁଣା" -> extractYearAsInt(b2.year).compareTo(extractYearAsInt(b1.year))
                        else -> 0
                    }
                }
            }
            filtered
        }

        filteredBooks = result
        isFiltering = false
    }

    val availableCategories = remember(allBooks) { allBooks.flatMap { it.category.split(",") }.map { it.trim() }.filter { it.isNotBlank() }.distinct().sorted() }
    val availableAuthors = remember(allBooks) { allBooks.flatMap { it.author.split(",") }.map { it.trim() }.filter { it.isNotBlank() }.distinct().sorted() }
    val availablePublishers = remember(allBooks) { allBooks.flatMap { it.publisher.split(",") }.map { it.trim() }.filter { it.isNotBlank() }.distinct().sorted() }
    val availableYears = remember(allBooks) { allBooks.flatMap { it.year.split(",") }.map { it.trim() }.filter { it.isNotBlank() }.distinct().sorted() }

    if (selectedBook != null) {
        PurunaBahiDownloadSheet(
            book = selectedBook!!,
            onDismiss = { selectedBook = null },
            onRead = { title, filePath ->
                selectedBook = null
                onReadBook(title, filePath)
            },
            onDownloadStatusChanged = { refreshDownloadedFiles() }
        )
    }

    // -------------------------------------------------------------------------
    // ସଜାଡନ୍ତୁ (SORT) ବଟମ୍ ସିଟ୍
    // -------------------------------------------------------------------------
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            sheetState = sortSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "କିପରି ସଜାଡିବେ?",
                    fontFamily = OdiaFonti,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                sortOptionsList.forEach { option ->
                    val isSelected = sortOption == option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent)
                            .clickable {
                                sortOption = option
                                scope.launch {
                                    sortSheetState.hide()
                                    showSortSheet = false
                                    listState.scrollToItem(0)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = option,
                            fontFamily = OdiaFonti,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // ସନ୍ଧାନ ବିକଳ୍ପ (FILTER) ବଟମ୍ ସିଟ୍
    // -------------------------------------------------------------------------
    if (showFilterSheet) {
        val totalFiltersApplied = selectedCategories.size + selectedAuthors.size + selectedPublishers.size + selectedYears.size
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            var activeFilterTab by remember { mutableStateOf("ବିଭାଗ") }
            val tabs = listOf("ବିଭାଗ", "ଲେଖକ", "ପ୍ରକାଶକ", "ବର୍ଷ")
            var sheetSearchQuery by remember(activeFilterTab) { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ସନ୍ଧାନ ବିକଳ୍ପ",
                        fontFamily = OdiaFonti,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (totalFiltersApplied > 0) {
                        Text(
                            text = "${convertEngToOdiaPurunaBahiText(totalFiltersApplied.toString())}ଟି ବଛା ଯାଇଛି",
                            fontFamily = OdiaFonti,
                            fontSize = 14.sp,
                            color = Color(0xFFD84315),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .weight(0.35f)
                            .fillMaxHeight()
                            .background(Color(0xFFF5F5F5))
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = activeFilterTab == tab
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                                    .clickable { activeFilterTab = tab }
                                    .padding(vertical = 18.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = tab,
                                    fontFamily = OdiaFonti,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    VerticalDivider(modifier = Modifier.fillMaxHeight().width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))

                    Column(
                        modifier = Modifier
                            .weight(0.65f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        OutlinedTextField(
                            value = sheetSearchQuery,
                            onValueChange = { sheetSearchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            placeholder = { Text("ଖୋଜନ୍ତୁ...", fontFamily = OdiaFonti, color = Color.Gray, fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(20.dp), tint = Color.Gray) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                hintLocales = LocaleList(
                                    androidx.compose.ui.text.intl.Locale("or-Orya-IN"),
                                    androidx.compose.ui.text.intl.Locale("or-IN")
                                )
                            )
                        )

                        val rawList = when (activeFilterTab) {
                            "ବିଭାଗ" -> availableCategories
                            "ଲେଖକ" -> availableAuthors
                            "ପ୍ରକାଶକ" -> availablePublishers
                            "ବର୍ଷ" -> availableYears
                            else -> emptyList()
                        }

                        val currentList = if (sheetSearchQuery.isBlank()) rawList else {
                            rawList.filter { it.contains(sheetSearchQuery, ignoreCase = true) }
                        }

                        val currentSelectedSet = when (activeFilterTab) {
                            "ବିଭାଗ" -> selectedCategories
                            "ଲେଖକ" -> selectedAuthors
                            "ପ୍ରକାଶକ" -> selectedPublishers
                            "ବର୍ଷ" -> selectedYears
                            else -> emptySet()
                        }

                        if (currentList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("ମିଳିଲା ନାହିଁ", fontFamily = OdiaFonti, color = Color.Gray, fontSize = 14.sp)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(currentList) { item ->
                                    val isChecked = currentSelectedSet.contains(item)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val newSet = currentSelectedSet.toMutableSet()
                                                if (isChecked) newSet.remove(item) else newSet.add(item)
                                                when (activeFilterTab) {
                                                    "ବିଭାଗ" -> selectedCategories = newSet
                                                    "ଲେଖକ" -> selectedAuthors = newSet
                                                    "ପ୍ରକାଶକ" -> selectedPublishers = newSet
                                                    "ବର୍ଷ" -> selectedYears = newSet
                                                }
                                            }
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = null,
                                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = item,
                                            fontFamily = OdiaFonti,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedCategories = emptySet()
                            selectedAuthors = emptySet()
                            selectedPublishers = emptySet()
                            selectedYears = emptySet()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ସଫା କରନ୍ତୁ", fontFamily = OdiaFonti)
                    }
                    Button(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showFilterSheet = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ପ୍ରୟୋଗ କରନ୍ତୁ", fontFamily = OdiaFonti)
                    }
                }
            }
        }
    }
    // -------------------------------------------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ପୁରୁଣା ବହି", fontFamily = OdiaFonti, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    // Search Icon Appears when Header is Hidden (Scrolled down)
                    AnimatedVisibility(
                        visible = !isHeaderVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            // Only show the header, do not scroll to top
                            isHeaderVisible = true
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { fetchBooks() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (isOffline) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD32F2F))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ଇଣ୍ଟରନେଟ୍ ସଂଯୋଗ ନାହିଁ । କେବଳ ଡାଉନଲୋଡ୍ ହୋଇଥିବା ବହି ପଢିପାରିବେ ।",
                        fontFamily = OdiaFonti,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusManager.clearFocus() }
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Animating Header Content
                AnimatedVisibility(
                    visible = isHeaderVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            placeholder = { Text("ସନ୍ଧାନ କରନ୍ତୁ ବା ବହି ଆଇଡି ଦିଅନ୍ତୁ...", fontFamily = OdiaFonti, color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color.Gray)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search,
                                hintLocales = LocaleList(
                                    androidx.compose.ui.text.intl.Locale("or-Orya-IN"),
                                    androidx.compose.ui.text.intl.Locale("or-IN")
                                )
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            )
                        )

                        // Info and Sort Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ସମୁଦାୟ ${convertEngToOdiaPurunaBahiText(filteredBooks.size.toString())}ଟି ବହି",
                                fontFamily = OdiaFonti,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )

                            TextButton(onClick = { showSortSheet = true }) {
                                Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (sortOption == "କୌଣସିଟି ନୁହେଁ") "ସଜାଡନ୍ତୁ" else sortOption,
                                    fontFamily = OdiaFonti,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Active Filters Row (Without the Downloaded Chip)
                        val activeFilters = selectedCategories + selectedAuthors + selectedPublishers + selectedYears
                        if (activeFilters.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(activeFilters.toList()) { filterName ->
                                    InputChip(
                                        selected = true,
                                        onClick = {
                                            if (selectedCategories.contains(filterName)) selectedCategories -= filterName
                                            if (selectedAuthors.contains(filterName)) selectedAuthors -= filterName
                                            if (selectedPublishers.contains(filterName)) selectedPublishers -= filterName
                                            if (selectedYears.contains(filterName)) selectedYears -= filterName
                                        },
                                        label = { Text(filterName, fontFamily = OdiaFonti, fontSize = 13.sp) },
                                        trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp)) },
                                        colors = InputChipDefaults.inputChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // Lists
                if (isLoading || isFiltering) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
                    ) {
                        items(6) { SkeletonBookItem() }
                    }
                } else if (isError) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ତଥ୍ୟ ଆଣିବାରେ ବିଫଳ ହେଲା ।", fontFamily = OdiaFonti, color = Color.Red, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { fetchBooks() }, shape = RoundedCornerShape(10.dp)) {
                                Text("ପୁଣି ଚେଷ୍ଟା କରନ୍ତୁ", fontFamily = OdiaFonti)
                            }
                        }
                    }
                } else if (filteredBooks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("କୌଣସି ବହି ମିଳିଲା ନାହିଁ ।", fontFamily = OdiaFonti, color = Color.Gray, fontSize = 18.sp)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
                    ) {
                        items(filteredBooks, key = { it.id }) { book ->
                            val fileName = "${book.id}_v${book.version}.pdf"
                            val isCached = downloadedFiles.contains(fileName)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        selectedBook = book
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp, 20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(if (isCached) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isCached) Icons.Default.Check else Icons.Default.Book,
                                            contentDescription = null,
                                            tint = if (isCached) Color(0xFF2E7D32) else Color(0xFFD4AF37),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = book.title,
                                            fontFamily = OdiaFonti,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "ଲେଖକ: ${book.author}",
                                            fontFamily = OdiaFonti,
                                            fontSize = 14.sp,
                                            color = Color.DarkGray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "ପ୍ରକାଶକ: ${book.publisher} • ବର୍ଷ: ${book.year}",
                                            fontFamily = OdiaFonti,
                                            fontSize = 13.sp,
                                            color = Color.Gray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }

            // Scroll to Top FAB (Positioned above bottom toggle)
            AnimatedVisibility(
                visible = listState.firstVisibleItemIndex > 4,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 16.dp)
            ) {
                FloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
                }
            }

            // Bottom Centered Floating Toggle (All vs Downloaded)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val allSelected = !showOnlyDownloaded
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (allSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable {
                                    showOnlyDownloaded = false
                                    scope.launch { listState.scrollToItem(0) }
                                }
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "ସମସ୍ତ ବହି",
                                color = if (allSelected) Color.White else Color.Gray,
                                fontFamily = OdiaFonti,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (!allSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable {
                                    showOnlyDownloaded = true
                                    scope.launch { listState.scrollToItem(0) }
                                }
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "ଡାଉନଲୋଡ୍",
                                color = if (!allSelected) Color.White else Color.Gray,
                                fontFamily = OdiaFonti,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class PurunaBahiDownloadState {
    object Idle : PurunaBahiDownloadState()
    object FetchingSize : PurunaBahiDownloadState()
    data class Ready(val sizeMb: String) : PurunaBahiDownloadState()
    data class Downloading(val progress: Int) : PurunaBahiDownloadState()
    data class Downloaded(val file: File) : PurunaBahiDownloadState()
    data class Error(val message: String) : PurunaBahiDownloadState()
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface, fontWeight: FontWeight = FontWeight.Normal) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label : ",
            fontFamily = OdiaFonti,
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            fontFamily = OdiaFonti,
            fontSize = 15.sp,
            color = valueColor,
            fontWeight = fontWeight,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurunaBahiDownloadSheet(book: PurunaBahiData, onDismiss: () -> Unit, onRead: (String, String) -> Unit, onDownloadStatusChanged: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var state by remember { mutableStateOf<PurunaBahiDownloadState>(PurunaBahiDownloadState.Idle) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    fun checkFileState() {
        scope.launch(Dispatchers.IO) {
            val dir = File(context.filesDir, "puruna_books")
            if (!dir.exists()) dir.mkdirs()

            val targetFile = File(dir, "${book.id}_v${book.version}.pdf")
            val tempFile = File(dir, "${book.id}_v${book.version}.tmp")

            if (targetFile.exists() && targetFile.length() > 0) {
                withContext(Dispatchers.Main) {
                    state = PurunaBahiDownloadState.Downloaded(targetFile)
                }
            } else {
                if (tempFile.exists()) tempFile.delete()
                withContext(Dispatchers.Main) { state = PurunaBahiDownloadState.FetchingSize }

                try {
                    val connection = URL(book.url).openConnection() as HttpURLConnection
                    connection.instanceFollowRedirects = true
                    connection.requestMethod = "HEAD"
                    connection.connect()
                    val fileLength = connection.contentLength
                    val sizeMb = if (fileLength > 0) String.format(Locale.US, "%.1f", fileLength / (1024.0 * 1024.0)) else "ଅଜଣା"
                    withContext(Dispatchers.Main) { state = PurunaBahiDownloadState.Ready(sizeMb) }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { state = PurunaBahiDownloadState.Ready("ଅଜଣା (ଇଣ୍ଟରନେଟ୍ ଯାଞ୍ଚ କରନ୍ତୁ)") }
                }
            }
        }
    }

    LaunchedEffect(book) {
        state = PurunaBahiDownloadState.Idle
        checkFileState()
    }

    fun startDownload() {
        state = PurunaBahiDownloadState.Downloading(0)
        scope.launch(Dispatchers.IO) {
            val dir = File(context.filesDir, "puruna_books")
            val targetFile = File(dir, "${book.id}_v${book.version}.pdf")
            val tempFile = File(dir, "${book.id}_v${book.version}.tmp")

            try {
                var url = URL(book.url)
                var connection = url.openConnection() as HttpURLConnection
                var redirectCount = 0

                while (redirectCount < 5) {
                    connection.instanceFollowRedirects = true
                    connection.connect()
                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                        responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                        responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
                        responseCode == 307 || responseCode == 308) {

                        val newUrl = connection.getHeaderField("Location")
                        connection.disconnect()
                        url = URL(newUrl)
                        connection = url.openConnection() as HttpURLConnection
                        redirectCount++
                    } else {
                        break
                    }
                }

                val fileLength = connection.contentLength
                val input = connection.inputStream
                val output = FileOutputStream(tempFile)
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int

                while (input.read(data).also { count = it } != -1) {
                    total += count
                    if (fileLength > 0) {
                        val progress = (total * 100 / fileLength).toInt()
                        withContext(Dispatchers.Main) { state = PurunaBahiDownloadState.Downloading(progress) }
                    }
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                input.close()

                if (tempFile.exists()) {
                    tempFile.renameTo(targetFile)
                }

                withContext(Dispatchers.Main) {
                    state = PurunaBahiDownloadState.Downloaded(targetFile)
                    onDownloadStatusChanged()
                }
            } catch (e: Exception) {
                if (tempFile.exists()) tempFile.delete()
                withContext(Dispatchers.Main) { state = PurunaBahiDownloadState.Error("ଡାଉନଲୋଡ୍ ବିଫଳ ହେଲା, ଇଣ୍ଟରନେଟ୍ ଯାଞ୍ଚ କରନ୍ତୁ") }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { if (state !is PurunaBahiDownloadState.Downloading) onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))

                // Book Title Spacing Added Here
                Text(
                    text = book.title,
                    fontFamily = OdiaFonti,
                    fontSize = 20.sp,
                    lineHeight = 30.sp, // Important for 2 line titles
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color.LightGray.copy(alpha = 0.5f))

            val pureId = book.id.replace(Regex("[^0-9]"), "")
            if (pureId.isNotEmpty()) {
                DetailRow("ବହି ଆଇଡି", convertEngToOdiaPurunaBahiText(pureId))
            }
            DetailRow("ଲେଖକ/ସଂପାଦକ", book.author)
            DetailRow("ପ୍ରକାଶନ ବର୍ଷ", book.year)
            DetailRow("ଉତ୍ସ", book.source)
            DetailRow("ପ୍ରକାଶକ", book.publisher, Color(0xFFD84315), FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                when (val s = state) {
                    is PurunaBahiDownloadState.FetchingSize, PurunaBahiDownloadState.Idle -> {
                        Text("ଆକାର ଯାଞ୍ଚ ହେଉଛି...", fontFamily = OdiaFonti, fontSize = 15.sp, color = Color.DarkGray)
                    }
                    is PurunaBahiDownloadState.Ready -> {
                        Text("ବହିର ଆକାର : ${if (!s.sizeMb.startsWith("ଅଜଣା")) convertEngToOdiaPurunaBahiText(s.sizeMb) else s.sizeMb} ଏମବି", fontFamily = OdiaFonti, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    is PurunaBahiDownloadState.Downloading -> {
                        Column {
                            Text("ଡାଉନଲୋଡ୍ ହେଉଛି: ${convertEngToOdiaPurunaBahiText(s.progress.toString())}%", fontFamily = OdiaFonti, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = s.progress / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                    is PurunaBahiDownloadState.Downloaded -> {
                        Text("ବହିଟି ପଢିବା ପାଇଁ ପ୍ରସ୍ତୁତ ଅଛି ।", fontFamily = OdiaFonti, fontSize = 15.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                    is PurunaBahiDownloadState.Error -> {
                        Text(s.message, fontFamily = OdiaFonti, fontSize = 15.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state is PurunaBahiDownloadState.Downloaded) {
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.background(Color(0xFFFFEBEE), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (state !is PurunaBahiDownloadState.Downloading) {
                    TextButton(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    }) {
                        Text("ବନ୍ଦ କରନ୍ତୁ", fontFamily = OdiaFonti, color = Color.Gray, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                when (state) {
                    is PurunaBahiDownloadState.Downloaded -> {
                        Button(
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    onRead(book.title, (state as PurunaBahiDownloadState.Downloaded).file.absolutePath)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier.height(48.dp).padding(horizontal = 16.dp)
                        ) {
                            Text("ପଢନ୍ତୁ", fontFamily = OdiaFonti, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    is PurunaBahiDownloadState.Downloading -> {
                        Button(onClick = { }, enabled = false, shape = RoundedCornerShape(12.dp), modifier = Modifier.height(48.dp)) {
                            Text("ଅପେକ୍ଷା କରନ୍ତୁ", fontFamily = OdiaFonti, fontSize = 16.sp)
                        }
                    }
                    is PurunaBahiDownloadState.FetchingSize, PurunaBahiDownloadState.Idle -> {
                        Button(
                            onClick = { },
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.LightGray, disabledContentColor = Color.DarkGray),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("ଅପେକ୍ଷା କରନ୍ତୁ...", fontFamily = OdiaFonti, fontSize = 16.sp)
                        }
                    }
                    else -> {
                        Button(
                            onClick = { startDownload() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp).padding(horizontal = 8.dp)
                        ) {
                            Text("ଡାଉନଲୋଡ୍", fontFamily = OdiaFonti, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("ନିଶ୍ଚିତ କରନ୍ତୁ", fontFamily = OdiaFonti, fontWeight = FontWeight.Bold) },
            text = { Text("ଆପଣ ନିଶ୍ଚିତ କି ଏହି ବହିଟିକୁ ଡିଲିଟ୍ କରିବାକୁ ଚାହୁଁଛନ୍ତି?", fontFamily = OdiaFonti, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val dir = File(context.filesDir, "puruna_books")
                            val targetFile = File(dir, "${book.id}_v${book.version}.pdf")
                            if (targetFile.exists()) targetFile.delete()

                            withContext(Dispatchers.Main) {
                                showDeleteConfirm = false
                                checkFileState()
                                onDownloadStatusChanged()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("ହଁ", fontFamily = OdiaFonti, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("ନା", fontFamily = OdiaFonti, color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}
// --- END OF FILE PurunaBahi.kt ---
