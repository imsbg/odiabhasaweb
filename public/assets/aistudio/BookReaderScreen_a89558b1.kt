// --- START OF FILE BookReaderScreen.kt ---

package `in`.sandeep.odiabhasa

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

// --- Theme & Enums ---

enum class ReaderTheme { LIGHT, DARK, SEPIA }
enum class ReaderTextAlign { LEFT, CENTER, RIGHT, JUSTIFY }

data class ThemeColors(
    val background: Color,
    val onBackground: Color,
    val primary: Color,
    val onPrimaryContainer: Color,
    val primaryContainer: Color
)

private val lightThemeColors = ThemeColors(background = Color.White, onBackground = Color.Black, primary = Color(0xFF6200EE), onPrimaryContainer = Color.White, primaryContainer = Color(0xFF6200EE))
private val darkThemeColors = ThemeColors(background = Color(0xFF121212), onBackground = Color.White, primary = Color(0xFFBB86FC), onPrimaryContainer = Color.Black, primaryContainer = Color(0xFFBB86FC))
private val sepiaBaseThemeColors = ThemeColors(background = Color(0xFFFBF0D9), onBackground = Color(0xFF5B4636), primary = Color(0xFF795548), onPrimaryContainer = Color.White, primaryContainer = Color(0xFF795548))

// --- Lyrics Data Models ---
data class LrcWord(
    val text: String,
    val startTime: Long
)

data class LrcLine(
    val startTime: Long,
    val content: String,
    val words: List<LrcWord> = emptyList()
)

// --- Odia Helper Functions ---

fun toOdiaNumeral(number: Int): String {
    val odiaDigits = charArrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return number.toString().map { if(it.isDigit()) odiaDigits[it.toString().toInt()] else it }.joinToString("")
}

fun toOdiaDecimal(number: Float, withSuffix: Boolean = true): String {
    val odiaDigits = charArrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    val formatted = String.format("%.2f", number)
    val converted = formatted.map { char ->
        if (char.isDigit()) odiaDigits[char.toString().toInt()] else char
    }.joinToString("")
    return if(withSuffix) "$converted ଗୁଣା" else converted
}

fun formatFileSizeToOdia(bytes: Long): String {
    if (bytes <= 0) return ""
    val kb = 1024.0
    val mb = kb * 1024.0

    return when {
        bytes < kb -> "${toOdiaNumeral(bytes.toInt())} ବି"
        bytes < mb -> "${toOdiaNumeral((bytes / kb).toInt())} କେବି"
        else -> "${toOdiaDecimal((bytes / mb).toFloat(), withSuffix = false)} ଏମବି"
    }
}

fun formatMillisecondsToOdia(millis: Long): String {
    if (millis <= 0) {
        return "୦୦:୦୦"
    }

    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val timeStr = String.format("%02d:%02d", minutes, seconds)

    val odiaDigits = charArrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return timeStr.map { if(it.isDigit()) odiaDigits[it.toString().toInt()] else it }.joinToString("")
}

// Suspend function to check file size
suspend fun getFileSize(urlString: String): Long = withContext(Dispatchers.IO) {
    try {
        val url = URL(urlString)
        val connection = url.openConnection()
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        val length = connection.contentLengthLong
        length
    } catch (e: Exception) {
        -1L
    }
}

// Helper to parse time string
fun parseTimeTag(minutes: String, seconds: String, millisStr: String): Long {
    val millisPart = if (millisStr.length == 2) millisStr.toLong() * 10 else millisStr.toLong()
    return (minutes.toLong() * 60 * 1000) + (seconds.toLong() * 1000) + millisPart
}

// --- LRC PARSER ---
suspend fun fetchAndParseLrc(urlString: String): List<LrcLine> = withContext(Dispatchers.IO) {
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
                val lineStartTime = parseTimeTag(min, sec, mil)
                val trimmedText = rawText.trim()

                val words = mutableListOf<LrcWord>()

                val firstTagIndex = trimmedText.indexOf('<')

                if (firstTagIndex == -1) {
                    if (trimmedText.isNotBlank()) words.add(LrcWord(trimmedText, lineStartTime))
                } else {
                    val firstWordText = trimmedText.substring(0, firstTagIndex).trim()
                    if (firstWordText.isNotEmpty()) {
                        words.add(LrcWord(firstWordText, lineStartTime))
                    }
                    val wordMatches = wordRegex.findAll(trimmedText)
                    wordMatches.forEach { wm ->
                        val (wmMin, wmSec, wmMil, wordText) = wm.destructured
                        val wordTime = parseTimeTag(wmMin, wmSec, wmMil)
                        if (wordText.isNotBlank()) {
                            words.add(LrcWord(wordText.trim(), wordTime))
                        }
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
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}


data class DetailedSearchResult(
    val pageIndex: Int,
    val blockIndex: Int,
    val matchRange: IntRange
)

// Main Screen
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun BookReaderScreen(
    bookDetails: BookDetails,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("odia_reader_prefs", Context.MODE_PRIVATE) }

    // --- State ---
    var currentTheme by remember {
        val savedThemeName = prefs.getString("theme", ReaderTheme.LIGHT.name)
        mutableStateOf(ReaderTheme.valueOf(savedThemeName ?: ReaderTheme.LIGHT.name))
    }
    var currentFontSize by remember {
        val savedSize = prefs.getFloat("font_size", 18f)
        mutableStateOf(savedSize.sp)
    }
    var sepiaIntensity by remember {
        val savedIntensity = prefs.getFloat("sepia_intensity", 0.5f)
        mutableStateOf(savedIntensity)
    }
    var currentTextAlign by remember {
        val savedAlignName = prefs.getString("text_align", ReaderTextAlign.LEFT.name)
        mutableStateOf(ReaderTextAlign.valueOf(savedAlignName ?: ReaderTextAlign.LEFT.name))
    }

    var showSettingsBottomSheet by remember { mutableStateOf(false) }

    // Search State
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var currentSearchResultIndex by remember { mutableStateOf(-1) }
    var scrollTarget by remember { mutableStateOf<DetailedSearchResult?>(null) }

    // Audio State
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var isPlayerVisible by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var showSpeedControl by remember { mutableStateOf(false) }
    var currentAudioUrl by remember { mutableStateOf<String?>(null) }
    var audioFileSizeText by remember { mutableStateOf("") }
    var audioDuration by remember { mutableLongStateOf(0L) }
    var currentAudioPosition by remember { mutableLongStateOf(0L) }

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    // Lyrics State
    var playingLyricsUrl by remember { mutableStateOf<String?>(null) }
    var lrcLines by remember { mutableStateOf<List<LrcLine>>(emptyList()) }
    var showLyrics by remember { mutableStateOf(false) }
    var currentLrcIndex by remember { mutableIntStateOf(-1) }
    val lyricsListState = rememberLazyListState()

    // --- NEW: Text-to-Speech (TTS) State ---
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    var isTtsPlaying by remember { mutableStateOf(false) }
    var showTtsPlayer by remember { mutableStateOf(false) }
    var showTtsInstallDialog by remember { mutableStateOf(false) }
    var ttsPlaybackSpeed by remember { mutableFloatStateOf(1.0f) }
    var currentTtsBlockIndex by remember { mutableIntStateOf(-1) }
    var totalTtsBlocks by remember { mutableIntStateOf(0) }
    var isTtsPaused by remember { mutableStateOf(false) }


    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val currentPage = bookDetails.pages.getOrNull(pagerState.currentPage)


    fun playTtsFromBlock(startFromIndex: Int) {
        if (!isTtsReady) {
            showTtsInstallDialog = true
            return
        }
        currentPage?.let { page ->
            tts?.stop()
            val speakableBlocks = page.content.filter {
                val text = it.text ?: it.spans?.joinToString("") { s -> s.text }
                !text.isNullOrBlank()
            }
            totalTtsBlocks = speakableBlocks.size

            if (startFromIndex >= speakableBlocks.size) {
                // We've reached the end
                showTtsPlayer = false
                isTtsPlaying = false
                currentTtsBlockIndex = -1
                return
            }

            speakableBlocks.forEachIndexed { index, block ->
                if (index >= startFromIndex) {
                    val textToSpeak = (block.text ?: block.spans?.joinToString("") { it.text })
                    val queueMode = if (index == startFromIndex) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                    val utteranceId = "block_$index"
                    tts?.speak(textToSpeak, queueMode, null, utteranceId)
                    // Add pause after headers
                    if (block.type == "header") {
                        tts?.playSilentUtterance(750, TextToSpeech.QUEUE_ADD, "silence_after_header_$index")
                    }
                }
            }
            showTtsPlayer = true
        }
    }


    // Moved helper functions here to resolve scope issues and ensure they are defined before use.
    fun stopTts() {
        tts?.stop()
        isTtsPlaying = false
        showTtsPlayer = false
        isTtsPaused = false
        currentTtsBlockIndex = -1
        totalTtsBlocks = 0
    }

    fun stopAudio(closePlayer: Boolean = true) {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
            isAudioPlaying = false
            currentAudioUrl = null

            playingLyricsUrl = null
            lrcLines = emptyList()
            currentLrcIndex = -1

            if (closePlayer) {
                isPlayerVisible = false
                showLyrics = false
            }
            currentAudioPosition = 0L
            audioDuration = 0L
            sliderPosition = 0f
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateSpeed(speed: Float) {
        playbackSpeed = speed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(speed) ?: PlaybackParams().setSpeed(speed)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun playAudio(url: String, lyricsUrl: String?) {
        if (currentAudioUrl == url && mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                isAudioPlaying = false
            } else {
                mediaPlayer!!.start()
                isAudioPlaying = true
                updateSpeed(playbackSpeed)
            }
        } else {
            try {
                if (mediaPlayer?.isPlaying == true) mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (e: Exception) { e.printStackTrace() }

            currentAudioUrl = url
            playingLyricsUrl = lyricsUrl
            lrcLines = emptyList()
            currentLrcIndex = -1
            currentAudioPosition = 0L
            audioDuration = 0L
            sliderPosition = 0f

            val mp = MediaPlayer().apply {
                try {
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener {
                        val d = it.duration.toLong()
                        if (d > 0) audioDuration = d

                        it.start()
                        isAudioPlaying = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            try {
                                it.playbackParams = PlaybackParams().setSpeed(playbackSpeed)
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    }
                    setOnCompletionListener {
                        isAudioPlaying = false
                        currentAudioPosition = audioDuration
                        sliderPosition = audioDuration.toFloat()
                    }
                    setOnErrorListener { _, _, _ -> isAudioPlaying = false; true }
                } catch (e: Exception) { e.printStackTrace() }
            }
            mediaPlayer = mp
        }
    }
    // End of moved helper functions


    // --- NEW: TTS Initialization and Lifecycle ---
    val utteranceProgressListener = remember {
        object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                scope.launch(Dispatchers.Main) {
                    isTtsPlaying = true
                    isTtsPaused = false
                    val blockIndexStr = utteranceId?.removePrefix("block_")
                    if (blockIndexStr != null) {
                        currentTtsBlockIndex = blockIndexStr.toIntOrNull() ?: -1
                    }
                }
            }

            override fun onDone(utteranceId: String?) {
                scope.launch(Dispatchers.Main) {
                    val blockIndexStr = utteranceId?.removePrefix("block_")
                    val doneIndex = blockIndexStr?.toIntOrNull() ?: -1
                    if (doneIndex == totalTtsBlocks - 1) {
                        isTtsPlaying = false
                        isTtsPaused = false
                        showTtsPlayer = false
                        currentTtsBlockIndex = -1
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                scope.launch(Dispatchers.Main) {
                    isTtsPlaying = false
                    isTtsPaused = false
                }
            }
        }
    }

    LaunchedEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val odiaLocale = Locale("or", "IN")
                val result = tts?.isLanguageAvailable(odiaLocale)
                if (result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    tts?.language = odiaLocale
                    isTtsReady = true
                    tts?.setOnUtteranceProgressListener(utteranceProgressListener)
                } else {
                    isTtsReady = false
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            tts?.stop()
            tts?.shutdown()
            tts = null
        }
    }

    // Stop TTS when page changes
    LaunchedEffect(pagerState.currentPage) {
        stopTts()
        stopAudio(closePlayer = true)
    }

    val derivedSepiaTheme = remember(sepiaIntensity) {
        ThemeColors(
            background = lerp(Color(0xFFFCF5E5), sepiaBaseThemeColors.background, sepiaIntensity),
            onBackground = lerp(Color(0xFF705D4D), sepiaBaseThemeColors.onBackground, sepiaIntensity),
            primary = sepiaBaseThemeColors.primary,
            onPrimaryContainer = sepiaBaseThemeColors.onPrimaryContainer,
            primaryContainer = sepiaBaseThemeColors.primaryContainer
        )
    }

    val theme = when (currentTheme) {
        ReaderTheme.LIGHT -> lightThemeColors
        ReaderTheme.DARK -> darkThemeColors
        ReaderTheme.SEPIA -> derivedSepiaTheme
    }

    val currentPageTitle = remember(currentPage) {
        currentPage?.content?.firstOrNull { it.type == "header" }?.text?.takeIf { it.isNotBlank() }
            ?: bookDetails.title
    }

    val hasAudioLink = !currentPage?.audio.isNullOrBlank()

    LaunchedEffect(currentAudioUrl) {
        audioFileSizeText = ""
        if (currentAudioUrl != null) {
            launch {
                val size = getFileSize(currentAudioUrl!!)
                if (size > 0) audioFileSizeText = formatFileSizeToOdia(size)
            }
        }
    }

    LaunchedEffect(playingLyricsUrl) {
        if (!playingLyricsUrl.isNullOrBlank()) {
            launch {
                lrcLines = fetchAndParseLrc(playingLyricsUrl!!)
            }
        }
    }

    LaunchedEffect(isAudioPlaying) {
        while (isAudioPlaying) {
            mediaPlayer?.let { mp ->
                try {
                    if (mp.isPlaying) {
                        val currentPos = mp.currentPosition.toLong()
                        val reportedDuration = mp.duration.toLong()
                        val realDuration = max(reportedDuration, max(currentPos, audioDuration))
                        if (realDuration > audioDuration) {
                            audioDuration = realDuration
                        }
                        if (!isSeeking) {
                            currentAudioPosition = currentPos
                            sliderPosition = currentPos.toFloat()
                        }
                        if (lrcLines.isNotEmpty()) {
                            val index = lrcLines.indexOfLast { it.startTime <= currentAudioPosition }
                            if (index != currentLrcIndex) {
                                currentLrcIndex = index
                                if (showLyrics && index >= 0) {
                                    lyricsListState.animateScrollToItem(index)
                                }
                            }
                        }
                    }
                } catch (e: Exception) { /* Ignore */ }
            }
            delay(32)
        }
    }

    // Search Logic
    val searchResults = remember(searchQuery, bookDetails.pages) {
        if (searchQuery.length < 2) {
            emptyList()
        } else {
            val results = mutableListOf<DetailedSearchResult>()
            val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
            bookDetails.pages.forEachIndexed { pageIndex, page ->
                val speakableContent = page.content.filter {
                    val text = it.text ?: it.spans?.joinToString("") { s -> s.text }
                    !text.isNullOrBlank()
                }
                speakableContent.forEachIndexed { blockIndex, block ->
                    val text = block.text ?: block.spans?.joinToString("") { it.text } ?: ""
                    regex.findAll(text).forEach { matchResult ->
                        results.add(DetailedSearchResult(pageIndex, blockIndex, matchResult.range))
                    }
                }
            }
            results
        }
    }

    fun navigateToSearchResult(index: Int) {
        if (index in searchResults.indices) {
            currentSearchResultIndex = index
            val result = searchResults[index]
            scrollTarget = result
            if (pagerState.currentPage != result.pageIndex) {
                scope.launch { pagerState.scrollToPage(result.pageIndex) }
            }
        }
    }

    var previousSearchQuery by remember { mutableStateOf("") }
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && previousSearchQuery.isBlank()) {
            currentSearchResultIndex = if (searchResults.isNotEmpty()) 0 else -1
            if (searchResults.isNotEmpty()) navigateToSearchResult(0)
        } else if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
            if (currentSearchResultIndex == -1 || currentSearchResultIndex >= searchResults.size) {
                currentSearchResultIndex = 0
                navigateToSearchResult(0)
            }
        } else if (searchQuery.isBlank()){
            currentSearchResultIndex = -1
        }
        previousSearchQuery = searchQuery
    }

    Scaffold(
        containerColor = theme.background,
        contentColor = theme.onBackground,
        topBar = {
            if (isSearchActive) {
                SearchAppBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClose = { isSearchActive = false; searchQuery = "" },
                    onNavigatePrevious = { navigateToSearchResult(currentSearchResultIndex - 1) },
                    onNavigateNext = { navigateToSearchResult(currentSearchResultIndex + 1) },
                    currentResultIndex = currentSearchResultIndex,
                    totalResults = searchResults.size,
                    theme = theme
                )
            } else {
                TopAppBar(
                    title = {
                        Column {
                            Text(bookDetails.title, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(bookDetails.author, fontFamily = OdiaFont, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = theme.onBackground.copy(alpha = 0.7f))
                        }
                    },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ପଛକୁ ଯାଆନ୍ତୁ") } },
                    actions = {
                        if (hasAudioLink) {
                            IconButton(onClick = {
                                isPlayerVisible = !isPlayerVisible
                                if (isPlayerVisible && currentAudioUrl != currentPage?.audio) {
                                    stopTts() // Stop TTS if opening audio player
                                    val lyrics = try {
                                        val field = currentPage!!.javaClass.getDeclaredField("lyrics")
                                        field.isAccessible = true
                                        field.get(currentPage) as? String
                                    } catch (e: Exception) { null }
                                    playAudio(currentPage!!.audio!!, lyrics)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleOutline,
                                    contentDescription = "ଶୁଣନ୍ତୁ",
                                    tint = if (isPlayerVisible || isAudioPlaying) theme.primary else theme.onBackground
                                )
                            }
                        }
                        IconButton(onClick = { isSearchActive = true }) { Icon(Icons.Default.Search, contentDescription = "ଖୋଜନ୍ତୁ") }
                        IconButton(onClick = { showSettingsBottomSheet = true }) { Icon(Icons.Default.Tune, contentDescription = "ସେଟିଂସ୍") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = theme.background,
                        titleContentColor = theme.onBackground,
                        navigationIconContentColor = theme.onBackground,
                        actionIconContentColor = theme.onBackground
                    )
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().background(theme.background)) {
                AnimatedVisibility(visible = isAudioPlaying) {
                    val progress = if (audioDuration > 0) currentAudioPosition.toFloat() / audioDuration.toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(3.dp),
                        color = theme.primary,
                        trackColor = theme.primary.copy(alpha = 0.2f),
                    )
                }

                // --- NEW: TTS Player Controls ---
                AnimatedVisibility(
                    visible = showTtsPlayer,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    TtsPlayerControls(
                        isPlaying = isTtsPlaying,
                        speed = ttsPlaybackSpeed,
                        onPlayPause = {
                            if (isTtsPlaying) {
                                tts?.stop()
                                isTtsPaused = true
                                isTtsPlaying = false
                            } else {
                                isTtsPaused = false
                                playTtsFromBlock(currentTtsBlockIndex.coerceAtLeast(0))
                            }
                        },
                        onSpeedChange = { newSpeed ->
                            ttsPlaybackSpeed = newSpeed
                            tts?.setSpeechRate(newSpeed)
                            if (isTtsPlaying) {
                                playTtsFromBlock(currentTtsBlockIndex.coerceAtLeast(0))
                            }
                        },
                        onStop = { stopTts() },
                        theme = theme,
                        currentBlock = currentTtsBlockIndex,
                        totalBlocks = totalTtsBlocks,
                        onSeek = { blockIndex ->
                            isTtsPaused = false
                            playTtsFromBlock(blockIndex)
                        }
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "${toOdiaNumeral(pagerState.currentPage + 1)} / ${toOdiaNumeral(bookDetails.pages.size)}",
                        modifier = Modifier.align(Alignment.CenterEnd),
                        fontFamily = OdiaFont,
                        color = theme.onBackground
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            HorizontalPager(
                count = bookDetails.pages.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                PageContent(
                    page = bookDetails.pages[pageIndex],
                    bookDetails = bookDetails,
                    fontSize = currentFontSize,
                    currentTextAlign = currentTextAlign,
                    textColor = theme.onBackground,
                    onPageJump = { targetPage -> scope.launch { pagerState.scrollToPage(targetPage - 1) } },
                    theme = theme,
                    searchResults = searchResults,
                    currentResultIndex = currentSearchResultIndex,
                    scrollTarget = scrollTarget,
                    pageIndex = pageIndex,
                    currentTtsBlockIndex = if (pagerState.currentPage == pageIndex) currentTtsBlockIndex else -1
                )
            }

            // --- NEW: TTS Play Button with condition and new padding ---
            if (!isSearchActive && !isPlayerVisible && pagerState.currentPage > 1) {
                FilledIconButton(
                    onClick = {
                        stopAudio() // Stop audio if starting TTS
                        playTtsFromBlock(0)
                    },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = theme.primaryContainer,
                        contentColor = theme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 64.dp) // Adjusted padding
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "ପୃଷ୍ଠା ପଢ଼ନ୍ତୁ"
                    )
                }
            }


            if (isPlayerVisible) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Transparent).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { isPlayerVisible = false })
            }

            AnimatedVisibility(
                visible = isPlayerVisible,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = theme.primaryContainer, contentColor = theme.onPrimaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                        .pointerInput(Unit) { detectVerticalDragGestures { _, dragAmount -> if (dragAmount < -20) isPlayerVisible = false } }
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            FilledIconButton(
                                onClick = {
                                    if (!currentPage?.audio.isNullOrBlank()) {
                                        val lyrics = try {
                                            val field = currentPage!!.javaClass.getDeclaredField("lyrics")
                                            field.isAccessible = true
                                            field.get(currentPage) as? String
                                        } catch (e: Exception) { null }
                                        playAudio(currentPage!!.audio!!, lyrics)
                                    }
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = theme.onPrimaryContainer, contentColor = theme.primaryContainer),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                    Text(text = currentPageTitle, style = MaterialTheme.typography.titleMedium, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = theme.onPrimaryContainer.copy(alpha = 0.7f), modifier = Modifier.size(24.dp).clickable { isPlayerVisible = false })
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "ଓଡ଼ିଆ ଭାଷା", style = MaterialTheme.typography.bodySmall, fontFamily = OdiaFont, color = theme.onPrimaryContainer.copy(alpha = 0.7f))
                                    if (audioFileSizeText.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "• $audioFileSizeText", style = MaterialTheme.typography.bodySmall, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = theme.onPrimaryContainer.copy(alpha = 0.9f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SuggestionChip(
                                        onClick = { showSpeedControl = !showSpeedControl },
                                        label = { Text(toOdiaDecimal(playbackSpeed), fontFamily = OdiaFont, style = MaterialTheme.typography.labelMedium) },
                                        icon = { Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = theme.onPrimaryContainer.copy(alpha = 0.1f), labelColor = theme.onPrimaryContainer, iconContentColor = theme.onPrimaryContainer),
                                        border = BorderStroke(1.dp, theme.onPrimaryContainer.copy(alpha = 0.3f)),
                                        modifier = Modifier.height(32.dp)
                                    )
                                    if (!playingLyricsUrl.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        SuggestionChip(
                                            onClick = { showLyrics = !showLyrics },
                                            label = { Text("ପଦାବଳୀ", fontFamily = OdiaFont, style = MaterialTheme.typography.labelMedium) },
                                            icon = { Icon(if(showLyrics) Icons.Filled.QueueMusic else Icons.Filled.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = if(showLyrics) theme.onPrimaryContainer.copy(alpha = 0.3f) else theme.onPrimaryContainer.copy(alpha = 0.1f), labelColor = theme.onPrimaryContainer, iconContentColor = theme.onPrimaryContainer),
                                            border = BorderStroke(1.dp, theme.onPrimaryContainer.copy(alpha = 0.3f)),
                                            modifier = Modifier.height(32.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (playbackSpeed != 1.0f) {
                                        IconButton(onClick = { updateSpeed(1.0f) }, modifier = Modifier.size(32.dp)) { Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = theme.onPrimaryContainer.copy(alpha = 0.8f), modifier = Modifier.size(20.dp)) }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Slider(
                                value = sliderPosition.coerceIn(0f, max(1f, audioDuration.toFloat())),
                                onValueChange = {
                                    isSeeking = true
                                    sliderPosition = it
                                },
                                onValueChangeFinished = {
                                    currentAudioPosition = sliderPosition.toLong()
                                    mediaPlayer?.seekTo(currentAudioPosition.toInt())
                                    scope.launch {
                                        delay(150)
                                        isSeeking = false
                                    }
                                },
                                valueRange = 0f..max(audioDuration.toFloat(), 1f),
                                colors = SliderDefaults.colors(thumbColor = theme.onPrimaryContainer, activeTrackColor = theme.onPrimaryContainer, inactiveTrackColor = theme.onPrimaryContainer.copy(alpha = 0.3f))
                            )
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = formatMillisecondsToOdia(if(isSeeking) sliderPosition.toLong() else currentAudioPosition), fontFamily = OdiaFont, style = MaterialTheme.typography.labelSmall, color = theme.onPrimaryContainer.copy(alpha = 0.9f))
                                Text(text = formatMillisecondsToOdia(audioDuration), fontFamily = OdiaFont, style = MaterialTheme.typography.labelSmall, color = theme.onPrimaryContainer.copy(alpha = 0.9f))
                            }
                        }

                        AnimatedVisibility(visible = showLyrics) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                                    .heightIn(min = 100.dp, max = 240.dp)
                                    .background(theme.onPrimaryContainer.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                                    .animateContentSize(animationSpec = tween(300))
                            ) {
                                if (lrcLines.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(color = theme.onPrimaryContainer, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("ପଦାବଳୀ ଲୋଡ୍ ହେଉଛି...", fontFamily = OdiaFont, style = MaterialTheme.typography.bodySmall, color = theme.onPrimaryContainer.copy(alpha = 0.7f))
                                        }
                                    }
                                } else {
                                    LazyColumn(state = lyricsListState, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        itemsIndexed(lrcLines) { index, line ->
                                            val isActiveLine = index == currentLrcIndex
                                            val nextLineTime = lrcLines.getOrNull(index + 1)?.startTime ?: (line.startTime + 5000)
                                            KaraokeText(
                                                line = line,
                                                endTime = nextLineTime,
                                                currentTime = currentAudioPosition,
                                                isActive = isActiveLine,
                                                theme = theme
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        AnimatedVisibility(visible = showSpeedControl) {
                            Column(modifier = Modifier.padding(top = 24.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("୦.୨୫ ଗୁଣା", fontFamily = OdiaFont, fontSize = 12.sp, color = theme.onPrimaryContainer.copy(alpha = 0.8f))
                                    Text("୪.୦୦ ଗୁଣା", fontFamily = OdiaFont, fontSize = 12.sp, color = theme.onPrimaryContainer.copy(alpha = 0.8f))
                                }
                                Slider(value = playbackSpeed, onValueChange = { updateSpeed(it) }, valueRange = 0.25f..4.0f, steps = 14, colors = SliderDefaults.colors(thumbColor = theme.onPrimaryContainer, activeTrackColor = theme.onPrimaryContainer, inactiveTrackColor = theme.onPrimaryContainer.copy(alpha = 0.3f)))
                            }
                        }

                        // --- NEW BIG SCREEN BUTTON (Fixed with correct Context) ---
                        if (currentAudioUrl != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = {
                                    try {
                                        if (mediaPlayer?.isPlaying == true) {
                                            mediaPlayer?.pause()
                                            isAudioPlaying = false
                                        }

                                        // Ensure explicit Intent to BigScreen
                                        val intent = Intent(context, BigScreen::class.java).apply {
                                            putExtra("audioUrl", currentAudioUrl)
                                            putExtra("position", currentAudioPosition)
                                            putExtra("isPlaying", true)
                                            putExtra("lyricsUrl", playingLyricsUrl)
                                            putExtra("title", currentPageTitle)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, theme.onPrimaryContainer.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = theme.onPrimaryContainer
                                )
                            ) {
                                Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ବଡ଼ ଆକାରରେ ଦେଖନ୍ତୁ", fontFamily = OdiaFont)
                            }
                        }
                        // --- END NEW BUTTON ---
                    }
                }
            }
        }
        if (showSettingsBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showSettingsBottomSheet = false }, containerColor = theme.background) {
                SettingsContent(onThemeChange = { currentTheme = it; prefs.edit().putString("theme", it.name).apply() }, onFontSizeChange = { currentFontSize = it; prefs.edit().putFloat("font_size", it.value).apply() }, onSepiaIntensityChange = { sepiaIntensity = it; prefs.edit().putFloat("sepia_intensity", it).apply() }, onTextAlignChange = { currentTextAlign = it; prefs.edit().putString("text_align", it.name).apply() }, onReset = { currentTheme = ReaderTheme.LIGHT; currentFontSize = 18.sp; sepiaIntensity = 0.5f; currentTextAlign = ReaderTextAlign.LEFT; prefs.edit().putString("theme", ReaderTheme.LIGHT.name).putFloat("font_size", 18f).putFloat("sepia_intensity", 0.5f).putString("text_align", ReaderTextAlign.LEFT.name).apply() }, currentFontSize = currentFontSize, currentTheme = currentTheme, currentSepiaIntensity = sepiaIntensity, currentTextAlign = currentTextAlign, themeColors = theme)
            }
        }

        // --- TTS Installation Dialog ---
        if (showTtsInstallDialog) {
            TtsInstallDialog(
                onDismiss = { showTtsInstallDialog = false },
                onOpenSettings = {
                    val intent = Intent("com.android.settings.TTS_SETTINGS")
                    context.startActivity(intent)
                    showTtsInstallDialog = false
                },
                theme = theme
            )
        }
    }
}

// ---------------- WORD-LEVEL ACCURATE KARAOKE ANIMATION ----------------

@Composable
fun KaraokeText(
    line: LrcLine,
    endTime: Long,
    currentTime: Long,
    isActive: Boolean,
    theme: ThemeColors
) {
    val isPast = !isActive && endTime < currentTime
    val progress = remember(currentTime, line, endTime, isActive) {
        if (isPast) 1f
        else if (!isActive) 0f
        else if (currentTime > endTime) 1f
        else if (line.words.isNotEmpty()) {
            val totalChars = line.content.length.toFloat()
            if (totalChars == 0f) 0f else {
                var charsPassed = 0f
                var currentWordCharProgress = 0f

                line.words.forEachIndexed { i, word ->
                    val wordEnd = if (i < line.words.size - 1) line.words[i + 1].startTime else endTime
                    val wordLen = word.text.length + 1

                    if (currentTime >= wordEnd) {
                        charsPassed += wordLen
                    } else if (currentTime >= word.startTime) {
                        val duration = wordEnd - word.startTime
                        val p = if(duration > 0) (currentTime - word.startTime).toFloat() / duration.toFloat() else 1f
                        currentWordCharProgress = wordLen * p
                    }
                }
                (charsPassed + currentWordCharProgress) / totalChars
            }
        } else {
            val duration = endTime - line.startTime
            if (duration > 0) (currentTime - line.startTime).toFloat() / duration.toFloat() else 0f
        }
    }

    val feather = 0.15f

    val textStyle = TextStyle(
        fontFamily = OdiaFont,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        fontWeight = if (isActive || isPast) FontWeight.Bold else FontWeight.Normal
    )

    Box(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = line.content,
            style = textStyle,
            color = theme.onPrimaryContainer.copy(alpha = if (isPast) 1.0f else 0.5f)
        )
        if (isActive) {
            Text(
                text = line.content,
                style = textStyle,
                color = theme.onPrimaryContainer,
                modifier = Modifier.graphicsLayer { alpha = 0.99f }
                    .drawWithCache {
                        val brush = Brush.horizontalGradient(
                            0f to theme.onPrimaryContainer,
                            (progress - feather).coerceAtLeast(0f) to theme.onPrimaryContainer,
                            progress to theme.onPrimaryContainer.copy(alpha = 0f),
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

// ---------------- SUB COMPOSABLES ----------------

@Composable
fun SearchAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit,
    currentResultIndex: Int,
    totalResults: Int,
    theme: ThemeColors
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    TopAppBar(
        title = {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                textStyle = TextStyle(color = theme.onBackground, fontFamily = OdiaFont, fontSize = 16.sp),
                cursorBrush = SolidColor(theme.primary),
                singleLine = true,
                decorationBox = { innerTextField -> if (query.isEmpty()) Text("ଶବ୍ଦ ଲେଖନ୍ତୁ...", color = theme.onBackground.copy(alpha = 0.5f), fontFamily = OdiaFont) else innerTextField() }
            )
        },
        navigationIcon = { IconButton(onClick = onClose) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ବନ୍ଦ କରନ୍ତୁ") } },
        actions = {
            if (query.isNotBlank() && totalResults > 0) {
                Text(text = "${toOdiaNumeral(currentResultIndex + 1)}/${toOdiaNumeral(totalResults)}", color = theme.onBackground, fontFamily = OdiaFont, modifier = Modifier.align(Alignment.CenterVertically))
            }
            IconButton(onClick = onNavigatePrevious, enabled = currentResultIndex > 0) { Icon(Icons.Default.KeyboardArrowUp, contentDescription = "ପୂର୍ବବର୍ତ୍ତୀ") }
            IconButton(onClick = onNavigateNext, enabled = totalResults > 0 && currentResultIndex < totalResults - 1) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "ପରବର୍ତ୍ତୀ") }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.background, titleContentColor = theme.onBackground, navigationIconContentColor = theme.onBackground, actionIconContentColor = theme.onBackground)
    )
}

@Composable
fun PageContent(
    page: BookPage,
    bookDetails: BookDetails,
    fontSize: androidx.compose.ui.unit.TextUnit,
    currentTextAlign: ReaderTextAlign,
    textColor: Color,
    onPageJump: (Int) -> Unit,
    theme: ThemeColors,
    searchResults: List<DetailedSearchResult>,
    currentResultIndex: Int,
    scrollTarget: DetailedSearchResult?,
    pageIndex: Int,
    currentTtsBlockIndex: Int
) {
    val isTocPage = page.pageNumber == 2
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val speakableContent = remember(page.content) {
        page.content.filter {
            val text = it.text ?: it.spans?.joinToString("") { s -> s.text }
            !text.isNullOrBlank()
        }
    }


    LaunchedEffect(scrollTarget) { if (scrollTarget != null && scrollTarget.pageIndex == pageIndex) scope.launch { lazyListState.animateScrollToItem(scrollTarget.blockIndex) } }
    LaunchedEffect(currentTtsBlockIndex) {
        if (currentTtsBlockIndex != -1) {
            scope.launch {
                lazyListState.animateScrollToItem(currentTtsBlockIndex, -150)
            }
        }
    }
    val userComposeTextAlign = when(currentTextAlign) { ReaderTextAlign.LEFT -> TextAlign.Start; ReaderTextAlign.CENTER -> TextAlign.Center; ReaderTextAlign.RIGHT -> TextAlign.End; ReaderTextAlign.JUSTIFY -> TextAlign.Justify }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 108.dp), verticalArrangement = if (page.pageNumber == 1) Arrangement.Center else Arrangement.Top, horizontalAlignment = if (page.pageNumber == 1) Alignment.CenterHorizontally else Alignment.Start) {
            itemsIndexed(speakableContent) { blockIndex, contentBlock ->
                val effectiveTextAlign = if (page.pageNumber == 1) TextAlign.Center else userComposeTextAlign
                val isHighlightedForTts = blockIndex == currentTtsBlockIndex
                val highlightModifier = if (isHighlightedForTts) {
                    Modifier
                        .background(Color.Yellow.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                } else Modifier

                when (contentBlock.type) {
                    "header" -> Text(text = contentBlock.text ?: "", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = fontSize * 1.5f, lineHeight = (fontSize * 1.5f) * 1.4f, color = textColor, textAlign = effectiveTextAlign, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).then(highlightModifier))
                    "paragraph" -> {
                        val fullText = contentBlock.spans?.joinToString("") { it.text } ?: ""
                        val annotatedString = buildAnnotatedString {
                            append(fullText)
                            val matchesInBlock = searchResults.filter { it.pageIndex == pageIndex && it.blockIndex == blockIndex }
                            matchesInBlock.forEach { match ->
                                val isCurrent = searchResults.indexOf(match) == currentResultIndex
                                addStyle(
                                    style = SpanStyle(
                                        background = if (isCurrent) theme.primary else theme.primary.copy(alpha = 0.3f),
                                        color = if (isCurrent) theme.onPrimaryContainer else textColor
                                    ),
                                    start = match.matchRange.first,
                                    end = match.matchRange.last + 1
                                )
                            }
                        }
                        val targetPage = if (isTocPage) bookDetails.pages.find { p -> p.pageNumber != page.pageNumber && p.content.any { cb -> val headerText = (cb.text ?: cb.spans?.joinToString("") { it.text })?.trim(); cb.type == "header" && !headerText.isNullOrEmpty() && fullText.trim().contains(headerText) } }?.pageNumber else null
                        Text(text = annotatedString, fontFamily = OdiaFont, fontSize = fontSize, color = if (targetPage != null) theme.primary else textColor, lineHeight = fontSize * 1.6f, textAlign = effectiveTextAlign, modifier = (if (targetPage != null) Modifier.clickable { onPageJump(targetPage) } else Modifier).then(highlightModifier))
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        val canScroll by remember { derivedStateOf { lazyListState.canScrollForward } }
        AnimatedVisibility(visible = canScroll, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp), enter = fadeIn(), exit = fadeOut()) {
            Surface(modifier = Modifier.clickable { scope.launch { lazyListState.animateScrollBy(400f) } }, shape = RoundedCornerShape(16.dp), color = theme.primaryContainer.copy(alpha = 0.8f), contentColor = theme.onPrimaryContainer) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ତଳେ ଆହୁରି ଅଛି", fontFamily = OdiaFont, fontSize = 12.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    onThemeChange: (ReaderTheme) -> Unit,
    onFontSizeChange: (androidx.compose.ui.unit.TextUnit) -> Unit,
    onSepiaIntensityChange: (Float) -> Unit,
    onTextAlignChange: (ReaderTextAlign) -> Unit,
    onReset: () -> Unit,
    currentFontSize: androidx.compose.ui.unit.TextUnit,
    currentTheme: ReaderTheme,
    currentSepiaIntensity: Float,
    currentTextAlign: ReaderTextAlign,
    themeColors: ThemeColors
) {
    Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
        Text("ନିର୍ଦ୍ଧାରଣ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = themeColors.onBackground, modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally))
        Text("ଅକ୍ଷରର ଆକାର", fontFamily = OdiaFont, fontSize = 16.sp, color = themeColors.onBackground.copy(alpha = 0.8f))
        Slider(value = currentFontSize.value, onValueChange = { onFontSizeChange(it.sp) }, valueRange = 12f..32f, steps = 9, colors = SliderDefaults.colors(thumbColor = themeColors.primary, activeTrackColor = themeColors.primary))
        Spacer(modifier = Modifier.height(24.dp))
        Text("ପାଠ୍ୟ ସଜ୍ଜା", fontFamily = OdiaFont, fontSize = 16.sp, color = themeColors.onBackground.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip({ onTextAlignChange(ReaderTextAlign.LEFT) }, { Text("ବାମ ପାର୍ଶ୍ୱକୁ ସଜାଇବା", fontFamily = OdiaFont) }, currentTextAlign == ReaderTextAlign.LEFT, themeColors)
            AssistChip({ onTextAlignChange(ReaderTextAlign.CENTER) }, { Text("ମଧ୍ୟ ଭାଗରେ ରଖିବା", fontFamily = OdiaFont) }, currentTextAlign == ReaderTextAlign.CENTER, themeColors)
            AssistChip({ onTextAlignChange(ReaderTextAlign.RIGHT) }, { Text("ଡାହାଣ ପାର୍ଶ୍ୱକୁ ସଜାଇବା", fontFamily = OdiaFont) }, currentTextAlign == ReaderTextAlign.RIGHT, themeColors)
            AssistChip({ onTextAlignChange(ReaderTextAlign.JUSTIFY) }, { Text("ଉଭୟ ପାର୍ଶ୍ୱକୁ ସମାନ କରିବା", fontFamily = OdiaFont) }, currentTextAlign == ReaderTextAlign.JUSTIFY, themeColors)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("ରୂପରେଖ", fontFamily = OdiaFont, fontSize = 16.sp, color = themeColors.onBackground.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            AssistChip({ onThemeChange(ReaderTheme.LIGHT) }, { Text("ଆଲୋକିତ", fontFamily = OdiaFont) }, currentTheme == ReaderTheme.LIGHT, themeColors)
            AssistChip({ onThemeChange(ReaderTheme.DARK) }, { Text("ଅନ୍ଧାରିଆ", fontFamily = OdiaFont) }, currentTheme == ReaderTheme.DARK, themeColors)
            AssistChip({ onThemeChange(ReaderTheme.SEPIA) }, { Text("ଆଖି ସୁରକ୍ଷା", fontFamily = OdiaFont) }, currentTheme == ReaderTheme.SEPIA, themeColors)
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (currentTheme == ReaderTheme.SEPIA) {
            Text("ଆଖି ସୁରକ୍ଷା ର ତୀବ୍ରତା", fontFamily = OdiaFont, fontSize = 16.sp, color = themeColors.onBackground.copy(alpha = 0.8f))
            Slider(value = currentSepiaIntensity, onValueChange = onSepiaIntensityChange, valueRange = 0f..1f, colors = SliderDefaults.colors(thumbColor = themeColors.primary, activeTrackColor = themeColors.primary))
            Spacer(modifier = Modifier.height(24.dp))
        }
        OutlinedButton(onClick = onReset, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColors.primary), border = BorderStroke(1.dp, themeColors.primary.copy(alpha = 0.5f))) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("ପୂର୍ବ ସ୍ଥାପନ କରନ୍ତୁ", fontFamily = OdiaFont)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistChip(onClick: () -> Unit, label: @Composable () -> Unit, selected: Boolean, themeColors: ThemeColors) {
    SuggestionChip(
        onClick = onClick,
        label = label,
        colors = if (selected) SuggestionChipDefaults.suggestionChipColors(containerColor = themeColors.primaryContainer, labelColor = themeColors.onPrimaryContainer) else SuggestionChipDefaults.suggestionChipColors(containerColor = themeColors.background, labelColor = themeColors.onBackground),
        border = BorderStroke(width = 1.dp, color = if (selected) themeColors.primary else themeColors.onBackground.copy(alpha = 0.5f))
    )
}

// --- NEW: TTS Player Composable (Redesigned) ---
@Composable
fun TtsPlayerControls(
    isPlaying: Boolean,
    speed: Float,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onStop: () -> Unit,
    theme: ThemeColors,
    currentBlock: Int,
    totalBlocks: Int,
    onSeek: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Seek Bar
            if (totalBlocks > 1) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = currentBlock.toFloat(),
                        onValueChange = { onSeek(it.roundToInt()) },
                        valueRange = 0f..max(0f, (totalBlocks - 1).toFloat()),
                        steps = max(0, totalBlocks - 2),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = theme.primary,
                            activeTrackColor = theme.primary,
                            inactiveTrackColor = theme.primary.copy(alpha = 0.3f)
                        )
                    )
                    Text(
                        text = "ଅନୁଚ୍ଛେଦ ${toOdiaNumeral(currentBlock + 1)} / ${toOdiaNumeral(totalBlocks)}",
                        fontFamily = OdiaFont,
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Main Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Speed Control
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onSpeedChange((speed - 0.25f).coerceAtLeast(0.5f)) }) {
                        Icon(Icons.Default.Remove, contentDescription = "ଧୀର କରନ୍ତୁ", tint = theme.onBackground)
                    }
                    Text(
                        text = toOdiaDecimal(speed, withSuffix = true),
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        color = theme.onBackground,
                        modifier = Modifier.width(80.dp),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { onSpeedChange((speed + 0.25f).coerceAtMost(2.5f)) }) {
                        Icon(Icons.Default.Add, contentDescription = "ଶୀଘ୍ର କରନ୍ତୁ", tint = theme.onBackground)
                    }
                }

                // Play/Pause Button
                FilledIconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "ବିରତି" else "ପଢ଼ନ୍ତୁ",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Stop Button
                IconButton(onClick = onStop, modifier = Modifier.size(56.dp)) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = "ପଢ଼ିବା ବନ୍ଦ କରନ୍ତୁ",
                        tint = theme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}


// --- NEW: TTS Install Dialog Composable ---
@Composable
fun TtsInstallDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
    theme: ThemeColors
) {
    val instructions = """ଏହି ସୁବିଧା ପାଇଁ ଆପଣଙ୍କୁ 'Google Text-to-Speech' ସେବା ସ୍ଥାପନ କରିବାକୁ ପଡିବ ଏବଂ ତା'ପରେ ଏହାର ସେଟିଂସରେ ଓଡ଼ିଆ (ଭାରତ) ଭାଷା ଡାଉନଲୋଡ୍ କରିବାକୁ ହେବ ।

ପଦକ୍ଷେପ: (କେବଳ ପିତାମତା'ଙ୍କ ପାଇଁ)
୧. ପ୍ଲେ ଷ୍ଟୋରରୁ 'Speech Recognition & Synthesis' by Google ସ୍ଥାପନ କରନ୍ତୁ ।
୨. ଫୋନର ସେଟିଂସକୁ ଯାଆନ୍ତୁ → Accessibility → Text-to-speech output ।
୩. Preferred engine ଭାବରେ 'Speech Services by Google' ଚୟନ କରନ୍ତୁ ।
୪. Preferred engine ପାଖରେ ଥିବା ସେଟିଂସ ଆଇକନକୁ ଦବାନ୍ତୁ ।
୫. 'Install voice data' ରେ ଯାଇ 'Odia (India)' ଡାଉନଲୋଡ୍ କରନ୍ତୁ ।"""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "ଟିଟିଏସ୍ ସେବା ନାହିଁ",
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                color = theme.onBackground
            )
        },
        text = {
            Text(
                instructions,
                fontFamily = OdiaFont,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = theme.onBackground.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text("ସେଟିଂସ ଖୋଲନ୍ତୁ", fontFamily = OdiaFont, color = theme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ବୁଝିଲି", fontFamily = OdiaFont, color = theme.onBackground.copy(alpha = 0.7f))
            }
        },
        containerColor = theme.background
    )
}
// --- END OF FILE BookReaderScreen.kt ---