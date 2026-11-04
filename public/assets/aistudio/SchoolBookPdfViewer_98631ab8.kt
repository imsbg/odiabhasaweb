package `in`.sandeep.odiabhasa

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

// Mutex ensures PDF renders one page at a time (fixes crashes)
val pdfMutex = Mutex()

private fun getOdiaNum(num: Int): String {
    val odiaNumerals = listOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return num.toString().map { if (it.isDigit()) odiaNumerals[it.toString().toInt()] else it }.joinToString("")
}

private fun odiaStringToInt(odiaStr: String): Int? {
    val engStr = odiaStr.map { char ->
        if (char in '୦'..'୯') {
            (char - '୦').toString()
        } else {
            char.toString()
        }
    }.joinToString("")
    return engStr.toIntOrNull()
}

// Data class to store drawing information
data class DrawingStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isHighlighter: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdiaTooltipIconButton(
    onClick: () -> Unit,
    tooltipText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(tooltipText) } },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick, modifier = modifier) { content() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    tooltipText: String = "ରଙ୍ଗ"
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(tooltipText) } },
        state = rememberTooltipState()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color)
                .border(3.dp, if (isSelected) Color.Gray else Color.Transparent, CircleShape)
                .clickable(onClick = onClick)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiColorPickerButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val sweepGradient = Brush.sweepGradient(
        listOf(Color.Red, Color.Magenta, Color.Blue, Color.Cyan, Color.Green, Color.Yellow, Color.Red)
    )
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text("ଆହୁରି ରଙ୍ଗ") } },
        state = rememberTooltipState()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(sweepGradient)
                .border(3.dp, if (isSelected) Color.Gray else Color.Transparent, CircleShape)
                .clickable(onClick = onClick)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolBookPdfViewerScreen(title: String, filePath: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current

    // FEATURE: Keep Screen Awake
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    // Theme State
    var isDarkMode by remember { mutableStateOf(false) }

    // UI Theme Variables
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFE0E0E0)
    val topBarBg = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val contentCol = if (isDarkMode) Color.White else Color.Black

    var pdfState by remember { mutableStateOf<Pair<ParcelFileDescriptor, PdfRenderer>?>(null) }
    val pdfRenderer = pdfState?.second

    var totalPages by remember { mutableIntStateOf(0) }
    var showGridPreview by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }.toInt()
    val bookId = remember(filePath) { File(filePath).name }

    // Safely load PDF in background
    LaunchedEffect(filePath) {
        var localFd: ParcelFileDescriptor? = null
        var localRenderer: PdfRenderer? = null
        try {
            withContext(Dispatchers.IO) {
                val file = File(filePath)
                if (file.exists()) {
                    localFd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    localRenderer = PdfRenderer(localFd!!)
                    val count = localRenderer!!.pageCount

                    withContext(Dispatchers.Main) {
                        totalPages = count
                        pdfState = Pair(localFd!!, localRenderer!!)
                    }
                }
            }
            awaitCancellation()
        } catch (e: Exception) {
            if (e !is CancellationException) e.printStackTrace()
        } finally {
            withContext(NonCancellable + Dispatchers.IO) {
                withContext(Dispatchers.Main) { pdfState = null }
                try {
                    localRenderer?.close()
                    localFd?.close()
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { totalPages })

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var isDrawingMode by remember { mutableStateOf(false) }
    var currentDrawColor by remember { mutableStateOf(Color.Red) }
    var isHighlighterMode by remember { mutableStateOf(false) }
    var undoTrigger by remember { mutableIntStateOf(0) }
    var redoTrigger by remember { mutableIntStateOf(0) }
    var clearAllTrigger by remember { mutableIntStateOf(0) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }

    // Adaptive default black/white for dark mode compatibility
    val defaultPenColor = if (isDarkMode) Color.White else Color.Black
    val predefinedColors = listOf(defaultPenColor, Color.Red, Color.Blue, Color(0xFF008000), Color(0xFFFFEB3B))

    // Ensure currently selected color adapts if it was black/white when theme switches
    LaunchedEffect(isDarkMode) {
        if (isDarkMode && currentDrawColor == Color.Black) currentDrawColor = Color.White
        else if (!isDarkMode && currentDrawColor == Color.White) currentDrawColor = Color.Black
    }

    var showSeekbar by remember { mutableStateOf(false) }
    var userActivityTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isIndicatorVisible by remember { mutableStateOf(true) }

    LaunchedEffect(userActivityTime) {
        isIndicatorVisible = true
        delay(5000)
        isIndicatorVisible = false
    }

    LaunchedEffect(pagerState.currentPage) {
        if (!isDrawingMode) { scale = 1f; offsetX = 0f; offsetY = 0f }
    }

    BackHandler(enabled = showGridPreview || showSeekbar || isDrawingMode) {
        if (showGridPreview) showGridPreview = false
        else if (showSeekbar) showSeekbar = false
        else if (isDrawingMode) isDrawingMode = false
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("ସତର୍କ ସୂଚନା", fontWeight = FontWeight.Bold, color = contentCol) },
            text = { Text("ଏହି ପୃଷ୍ଠାରେ ଥିବା ସମସ୍ତ ଗାରକୁ ଲିଭାଇବାକୁ ଚାହୁଁଛନ୍ତି କି?", fontSize = 16.sp, color = contentCol) },
            confirmButton = {
                TextButton(onClick = { clearAllTrigger++; showClearDialog = false }) {
                    Text("ହଁ", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("ନା", color = contentCol, fontSize = 16.sp)
                }
            },
            containerColor = topBarBg
        )
    }

    if (showColorPickerDialog) {
        val extendedColors = listOf(
            Color.Black, Color.DarkGray, Color.Gray, Color.LightGray,
            Color.Red, Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
            Color.Blue, Color(0xFF03A9F4), Color.Cyan, Color(0xFF009688),
            Color(0xFF4CAF50), Color(0xFF8BC34A), Color.Yellow, Color(0xFFFF9800),
            Color(0xFFFF5722), Color(0xFF795548), Color(0xFF607D8B), Color.White
        )
        AlertDialog(
            onDismissRequest = { showColorPickerDialog = false },
            title = { Text("ରଙ୍ଗ ବାଛନ୍ତୁ", fontWeight = FontWeight.Bold, color = contentCol) },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(extendedColors.size) { index ->
                        val color = extendedColors[index]
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(3.dp, if (currentDrawColor == color) Color.Gray else Color.Transparent, CircleShape)
                                .clickable {
                                    currentDrawColor = color
                                    userActivityTime = System.currentTimeMillis()
                                    showColorPickerDialog = false
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPickerDialog = false }) { Text("ବନ୍ଦ କରନ୍ତୁ", color = contentCol) }
            },
            containerColor = topBarBg
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontFamily = null, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    OdiaTooltipIconButton(onClick = onBack, tooltipText = "ପଛକୁ ଯାଆନ୍ତୁ") {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ପଛକୁ ଯାଆନ୍ତୁ")
                    }
                },
                actions = {
                    // Dark Mode Toggle
                    OdiaTooltipIconButton(
                        onClick = { isDarkMode = !isDarkMode },
                        tooltipText = if (isDarkMode) "ଆଲୁଅ ମୋଡ୍" else "ଅନ୍ଧାର ମୋଡ୍"
                    ) {
                        Icon(if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4, contentDescription = null)
                    }

                    // Drawing Mode Toggle
                    OdiaTooltipIconButton(
                        onClick = {
                            isDrawingMode = !isDrawingMode
                            showSeekbar = false
                            userActivityTime = System.currentTimeMillis()
                        },
                        tooltipText = "ଆଙ୍କନ୍ତୁ"
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "ଆଙ୍କନ୍ତୁ", tint = if (isDrawingMode) MaterialTheme.colorScheme.primary else contentCol)
                    }

                    // All Pages Toggle
                    OdiaTooltipIconButton(onClick = { showGridPreview = true }, tooltipText = "ସବୁ ପୃଷ୍ଠା") {
                        Icon(Icons.Default.GridView, contentDescription = "ସବୁ ପୃଷ୍ଠା")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarBg,
                    titleContentColor = contentCol,
                    navigationIconContentColor = contentCol,
                    actionIconContentColor = contentCol
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(paddingValues)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                            userActivityTime = System.currentTimeMillis()
                        }
                    }
                }
        ) {
            if (pdfRenderer != null && totalPages > 0) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(isDrawingMode) {
                            if (!isDrawingMode) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 4f)
                                    if (scale > 1f) {
                                        offsetX += pan.x; offsetY += pan.y
                                    } else {
                                        offsetX = 0f; offsetY = 0f
                                    }
                                }
                            }
                        },
                    userScrollEnabled = scale == 1f && !isDrawingMode,
                    pageSpacing = 16.dp
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offsetX, translationY = offsetY),
                        contentAlignment = Alignment.Center
                    ) {
                        PdfPageRenderer(
                            context = context,
                            renderer = pdfRenderer,
                            pageIndex = page,
                            widthPx = screenWidthPx * 2,
                            bookId = bookId,
                            isDrawingMode = isDrawingMode,
                            drawColor = currentDrawColor,
                            isHighlighter = isHighlighterMode,
                            isCurrentPage = (pagerState.currentPage == page),
                            undoTrigger = undoTrigger,
                            redoTrigger = redoTrigger,
                            clearAllTrigger = clearAllTrigger,
                            isDarkMode = isDarkMode // Pass theme state
                        )
                    }
                }

                // Drawing Toolbar
                AnimatedVisibility(
                    visible = isDrawingMode,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = topBarBg,
                        tonalElevation = 8.dp,
                        shadowElevation = 12.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                OdiaTooltipIconButton(onClick = { undoTrigger++; userActivityTime = System.currentTimeMillis() }, tooltipText = "ପଛକୁ ଫେରନ୍ତୁ") {
                                    Icon(Icons.Default.Undo, "Undo", tint = contentCol)
                                }
                                OdiaTooltipIconButton(onClick = { redoTrigger++; userActivityTime = System.currentTimeMillis() }, tooltipText = "ଆଗକୁ ଯାଆନ୍ତୁ") {
                                    Icon(Icons.Default.Redo, "Redo", tint = contentCol)
                                }

                                Box(modifier = Modifier.padding(horizontal = 8.dp).width(2.dp).height(24.dp).background(Color.Gray))

                                OdiaTooltipIconButton(onClick = { isHighlighterMode = false; userActivityTime = System.currentTimeMillis() }, tooltipText = "କଲମ") {
                                    Icon(Icons.Default.Edit, "Pen", tint = if (!isHighlighterMode) MaterialTheme.colorScheme.primary else Color.Gray)
                                }
                                OdiaTooltipIconButton(onClick = { isHighlighterMode = true; userActivityTime = System.currentTimeMillis() }, tooltipText = "ହାଇଲାଇଟର୍") {
                                    Icon(Icons.Default.Brush, "Highlighter", tint = if (isHighlighterMode) Color(0xFFD4AF37) else Color.Gray)
                                }

                                Box(modifier = Modifier.padding(horizontal = 8.dp).width(2.dp).height(24.dp).background(Color.Gray))

                                OdiaTooltipIconButton(onClick = { showClearDialog = true; userActivityTime = System.currentTimeMillis() }, tooltipText = "ସବୁ ଲିଭାନ୍ତୁ") {
                                    Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                predefinedColors.forEach { color ->
                                    TooltipColorSwatch(color = color, isSelected = currentDrawColor == color, onClick = { currentDrawColor = color; userActivityTime = System.currentTimeMillis() })
                                }
                                val isCustomColor = currentDrawColor !in predefinedColors
                                MultiColorPickerButton(isSelected = isCustomColor, onClick = { showColorPickerDialog = true; userActivityTime = System.currentTimeMillis() })
                            }
                        }
                    }
                }

                // Page Number Indicator
                AnimatedVisibility(
                    visible = isIndicatorVisible || showSeekbar,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkMode) Color(0xFF333333).copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.7f))
                            .clickable { showSeekbar = !showSeekbar; userActivityTime = System.currentTimeMillis() }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "${getOdiaNum(pagerState.currentPage + 1)} / ${getOdiaNum(totalPages)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                // Zoom Out Button
                AnimatedVisibility(
                    visible = scale > 1f,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = { scale = 1f; offsetX = 0f; offsetY = 0f; userActivityTime = System.currentTimeMillis() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.ZoomOutMap, contentDescription = "ଛୋଟ କରନ୍ତୁ")
                    }
                }

                // Seekbar / Slider
                AnimatedVisibility(
                    visible = showSeekbar,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp, start = 16.dp, end = 16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = topBarBg.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            var sliderVal by remember { mutableFloatStateOf(pagerState.currentPage.toFloat()) }

                            Text(
                                text = "ପୃଷ୍ଠା: ${getOdiaNum(sliderVal.toInt() + 1)} / ${getOdiaNum(totalPages)}",
                                fontWeight = FontWeight.Bold,
                                color = contentCol,
                                fontSize = 16.sp
                            )

                            LaunchedEffect(pagerState.currentPage) { sliderVal = pagerState.currentPage.toFloat() }

                            Slider(
                                value = sliderVal,
                                onValueChange = { sliderVal = it; userActivityTime = System.currentTimeMillis() },
                                onValueChangeFinished = {
                                    scope.launch { pagerState.scrollToPage(sliderVal.toInt()) }
                                    showSeekbar = false
                                },
                                valueRange = 0f..(if (totalPages > 0) totalPages - 1 else 0).toFloat(),
                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }

            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(50.dp), color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    // --- GRID VIEW OVERLAY ---
    AnimatedVisibility(
        visible = showGridPreview,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ସବୁ ପୃଷ୍ଠା", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                    navigationIcon = {
                        OdiaTooltipIconButton(onClick = { showGridPreview = false }, tooltipText = "ବନ୍ଦ କରନ୍ତୁ") {
                            Icon(Icons.Default.Close, contentDescription = "ବନ୍ଦ କରନ୍ତୁ")
                        }
                    },
                    actions = {
                        // FEATURE: Jump To Page Icon
                        OdiaTooltipIconButton(onClick = { showJumpDialog = true }, tooltipText = "ପୃଷ୍ଠାକୁ ଯାଆନ୍ତୁ") {
                            Icon(Icons.Default.Search, contentDescription = "ଖୋଜନ୍ତୁ")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topBarBg,
                        titleContentColor = contentCol,
                        navigationIconContentColor = contentCol,
                        actionIconContentColor = contentCol
                    )
                )
            }
        ) { gridPadding ->
            Box(Modifier.fillMaxSize().background(bgColor).padding(gridPadding)) {

                // FEATURE: Jump To Page Dialog
                if (showJumpDialog) {
                    var jumpInput by remember { mutableStateOf("") }

                    AlertDialog(
                        onDismissRequest = { showJumpDialog = false },
                        title = { Text("ପୃଷ୍ଠାକୁ ଯାଆନ୍ତୁ", fontWeight = FontWeight.Bold, color = contentCol) },
                        text = {
                            OutlinedTextField(
                                value = jumpInput,
                                onValueChange = { input ->
                                    // Auto-convert any English number typed to Odia numerals
                                    val odiaConverted = input.map { char ->
                                        if (char in '0'..'9') {
                                            (char - '0' + 0x0B66).toChar() // '୦' is U+0B66
                                        } else char
                                    }.filter { it in '୦'..'୯' }.joinToString("")
                                    jumpInput = odiaConverted
                                },
                                label = { Text("ପୃଷ୍ଠା ସଂଖ୍ୟା (୧ - ${getOdiaNum(totalPages)})") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = contentCol,
                                    unfocusedTextColor = contentCol
                                )
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val pageNum = odiaStringToInt(jumpInput)
                                if (pageNum != null && pageNum in 1..totalPages) {
                                    scope.launch {
                                        pagerState.scrollToPage(pageNum - 1)
                                        showGridPreview = false
                                        showJumpDialog = false
                                    }
                                } else {
                                    jumpInput = "" // Clear invalid input
                                }
                            }) { Text("ଯାଆନ୍ତୁ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showJumpDialog = false }) { Text("ବାତିଲ କରନ୍ତୁ", color = Color.Gray) }
                        },
                        containerColor = topBarBg
                    )
                }

                if (pdfRenderer != null && totalPages > 0) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
                    ) {
                        items(totalPages) { index ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        scope.launch {
                                            pagerState.scrollToPage(index)
                                            showGridPreview = false
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = topBarBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                                        PdfPageRenderer(
                                            context = context,
                                            renderer = pdfRenderer,
                                            pageIndex = index,
                                            widthPx = screenWidthPx / 2,
                                            bookId = bookId,
                                            isDrawingMode = false, drawColor = Color.Black, isHighlighter = false,
                                            isCurrentPage = false, undoTrigger = 0, redoTrigger = 0, clearAllTrigger = 0,
                                            isDarkMode = isDarkMode
                                        )
                                    }
                                    Box(modifier = Modifier.fillMaxWidth().background(if (isDarkMode) Color(0xFF333333) else Color(0xFFEAEAEA)).padding(8.dp), contentAlignment = Alignment.Center) {
                                        Text(text = getOdiaNum(index + 1), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = contentCol)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PdfPageRenderer(
    context: Context,
    renderer: PdfRenderer,
    pageIndex: Int,
    widthPx: Int,
    bookId: String,
    isDrawingMode: Boolean,
    drawColor: Color,
    isHighlighter: Boolean,
    isCurrentPage: Boolean,
    undoTrigger: Int,
    redoTrigger: Int,
    clearAllTrigger: Int,
    isDarkMode: Boolean
) {
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    var strokes by remember { mutableStateOf<List<DrawingStroke>>(emptyList()) }
    var redoStrokes by remember { mutableStateOf<List<DrawingStroke>>(emptyList()) }
    var currentStroke by remember { mutableStateOf<DrawingStroke?>(null) }

    // ColorMatrix for Dark Mode Inversion
    val colorFilter = remember(isDarkMode) {
        if (isDarkMode) {
            ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            )))
        } else null
    }

    LaunchedEffect(pageIndex, widthPx, renderer) {
        withContext(Dispatchers.IO) {
            pdfMutex.withLock {
                try {
                    val page = renderer.openPage(pageIndex)
                    val heightPx = (widthPx.toFloat() / page.width * page.height).toInt()
                    val bmp = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
                    bmp.eraseColor(android.graphics.Color.WHITE)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    bitmap = bmp
                } catch (e: IllegalStateException) {
                    // Safe Ignore
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    LaunchedEffect(Unit) {
        strokes = withContext(Dispatchers.IO) { loadStrokesLocally(context, bookId, pageIndex) }
    }

    LaunchedEffect(undoTrigger) {
        if (undoTrigger > 0 && isCurrentPage && strokes.isNotEmpty()) {
            val last = strokes.last()
            strokes = strokes.dropLast(1)
            redoStrokes = redoStrokes + last
            saveStrokesLocally(context, bookId, pageIndex, strokes)
        }
    }

    LaunchedEffect(redoTrigger) {
        if (redoTrigger > 0 && isCurrentPage && redoStrokes.isNotEmpty()) {
            val next = redoStrokes.last()
            redoStrokes = redoStrokes.dropLast(1)
            strokes = strokes + next
            saveStrokesLocally(context, bookId, pageIndex, strokes)
        }
    }

    LaunchedEffect(clearAllTrigger) {
        if (clearAllTrigger > 0 && isCurrentPage && strokes.isNotEmpty()) {
            redoStrokes = strokes
            strokes = emptyList()
            saveStrokesLocally(context, bookId, pageIndex, strokes)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (bitmap != null) {
            val imgRatio = bitmap!!.width.toFloat() / bitmap!!.height.toFloat()
            val boxRatio = maxWidth.value / maxHeight.value

            val (actualWidth, actualHeight) = if (boxRatio > imgRatio) {
                Pair(maxHeight * imgRatio, maxHeight)
            } else {
                Pair(maxWidth, maxWidth / imgRatio)
            }

            Box(modifier = Modifier.size(actualWidth, actualHeight)) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "ପୃଷ୍ଠା ${getOdiaNum(pageIndex + 1)}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = colorFilter // Applies Dark Mode Inversion
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { canvasSize = it }
                        .pointerInput(isDrawingMode, isCurrentPage, drawColor, isHighlighter, canvasSize) {
                            if (isDrawingMode && isCurrentPage && canvasSize.width > 0) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val strokeWidth = if (isHighlighter) 40f else 8f
                                        val normOffset = Offset(offset.x / canvasSize.width.toFloat(), offset.y / canvasSize.height.toFloat())
                                        currentStroke = DrawingStroke(listOf(normOffset), drawColor, strokeWidth, isHighlighter)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        val normOffset = Offset(change.position.x / canvasSize.width.toFloat(), change.position.y / canvasSize.height.toFloat())
                                        currentStroke = currentStroke?.let { it.copy(points = it.points + normOffset) }
                                    },
                                    onDragEnd = {
                                        currentStroke?.let { stroke ->
                                            strokes = strokes + stroke
                                            redoStrokes = emptyList()
                                            currentStroke = null
                                            scope.launch(Dispatchers.IO) {
                                                saveStrokesLocally(context, bookId, pageIndex, strokes)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    val drawCustomStroke = { stroke: DrawingStroke ->
                        if (stroke.points.size > 1) {
                            val isNormalized = stroke.points.first().x <= 2f && stroke.points.first().y <= 2f
                            val scaleX = if (isNormalized) w else 1f
                            val scaleY = if (isNormalized) h else 1f

                            val path = Path().apply {
                                moveTo(stroke.points.first().x * scaleX, stroke.points.first().y * scaleY)
                                for (i in 1 until stroke.points.size) {
                                    lineTo(stroke.points[i].x * scaleX, stroke.points[i].y * scaleY)
                                }
                            }

                            // Smart color swap for visibility in Dark Mode
                            val displayColor = if (isDarkMode && stroke.color == Color.Black) Color.White
                            else if (!isDarkMode && stroke.color == Color.White) Color.Black
                            else stroke.color

                            drawPath(
                                path = path,
                                color = displayColor.copy(alpha = if (stroke.isHighlighter) 0.4f else 1f),
                                style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }

                    strokes.forEach { drawCustomStroke(it) }
                    currentStroke?.let { drawCustomStroke(it) }
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.size(30.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
        }
    }
}

// ========================================================================
// MEMORY SYSTEM
// ========================================================================
private fun saveStrokesLocally(context: Context, bookId: String, page: Int, strokes: List<DrawingStroke>) {
    try {
        val file = File(context.filesDir, "draw_${bookId.hashCode()}_page_$page.json")
        val array = JSONArray()
        for (stroke in strokes) {
            val obj = JSONObject()
            obj.put("color", stroke.color.toArgb())
            obj.put("width", stroke.strokeWidth.toDouble())
            obj.put("highlighter", stroke.isHighlighter)
            val pointsArray = JSONArray()
            for (point in stroke.points) {
                val pObj = JSONObject()
                pObj.put("x", point.x.toDouble())
                pObj.put("y", point.y.toDouble())
                pointsArray.put(pObj)
            }
            obj.put("points", pointsArray)
            array.put(obj)
        }
        file.writeText(array.toString())
    } catch (e: Exception) { e.printStackTrace() }
}

private fun loadStrokesLocally(context: Context, bookId: String, page: Int): List<DrawingStroke> {
    val result = mutableListOf<DrawingStroke>()
    try {
        val file = File(context.filesDir, "draw_${bookId.hashCode()}_page_$page.json")
        if (!file.exists()) return emptyList()
        val content = file.readText()
        val array = JSONArray(content)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val color = Color(obj.getInt("color"))
            val width = obj.getDouble("width").toFloat()
            val isHighlighter = obj.getBoolean("highlighter")
            val pointsArray = obj.getJSONArray("points")
            val points = mutableListOf<Offset>()
            for (j in 0 until pointsArray.length()) {
                val pObj = pointsArray.getJSONObject(j)
                points.add(Offset(pObj.getDouble("x").toFloat(), pObj.getDouble("y").toFloat()))
            }
            result.add(DrawingStroke(points, color, width, isHighlighter))
        }
    } catch (e: Exception) { e.printStackTrace() }
    return result
}