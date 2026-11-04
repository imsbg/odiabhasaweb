// --- START OF FILE BigScreen.kt ---

package `in`.sandeep.odiabhasa

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.math.max

// --- Private Helpers to avoid Conflicts ---
private fun formatTimeBigScreen(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

private suspend fun fetchLrcBigScreen(urlString: String): List<LrcLine> = withContext(Dispatchers.IO) {
    try {
        val url = URL(urlString)
        val content = url.readText()
        val lines = mutableListOf<LrcLine>()
        val lineRegex = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{1,3})\\](.*)")
        val wordRegex = Regex("<(\\d{2}):(\\d{2})\\.(\\d{1,3})>([^<]*)")

        content.lines().forEach { line ->
            val matchResult = lineRegex.find(line)
            if (matchResult != null) {
                val (min, sec, mil, rawText) = matchResult.destructured
                val lineStartTime = (min.toLong() * 60 * 1000) + (sec.toLong() * 1000) + (if (mil.length == 2) mil.toLong() * 10 else mil.toLong())
                val trimmedText = rawText.trim()
                val words = mutableListOf<LrcWord>()

                val firstTagIndex = trimmedText.indexOf('<')
                if (firstTagIndex == -1) {
                    if (trimmedText.isNotBlank()) words.add(LrcWord(trimmedText, lineStartTime))
                } else {
                    val firstWordText = trimmedText.substring(0, firstTagIndex).trim()
                    if (firstWordText.isNotEmpty()) words.add(LrcWord(firstWordText, lineStartTime))
                    wordRegex.findAll(trimmedText).forEach { wm ->
                        val (wmMin, wmSec, wmMil, wordText) = wm.destructured
                        val wordTime = (wmMin.toLong() * 60 * 1000) + (wmSec.toLong() * 1000) + (if (wmMil.length == 2) wmMil.toLong() * 10 else wmMil.toLong())
                        if (wordText.isNotBlank()) words.add(LrcWord(wordText.trim(), wordTime))
                    }
                }

                if (words.isNotEmpty()) {
                    val cleanContent = words.joinToString(" ") { it.text }
                    lines.add(LrcLine(lineStartTime, cleanContent, words))
                } else if (trimmedText.isNotEmpty()) {
                    lines.add(LrcLine(lineStartTime, trimmedText, emptyList()))
                }
            }
        }
        lines.sortedBy { it.startTime }
    } catch (e: Exception) { emptyList() }
}

class BigScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        val audioUrl = intent.getStringExtra("audioUrl")
        val startPosition = intent.getLongExtra("position", 0L)
        val lyricsUrl = intent.getStringExtra("lyricsUrl")
        val title = intent.getStringExtra("title") ?: "Audio"
        val wasPlaying = intent.getBooleanExtra("isPlaying", false)

        setContent {
            // Using ThemeColors from your shared package
            val appleTheme = ThemeColors(
                background = Color(0xFF2D2436),
                onBackground = Color.White,
                primary = Color.White,
                onPrimaryContainer = Color.White,
                primaryContainer = Color(0xFF555555)
            )

            AppleMusicPlayerScreen(
                audioUrl = audioUrl,
                lyricsUrl = lyricsUrl,
                initialPosition = startPosition,
                initialPlayState = wasPlaying,
                title = title,
                theme = appleTheme,
                onClose = { finish() }
            )
        }
    }
}

@Composable
fun AppleMusicPlayerScreen(
    audioUrl: String?,
    lyricsUrl: String?,
    initialPosition: Long,
    initialPlayState: Boolean,
    title: String,
    theme: ThemeColors,
    onClose: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // --- Media State ---
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(initialPosition) }
    var isSeeking by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(initialPosition.toFloat()) }

    // --- Lyrics State ---
    var lrcLines by remember { mutableStateOf<List<LrcLine>>(emptyList()) }
    var currentLrcIndex by remember { mutableIntStateOf(-1) }
    val listState = rememberLazyListState()

    // --- Background ---
    val randomBackgroundBrush = remember {
        val palettes = listOf(
            // 1. Purple/Dark
            listOf(Color(0xFF663E5C), Color(0xFF42385D), Color(0xFF23232C)),
            // 2. Deep Blue
            listOf(Color(0xFF1CB5E0), Color(0xFF000046)),
            // 3. Dark Red
            listOf(Color(0xFF870000), Color(0xFF190A05)),
            // 4. Purple/Teal
            listOf(Color(0xFF360033), Color(0xFF0b8793)),
            // 5. Dark Grey/Black
            listOf(Color(0xFF232526), Color(0xFF414345)),
            // 6. AMOLED Black (Added this instead of the Sky Blue one)
            listOf(Color.Black, Color.Black)
        )
        val selectedColors = palettes.random()
        Brush.verticalGradient(colors = selectedColors)
    }

    LaunchedEffect(lyricsUrl) {
        if (!lyricsUrl.isNullOrBlank()) {
            lrcLines = fetchLrcBigScreen(lyricsUrl)
        }
    }

    DisposableEffect(audioUrl) {
        if (audioUrl != null) {
            val mp = MediaPlayer()
            try {
                mp.setDataSource(audioUrl)
                mp.prepareAsync()
                mp.setOnPreparedListener { player ->
                    duration = player.duration.toLong()
                    player.seekTo(initialPosition.toInt())
                    player.start()
                    isPlaying = true
                }
                mp.setOnCompletionListener {
                    isPlaying = false
                    currentPosition = duration
                    sliderValue = duration.toFloat()
                }
            } catch (e: Exception) { e.printStackTrace() }
            mediaPlayer = mp
        }
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LaunchedEffect(isPlaying, mediaPlayer) {
        while (isPlaying && mediaPlayer != null) {
            try {
                if (mediaPlayer!!.isPlaying) {
                    val pos = mediaPlayer!!.currentPosition.toLong()
                    if (!isSeeking) {
                        currentPosition = pos
                        sliderValue = pos.toFloat()
                    }
                    if (lrcLines.isNotEmpty()) {
                        val index = lrcLines.indexOfLast { it.startTime <= pos }
                        if (index != currentLrcIndex) {
                            currentLrcIndex = index
                            if (index >= 0) {
                                listState.animateScrollToItem(index, -500)
                            }
                        }
                    }
                }
            } catch (e: Exception) { /* Ignore */ }
            delay(16)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = randomBackgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontFamily = OdiaFont,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ଓଡ଼ିଆ ଭାଷା",
                        fontFamily = OdiaFont,
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lyrics
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (lrcLines.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (lyricsUrl.isNullOrBlank()) {
                            Text(
                                "ପଦାବଳୀ ଉପଲବ୍ଧ ନାହିଁ",
                                fontFamily = OdiaFont,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(vertical = 200.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(lrcLines) { index, line ->
                            val isActive = index == currentLrcIndex
                            val nextLineTime = lrcLines.getOrNull(index + 1)?.startTime ?: (line.startTime + 5000)

                            AppleMusicKaraokeLine(
                                line = line,
                                isActive = isActive,
                                currentTime = currentPosition,
                                endTime = nextLineTime,
                                onSeek = { seekTime ->
                                    mediaPlayer?.seekTo(seekTime.toInt())
                                    currentPosition = seekTime
                                    sliderValue = seekTime.toFloat()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        isSeeking = true
                        sliderValue = it
                    },
                    onValueChangeFinished = {
                        mediaPlayer?.seekTo(sliderValue.toInt())
                        currentPosition = sliderValue.toLong()
                        scope.launch {
                            delay(50)
                            isSeeking = false
                        }
                    },
                    valueRange = 0f..max(1f, duration.toFloat()),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.height(20.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatTimeBigScreen(if (isSeeking) sliderValue.toLong() else currentPosition),
                        fontFamily = OdiaFont,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        formatTimeBigScreen(duration),
                        fontFamily = OdiaFont,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    FilledIconButton(
                        onClick = {
                            if (isPlaying) {
                                mediaPlayer?.pause()
                                isPlaying = false
                            } else {
                                mediaPlayer?.start()
                                isPlaying = true
                            }
                        },
                        modifier = Modifier.size(80.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.7f),
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- KARAOKE LINE (FlowRow + Soft Gradient Wipe) ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppleMusicKaraokeLine(
    line: LrcLine,
    isActive: Boolean,
    currentTime: Long,
    endTime: Long,
    onSeek: (Long) -> Unit
) {
    val targetScale = if (isActive) 1.0f else 0.95f
    val scale by animateFloatAsState(targetValue = targetScale, animationSpec = tween(500, easing = LinearEasing), label = "scale")
    val targetBlur = if (isActive) 0.dp else 2.dp
    val fontSize = 32.sp

    // Soft edge for the gradient wipe (0.3f = 30% of word width)
    val feather = 0.3f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .scale(scale)
            .blur(targetBlur)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onSeek(line.startTime)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        if (line.words.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Center
            ) {
                line.words.forEachIndexed { index, word ->
                    val wordEnd = if (index < line.words.size - 1) line.words[index + 1].startTime else endTime
                    val wordDuration = wordEnd - word.startTime

                    // Calculate progress for this specific word
                    val progress = when {
                        !isActive -> 0f
                        currentTime > wordEnd -> 1f
                        currentTime < word.startTime -> 0f
                        wordDuration > 0 -> (currentTime - word.startTime).toFloat() / wordDuration.toFloat()
                        else -> 0f
                    }

                    if (index > 0) {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    // Individual Word Box
                    Box {
                        // 1. Inactive/Dimmed Layer (Gray)
                        Text(
                            text = word.text,
                            style = TextStyle(
                                fontFamily = OdiaFont,
                                fontSize = fontSize,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        )

                        // 2. Active/Bright Layer
                        if (isActive && progress > 0f) {
                            if (progress >= 1f) {
                                // COMPLETE: Word is fully sung, show as solid White (No feather/gradient)
                                Text(
                                    text = word.text,
                                    style = TextStyle(
                                        fontFamily = OdiaFont,
                                        fontSize = fontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            } else {
                                // WIPING: Word is being sung, use the gradient feather
                                Text(
                                    text = word.text,
                                    style = TextStyle(
                                        fontFamily = OdiaFont,
                                        fontSize = fontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier
                                        .graphicsLayer { alpha = 0.99f }
                                        .drawWithCache {
                                            // Feathered Gradient Logic
                                            val brush = Brush.horizontalGradient(
                                                0f to Color.White,
                                                (progress - feather).coerceAtLeast(0f) to Color.White, // Solid White part
                                                progress to Color.White.copy(alpha = 0f),              // Fade to Transparent
                                                1f to Color.Transparent,
                                                startX = 0f,
                                                endX = size.width,
                                                tileMode = TileMode.Clamp
                                            )
                                            onDrawWithContent {
                                                drawContent()
                                                drawRect(brush = brush, blendMode = BlendMode.SrcIn)
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Fallback for lines without detailed word timing
            Text(
                text = line.content,
                style = TextStyle(
                    fontFamily = OdiaFont,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = if (isActive) Color.White else Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}
// --- END OF FILE BigScreen.kt ---