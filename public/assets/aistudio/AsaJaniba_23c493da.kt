package `in`.sandeep.odiabhasa

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL

// --- Custom Colors ---
private val AppBackground = Color(0xFFF4F1EB)
private val HeaderBrown = Color(0xFF6D5240)
private val CardBackground = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF2C241B)
private val TextSecondary = Color(0xFF7A7063)
private val AccentOrange = Color(0xFFE65100)
private val SuccessGreen = Color(0xFF2E7D32)
private val DeleteRed = Color(0xFFD32F2F)

// --- Helper for Odia Numerals ---
fun String.toOdiaString(): String {
    val odiaDigits = mapOf(
        '0' to '୦', '1' to '୧', '2' to '୨', '3' to '୩', '4' to '୪',
        '5' to '୫', '6' to '୬', '7' to '୭', '8' to '୮', '9' to '୯'
    )
    return this.map { odiaDigits[it] ?: it }.joinToString("")
}

fun Int.toOdiaString() = this.toString().toOdiaString()

// --- Data Models ---
data class CategoryTab(val id: String, val title: String, val url: String, val icon: String)

data class WildlifeItem(
    val odiaName: String,
    val englishName: String,
    val scientificName: String,
    val image: String,
    val category: String,
    val descriptionOdia: String,
    val facts: List<String>,
    val extraDetails: Map<String, String>
)

data class CategoryData(
    val version: Int,
    val items: List<WildlifeItem>,
    val rawJson: String
)

data class AppConfig(
    val maintenanceMode: Boolean = false,
    val maintenanceMessage: String = "",
    val hiddenTabs: List<String> = emptyList(),
    val hasNotification: Boolean = false,
    val notifTitle: String = "",
    val notifMessage: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsaJanibaScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val masterCategories = listOf(
        CategoryTab("animals", "ପଶୁ ଓ ପକ୍ଷୀ", "https://imsbg.github.io/animals/odia-bhasa/data.json", "🦁"),
        CategoryTab("fruits", "ଫଳ", "https://imsbg.github.io/fruits-and-vegetables/data/fruits.json", "🥭"),
        CategoryTab("vegetables", "ପନିପରିବା", "https://imsbg.github.io/fruits-and-vegetables/data/vegetables.json", "🥔")
    )

    var appConfig by remember { mutableStateOf(AppConfig()) }
    var activeCategories by remember { mutableStateOf(masterCategories) }
    var selectedCategory by remember { mutableStateOf(masterCategories[0]) }

    var itemsList by remember { mutableStateOf<List<WildlifeItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Download / Offline States
    var isCategoryDownloaded by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }

    // Update mechanism states
    var isUpdateAvailable by remember { mutableStateOf(false) }
    var latestNetworkData by remember { mutableStateOf<CategoryData?>(null) }

    var selectedItemForDialog by remember { mutableStateOf<WildlifeItem?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showNotificationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        appConfig = fetchAppConfig()
        activeCategories = masterCategories.filter { tab -> !appConfig.hiddenTabs.contains(tab.id) }
        if (activeCategories.isNotEmpty() && !activeCategories.contains(selectedCategory)) {
            selectedCategory = activeCategories[0]
        }
    }

    LaunchedEffect(selectedCategory) {
        isLoading = true
        isUpdateAvailable = false
        latestNetworkData = null

        // Check if this category is marked as downloaded
        isCategoryDownloaded = File(context.cacheDir, "offline_flag_${selectedCategory.id}").exists()

        val cached = withContext(Dispatchers.IO) { getCachedData(context, selectedCategory) }
        if (cached != null && cached.items.isNotEmpty()) {
            itemsList = cached.items
            isLoading = false
        }

        val network = withContext(Dispatchers.IO) { getNetworkData(selectedCategory) }
        if (network != null) {
            if (cached == null || cached.items.isEmpty()) {
                itemsList = network.items
                isLoading = false
                withContext(Dispatchers.IO) { saveToCache(context, selectedCategory, network.rawJson) }
            } else if (network.version > cached.version) {
                latestNetworkData = network
                isUpdateAvailable = true
            }
        } else {
            isLoading = false
        }
    }

    val displayedItems = if (searchQuery.isBlank()) {
        itemsList
    } else {
        itemsList.filter {
            it.odiaName.contains(searchQuery, ignoreCase = true) ||
                    it.englishName.contains(searchQuery, ignoreCase = true)
        }
    }

    // Estimate Size (Assuming avg ~150KB per image)
    val estimatedSizeMB = String.format("%.1f", (itemsList.size * 0.15)).toOdiaString()

    Scaffold(containerColor = AppBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InteractiveHeader(
                onBack = {
                    if (isSearchActive) {
                        isSearchActive = false
                        searchQuery = ""
                    } else {
                        onBack()
                    }
                },
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchClick = { isSearchActive = true },
                hasNotification = appConfig.hasNotification,
                onNotificationClick = { showNotificationDialog = true }
            )

            if (appConfig.maintenanceMode) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Text("⚠️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = appConfig.maintenanceMessage.ifEmpty { "ସର୍ଭର ମେଣ୍ଟେନାନ୍ସ ଚାଲିଛି..." },
                            fontFamily = OdiaFont,
                            fontSize = 18.sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(count = activeCategories.size) { index ->
                        val category = activeCategories[index]
                        CategoryChip(
                            category = category,
                            isSelected = selectedCategory.id == category.id,
                            onClick = {
                                selectedCategory = category
                                isSearchActive = false
                                searchQuery = ""
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content Count & Offline Controls using SVGs
                if (!isLoading && displayedItems.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ମୋଟ: ${displayedItems.size.toOdiaString()}",
                            fontFamily = OdiaFont,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )

                        if (isDownloading) {
                            Text(
                                text = "ଡାଉନଲୋଡ୍... ${(downloadProgress * 100).toInt().toOdiaString()}%",
                                fontFamily = OdiaFont,
                                color = AccentOrange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (isCategoryDownloaded) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SuccessGreen.copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_check), // YOUR SVG
                                            contentDescription = "Downloaded",
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ସେଭ୍ ହୋଇଛି", // "Saved"
                                            fontFamily = OdiaFont,
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                // Delete Asset Button
                                IconButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(DeleteRed.copy(alpha = 0.1f), RoundedCornerShape(50))
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delete), // YOUR SVG
                                        contentDescription = "Delete",
                                        tint = DeleteRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        } else {
                            Surface(
                                onClick = { showDownloadDialog = true },
                                shape = RoundedCornerShape(50),
                                color = AccentOrange.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_download), // YOUR SVG
                                        contentDescription = "Download",
                                        tint = AccentOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ଅଫଲାଇନ୍ ସେଭ୍ (~$estimatedSizeMB ଏମବି)",
                                        fontFamily = OdiaFont,
                                        color = AccentOrange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(6) { ShimmerItemCard() }
                        }
                    } else if (displayedItems.isEmpty()) {
                        Text(
                            text = "କିଛି ମିଳିଲା ନାହିଁ 😕",
                            fontFamily = OdiaFont,
                            color = TextSecondary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(count = displayedItems.size) { index ->
                                val item = displayedItems[index]
                                ItemCard(item = item, onClick = { selectedItemForDialog = item })
                            }
                        }
                    }

                    // Floating Update Button
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isUpdateAvailable,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                latestNetworkData?.let { freshData ->
                                    itemsList = freshData.items
                                    isUpdateAvailable = false
                                    coroutineScope.launch(Dispatchers.IO) {
                                        saveToCache(context, selectedCategory, freshData.rawJson)
                                    }
                                }
                            },
                            containerColor = AccentOrange,
                            contentColor = Color.White,
                            icon = { Icon(Icons.Default.Refresh, contentDescription = "Refresh") },
                            text = { Text("ନୂଆ ତଥ୍ୟ ଆସିଛି - ଅପଡେଟ୍ କରନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }
    }

    // Offline Download Dialog
    if (showDownloadDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadDialog = false },
            title = { Text("ଅଫଲାଇନ୍ ଡାଉନଲୋଡ୍", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
            text = { Text("ବିନା ଇଣ୍ଟରନେଟରେ ଦେଖିବା ପାଇଁ ଆପଣ ଏହି ବିଭାଗର ସମସ୍ତ ଫଟୋ ଡାଉନଲୋଡ୍ କରିବାକୁ ଚାହୁଁଛନ୍ତି କି? (ପାଖାପାଖି $estimatedSizeMB ଏମବି ଡାଟା ଖର୍ଚ୍ଚ ହେବ)", fontFamily = OdiaFont, lineHeight = 22.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showDownloadDialog = false
                    isDownloading = true
                    downloadProgress = 0f
                    coroutineScope.launch(Dispatchers.IO) {
                        val loader = context.imageLoader
                        val total = itemsList.size
                        var count = 0
                        itemsList.forEach { item ->
                            if (item.image.isNotEmpty()) {
                                val req = ImageRequest.Builder(context).data(item.image).build()
                                loader.execute(req)
                            }
                            count++
                            withContext(Dispatchers.Main) { downloadProgress = count.toFloat() / total }
                        }

                        // Mark as downloaded
                        File(context.cacheDir, "offline_flag_${selectedCategory.id}").createNewFile()

                        delay(500)
                        withContext(Dispatchers.Main) {
                            isDownloading = false
                            isCategoryDownloaded = true
                            downloadProgress = 0f
                        }
                    }
                }) {
                    Text("ହଁ, ଡାଉନଲୋଡ୍ କରନ୍ତୁ", fontFamily = OdiaFont, color = AccentOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDownloadDialog = false }) {
                    Text("ବାତିଲ୍", fontFamily = OdiaFont, color = TextSecondary)
                }
            },
            containerColor = Color.White
        )
    }

    // Delete Assets Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("ଡିଲିଟ୍ କରନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = DeleteRed) },
            text = { Text("ଆପଣ ଏହି ବିଭାଗର ସମସ୍ତ ଅଫଲାଇନ୍ ଫଟୋ ଡିଲିଟ୍ କରିବାକୁ ଚାହୁଁଛନ୍ତି କି? ଏହାଦ୍ୱାରା ମୋବାଇଲର ଷ୍ଟୋରେଜ୍ ଖାଲି ହେବ।", fontFamily = OdiaFont, lineHeight = 22.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    coroutineScope.launch(Dispatchers.IO) {
                        val loader = context.imageLoader
                        itemsList.forEach { item ->
                            if (item.image.isNotEmpty()) {
                                loader.diskCache?.remove(item.image) // Remove from Coil disk cache
                            }
                        }
                        File(context.cacheDir, "offline_flag_${selectedCategory.id}").delete()
                        withContext(Dispatchers.Main) { isCategoryDownloaded = false }
                    }
                }) {
                    Text("ହଁ, ଡିଲିଟ୍ କରନ୍ତୁ", fontFamily = OdiaFont, color = DeleteRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ବାତିଲ୍", fontFamily = OdiaFont, color = TextSecondary)
                }
            },
            containerColor = Color.White
        )
    }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text(appConfig.notifTitle, fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
            text = { Text(appConfig.notifMessage, fontFamily = OdiaFont) },
            confirmButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text("ଠିକ୍ ଅଛି", fontFamily = OdiaFont, color = HeaderBrown)
                }
            },
            containerColor = Color.White
        )
    }

    if (selectedItemForDialog != null) {
        ItemDetailBottomSheet(
            item = selectedItemForDialog!!,
            onDismiss = { selectedItemForDialog = null }
        )
    }
}

@Composable
fun InteractiveHeader(
    onBack: () -> Unit,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    hasNotification: Boolean,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBackground)
            .padding(top = 40.dp, start = 8.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }

        if (isSearchActive) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(end = 8.dp),
                placeholder = { Text("ଖୋଜନ୍ତୁ...", fontFamily = OdiaFont, color = TextSecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.05f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    cursorColor = TextPrimary
                ),
                shape = RoundedCornerShape(25.dp)
            )
        } else {
            Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                Text(
                    text = "ଆସ ଜାଣିବା",
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = TextPrimary
                )
            }
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
            }
            Box(modifier = Modifier.clickable { onNotificationClick() }) {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextPrimary)
                }
                if (hasNotification) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 12.dp, end = 12.dp)
                            .size(10.dp)
                            .background(AccentOrange, RoundedCornerShape(50))
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: CategoryTab, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) HeaderBrown else Color.White
    val contentColor = if (isSelected) Color.White else TextPrimary

    Surface(
        shape = RoundedCornerShape(50),
        color = bgColor,
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = Modifier
            .height(48.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = category.icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.title,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = contentColor
            )
        }
    }
}

@Composable
fun ItemCard(item: WildlifeItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.image)
                    .crossfade(true)
                    .build(),
                contentDescription = item.odiaName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                loading = {
                    Box(modifier = Modifier.fillMaxSize().shimmerEffect())
                },
                error = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_monochrome),
                        contentDescription = "Error",
                        contentScale = ContentScale.Crop
                    )
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.odiaName,
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.englishName,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailBottomSheet(item: WildlifeItem, onDismiss: () -> Unit) {
    var showFullScreenImage by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    if (showFullScreenImage) {
        Dialog(
            onDismissRequest = { showFullScreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.odiaName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize().shimmerEffect())
                    },
                    error = {
                        Image(painterResource(id = R.drawable.logo_monochrome), contentDescription = "Error")
                    }
                )
                IconButton(
                    onClick = { showFullScreenImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppBackground,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Banner Image
                Box(modifier = Modifier.fillMaxWidth()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.odiaName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable { showFullScreenImage = true },
                        loading = {
                            Box(modifier = Modifier.fillMaxSize().shimmerEffect())
                        },
                        error = {
                            Image(
                                painter = painterResource(id = R.drawable.logo_monochrome),
                                contentDescription = "Error",
                                contentScale = ContentScale.Crop
                            )
                        }
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                    ) {
                        Text(
                            text = "🔍 ଟ୍ୟାପ୍ କରନ୍ତୁ",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontFamily = OdiaFont
                        )
                    }
                }

                // Text Content
                Column(modifier = Modifier.padding(24.dp)) {
                    Surface(
                        color = HeaderBrown.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.category,
                            fontFamily = OdiaFont,
                            fontSize = 12.sp,
                            color = HeaderBrown,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = item.odiaName, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = TextPrimary)
                    Text(text = item.englishName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    if (item.scientificName.isNotEmpty() && item.scientificName != "Unknown") {
                        Text(text = "Sc: ${item.scientificName}", fontSize = 14.sp, fontStyle = FontStyle.Italic, color = TextSecondary.copy(alpha = 0.8f))
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = item.descriptionOdia, fontFamily = OdiaFont, fontSize = 18.sp, color = TextPrimary, lineHeight = 26.sp)

                    // Dynamic details loading for Origin, Soil, Taste, Color etc.
                    if (item.extraDetails.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            color = HeaderBrown.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(
                                    text = "ଅଧିକ ତଥ୍ୟ",
                                    fontFamily = OdiaFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = HeaderBrown
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                item.extraDetails.forEach { (key, value) ->
                                    val cleanKey = key.replace("_", " ")
                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                        Text(
                                            text = "$cleanKey :",
                                            fontFamily = OdiaFont,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            modifier = Modifier.weight(0.4f)
                                        )
                                        Text(
                                            text = value.toOdiaString(), // Convert any digits in value to Odia
                                            fontFamily = OdiaFont,
                                            color = TextSecondary,
                                            fontSize = 15.sp,
                                            modifier = Modifier.weight(0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (item.facts.isNotEmpty()) {
                        Text(text = "ବିଶେଷ ତଥ୍ୟ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = HeaderBrown)
                        Spacer(modifier = Modifier.height(12.dp))

                        for (fact in item.facts) {
                            Row(modifier = Modifier.padding(bottom = 12.dp), verticalAlignment = Alignment.Top) {
                                Text(text = "🐾", fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, end = 8.dp))
                                Text(text = fact.toOdiaString(), fontFamily = OdiaFont, fontSize = 16.sp, color = TextPrimary, lineHeight = 24.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            val showFloatingButton by remember {
                derivedStateOf {
                    item.facts.isNotEmpty() && scrollState.value < (scrollState.maxValue - 50)
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = showFloatingButton,
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Surface(
                    onClick = {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue, animationSpec = tween(700))
                        }
                    },
                    shape = RoundedCornerShape(50),
                    color = HeaderBrown,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ବିଶେଷ ତଥ୍ୟ ପାଇଁ ତଳକୁ ଯାଆନ୍ତୁ",
                            color = Color.White,
                            fontFamily = OdiaFont,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Scroll Down", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerItemCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .shimmerEffect()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.height(18.dp).fillMaxWidth(0.8f).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.height(12.dp).fillMaxWidth(0.5f).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            }
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer_anim"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE0E0E0),
                Color(0xFFF5F5F5),
                Color(0xFFE0E0E0)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

// --- Network & Cache Fetchers ---

suspend fun fetchAppConfig(): AppConfig = withContext(Dispatchers.IO) {
    try {
        val result = URL("https://imsbg.github.io/animals/notification.json").readText()
        val jsonObj = JSONObject(result)

        val hiddenList = mutableListOf<String>()
        if (jsonObj.has("hiddenTabs")) {
            val array = jsonObj.getJSONArray("hiddenTabs")
            for (i in 0 until array.length()) hiddenList.add(array.getString(i))
        }

        val notifObj = jsonObj.optJSONObject("notification")

        AppConfig(
            maintenanceMode = jsonObj.optBoolean("maintenanceMode", false),
            maintenanceMessage = jsonObj.optString("maintenanceMessage", ""),
            hiddenTabs = hiddenList,
            hasNotification = notifObj?.optBoolean("hasNew", false) ?: false,
            notifTitle = notifObj?.optString("title", "") ?: "",
            notifMessage = notifObj?.optString("message", "") ?: ""
        )
    } catch (e: Exception) {
        e.printStackTrace()
        AppConfig()
    }
}

fun getCacheFile(context: Context, categoryId: String): File {
    return File(context.cacheDir, "wildlife_cache_$categoryId.json")
}

fun parseWildlifeJson(jsonString: String): CategoryData {
    val jsonArray = JSONArray(jsonString)
    val itemsList = mutableListOf<WildlifeItem>()

    var version = 0
    if (jsonArray.length() > 0) {
        version = jsonArray.getJSONObject(0).optInt("version", 0)
    }

    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)

        val factsList = mutableListOf<String>()
        val extraDetails = mutableMapOf<String, String>()

        if (jsonObject.has("details")) {
            val detailsObj = jsonObject.getJSONObject("details")
            val keys = detailsObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (key == "ବିଶେଷ_ତଥ୍ୟ") {
                    val factsArray = detailsObj.getJSONArray(key)
                    for (j in 0 until factsArray.length()) {
                        factsList.add(factsArray.getString(j))
                    }
                } else {
                    extraDetails[key] = detailsObj.getString(key)
                }
            }
        }

        itemsList.add(
            WildlifeItem(
                odiaName = jsonObject.optString("odiaName", "Unknown"),
                englishName = jsonObject.optString("name", "Unknown"),
                scientificName = jsonObject.optString("scientificName", ""),
                image = jsonObject.optString("image", ""),
                category = jsonObject.optString("category", ""),
                descriptionOdia = jsonObject.optString("descriptionOdia", ""),
                facts = factsList,
                extraDetails = extraDetails
            )
        )
    }

    return CategoryData(version, itemsList, jsonString)
}

suspend fun getCachedData(context: Context, category: CategoryTab): CategoryData? = withContext(Dispatchers.IO) {
    try {
        val file = getCacheFile(context, category.id)
        if (file.exists()) {
            parseWildlifeJson(file.readText())
        } else null
    } catch (e: Exception) {
        null
    }
}

suspend fun getNetworkData(category: CategoryTab): CategoryData? = withContext(Dispatchers.IO) {
    try {
        val jsonString = URL(category.url).readText()
        parseWildlifeJson(jsonString)
    } catch (e: Exception) {
        null
    }
}

fun saveToCache(context: Context, category: CategoryTab, jsonString: String) {
    try {
        getCacheFile(context, category.id).writeText(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}