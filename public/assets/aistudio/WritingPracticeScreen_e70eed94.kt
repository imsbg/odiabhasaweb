package `in`.sandeep.odiabhasa

import android.content.Context
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.min

// --- PREFERENCES MANAGER FOR UI SETTINGS ---
// Handles saving the Preview Text Size and Slate Visibility
class PracticeSettingsPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("odia_practice_ui_settings", Context.MODE_PRIVATE)

    var previewTextSize: Float
        get() = prefs.getFloat("preview_text_size", 48f) // Default SP size
        set(value) = prefs.edit().putFloat("preview_text_size", value).apply()

    var showBackground: Boolean
        get() = prefs.getBoolean("show_bg_text", true)
        set(value) = prefs.edit().putBoolean("show_bg_text", value).apply()
}

// --- DATA MODELS ---

data class StrokeData(
    val path: Path,
    val color: Color,
    val points: List<Pair<Float, Float>>,
    val strokeWidth: Float
)

data class WritingUiState(
    val selectedCategory: String = "Swara",
    val practiceList: List<String> = swarabarnaList.filter { it !in deprecatedList },
    val currentIndex: Int = 0,
    val currentChar: String = swarabarnaList.filter { it !in deprecatedList }.firstOrNull() ?: "",
    val drawings: Map<String, List<StrokeData>> = emptyMap(),
    val undonePaths: List<StrokeData> = emptyList(),
    val strokeWidth: Float = 15f
)

data class CharStats(
    val undoCount: Int = 0,
    val redoCount: Int = 0,
    val clearCount: Int = 0
)

data class PracticeStats(
    val totalUndo: Int = 0,
    val totalRedo: Int = 0,
    val totalClears: Int = 0,
    val perCharStats: Map<String, CharStats> = emptyMap()
)

// --- HELPERS ---

private val excludedPracticeChars = listOf("ଡ଼", "ଢ଼")

private fun Int.toOdia(): String {
    return this.toString().map {
        when (it) {
            '0' -> '୦'; '1' -> '୧'; '2' -> '୨'; '3' -> '୩'; '4' -> '୪'
            '5' -> '୫'; '6' -> '୬'; '7' -> '୭'; '8' -> '୮'; '9' -> '୯'
            else -> it
        }
    }.joinToString("")
}

// --- TOOLTIP COMPONENT ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdiaTooltip(
    text: String,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(text, fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
            }
        },
        state = rememberTooltipState()
    ) {
        content()
    }
}

// --- AUDIO PLAYER HELPER ---
fun playPracticeSound(context: Context, fileName: String) {
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
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { PracticeSettingsPrefs(context) }

    // UI States
    var uiState by remember { mutableStateOf(WritingUiState()) }
    var stats by remember { mutableStateOf(PracticeStats()) }

    // Dialog & Sheet States
    var showResetDialog by remember { mutableStateOf(false) }
    var showStatsPage by remember { mutableStateOf(false) }
    var openStrokeBottomSheet by remember { mutableStateOf(false) }
    var showResizeDialog by remember { mutableStateOf(false) } // For Preview Resize
    val bottomSheetState = rememberModalBottomSheetState()

    // Preferences States (Persisted)
    var showBackgroundLetter by remember { mutableStateOf(prefs.showBackground) }
    var previewTextSize by remember { mutableFloatStateOf(prefs.previewTextSize) }

    // Audio State
    var audioMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    var isLoading by remember { mutableStateOf(true) }

    // --- INITIAL DATA LOAD ---
    LaunchedEffect(Unit) {
        // Load Audio Files
        withContext(Dispatchers.IO) {
            try {
                val files = context.assets.list("")
                if (files != null) {
                    audioMap = files
                        .filter { it.endsWith(".mp3", ignoreCase = true) }
                        .associateBy { it.substringBeforeLast(".") }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        // Load Practice Data
        val (loadedUiData, loadedStats) = PracticeRepository.loadData(context)
        val (lastCategory, lastIndex, loadedDrawings) = loadedUiData
        val newList = when (lastCategory) {
            "Swara" -> swarabarnaList; "Byanjana" -> byanjanabarnaList
            "Yukta" -> yuktakshyaraList; "Sankhya" -> sankhyaList
            else -> swarabarnaList
        }.filter { it !in deprecatedList && it !in excludedPracticeChars }

        val newIndex = if (newList.isNotEmpty()) lastIndex.coerceIn(0, newList.lastIndex) else 0
        val newChar = newList.getOrElse(newIndex) { "" }

        uiState = uiState.copy(
            drawings = loadedDrawings,
            selectedCategory = lastCategory,
            practiceList = newList,
            currentIndex = newIndex,
            currentChar = newChar
        )
        stats = loadedStats
        isLoading = false
    }

    // --- SAVE DATA ON CHANGE ---
    LaunchedEffect(uiState, stats) {
        if (!isLoading) {
            PracticeRepository.saveData(context, uiState.selectedCategory, uiState.currentIndex, uiState.drawings, stats)
        }
    }

    // --- SAVE PREFERENCES ON CHANGE ---
    fun updateBackgroundVisibility(visible: Boolean) {
        showBackgroundLetter = visible
        prefs.showBackground = visible
    }

    fun updatePreviewSize(size: Float) {
        previewTextSize = size
        prefs.previewTextSize = size
    }

    // --- ACTIONS ---
    val onSelectCategory = { category: String ->
        val newList = when (category) {
            "Swara" -> swarabarnaList; "Byanjana" -> byanjanabarnaList
            "Yukta" -> yuktakshyaraList; "Sankhya" -> sankhyaList
            else -> swarabarnaList
        }.filter { it !in deprecatedList && it !in excludedPracticeChars }
        uiState = uiState.copy(selectedCategory = category, practiceList = newList, currentIndex = 0, currentChar = newList.firstOrNull() ?: "", undonePaths = emptyList())
    }

    val onNextChar = { if (uiState.currentIndex < uiState.practiceList.size - 1) { val newIndex = uiState.currentIndex + 1; uiState = uiState.copy(currentIndex = newIndex, currentChar = uiState.practiceList[newIndex], undonePaths = emptyList()) } }
    val onPreviousChar = { if (uiState.currentIndex > 0) { val newIndex = uiState.currentIndex - 1; uiState = uiState.copy(currentIndex = newIndex, currentChar = uiState.practiceList[newIndex], undonePaths = emptyList()) } }
    val onAddPath = { stroke: StrokeData -> val newDrawings = uiState.drawings.toMutableMap().apply { this[uiState.currentChar] = (this[uiState.currentChar] ?: emptyList()) + stroke }; uiState = uiState.copy(drawings = newDrawings, undonePaths = emptyList()) }
    val onUndo = {
        val currentPaths = uiState.drawings[uiState.currentChar] ?: emptyList()
        if (currentPaths.isNotEmpty()) {
            val newDrawings = uiState.drawings.toMutableMap().apply { this[uiState.currentChar] = currentPaths.dropLast(1) }
            uiState = uiState.copy(drawings = newDrawings, undonePaths = uiState.undonePaths + currentPaths.last())
            val char = uiState.currentChar
            val currentCharStats = stats.perCharStats[char] ?: CharStats()
            stats = stats.copy(totalUndo = stats.totalUndo + 1, perCharStats = stats.perCharStats + (char to currentCharStats.copy(undoCount = currentCharStats.undoCount + 1)))
        }
    }
    val onRedo = {
        if (uiState.undonePaths.isNotEmpty()) {
            val newDrawings = uiState.drawings.toMutableMap().apply { this[uiState.currentChar] = (this[uiState.currentChar] ?: emptyList()) + uiState.undonePaths.last() }
            uiState = uiState.copy(drawings = newDrawings, undonePaths = uiState.undonePaths.dropLast(1))
            val char = uiState.currentChar
            val currentCharStats = stats.perCharStats[char] ?: CharStats()
            stats = stats.copy(totalRedo = stats.totalRedo + 1, perCharStats = stats.perCharStats + (char to currentCharStats.copy(redoCount = currentCharStats.redoCount + 1)))
        }
    }
    val onClearLetter = {
        val currentDrawings = uiState.drawings[uiState.currentChar]
        if (currentDrawings != null && currentDrawings.isNotEmpty()) {
            val newDrawings = uiState.drawings.toMutableMap().apply { this[uiState.currentChar] = emptyList() }
            uiState = uiState.copy(drawings = newDrawings, undonePaths = emptyList())
            val char = uiState.currentChar
            val currentCharStats = stats.perCharStats[char] ?: CharStats()
            stats = stats.copy(totalClears = stats.totalClears + 1, perCharStats = stats.perCharStats + (char to currentCharStats.copy(clearCount = currentCharStats.clearCount + 1)))
        }
    }

    // --- GRAPHICS & PAINT ---
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val typefaceState = remember { fontFamilyResolver.resolve(OdiaFont) }
    val odiaTypeface = typefaceState.value as? Typeface ?: Typeface.DEFAULT
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // Logic to AUTO-FIT the background letter to the canvas (Matrix scaling)
    val letterData = remember(uiState.currentChar, canvasSize, odiaTypeface) {
        if (canvasSize.width == 0 || canvasSize.height == 0 || uiState.currentChar.isEmpty()) return@remember null
        val rawPath = android.graphics.Path()
        val baseTextSize = 100f
        val textPaint = Paint().apply {
            textSize = baseTextSize; typeface = odiaTypeface; textAlign = Paint.Align.LEFT
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textLocale = java.util.Locale("or", "IN")
            }
        }
        textPaint.getTextPath(uiState.currentChar, 0, uiState.currentChar.length, 0f, 0f, rawPath)
        val bounds = RectF(); rawPath.computeBounds(bounds, true)
        if (bounds.width() == 0f || bounds.height() == 0f) return@remember null
        val padding = with(density) { 32.dp.toPx() }
        val availableWidth = canvasSize.width - (padding * 2); val availableHeight = canvasSize.height - (padding * 2)
        val scaleX = availableWidth / bounds.width(); val scaleY = availableHeight / bounds.height()
        val finalScale = min(scaleX, scaleY)
        val matrix = Matrix(); matrix.postTranslate(-bounds.left, -bounds.top); matrix.postScale(finalScale, finalScale)
        val scaledWidth = bounds.width() * finalScale; val scaledHeight = bounds.height() * finalScale
        val centerX = (canvasSize.width - scaledWidth) / 2f; val centerY = (canvasSize.height - scaledHeight) / 2f
        matrix.postTranslate(centerX, centerY)
        val finalPath = android.graphics.Path(); rawPath.transform(matrix, finalPath)
        finalPath
    }

    val distinctColors = remember { listOf(Color(0xFF673AB7), Color(0xFFE91E63), Color(0xFFF44336), Color(0xFF009688), Color(0xFF3F51B5), Color(0xFFFF9800), Color(0xFF4CAF50), Color(0xFF9C27B0), Color(0xFF2196F3), Color(0xFF795548)) }
    val drawColor = remember(uiState.currentChar) { if (uiState.currentChar.isEmpty()) Color.Black else distinctColors[abs(uiState.currentChar.hashCode()) % distinctColors.size] }

    // --- DIALOGS ---

    // 1. Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("ସତର୍କ ସୂଚନା", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
            text = { Text("ଆପଣ ସମସ୍ତ ଲେଖା ଏବଂ ପରିସଂଖ୍ୟାନ ଡିଲିଟ୍ କରିବାକୁ ଚାହୁଁଛନ୍ତି କି?", fontFamily = OdiaFont) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        PracticeRepository.clearAll(context)
                        uiState = WritingUiState()
                        stats = PracticeStats()
                        showResetDialog = false
                    }
                }) {
                    Text("ହଁ", fontFamily = OdiaFont, color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("ନା", fontFamily = OdiaFont, color = Color.Black) } },
            containerColor = Color.White
        )
    }

    // 2. Resize Dialog (For PREVIEW Alphabet)
    if (showResizeDialog) {
        AlertDialog(
            onDismissRequest = { showResizeDialog = false },
            icon = { Icon(Icons.Default.ZoomIn, contentDescription = null) },
            title = { Text("ଅକ୍ଷର ଆକାର", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ଉପର ଅକ୍ଷରର ଆକାର ବଦଳାନ୍ତୁ", fontFamily = OdiaFont, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = previewTextSize,
                        onValueChange = { updatePreviewSize(it) },
                        valueRange = 30f..100f, // Reasonable range for preview
                        steps = 14
                    )
                    Text(previewTextSize.toInt().toOdia(), fontFamily = OdiaFont)
                }
            },
            confirmButton = {
                TextButton(onClick = { showResizeDialog = false }) {
                    Text("ଠିକ୍ ଅଛି", fontFamily = OdiaFont)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = { Text(if (showStatsPage) "ପରିସଂଖ୍ୟାନ" else "ଲେଖି ଶିଖନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Normal) },
            navigationIcon = {
                val backDesc = "ପଛକୁ ଯାଆନ୍ତୁ"
                OdiaTooltip(backDesc) {
                    IconButton(onClick = { if (showStatsPage) showStatsPage = false else onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, backDesc)
                    }
                }
            },
            actions = {
                if(!showStatsPage){
                    OdiaTooltip("ଗାର ର ଆକାର ବଦଳାନ୍ତୁ") {
                        IconButton(onClick = { openStrokeBottomSheet = true }) {
                            Icon(Icons.Default.LineWeight, "ଷ୍ଟ୍ରୋକ ଆକାର")
                        }
                    }
                }
                OdiaTooltip(if (showStatsPage) "ଲେଖା ଅଭ୍ୟାସକୁ ଫେରନ୍ତୁ" else "ପରିସଂଖ୍ୟାନ ଦେଖନ୍ତୁ") {
                    IconButton(onClick = { showStatsPage = !showStatsPage }) {
                        Icon(if (showStatsPage) Icons.Default.Edit else Icons.Filled.BarChart, "ପରିସଂଖ୍ୟାନ")
                    }
                }
                OdiaTooltip("ସମସ୍ତ ତଥ୍ୟ ଲିଭାନ୍ତୁ") {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Delete, "ସବୁ ଡିଲିଟ୍ କରନ୍ତୁ", tint = Color.Red.copy(alpha = 0.7f))
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFFF3E0)))
        },
        containerColor = Color(0xFFFFF3E0)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (showStatsPage) {
                StatsContent(stats = stats)
            } else {

                // --- BOTTOM SHEET FOR STROKE WIDTH ---
                if (openStrokeBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { openStrokeBottomSheet = false },
                        sheetState = bottomSheetState,
                        containerColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("ଗାର ର ଆକାର ବାଛନ୍ତୁ", fontFamily = OdiaFont, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            var sliderPosition by remember(uiState.strokeWidth) { mutableStateOf(uiState.strokeWidth) }
                            Slider(
                                value = sliderPosition,
                                onValueChange = { sliderPosition = it },
                                onValueChangeFinished = { uiState = uiState.copy(strokeWidth = sliderPosition) },
                                valueRange = 10f..40f,
                                steps = 7,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )
                            Text(text = sliderPosition.toInt().toOdia(), fontFamily = OdiaFont, fontSize = 18.sp)
                        }
                    }
                }

                // --- CATEGORY BUTTONS ---
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    OdiaTooltip("ସ୍ୱରବର୍ଣ୍ଣ") { CategoryButton("ସ୍ୱରବର୍ଣ୍ଣ", uiState.selectedCategory == "Swara") { onSelectCategory("Swara") } }
                    OdiaTooltip("ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ") { CategoryButton("ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ", uiState.selectedCategory == "Byanjana") { onSelectCategory("Byanjana") } }
                    OdiaTooltip("ଯୁକ୍ତାକ୍ଷର") { CategoryButton("ଯୁକ୍ତାକ୍ଷର", uiState.selectedCategory == "Yukta") { onSelectCategory("Yukta") } }
                    OdiaTooltip("ସଂଖ୍ୟା") { CategoryButton("ସଂଖ୍ୟା", uiState.selectedCategory == "Sankhya") { onSelectCategory("Sankhya") } }
                }

                // --- PREVIEW HEADER (RESIZABLE) ---
                // Long press here opens the resize dialog
                OdiaTooltip("ଚାପି ଧରି ଆକାର ବଦଳାନ୍ତୁ") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        showResizeDialog = true
                                    }
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 24.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.currentChar,
                                fontFamily = OdiaFont,
                                fontSize = previewTextSize.sp, // DYNAMIC SIZE
                                color = Color.Black
                            )
                        }
                    }
                }

                // --- MAIN SLATE AREA ---
                var currentPath by remember { mutableStateOf<Path?>(null) }
                val currentPoints = remember { mutableStateListOf<Pair<Float, Float>>() }
                var drawTrigger by remember { mutableLongStateOf(0L) }

                val audioFileName = audioMap[uiState.currentChar]

                BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val isCompact = maxWidth < 600.dp
                    val canvasModifier = if (isCompact) Modifier.fillMaxWidth().aspectRatio(1f) else Modifier.fillMaxHeight().aspectRatio(1f)

                    Box(modifier = canvasModifier.clip(RoundedCornerShape(20.dp)).background(Color.White).padding(4.dp).onSizeChanged { canvasSize = it }) {

                        // DRAWING CANVAS
                        Canvas(modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(uiState.currentChar) {
                                awaitEachGesture {
                                    val down = awaitFirstDown()
                                    currentPoints.clear()

                                    currentPath = Path().apply { moveTo(down.position.x, down.position.y) }
                                    currentPoints.add(down.position.x to down.position.y)
                                    drawTrigger++

                                    drag(down.id) { change ->
                                        change.consume()
                                        val currentPt = change.position
                                        currentPath?.lineTo(currentPt.x, currentPt.y)
                                        currentPoints.add(currentPt.x to currentPt.y)
                                        drawTrigger++
                                    }

                                    if (currentPath != null && currentPoints.isNotEmpty()) {
                                        onAddPath(StrokeData(currentPath!!, drawColor, currentPoints.toList(), with(density) { uiState.strokeWidth.dp.toPx() }))
                                    }
                                    currentPath = null
                                }
                            }
                        ) {
                            drawTrigger
                            // 1. Draw Background Letter (If visible) - AUTO FIT LOGIC
                            if (showBackgroundLetter) {
                                letterData?.let { bgPath ->
                                    drawContext.canvas.nativeCanvas.apply {
                                        val paint = Paint().apply {
                                            color = android.graphics.Color.parseColor("#E0E0E0")
                                            style = Paint.Style.STROKE
                                            strokeWidth = 5.dp.toPx()
                                            strokeCap = Paint.Cap.ROUND
                                            strokeJoin = Paint.Join.ROUND
                                            isAntiAlias = true
                                        }
                                        drawPath(bgPath, paint)
                                    }
                                }
                            }

                            // 2. Draw Saved Strokes
                            (uiState.drawings[uiState.currentChar] ?: emptyList()).forEach { strokeData ->
                                drawPath(
                                    path = strokeData.path,
                                    color = strokeData.color,
                                    style = Stroke(width = strokeData.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }

                            // 3. Draw Current Active Stroke
                            currentPath?.let { path ->
                                drawPath(
                                    path = path,
                                    color = drawColor,
                                    style = Stroke(width = with(density) { uiState.strokeWidth.dp.toPx() }, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }

                        // --- CORNER BUTTONS OVERLAY ---

                        // Bottom Left: Eye (Visibility)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            OdiaTooltip(if(showBackgroundLetter) "ପଛ ଅକ୍ଷର ଲୁଚାନ୍ତୁ" else "ପଛ ଅକ୍ଷର ଦେଖାନ୍ତୁ") {
                                IconButton(
                                    onClick = { updateBackgroundVisibility(!showBackgroundLetter) },
                                    modifier = Modifier.background(Color(0xFFE8F5E9), CircleShape).border(1.dp, Color(0xFFC8E6C9), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (showBackgroundLetter) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Show/Hide",
                                        tint = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }

                        // Bottom Right: Speaker (Audio) - Only if audio exists
                        if (audioFileName != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                            ) {
                                OdiaTooltip("ଉଚ୍ଚାରଣ ଶୁଣନ୍ତୁ") {
                                    IconButton(
                                        onClick = { playPracticeSound(context, audioFileName) },
                                        modifier = Modifier.background(Color(0xFFE1F5FE), CircleShape).border(1.dp, Color(0xFFB3E5FC), CircleShape)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, "Play Audio", tint = Color(0xFF0277BD))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- NAVIGATION CONTROLS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isPrevEnabled = uiState.currentIndex > 0
                    val isNextEnabled = uiState.practiceList.isNotEmpty() && uiState.currentIndex < uiState.practiceList.size - 1

                    OdiaTooltip("ପୂର୍ବ ଅକ୍ଷର") {
                        FloatingActionButton(
                            onClick = onPreviousChar,
                            containerColor = if (isPrevEnabled) Color(0xFFFFCC80) else Color.LightGray,
                            contentColor = Color.Black
                        ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous") }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OdiaTooltip("ଗୋଟିଏ ପଦକ୍ଷେପ ପଛକୁ") {
                            IconButton(onClick = onUndo, enabled = (uiState.drawings[uiState.currentChar] ?: emptyList()).isNotEmpty()) { Icon(Icons.Default.Undo, "Undo", modifier = Modifier.size(30.dp)) }
                        }
                        OdiaTooltip("ସମ୍ପୂର୍ଣ୍ଣ ଲିଭାନ୍ତୁ") {
                            ExtendedFloatingActionButton(onClick = onClearLetter, containerColor = Color(0xFFFFAB91), contentColor = Color.Black, icon = { Icon(Icons.Default.Refresh, "Clear") }, text = { Text("ଲିଭାନ୍ତୁ", fontFamily = OdiaFont) })
                        }
                        OdiaTooltip("ପୁଣି ଥରେ ଫେରାଇ ଆଣନ୍ତୁ") {
                            IconButton(onClick = onRedo, enabled = uiState.undonePaths.isNotEmpty()) { Icon(Icons.Default.Redo, "Redo", modifier = Modifier.size(30.dp)) }
                        }
                    }

                    OdiaTooltip("ପରବର୍ତ୍ତୀ ଅକ୍ଷର") {
                        FloatingActionButton(
                            onClick = onNextChar,
                            containerColor = if (isNextEnabled) Color(0xFFA5D6A7) else Color.LightGray,
                            contentColor = Color.Black
                        ) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next") }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Hint text updated
                Text(text = "ଅକ୍ଷର ଉପରେ ଲେଖନ୍ତୁ", fontFamily = OdiaFont, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Gray)
            }
        }
    }
}


@Composable
fun StatsContent(stats: PracticeStats) {
    if (stats.totalUndo == 0 && stats.totalRedo == 0 && stats.totalClears == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "କୌଣସି ପରିସଂଖ୍ୟାନ ଉପଲବ୍ଧ ନାହିଁ ।",
                fontFamily = OdiaFont,
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "ସାମଗ୍ରିକ ପରିସଂଖ୍ୟାନ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StatsRow("ମୋଟ ପୂର୍ବ ସ୍ଥିତିକୁ:", stats.totalUndo)
                    StatsRow("ମୋଟ ପୁଣି ଥରେ:", stats.totalRedo)
                    StatsRow("ମୋଟ ସଫା:", stats.totalClears)
                }
            }
        }

        val sortedCharStats = stats.perCharStats.filter { it.value.undoCount > 0 || it.value.redoCount > 0 || it.value.clearCount > 0 }.toList().sortedBy { it.first }

        if(sortedCharStats.isNotEmpty()) {
            item {
                Text(
                    "ବର୍ଣ୍ଣ ଅନୁଯାୟୀ ପରିସଂଖ୍ୟାନ",
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(sortedCharStats) { (char, charStats) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1E8FF)),
                    border = BorderStroke(1.dp, Color(0xFFD0BCFF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$char",
                            fontFamily = OdiaFont,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ପୂର୍ବ ସ୍ଥିତିକୁ: ${charStats.undoCount.toOdia()}", fontFamily = OdiaFont, fontSize = 14.sp)
                            Text("ପୁଣି ଥରେ: ${charStats.redoCount.toOdia()}", fontFamily = OdiaFont, fontSize = 14.sp)
                            Text("ସଫା: ${charStats.clearCount.toOdia()}", fontFamily = OdiaFont, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsRow(label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = OdiaFont, fontSize = 16.sp)
        Text(value.toOdia(), fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
    Divider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun WarningBanner() {
    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFEBEE), border = BorderStroke(1.dp, Color(0xFFFFCDD2)), shadowElevation = 4.dp) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(Icons.Default.Info, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(24.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("ଦୟାକରି ଅକ୍ଷର ଉପରେ ଲେଖନ୍ତୁ", color = Color(0xFFB71C1C), fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun CategoryButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(if (isSelected) Color(0xFF673AB7) else Color.White).border(1.dp, if (isSelected) Color.Transparent else Color(0xFF673AB7), CircleShape).clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
        Text(text, fontFamily = OdiaFont, fontWeight = FontWeight.Normal, color = if (isSelected) Color.White else Color(0xFF673AB7), fontSize = 14.sp)
    }
}