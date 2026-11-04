package `in`.sandeep.odiabhasa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close // <<< NEW Icon
import androidx.compose.material.icons.filled.GridView // <<< NEW Icon
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.io.File

// Helper function to convert English numbers to Odia numerals
private fun convertToOdia(number: Int): String {
    val odiaNumerals = listOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return number.toString().map { odiaNumerals[it.toString().toInt()] }.joinToString("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MadhuBarnabodhaScreen(
    onBack: () -> Unit,
    viewModel: MadhuBarnabodhaViewModel = viewModel()
) {
    val bookPages = remember { viewModel.getDownloadedImageFiles() }
    val totalPages = bookPages.size
    val scope = rememberCoroutineScope() // <<< NEW: For launching pager actions

    var isZoomed by remember { mutableStateOf(false) }
    // <<< NEW: State for showing the fast-scroll slider and grid preview
    var showSlider by remember { mutableStateOf(false) }
    var showGridPreview by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ଛବିଳ ମଧୁ ବର୍ଣ୍ଣବୋଧ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                // <<< NEW: Action icon to show the grid preview
                actions = {
                    if (bookPages.isNotEmpty()) {
                        IconButton(onClick = { showGridPreview = true }) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Page Preview"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (bookPages.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { totalPages })

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = !isZoomed
                ) { pageIndex ->
                    ZoomableAsyncImage(
                        model = bookPages[pageIndex],
                        contentDescription = "ପୃଷ୍ଠା ${convertToOdia(pageIndex + 1)}",
                        onZoomChange = { zoomed -> isZoomed = zoomed }
                    )
                }

                // Page number display
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        // <<< NEW: Detect long press to show the slider
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { showSlider = true }
                            )
                        },
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    contentColor = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = "${convertToOdia(pagerState.currentPage + 1)} / ${convertToOdia(totalPages)}",
                        fontFamily = OdiaFont,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // <<< NEW: Fast scroll slider, visible only on long press
                AnimatedVisibility(
                    visible = showSlider,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp, start = 32.dp, end = 32.dp)
                ) {
                    Slider(
                        value = pagerState.currentPage.toFloat(),
                        onValueChange = { newPage ->
                            scope.launch {
                                pagerState.scrollToPage(newPage.toInt())
                            }
                        },
                        valueRange = 0f..(totalPages - 1).toFloat(),
                        steps = totalPages - 2, // makes the slider step between pages
                        onValueChangeFinished = {
                            // Hide slider after user finishes scrubbing
                            showSlider = false
                        }
                    )
                }
            }

            // <<< NEW: Full-screen Dialog for the page grid preview
            if (showGridPreview) {
                PageGridPreview(
                    pages = bookPages,
                    onPageSelected = { index ->
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                        showGridPreview = false // Close preview after selection
                    },
                    onDismiss = { showGridPreview = false }
                )
            }

        } else {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("ବହି ମିଳିଲା ନାହିଁ । ଦୟାକରି ପୁନର୍ବାର ଡାଉନଲୋଡ୍ କରନ୍ତୁ ।", fontFamily = OdiaFont)
            }
        }
    }
}


// <<< NEW: Composable for the Grid Preview Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PageGridPreview(
    pages: List<File>,
    onPageSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Full screen
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ସବୁ ପୃଷ୍ଟା", fontFamily = OdiaFont) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close Preview")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(pages) { index, file ->
                    GridPageItem(
                        file = file,
                        pageNumber = index + 1,
                        onClick = { onPageSelected(index) }
                    )
                }
            }
        }
    }
}

// <<< NEW: Composable for a single item in the Grid Preview
@Composable
private fun GridPageItem(
    file: File,
    pageNumber: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = file,
                contentDescription = "Page ${convertToOdia(pageNumber)}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f) // Adjust for book page aspect ratio
                    .clip(MaterialTheme.shapes.medium)
            )
            Text(
                text = convertToOdia(pageNumber),
                fontFamily = OdiaFont,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}


@Composable
fun ZoomableAsyncImage(
    model: File,
    contentDescription: String,
    onZoomChange: (Boolean) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .pointerInput(Unit) {
                // 1. Handle Double Tap to Zoom In/Out
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                            onZoomChange(false)
                        } else {
                            scale = 2.5f
                            onZoomChange(true)
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                // 2. Custom Gesture Detector for Smart Swipe vs Zoom
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        val scaleFactor = event.calculateZoom()
                        val panChange = event.calculatePan()
                        val pointerCount = event.changes.size

                        val isZooming = pointerCount > 1 || scale > 1f

                        if (isZooming) {
                            // Apply Zoom
                            if (scaleFactor != 1f) {
                                scale = (scale * scaleFactor).coerceAtLeast(1f)
                                // Reset offsets if we zoom back out to 1
                                if (scale == 1f) {
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            }

                            // Apply Pan (Move image)
                            if (scale > 1f) {
                                val extraWidth = (size.width * scale) - size.width
                                val extraHeight = (size.height * scale) - size.height

                                val maxX = extraWidth / 2
                                val maxY = extraHeight / 2

                                offsetX = (offsetX + panChange.x * scale).coerceIn(-maxX, maxX)
                                offsetY = (offsetY + panChange.y * scale).coerceIn(-maxY, maxY)
                            }

                            // Consume events so Pager doesn't steal them
                            event.changes.forEach {
                                if (it.positionChanged()) {
                                    it.consume()
                                }
                            }
                        }

                        onZoomChange(scale > 1.01f)

                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
        )

        AnimatedVisibility(
            visible = scale > 1.01f,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                    onZoomChange(false)
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomOutMap,
                    contentDescription = "Reset Zoom"
                )
            }
        }
    }
}