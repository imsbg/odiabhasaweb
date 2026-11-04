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
fun HistoryLangScreen(onBack: () -> Unit) {
    var uiState by remember { mutableStateOf<GenericHistoryUiState>(GenericHistoryUiState.Loading) }
    var retryTrigger by remember { mutableStateOf(0) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFontDialog by remember { mutableStateOf(false) }
    var fontSizeAdjustment by remember { mutableStateOf(0) }

    // Context is required to access local file storage for caching
    val context = LocalContext.current

    LaunchedEffect(retryTrigger) {
        uiState = GenericHistoryUiState.Loading
        try {
            val data = withContext(Dispatchers.IO) {
                // This screen points to the odiabhasa.json URL
                val urlString = "https://imsbg.github.io/odiabhasa/bahi/more/odiabhasa.json"
                val cacheFileName = "history_lang_cache.json"
                val cacheFile = File(context.filesDir, cacheFileName)
                val gson = Gson()

                try {
                    // 1. Attempt to fetch online content
                    val onlineJsonText = URL(urlString).readText()
                    val onlineData = gson.fromJson(onlineJsonText, GenericHistoryScreenData::class.java)

                    // 2. Check if we have a local cache to compare versions
                    if (cacheFile.exists()) {
                        val localJsonText = cacheFile.readText()
                        val localData = gson.fromJson(localJsonText, GenericHistoryScreenData::class.java)

                        // 3. Compare versions: If online is newer, update cache. Otherwise, use local.
                        // Note: Ensure your GenericHistoryScreenData class has a 'version' field (Int)
                        if (onlineData.version > localData.version) {
                            cacheFile.writeText(onlineJsonText) // Update cache
                            onlineData
                        } else {
                            localData // Use cached version if online is not newer
                        }
                    } else {
                        // No cache exists, save the online version and use it
                        cacheFile.writeText(onlineJsonText)
                        onlineData
                    }
                } catch (e: Exception) {
                    // 4. Offline Fallback: Network failed, try to load from cache
                    if (cacheFile.exists()) {
                        val localJsonText = cacheFile.readText()
                        gson.fromJson(localJsonText, GenericHistoryScreenData::class.java)
                    } else {
                        // No network and no cache -> Throw exception to trigger Error state
                        throw e
                    }
                }
            }
            uiState = GenericHistoryUiState.Success(data)
        } catch (e: Exception) {
            Log.e("HistoryLangScreen", "କିଛି ଗୋଟେ ଅସୁବିଧା ହେଲା", e)
            uiState = GenericHistoryUiState.Error
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            val screenTitle = (uiState as? GenericHistoryUiState.Success)?.data?.title ?: "ଓଡ଼ିଆ ଭାଷାର ଇତିହାସ"
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
                    // This screen uses the exact same content layout as the other one
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