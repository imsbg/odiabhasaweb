package `in`.sandeep.odiabhasa

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryOdishaScreen(onBack: () -> Unit) {
    var uiState by remember { mutableStateOf<GenericHistoryUiState>(GenericHistoryUiState.Loading) }
    var retryTrigger by remember { mutableStateOf(0) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFontDialog by remember { mutableStateOf(false) }
    var fontSizeAdjustment by remember { mutableStateOf(0) }

    // Get Context to access file storage
    val context = LocalContext.current

    LaunchedEffect(retryTrigger) {
        uiState = GenericHistoryUiState.Loading
        try {
            val data = withContext(Dispatchers.IO) {
                val urlString = "https://imsbg.github.io/odiabhasa/bahi/more/odisharaitihasa.json"
                val cacheFileName = "history_odisha_cache.json"
                val cacheFile = File(context.filesDir, cacheFileName)
                val gson = Gson()

                try {
                    // 1. Attempt to fetch online content
                    val onlineJsonText = URL(urlString).readText()
                    val onlineData = gson.fromJson(onlineJsonText, GenericHistoryScreenData::class.java)

                    // 2. Check and Update Cache logic
                    if (cacheFile.exists()) {
                        val localJsonText = cacheFile.readText()
                        val localData = gson.fromJson(localJsonText, GenericHistoryScreenData::class.java)

                        // If online version is higher, update the local file
                        if (onlineData.version > localData.version) {
                            cacheFile.writeText(onlineJsonText)
                        }
                    } else {
                        // First time: save to cache
                        cacheFile.writeText(onlineJsonText)
                    }

                    // Return the fresh online data
                    onlineData

                } catch (e: Exception) {
                    // 3. Offline Fallback: If network fails, try to load from cache
                    if (cacheFile.exists()) {
                        val localJsonText = cacheFile.readText()
                        gson.fromJson(localJsonText, GenericHistoryScreenData::class.java)
                    } else {
                        // No network and no cache -> Throw exception
                        throw e
                    }
                }
            }
            uiState = GenericHistoryUiState.Success(data)
        } catch (e: Exception) {
            Log.e("HistoryOdishaScreen", "Failed to fetch data", e)
            uiState = GenericHistoryUiState.Error
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            val screenTitle = (uiState as? GenericHistoryUiState.Success)?.data?.title ?: "ଓଡ଼ିଶାର ଇତିହାସ"
            HistoryTopAppBar(
                title = screenTitle,
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchActivated = { isSearchActive = true },
                onSearchDeactivated = { isSearchActive = false; searchQuery = "" },
                onFontClick = { showFontDialog = true },
                onBack = onBack
            )
        }
    ) { padding ->
        if (showFontDialog) {
            FontAdjustmentDialog(
                currentAdjustment = fontSizeAdjustment,
                onDismiss = { showFontDialog = false },
                onAdjust = { newAdjustment -> fontSizeAdjustment = newAdjustment }
            )
        }

        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is GenericHistoryUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                is GenericHistoryUiState.Error -> ErrorScreen(onRetry = { retryTrigger++ })
                is GenericHistoryUiState.Success -> {
                    HistoryContent(
                        data = state.data,
                        searchQuery = searchQuery,
                        fontSizeAdjustment = fontSizeAdjustment
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryContent(data: GenericHistoryScreenData, searchQuery: String, fontSizeAdjustment: Int) {
    LocalUriHandler.current

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        AsyncImage(
            model = data.imageUrl ?: "",
            contentDescription = "Odisha History",
            modifier = Modifier.fillMaxWidth().height(280.dp).padding(16.dp).clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            val filteredContent = remember(searchQuery, data.content) {
                if (searchQuery.isBlank()) data.content else data.content?.filter { item ->
                    when (item.type) {
                        "sectionTitle", "subHeading", "minorHeading", "bodyText" ->
                            item.text?.contains(searchQuery, ignoreCase = true) == true
                        "table" -> item.data?.rows?.any { row -> row.any { cell -> cell.contains(searchQuery, ignoreCase = true) } } == true
                        else -> true
                    }
                }
            }

            if (filteredContent.isNullOrEmpty() && searchQuery.isNotBlank()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("କିଛି ମିଳିଲା ନାହିଁ", fontFamily = OdiaFont, fontSize = 18.sp)
                }
            } else {
                filteredContent?.forEach { item ->
                    when (item.type) {
                        "sectionTitle" -> item.text?.let { SectionTitle(it, searchQuery, fontSizeAdjustment) }
                        "subHeading" -> item.text?.let { SubHeading(it, searchQuery, fontSizeAdjustment) }
                        "minorHeading" -> item.text?.let { MinorHeading(it, searchQuery, fontSizeAdjustment) }
                        "bodyText" -> item.text?.let { BodyText(it, searchQuery, fontSizeAdjustment) }
                        "spacer" -> item.height?.let { Spacer(Modifier.height(it.dp)) }
                        "table" -> item.data?.let { SimpleTable(it, searchQuery, fontSizeAdjustment) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            DisclaimerBox(
                text = data.disclaimer ?: "ଏଠାରେ ଉପଲବ୍ଧ ସମସ୍ତ ଶିକ୍ଷାମୂଳକ ସାମଗ୍ରୀ ବିଭିନ୍ନ ବିଶ୍ୱାସଯୋଗ୍ୟ ପୁସ୍ତକ ଓ ଇଣ୍ଟରନେଟ୍\u200Cରୁ ସଂଗୃହୀତ । ଆମେ ଶିଶୁମାନଙ୍କ ପାଇଁ ଏକ ସୁରକ୍ଷିତ ଏବଂ ଶୁଦ୍ଧ ଶିକ୍ଷଣ ପରିବେଶ ଯୋଗାଇଦେବାକୁ ପ୍ରତିବଦ୍ଧ । ଯଦି ଏଥିରେ କୌଣସି ତ୍ରୁଟି ପରିଲକ୍ଷିତ ହୁଏ, ତେବେ ଦୟାକରି ଆମକୁ ଜଣାନ୍ତୁ; ଆମେ ତୁରନ୍ତ ଏହାର ସଂଶୋଧନ କରିବୁ ।",
                searchQuery = searchQuery, fontSizeAdjustment = fontSizeAdjustment
            )

        }
    }
}