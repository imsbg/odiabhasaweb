package `in`.sandeep.odiabhasa

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

// ==========================================
// 1. GLOBALS, UTILS & PREFERENCES
// ==========================================

val DetailPinkStart = Color(0xFFF48FB1)
val DetailPinkEnd = Color(0xFFEC407A)
private var hasSessionHintShown = false

class GridPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("odia_grid_settings", Context.MODE_PRIVATE)

    var showDeprecated: Boolean
        get() = prefs.getBoolean("show_deprecated", true)
        set(value) = prefs.edit().putBoolean("show_deprecated", value).apply()

    var showAudioIcon: Boolean
        get() = prefs.getBoolean("show_audio", true)
        set(value) = prefs.edit().putBoolean("show_audio", value).apply()

    var columnCount: Int
        get() = prefs.getInt("column_count", 3)
        set(value) = prefs.edit().putInt("column_count", value).apply()
}

fun playSound(context: Context, fileName: String) {
    try {
        val assetFileDescriptor = context.assets.openFd(fileName)
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        assetFileDescriptor.close()
        mediaPlayer.prepare()
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun toOdiaDigit(number: Int): String {
    val odiaDigits = listOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return number.toString().map { char ->
        odiaDigits[char.toString().toInt()]
    }.joinToString("")
}

// ==========================================
// 2. COMMON GRID PAGE
// ==========================================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CommonGridPage(
    title: String,
    dataList: List<String>,
    gridState: LazyGridState,
    onBack: () -> Unit,
    onItemClick: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val prefs = remember { GridPreferences(context) }
    val scope = rememberCoroutineScope()

    var showDeprecated by remember { mutableStateOf(prefs.showDeprecated) }
    var showAudioGlobal by remember { mutableStateOf(prefs.showAudioIcon) }
    var columnCount by remember { mutableIntStateOf(prefs.columnCount) }

    val showScrollToTop by remember { derivedStateOf { gridState.firstVisibleItemIndex > 0 } }

    fun updateShowDeprecated(newVal: Boolean) { showDeprecated = newVal; prefs.showDeprecated = newVal }
    fun updateShowAudio(newVal: Boolean) { showAudioGlobal = newVal; prefs.showAudioIcon = newVal }
    fun updateColumnCount(newVal: Int) { columnCount = newVal; prefs.columnCount = newVal }

    fun resetDefaults() {
        updateColumnCount(3)
        updateShowAudio(true)
        updateShowDeprecated(true)
    }

    var showSettingsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var availableAudioMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val files = context.assets.list("")
                if (files != null) {
                    availableAudioMap = files
                        .filter { it.endsWith(".mp3", ignoreCase = true) }
                        .associateBy { it.substringBeforeLast(".") }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    val finalDisplayList = remember(dataList, showDeprecated) {
        if (showDeprecated) dataList else dataList.filter { !deprecatedList.contains(it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = title,
                        fontSize = 26.sp,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollToTop,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (gridState.firstVisibleItemIndex > 15) gridState.scrollToItem(15)
                            gridState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
                }
            }
        }
    ) { padding ->

        val dynamicFontSize = when (columnCount) {
            1 -> 120.sp; 2 -> 80.sp; 3 -> 45.sp; 4 -> 32.sp; 5 -> 24.sp; else -> 18.sp
        }
        val iconSize = if (columnCount == 1) 48.dp else 20.dp
        val iconPadding = if (columnCount == 1) 12.dp else 4.dp
        val iconTouchArea = if (columnCount == 1) 64.dp else 32.dp

        val isGridTooSmallForAudio = columnCount >= 4
        val isAudioVisible = showAudioGlobal && !isGridTooSmallForAudio

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            state = gridState,
            contentPadding = PaddingValues(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            items(finalDisplayList, key = { it }) { char ->
                val isDeprecated = deprecatedList.contains(char)
                val audioFileName = availableAudioMap[char]
                val hasAudio = audioFileName != null

                val borderColor = if (isDeprecated) Color(0xFFEF9A9A) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                val textColor = if (isDeprecated) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "char-$char"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        )
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(2.dp, borderColor, RoundedCornerShape(24.dp))
                        .clickable { onItemClick(char) }
                ) {
                    Text(
                        text = char,
                        fontSize = dynamicFontSize,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Normal,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        lineHeight = dynamicFontSize
                    )

                    if (hasAudio && isAudioVisible) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(iconPadding)
                                .size(iconTouchArea)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { playSound(context, audioFileName!!) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ପୃଷ୍ଠା ସଜାଣି", fontSize = 22.sp, fontWeight = FontWeight.Normal, fontFamily = OdiaFont)
                    TextButton(onClick = { resetDefaults() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ପୁନଃସ୍ଥାପନ", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ପୁରାତନ ବର୍ଣ୍ଣ ଦେଖାନ୍ତୁ", fontSize = 18.sp, fontFamily = OdiaFont)
                        Text("ପୁରାତନ ଅକ୍ଷରଗୁଡ଼ିକୁ ଦେଖନ୍ତୁ / ଲୁଚାନ୍ତୁ", fontSize = 13.sp, color = Color.Gray)
                    }
                    Switch(checked = showDeprecated, onCheckedChange = { updateShowDeprecated(it) })
                }
                Spacer(modifier = Modifier.height(16.dp))

                val isTooSmall = columnCount >= 4
                Row(
                    modifier = Modifier.fillMaxWidth().alpha(if (isTooSmall) 0.5f else 1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ସ୍ୱର ବଟନ୍ ଦେଖାନ୍ତୁ", fontSize = 18.sp, fontFamily = OdiaFont)
                        if(isTooSmall) {
                            Text("ସାଧାରଣ ବା ଛୋଟ ଆକାରରେ ଉପଲବ୍ଧ ନାହିଁ", fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("କେବଳ ସ୍ଵର ଓ ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ ପାଇଁ", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = showAudioGlobal,
                        onCheckedChange = { isChecked ->
                            if (isChecked && isTooSmall) updateColumnCount(3)
                            updateShowAudio(isChecked)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text("ଆକାର ପରିବର୍ତ୍ତନ", fontSize = 18.sp, fontFamily = OdiaFont)
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = columnCount.toFloat(),
                    onValueChange = { updateColumnCount(it.toInt()) },
                    valueRange = 1f..6f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = when {
                        columnCount == 1 -> "ବହୁତ ବଡ଼"
                        columnCount == 2 -> "ବଡ଼"
                        columnCount == 3 -> "ମଧ୍ୟମ"
                        columnCount == 4 -> "ସାଧାରଣ"
                        else -> "ବହୁତ ଛୋଟ"
                    },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = OdiaFont,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// ==========================================
// 3. DETAIL SCREEN
// ==========================================

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DetailScreen(
    title: String,
    initialChar: String,
    fullList: List<String>,
    onBack: () -> Unit,
    onCharChanged: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val initialIndex = fullList.indexOf(initialChar).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { fullList.size })
    val scope = rememberCoroutineScope()

    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var lastSpokenText by remember { mutableStateOf("") }
    var currentSpeechRate by remember { mutableFloatStateOf(0.50f) }

    // Check if the current screen list is for Numbers, Phala, Matra, or Barna Parichaya
    // to strictly disable the canvas drawing functionality
    val isDrawingDisabled = remember(title, fullList) {
        val titleLower = title.lowercase()
        val disableKeywords = listOf(
            "sankhya", "phala", "matra", "barna",
            "ସଂଖ୍ୟା", "ଫଳା", "ମାତ୍ରା", "ବର୍ଣ୍ଣ ପରିଚୟ"
        )
        val isNumberList = fullList.any { str -> str.any { it.isDigit() || it in '୦'..'୯' } }
        isNumberList || disableKeywords.any { keyword -> titleLower.contains(keyword) }
    }

    // Drawing Canvas States
    val charBoundsMap = remember { mutableStateOf<Map<String, Rect>>(emptyMap()) }
    var charToDraw by remember { mutableStateOf<String?>(null) }

    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenWidthPx = remember(config.screenWidthDp) { with(density) { config.screenWidthDp.dp.toPx() } }
    val screenHeightPx = remember(config.screenHeightDp) { with(density) { config.screenHeightDp.dp.toPx() } }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("or", "IN")
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    fun speakText(text: String) {
        if (text == "ଉଦାହରଣ ନାହିଁ") return
        if (text == lastSpokenText) {
            currentSpeechRate = when (currentSpeechRate) {
                0.50f -> 0.75f
                0.75f -> 1.0f
                else -> 0.50f
            }
        } else {
            currentSpeechRate = 0.50f
            lastSpokenText = text
        }
        tts?.let {
            it.setSpeechRate(currentSpeechRate)
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    var availableAudioSet by remember { mutableStateOf<Set<String>>(emptySet()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val files = context.assets.list("")
                if (files != null) {
                    availableAudioSet = files
                        .filter { it.endsWith(".mp3", ignoreCase = true) }
                        .map { it.substringBeforeLast(".") }
                        .toSet()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    var showHint by remember { mutableStateOf(false) }
    LaunchedEffect(pagerState.currentPage) {
        showHint = false
        onCharChanged(fullList[pagerState.currentPage])
        delay(5000)
        if (!hasSessionHintShown) {
            showHint = true
            hasSessionHintShown = true
            delay(5000)
            showHint = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "ବିସ୍ତୃତ ବିବରଣୀ",
                        fontSize = 26.sp,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val char = fullList[page]
                    val isDeprecated = deprecatedList.contains(char)
                    val details = getCharDetails(char)
                    val hasAudio = availableAudioSet.contains(char)

                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        val bgBrush = if (isDeprecated) {
                            Brush.linearGradient(colors = listOf(Color(0xFFE57373), Color(0xFFC62828)))
                        } else {
                            Brush.linearGradient(colors = listOf(DetailPinkStart, DetailPinkEnd))
                        }

                        val dynamicFontSize = when (char.length) {
                            1 -> 100.sp; 2 -> 80.sp; 3 -> 65.sp; else -> 50.sp
                        }

                        // The Base Modifier implementing the Hero Scale/Grow transition
                        val boxModifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "char-$char"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                            )
                            .size(220.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(brush = bgBrush)
                            .onGloballyPositioned { coords ->
                                val newMap = charBoundsMap.value.toMutableMap()
                                newMap[char] = coords.boundsInRoot()
                                charBoundsMap.value = newMap
                            }

                        Box(
                            // Safely disable opening the drawing pad if the list requires it
                            modifier = if (!isDrawingDisabled) {
                                boxModifier.clickable { charToDraw = char }
                            } else {
                                boxModifier
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                fontSize = dynamicFontSize,
                                fontFamily = OdiaFont,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            if (hasAudio) {
                                IconButton(
                                    onClick = { playSound(context, "$char.mp3") },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp)
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.VolumeUp,
                                        contentDescription = "Play Audio",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        if (isDeprecated) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(20.dp)).padding(20.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Old", tint = Color(0xFFD32F2F), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("ପୁରାତନ ବର୍ଣ୍ଣ", fontSize = 28.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Normal, color = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("ଏହି ବର୍ଣ୍ଣର ବ୍ୟବହାର ବର୍ତ୍ତମାନ ଓଡ଼ିଆ ଭାଷାରେ ନାହିଁ ।", fontSize = 18.sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, lineHeight = 26.sp)
                            }
                        } else {
                            DetailSectionTitle(title = "ଉଦାହରଣ ଶବ୍ଦ")
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                details.words.forEach { word ->
                                    Surface(
                                        modifier = Modifier.clickable { speakText(word) },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(2.dp, DetailPinkEnd.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = word,
                                            fontSize = 20.sp,
                                            fontFamily = OdiaFont,
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))
                            DetailSectionTitle(title = "ଉଦାହରଣ ବାକ୍ୟ")
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                details.sentences.forEachIndexed { index, sentence ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                                            .clickable { speakText(sentence) }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(DetailPinkStart.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = toOdiaDigit(index + 1),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = DetailPinkEnd,
                                                fontFamily = OdiaFont
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(15.dp))
                                        Text(
                                            text = sentence,
                                            fontSize = 20.sp,
                                            fontFamily = OdiaFont,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 28.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "← ଅନ୍ୟ ଅକ୍ଷର ଦେଖିବା ପାଇଁ ଘୁଞ୍ଚାନ୍ତୁ →",
                            fontFamily = OdiaFont,
                            fontSize = 14.sp,
                            color = Color.Gray.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 30.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showHint,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 8.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            // Dynamically update text hint depending on if drawing is allowed!
                            text = if (isDrawingDisabled) {
                                "ଶବ୍ଦ କିମ୍ବା ବାକ୍ୟ ଶୁଣିବା ପାଇଁ ତାହା ଉପରେ କ୍ଲିକ କରନ୍ତୁ"
                            } else {
                                "ଶବ୍ଦ ଶୁଣିବା ପାଇଁ ଶବ୍ଦ ଉପରେ ଏବଂ ଅକ୍ଷର ଆଙ୍କିବା ପାଇଁ ଅକ୍ଷର ଉପରେ କ୍ଲିକ୍ କରନ୍ତୁ"
                            },
                            fontFamily = OdiaFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }

        // =====================================
        // Animated Drawing Screen Overlay
        // =====================================

        // TransformOrigin strictly maps to exact X,Y fraction on the screen
        val originBounds = charBoundsMap.value[charToDraw] ?: Rect.Zero
        val originX = if (screenWidthPx > 0) originBounds.center.x / screenWidthPx else 0.5f
        val originY = if (screenHeightPx > 0) originBounds.center.y / screenHeightPx else 0.5f

        AnimatedVisibility(
            visible = charToDraw != null,
            enter = scaleIn(
                initialScale = 0.15f,
                transformOrigin = TransformOrigin(originX, originY),
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(300)),
            exit = scaleOut(
                targetScale = 0.15f,
                transformOrigin = TransformOrigin(originX, originY),
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(250)),
            modifier = Modifier.fillMaxSize()
        ) {
            charToDraw?.let { char ->
                DrawingCanvasOverlay(
                    char = char,
                    fullList = fullList,
                    onSwitchChar = { newChar ->
                        charToDraw = newChar
                        // Sync pager with the switched character
                        scope.launch { pagerState.animateScrollToPage(fullList.indexOf(newChar)) }
                    },
                    onDismiss = { charToDraw = null }
                )
            }
        }
    }
}

@Composable
fun DetailSectionTitle(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DetailPinkEnd)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 22.sp,
            fontFamily = OdiaFont,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

// ==========================================
// 4. DRAWING CANVAS OVERLAY FOR KIDS
// ==========================================

@Composable
fun DrawingCanvasOverlay(
    char: String,
    fullList: List<String>,
    onSwitchChar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val isDeprecated = deprecatedList.contains(char)
    val bgBrush = if (isDeprecated) {
        Brush.linearGradient(colors = listOf(Color(0xFFE57373), Color(0xFFC62828)))
    } else {
        Brush.linearGradient(colors = listOf(DetailPinkStart, DetailPinkEnd))
    }

    var paths by remember { mutableStateOf(listOf<Path>()) }
    var currentPath by remember { mutableStateOf(Path()) }
    var drawTrigger by remember { mutableIntStateOf(0) }
    var isRotated by remember { mutableStateOf(false) }

    // Smoothly animate the rotation of the inner canvas content
    val animatedRotation by animateFloatAsState(
        targetValue = if (isRotated) 90f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing)
    )

    // Clear canvas automatically if the character changes
    LaunchedEffect(char) {
        paths = emptyList()
        currentPath = Path()
        drawTrigger++
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .pointerInput(Unit) { detectTapGestures { } }
            .padding(vertical = 40.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Controls (Close, Switch, Rotate, Clear)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close Button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                // Middle Buttons: Prev & Next
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val currentIndex = fullList.indexOf(char)

                    IconButton(
                        onClick = { if (currentIndex > 0) onSwitchChar(fullList[currentIndex - 1]) },
                        modifier = Modifier.background(Color.White.copy(alpha = if (currentIndex > 0) 0.2f else 0.05f), CircleShape),
                        enabled = currentIndex > 0
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous", tint = Color.White)
                    }

                    IconButton(
                        onClick = { if (currentIndex < fullList.size - 1) onSwitchChar(fullList[currentIndex + 1]) },
                        modifier = Modifier.background(Color.White.copy(alpha = if (currentIndex < fullList.size - 1) 0.2f else 0.05f), CircleShape),
                        enabled = currentIndex < fullList.size - 1
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", tint = Color.White)
                    }
                }

                // Right Buttons: Rotate & Clear
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { isRotated = !isRotated },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rotate Canvas", tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            paths = emptyList()
                            currentPath = Path()
                            drawTrigger++
                        },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Canvas", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Box for the drawing canvas area - The Background
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(40.dp))
                    .background(brush = bgBrush), // Background strictly fits UI Bounds
                contentAlignment = Alignment.Center
            ) {
                val parentWidth = maxWidth
                val parentHeight = maxHeight

                // We create a massive perfect square so when it rotates, its corners NEVER clip
                // within the shortest boundaries of the screen!
                val maxSize = max(parentWidth, parentHeight)
                val shortEdge = min(parentWidth, parentHeight)

                Box(
                    modifier = Modifier
                        .size(maxSize) // Enormous invisible square
                        .graphicsLayer { rotationZ = animatedRotation } // It rotates smoothly
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                    drawTrigger++
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPath.lineTo(change.position.x, change.position.y)
                                    drawTrigger++
                                },
                                onDragEnd = {
                                    paths = paths + currentPath
                                    currentPath = Path()
                                    drawTrigger++
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Tracing Text Outline
                    Text(
                        text = char,
                        fontSize = (shortEdge.value * 0.85f).sp, // Sized dynamically to the shortest edge so it never clips!
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.6f),
                        style = LocalTextStyle.current.merge(
                            TextStyle(drawStyle = Stroke(width = 16f, join = StrokeJoin.Round))
                        ),
                        textAlign = TextAlign.Center
                    )

                    // User Drawings Canvas
                    val triggerState = drawTrigger
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val ignored = triggerState
                        val strokeStyle = Stroke(
                            width = 45f, // Kid friendly thick yellow brush
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                        paths.forEach { path ->
                            drawPath(path = path, color = Color.Yellow, style = strokeStyle)
                        }
                        drawPath(path = currentPath, color = Color.Yellow, style = strokeStyle)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "ଅକ୍ଷର ଉପରେ ଆଙ୍ଗୁଠିରେ ଆଙ୍କନ୍ତୁ",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = OdiaFont
            )
        }
    }
}