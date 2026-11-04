// --- START OF FILE HomeScreen.kt ---
package `in`.sandeep.odiabhasa

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.StiffnessMedium
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random

// --- CONFIGURATION ---
val OdiaFont = FontFamily(Font(R.font.odia))
val PinkGradientEnd = Color(0xFFEC407A)
const val BANNER_JSON_URL = "https://odiabhasa.pages.dev/banners/7.json"
const val QUIZ_JSON_URL = "https://sbgb.pages.dev/quiz/quiz.json"
const val MARQUEE_JSON_URL = "https://odiabhasa.pages.dev/marquee/2.json"
val allowedHosts = listOf("imsbg.github.io", "play.google.com")

// Session Cache: Resets to 0 only when the app is completely killed.
// Survives during in-app navigation.
object AppSessionCache {
    var lastSelectedTab: Int = 0
}

data class BannerData(
    val imageUrl: String,
    val caption: String,
    val paragraph: String,
    val link: String,
    val isLink: Boolean,
    val start: String?,
    val end: String?
)

data class MarqueeConfig(
    val version: Int,
    val show: Boolean,
    val quotes: Map<Int, String>
)

// --- QUIZ DATA MODELS ---
data class QuizMainConfig(
    val version: Int,
    val showQuestions: Boolean,
    val expirationTime: String?,
    val dailyLinks: Map<Int, String>
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val answerIndex: Int
)

// --- HELPERS ---
val odiaNumerals = charArrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
fun convertToOdiaNum(number: Int): String {
    return number.toString().map { odiaNumerals[it - '0'] }.joinToString("")
}

fun isUrlAllowed(url: String): Boolean {
    return try {
        val host = Uri.parse(url).host ?: return false
        allowedHosts.any { host.contains(it, ignoreCase = true) }
    } catch (e: Exception) { false }
}

fun isBannerCurrentlyValid(startStr: String?, endStr: String?): Boolean {
    if (startStr.isNullOrBlank() && endStr.isNullOrBlank()) return true
    try {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val currentTime = Date()
        val isAfterStartDate = if (!startStr.isNullOrBlank()) {
            val startDate = sdf.parse(startStr)
            currentTime.after(startDate) || currentTime == startDate
        } else true
        val isBeforeEndDate = if (!endStr.isNullOrBlank()) {
            val endDate = sdf.parse(endStr)
            currentTime.before(endDate) || currentTime == endDate
        } else true
        return isAfterStartDate && isBeforeEndDate
    } catch (e: Exception) { return false }
}

fun isQuizValid(expireStr: String?): Boolean {
    if (expireStr.isNullOrBlank()) return true
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val expireDate = sdf.parse(expireStr)
        Date().before(expireDate)
    } catch (e: Exception) { true }
}

fun parseBannerJson(jsonString: String): List<BannerData> {
    val list = mutableListOf<BannerData>()
    try {
        val rootObject = JSONObject(jsonString)
        val bannersArray = rootObject.getJSONArray("banners")
        for (i in 0 until bannersArray.length()) {
            val jsonObj = bannersArray.getJSONObject(i)
            list.add(BannerData(
                imageUrl = jsonObj.optString("image", ""),
                caption = jsonObj.optString("text", ""),
                paragraph = jsonObj.optString("paragraph", ""),
                link = jsonObj.optString("link", ""),
                isLink = jsonObj.optString("promo", "no").equals("yes", true),
                start = jsonObj.optString("start", "").takeIf { it.isNotBlank() },
                end = jsonObj.optString("end", "").takeIf { it.isNotBlank() }
            ))
        }
    } catch (e: Exception) { e.printStackTrace() }
    return list
}

fun parseMarqueeConfig(jsonString: String): MarqueeConfig {
    val obj = JSONObject(jsonString)
    val version = obj.optInt("version", 1)
    val show = obj.optString("show", "no").equals("yes", true)
    val quotesObj = obj.optJSONObject("quotes")
    val quotes = mutableMapOf<Int, String>()
    if (quotesObj != null) {
        quotesObj.keys().forEach { key ->
            quotes[key.toInt()] = quotesObj.getString(key)
        }
    }
    return MarqueeConfig(version, show, quotes)
}

fun parseQuizMainConfig(jsonString: String): QuizMainConfig {
    val obj = JSONObject(jsonString)
    val version = obj.optInt("version", 1)
    val showQuestions = obj.optString("showQuestions", "no").equals("yes", true)
    val expirationTime = obj.optString("expirationTime", "").takeIf { it.isNotBlank() }
    val linksObj = obj.optJSONObject("dailyLinks")
    val links = mutableMapOf<Int, String>()
    if (linksObj != null) {
        linksObj.keys().forEach { key ->
            links[key.toInt()] = linksObj.getString(key)
        }
    }
    return QuizMainConfig(version, showQuestions, expirationTime, links)
}

fun parseQuizQuestions(jsonString: String): List<QuizQuestion> {
    val list = mutableListOf<QuizQuestion>()
    val arr = JSONObject(jsonString).optJSONArray("questions") ?: return list
    for (i in 0 until arr.length()) {
        val qObj = arr.getJSONObject(i)
        val qText = qObj.optString("question", "")
        val ansIdx = qObj.optInt("answerIndex", 0)
        val optArr = qObj.optJSONArray("options")
        val opts = mutableListOf<String>()
        if (optArr != null) {
            for (j in 0 until optArr.length()) {
                opts.add(optArr.getString(j))
            }
        }
        list.add(QuizQuestion(qText, opts, ansIdx))
    }
    return list
}

// --- MAIN SCREEN ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavToSwara: () -> Unit,
    onNavToByanjana: () -> Unit,
    onNavToSankhya: () -> Unit,
    onNavToYukta: () -> Unit,
    onNavToBarna: () -> Unit,
    onNavToMatra: () -> Unit,
    onNavToPhala: () -> Unit,
    onNavToMenu: () -> Unit,
    onNavToHistoryOdisha: () -> Unit,
    onNavToHistoryLang: () -> Unit,
    onNavToLipyantara: () -> Unit,
    onNavToBahi: () -> Unit,
    onNavToPurunaBahi: () -> Unit,
    onNavToOdiaCalendar: () -> Unit,
    onNavToMadhuBarnabodha: () -> Unit,
    onNavToSchoolBooks: () -> Unit,
    onNavToWriting: () -> Unit,
    onNavToGanita: () -> Unit,
    onNavToHarmonium: () -> Unit,
    onNavToUtility: () -> Unit,
    onNavToAsaJaniba: () -> Unit,
    homeState: LazyListState,
) {
    var bannerList by remember { mutableStateOf(listOf<BannerData>()) }
    val scope = rememberCoroutineScope()

    val madhuBarnabodhaViewModel: MadhuBarnabodhaViewModel = viewModel()
    val downloadState by madhuBarnabodhaViewModel.downloadState.collectAsState()

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    // Quiz States
    var quizConfig by remember { mutableStateOf<QuizMainConfig?>(null) }
    var todayQuestions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var showQuizDialog by remember { mutableStateOf(false) }

    val prefs = context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
    var isQuizSolvedToday by remember { mutableStateOf(false) }
    var quizScore by remember { mutableIntStateOf(0) }

    // Marquee States
    val marqueePrefs = context.getSharedPreferences("marquee_prefs", Context.MODE_PRIVATE)
    var showMarquee by remember { mutableStateOf(marqueePrefs.getBoolean("marquee_show", false)) }
    var marqueeText by remember { mutableStateOf("") }

    // Uses the session cache to remember tab during in-app navigation
    val mainPagerState = rememberPagerState(
        initialPage = AppSessionCache.lastSelectedTab,
        pageCount = { 3 }
    )

    // Sticky header properties
    var headerHeightPx by remember { mutableFloatStateOf(0f) }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < 0f && headerHeightPx > 0f) { // Scrolling UP -> collapse header
                    val oldOffset = headerOffsetPx
                    headerOffsetPx = (oldOffset + delta).coerceIn(-headerHeightPx, 0f)
                    return Offset(0f, headerOffsetPx - oldOffset)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset, available: Offset, source: NestedScrollSource
            ): Offset {
                val delta = available.y
                if (delta > 0f && headerHeightPx > 0f) { // Scrolling DOWN -> expand header
                    val oldOffset = headerOffsetPx
                    headerOffsetPx = (oldOffset + delta).coerceIn(-headerHeightPx, 0f)
                    return Offset(0f, headerOffsetPx - oldOffset)
                }
                return Offset.Zero
            }
        }
    }

    // Save strictly to memory cache
    LaunchedEffect(mainPagerState.currentPage) {
        AppSessionCache.lastSelectedTab = mainPagerState.currentPage
    }

    // BACK HANDLER: If not on Home Tab (0), back button smoothly animates back to Home Tab
    BackHandler(enabled = mainPagerState.currentPage != 0) {
        scope.launch {
            mainPagerState.animateScrollToPage(0)
        }
    }

    // Initialize marquee from cache
    LaunchedEffect(Unit) {
        val todayDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        marqueeText = marqueePrefs.getString("marquee_text_$todayDayOfWeek", "") ?: ""
    }

    // Fetch banners, quiz, and marquee configuration
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            // Fetch Marquee
            try {
                val mStr = URL(MARQUEE_JSON_URL).readText()
                val mConfig = parseMarqueeConfig(mStr)
                val todayDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                val dayText = mConfig.quotes[todayDayOfWeek] ?: ""

                withContext(Dispatchers.Main) {
                    showMarquee = mConfig.show
                    marqueeText = dayText
                    val editor = marqueePrefs.edit()
                    editor.putBoolean("marquee_show", mConfig.show)
                        .putInt("marquee_version", mConfig.version)
                    mConfig.quotes.forEach { (day, text) ->
                        editor.putString("marquee_text_$day", text)
                    }
                    editor.apply()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Fetch Banners
            try {
                val jsonString = URL(BANNER_JSON_URL).readText()
                val parsedList = parseBannerJson(jsonString)
                val filteredList = parsedList.filter { isBannerCurrentlyValid(it.start, it.end) }
                if (filteredList.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        bannerList = filteredList
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Fetch Quiz Configuration
            try {
                val configStr = URL(QUIZ_JSON_URL).readText()
                val config = parseQuizMainConfig(configStr)
                if (config.showQuestions && isQuizValid(config.expirationTime)) {
                    val cal = Calendar.getInstance()
                    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                    val todayLink = config.dailyLinks[dayOfWeek]

                    if (!todayLink.isNullOrBlank()) {
                        val qStr = URL(todayLink).readText()
                        val questions = parseQuizQuestions(qStr)

                        withContext(Dispatchers.Main) {
                            quizConfig = config
                            todayQuestions = questions

                            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                            val savedDate = prefs.getString("quiz_last_solved_date", "")
                            val savedVersion = prefs.getInt("quiz_last_solved_version", -1)

                            if (savedDate == todayStr && savedVersion == config.version) {
                                isQuizSolvedToday = true
                                quizScore = prefs.getInt("quiz_last_solved_score", 0)
                            } else {
                                isQuizSolvedToday = false
                                quizScore = 0
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) { quizConfig = config }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (showQuizDialog && todayQuestions.isNotEmpty()) {
        DailyQuizDialog(
            questions = todayQuestions,
            isInitiallySolved = isQuizSolvedToday,
            initialScore = quizScore,
            onDismiss = { showQuizDialog = false },
            onQuizFinished = { finalScore ->
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                prefs.edit()
                    .putString("quiz_last_solved_date", todayStr)
                    .putInt("quiz_last_solved_version", quizConfig?.version ?: 1)
                    .putInt("quiz_last_solved_score", finalScore)
                    .apply()

                isQuizSolvedToday = true
                quizScore = finalScore
            }
        )
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(nestedScrollConnection)
        ) {
            // MAIN SCROLLABLE CONTENT WITH FIXED HEADER AND STICKY BANNER
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val extraHeight = headerHeightPx.roundToInt()
                        // Allow the column to grow to comfortably fit the sticky translation
                        val placeable = measurable.measure(
                            constraints.copy(maxHeight = constraints.maxHeight + extraHeight)
                        )
                        layout(placeable.width, placeable.height - extraHeight) {
                            val yOffset = headerOffsetPx.coerceIn(-headerHeightPx, 0f).roundToInt()
                            placeable.placeRelative(0, yOffset)
                        }
                    }
            ) {
                // --- FIXED TOP HEADER (Hides on Scroll Up) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            headerHeightPx = coordinates.size.height.toFloat()
                        }
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = if (isTablet) 32.dp else 0.dp)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Header Text - Simple Black color
                            Text(
                                text = "ଓଡ଼ିଆ ଭାଷା",
                                fontSize = 32.sp,
                                fontFamily = OdiaFont,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            // QUIZ BULB ICON
                            if (quizConfig?.showQuestions == true && isQuizValid(quizConfig?.expirationTime) && todayQuestions.isNotEmpty()) {
                                QuizBulbIcon(
                                    isSolved = isQuizSolvedToday,
                                    onClick = { showQuizDialog = true }
                                )
                            }
                        }

                        // --- MARQUEE TEXT BLOCK ---
                        if (showMarquee && marqueeText.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = marqueeText,
                                    fontSize = 14.sp,
                                    fontFamily = OdiaFont,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    modifier = Modifier.basicMarquee()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // --- FIXED BANNER CAROUSEL (Sticks to top on scroll) ---
                Box(
                    modifier = Modifier
                        .padding(horizontal = if (isTablet) 32.dp else 0.dp)
                        .padding(bottom = 12.dp)
                ) {
                    // Smart Carousel initialization: Replaces raw offline Box with standard Carousel format
                    val carouselData = if (bannerList.isNotEmpty()) bannerList else listOf(
                        BannerData(imageUrl = "", caption = "", paragraph = "", link = "", isLink = false, start = null, end = null)
                    )
                    BannerCarousel(dataList = carouselData, isTablet = isTablet)
                }

                // --- DYNAMIC SWIPEABLE CONTENT WITH SMOOTH SCALING ANIMATION ---
                HorizontalPager(
                    state = mainPagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    // Calculate deeper smooth scale and fade transition
                    val pageOffset = ((mainPagerState.currentPage - page) + mainPagerState.currentPageOffsetFraction).absoluteValue
                    val scale = lerp(start = 0.88f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                    val alpha = lerp(start = 0.1f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                    ) {
                        when (page) {
                            0 -> HomeTabContent(
                                homeState = homeState,
                                isTablet = isTablet,
                                onNavToSwara = onNavToSwara,
                                onNavToByanjana = onNavToByanjana,
                                onNavToSankhya = onNavToSankhya,
                                onNavToYukta = onNavToYukta,
                                onNavToBarna = onNavToBarna,
                                onNavToMatra = onNavToMatra,
                                onNavToPhala = onNavToPhala,
                                onNavToGanita = onNavToGanita,
                                onNavToWriting = onNavToWriting,
                                onNavToAsaJaniba = onNavToAsaJaniba
                            )
                            1 -> BooksTabContent(
                                isTablet = isTablet,
                                downloadState = downloadState,
                                onDownloadMadhu = { madhuBarnabodhaViewModel.downloadBook() },
                                onReadMadhu = onNavToMadhuBarnabodha,
                                onNavToSchoolBooks = onNavToSchoolBooks,
                                onNavToBahi = onNavToBahi,
                                onNavToPurunaBahi = onNavToPurunaBahi
                            )
                            2 -> ExploreTabContent(
                                isTablet = isTablet,
                                onNavToOdiaCalendar = onNavToOdiaCalendar,
                                onNavToHistoryOdisha = onNavToHistoryOdisha,
                                onNavToHistoryLang = onNavToHistoryLang,
                                onNavToLipyantara = onNavToLipyantara,
                                onNavToHarmonium = onNavToHarmonium,
                                onNavToUtility = onNavToUtility
                            )
                        }
                    }
                }
            }

            // --- NEW BOTTOM NAVIGATION LAYOUT ---
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Tabs Pill Container
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 28.dp,
                            shape = RoundedCornerShape(36.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            ambientColor = Color.Black.copy(alpha = 0.1f)
                        )
                        .clip(RoundedCornerShape(36.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IOSFloatingNavItem(
                        selected = mainPagerState.currentPage == 0,
                        icon = Icons.Default.Home,
                        label = "ଶିକ୍ଷା",
                        onClick = { scope.launch { mainPagerState.animateScrollToPage(0) } }
                    )
                    IOSFloatingNavItem(
                        selected = mainPagerState.currentPage == 1,
                        icon = Icons.Default.MenuBook,
                        label = "ବହି",
                        onClick = { scope.launch { mainPagerState.animateScrollToPage(1) } }
                    )
                    IOSFloatingNavItem(
                        selected = mainPagerState.currentPage == 2,
                        icon = Icons.Default.Explore,
                        label = "ଅନ୍ୟାନ୍ୟ",
                        onClick = { scope.launch { mainPagerState.animateScrollToPage(2) } }
                    )
                }

                // Black Circular Menu Button
                Box(
                    modifier = Modifier
                        .size(76.dp) // Large circular matching proportion from the image
                        .shadow(
                            elevation = 28.dp,
                            shape = CircleShape,
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            ambientColor = Color.Black.copy(alpha = 0.1f)
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onNavToMenu() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "ଅଧିକ",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// QUIZ COMPONENTS
// ==========================================

@Composable
fun QuizBulbIcon(isSolved: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "bulb_anim")

    val scale by if (!isSolved) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bulb_scale"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    val glowColor by if (!isSolved) {
        infiniteTransition.animateColor(
            initialValue = Color(0xFFFFC107),
            targetValue = Color(0xFFFF5722),
            animationSpec = infiniteRepeatable(
                animation = tween(1400),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bulb_glow"
        )
    } else {
        remember { mutableStateOf(Color.Gray) }
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(if (!isSolved) glowColor.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = "Daily Quiz",
            tint = if (!isSolved) glowColor else Color.Gray,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun DailyQuizDialog(
    questions: List<QuizQuestion>,
    isInitiallySolved: Boolean,
    initialScore: Int,
    onDismiss: () -> Unit,
    onQuizFinished: (Int) -> Unit
) {
    var dialogState by remember { mutableStateOf(if (isInitiallySolved) "RESULT" else "QUIZ") }
    var currentQIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(if (isInitiallySolved) initialScore else 0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ଦୈନିକ କୁଇଜ୍",
                    fontSize = 24.sp,
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (dialogState == "QUIZ") {
                    val q = questions[currentQIndex]

                    Text(
                        text = "ପ୍ରଶ୍ନ ${convertToOdiaNum(currentQIndex + 1)}/${convertToOdiaNum(questions.size)}",
                        fontSize = 16.sp,
                        fontFamily = OdiaFont,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = q.question,
                        fontSize = 20.sp,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    q.options.forEachIndexed { index, optionText ->
                        val isCorrect = index == q.answerIndex
                        val isSelected = index == selectedAnswer

                        val bgColor = when {
                            selectedAnswer == null -> MaterialTheme.colorScheme.surface
                            isCorrect -> Color(0xFF81C784) // Green
                            isSelected && !isCorrect -> Color(0xFFE57373) // Red
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val contentColor = when {
                            selectedAnswer == null -> MaterialTheme.colorScheme.onSurface
                            isCorrect || (isSelected && !isCorrect) -> Color.White
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.dp,
                                    if (selectedAnswer == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f) else Color.Transparent,
                                    RoundedCornerShape(16.dp)
                                )
                                .background(bgColor)
                                .clickable(enabled = selectedAnswer == null) {
                                    selectedAnswer = index
                                    if (index == q.answerIndex) score++

                                    scope.launch {
                                        delay(1200) // Delay to show result colors
                                        if (currentQIndex < questions.size - 1) {
                                            currentQIndex++
                                            selectedAnswer = null
                                        } else {
                                            dialogState = "RESULT"
                                            onQuizFinished(score)
                                        }
                                    }
                                }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optionText,
                                fontSize = 18.sp,
                                fontFamily = OdiaFont,
                                color = contentColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    // RESULT STATE
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ଆପଣଙ୍କ ସ୍କୋର",
                        fontSize = 20.sp,
                        fontFamily = OdiaFont,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${convertToOdiaNum(score)}/${convertToOdiaNum(questions.size)}",
                        fontSize = 38.sp,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            dialogState = "QUIZ"
                            currentQIndex = 0
                            score = 0
                            selectedAnswer = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("ପୁଣି ଖେଳନ୍ତୁ", fontFamily = OdiaFont, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = onDismiss) {
                        Text("ବନ୍ଦ କରନ୍ତୁ", fontFamily = OdiaFont, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// iOS STYLE NEW FLOATING TAB ITEM
// ==========================================
@Composable
fun RowScope.IOSFloatingNavItem(selected: Boolean, icon: ImageVector, label: String, onClick: () -> Unit) {
    // Smooth background pill crossfade inside the parent container
    val bgColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = tween(300),
        label = "BgAnim"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        animationSpec = tween(300),
        label = "ContentColorAnim"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .height(60.dp) // Thick comfortable touch area
            .clip(RoundedCornerShape(30.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Removes Android ripple for that native iOS feel
                onClick = onClick
            )
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = OdiaFont,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
            letterSpacing = 0.2.sp
        )
    }
}

// ==========================================
// TAB CONTENTS
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeTabContent(
    homeState: LazyListState,
    isTablet: Boolean,
    onNavToSwara: () -> Unit,
    onNavToByanjana: () -> Unit,
    onNavToSankhya: () -> Unit,
    onNavToYukta: () -> Unit,
    onNavToBarna: () -> Unit,
    onNavToMatra: () -> Unit,
    onNavToPhala: () -> Unit,
    onNavToGanita: () -> Unit,
    onNavToWriting: () -> Unit,
    onNavToAsaJaniba: () -> Unit
) {
    val paddingMod = Modifier.padding(horizontal = if (isTablet) 52.dp else 0.dp)

    LazyColumn(
        state = homeState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 150.dp) // Adjusted padding for pill
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = if (isTablet) 52.dp else 24.dp)) {
                Text("ବିଭାଗ", fontSize = 24.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    maxItemsInEachRow = if (isTablet) 6 else 3
                ) {
                    val cardMod = Modifier
                        .weight(1f)
                        .height(if (isTablet) 150.dp else 125.dp)
                    CategoryCard("ସ୍ୱରବର୍ଣ୍ଣ", "ଅ", Color(0xFFFFEAEA), cardMod) { onNavToSwara() }
                    CategoryCard("ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ", "କ", Color(0xFFE8F4FD), cardMod) { onNavToByanjana() }
                    CategoryCard("ବର୍ଣ୍ଣ ପରିଚୟ", "ବର୍ଣ୍ଣ", Color(0xFFE8FCEF), cardMod) { onNavToBarna() }
                    CategoryCard("ମାତ୍ରା", "ା", Color(0xFFFFF3CD), cardMod) { onNavToMatra() }
                    CategoryCard("ଫଳା", "୍ୟ", Color(0xFFF1E3F3), cardMod) { onNavToPhala() }
                    CategoryCard("ଯୁକ୍ତାକ୍ଷର", "ଙ୍କ", Color(0xFFE5E8FA), cardMod) { onNavToYukta() }
                    SankhyaSpecialCard(Modifier
                        .weight(1f)
                        .height(if (isTablet) 110.dp else 90.dp)) { onNavToSankhya() }
                }
            }
        }
        item { Box(Modifier.padding(horizontal = if (isTablet) 52.dp else 24.dp)) { GanitaCard(onClick = onNavToGanita) } }
        item { Box(paddingMod) { WritingEntryCard(onClick = onNavToWriting) } }
        item { Box(paddingMod) { AsaJanibaCard(onClick = onNavToAsaJaniba) } }
    }
}

@Composable
fun BooksTabContent(
    isTablet: Boolean,
    downloadState: BookDownloadState,
    onDownloadMadhu: () -> Unit,
    onReadMadhu: () -> Unit,
    onNavToSchoolBooks: () -> Unit,
    onNavToBahi: () -> Unit,
    onNavToPurunaBahi: () -> Unit
) {
    val booksState = rememberLazyListState()
    val paddingMod = Modifier.padding(horizontal = if (isTablet) 52.dp else 0.dp)

    LazyColumn(
        state = booksState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 150.dp) // Adjusted padding for pill
    ) {
        item {
            Text(
                text = "ପୁସ୍ତକ ଓ ପାଠାଗାର",
                fontSize = 24.sp,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = if (isTablet) 52.dp else 24.dp)
            )
        }
        item { Box(paddingMod) { MadhuBarnabodhaCard(downloadState, onDownloadMadhu, onReadMadhu) } }
        item { Box(paddingMod) { SchoolBooksCard(onClick = onNavToSchoolBooks) } }
        item { Box(paddingMod) { ReadBooksCard(onClick = onNavToBahi) } }
        item { Box(paddingMod) { PurunaBahiCard(onClick = onNavToPurunaBahi) } }
    }
}

@Composable
fun ExploreTabContent(
    isTablet: Boolean,
    onNavToOdiaCalendar: () -> Unit,
    onNavToHistoryOdisha: () -> Unit,
    onNavToHistoryLang: () -> Unit,
    onNavToLipyantara: () -> Unit,
    onNavToHarmonium: () -> Unit,
    onNavToUtility: () -> Unit
) {
    val exploreState = rememberLazyListState()
    val paddingMod = Modifier.padding(horizontal = if (isTablet) 52.dp else 0.dp)

    LazyColumn(
        state = exploreState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 150.dp) // Adjusted padding for pill
    ) {
        // --- NEW SECTION FOR UTILITY ---
        item {
            Text(
                text = "ଦରକାରୀ ଯନ୍ତ୍ରପାତି",
                fontSize = 24.sp,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = if (isTablet) 52.dp else 24.dp)
                    .padding(top = 8.dp)
            )
        }
        item { Box(paddingMod) { UtilityShortcutCard(onClick = onNavToUtility) } }

        item {
            Text(
                text = "ଆଉ କିଛି ଉପଯୋଗୀ",
                fontSize = 24.sp,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = if (isTablet) 52.dp else 24.dp)
            )
        }
        item { Box(paddingMod) { OdiaCalendarCard(onClick = onNavToOdiaCalendar) } }
        item { Box(paddingMod) { HarmoniumCard(onClick = onNavToHarmonium) } }


        item {
            Column(modifier = Modifier.padding(horizontal = if (isTablet) 52.dp else 24.dp)) {
                Spacer(Modifier.height(10.dp))
                Text("ଆମ ଇତିହାସ ଓ ଭାଷା", fontSize = 22.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                if (isTablet) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val cardMod = Modifier.weight(1f)
                        BigCard(modifier = cardMod, title = "ଓଡ଼ିଶାର ଇତିହାସ", imageRes = R.drawable.history_odisha, color = Color(0xFFFFCC80), onClick = onNavToHistoryOdisha)
                        BigCard(modifier = cardMod, title = "ଓଡ଼ିଆ ଭାଷାର ଇତିହାସ", imageRes = R.drawable.history_lang, color = Color(0xFFA5D6A7), onClick = onNavToHistoryLang)
                        BigCard(modifier = cardMod, title = "ଲିପ୍ୟନ୍ତର", imageRes = R.drawable.lipyantara, color = Color(0xFF90CAF9), onClick = onNavToLipyantara)
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        item { BigCard(title = "ଓଡ଼ିଶାର ଇତିହାସ", imageRes = R.drawable.history_odisha, color = Color(0xFFFFCC80), onClick = onNavToHistoryOdisha) }
                        item { BigCard(title = "ଓଡ଼ିଆ ଭାଷାର ଇତିହାସ", imageRes = R.drawable.history_lang, color = Color(0xFFA5D6A7), onClick = onNavToHistoryLang) }
                        item { BigCard(title = "ଲିପ୍ୟନ୍ତର", imageRes = R.drawable.lipyantara, color = Color(0xFF90CAF9), onClick = onNavToLipyantara) }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ଆମ ଭାଷା,\nଆମ ପରିଚୟ",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Black,
                        color = Color.Black.copy(alpha = 0.2f),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

// ==========================================
// UI COMPONENTS
// ==========================================

@Composable
fun UtilityShortcutCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .bouncingClickable(onClick = onClick)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Build, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("ଉପଯୋଗିତା", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
            Text("ଏକକ ପରିବର୍ତ୍ତନ ଓ ଅନ୍ୟାନ୍ୟ", fontSize = 14.sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun HarmoniumCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .bouncingClickable(onClick = onClick)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.MusicNote, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("ହାରମୋନିୟମ", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
            Text("ଚାଲ, ହାରମୋନିୟମ ବଜାଇବା!", fontSize = 14.sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun PurunaBahiCard(onClick: () -> Unit) {
    val brush = remember {
        Brush.linearGradient(
            listOf(Color(0xFFFFD700), Color(0xFFD4AF37), Color(0xFFB8860B)),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFFD4AF37).copy(alpha = 0.5f))
            .clip(RoundedCornerShape(28.dp))
            .background(brush)
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .bouncingClickable(onClick = onClick)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.LibraryBooks, null, tint = Color.White, modifier = Modifier.size(44.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("ପୁରୁଣା ବହି", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.White)
            Text("ପୁରୁଣା ଶିକ୍ଷାବର୍ଷର ପୁସ୍ତକ", fontSize = 14.sp, fontFamily = OdiaFont, color = Color.White.copy(0.9f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White)
    }
}

@Composable
fun SchoolBooksCard(onClick: () -> Unit) {
    val brush = remember {
        Brush.linearGradient(
            listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)),
            start = Offset(0f, Float.POSITIVE_INFINITY),
            end = Offset(Float.POSITIVE_INFINITY, 0f)
        )
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF00F2FE).copy(alpha = 0.5f))
            .clip(RoundedCornerShape(28.dp))
            .background(brush)
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .bouncingClickable(onClick = onClick)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(44.dp)); Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("ବିଦ୍ୟାଳୟ ବହି", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.White)
            Text("ପ୍ରଥମରୁ ଦଶମ ଶ୍ରେଣୀ", fontSize = 14.sp, fontFamily = OdiaFont, color = Color.White.copy(0.9f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White)
    }
}

@Composable
fun GanitaCard(onClick: () -> Unit) {
    val backgroundColor = remember { Color(0xFFFFD600) }
    val foregroundColor = remember { Color(0xFF455A64) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .graphicsLayer { rotationZ = -1.5f }
            .shadow(14.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFFFFD600).copy(alpha = 0.7f))
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .border(BorderStroke(1.5.dp, foregroundColor.copy(alpha = 0.1f)), RoundedCornerShape(28.dp))
            .bouncingClickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "କୁନି ଗଣିତ", fontSize = 28.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Black, color = foregroundColor)
        Text(text = "୨ + ୩ = ?", color = foregroundColor, fontSize = 26.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Black)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel(dataList: List<BannerData>, isTablet: Boolean) {
    val pagerState = rememberPagerState(pageCount = { dataList.size })
    val context = LocalContext.current
    var showMathDialog by remember { mutableStateOf(false) }
    var selectedLink by remember { mutableStateOf("") }
    var mathQuestion by remember { mutableStateOf("") }
    var mathAnswer by remember { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    // Collect drag state to prevent auto-scroll from fighting user swipes
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    fun generateQuestion() {
        val n1 = Random.nextInt(1, 21); val n2 = Random.nextInt(1, 21); val isAdd = Random.nextBoolean()
        val (num1, num2) = if (n1 >= n2) n1 to n2 else n2 to n1
        mathAnswer = if (isAdd) num1 + num2 else num1 - num2
        mathQuestion = "${convertToOdiaNum(num1)} ${if (isAdd) "+" else "-"} ${convertToOdiaNum(num2)} = ?"
        userAnswer = ""; errorMsg = ""
    }

    // Bulletproof Auto-Scroll mechanism
    LaunchedEffect(isDragged, dataList.size) {
        if (isDragged || dataList.size <= 1) return@LaunchedEffect

        while (true) {
            val currentItem = dataList[pagerState.currentPage]
            val delayTime = if (currentItem.isLink) 10000L else 6000L

            delay(delayTime)

            if (!pagerState.isScrollInProgress) {
                val next = (pagerState.currentPage + 1) % dataList.size
                try {
                    pagerState.animateScrollToPage(
                        page = next,
                        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                    )
                } catch (e: CancellationException) {
                    // Ignore
                }
            }
        }
    }

    if (showMathDialog) {
        AlertDialog(onDismissRequest = { showMathDialog = false },
            title = { Text("ଅଭିଭାବକଙ୍କ ପାଇଁ ପ୍ରଶ୍ନ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("ଏହି ଲିଙ୍କ୍‌ଟି ଆପଣଙ୍କୁ ଆମ ଆପ୍‌ ବାହାରେ ଏକ ଅନ୍ୟ ୱେବସାଇଟ୍‌କୁ ନେଇଯିବ ।", fontFamily = OdiaFont, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp)); Text(mathQuestion, fontSize = 28.sp, fontWeight = FontWeight.Black, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp)); OutlinedTextField(value = userAnswer, onValueChange = { userAnswer = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                    if (errorMsg.isNotEmpty()) Text(errorMsg, color = Color.Red, fontSize = 12.sp, fontFamily = OdiaFont)
                }
            },
            confirmButton = { TextButton(onClick = { if (userAnswer.toIntOrNull() == mathAnswer) { showMathDialog = false; context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(selectedLink))) } else errorMsg = "ଭୁଲ୍ ଉତ୍ତର" }) { Text("ଠିକ୍ ଅଛି", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { showMathDialog = false }) { Text("ବନ୍ଦ କରନ୍ତୁ", fontFamily = OdiaFont) } }
        )
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = if (isTablet) 60.dp else 24.dp),
            pageSpacing = 16.dp,
            modifier = Modifier.height(if (isTablet) 300.dp else 200.dp)
        ) { page ->
            val item = dataList[page]

            // Calculate offsets for Parallax staggered transition
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
            val absOffset = pageOffset.absoluteValue

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(Modifier.fillMaxSize()) {

                    // --- IMAGE LAYER (Smooth fade/scale) ---
                    val imageAlpha = lerp(start = 0.5f, stop = 1f, fraction = 1f - absOffset.coerceIn(0f, 1f))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = imageAlpha }
                    ) {
                        if (item.imageUrl.isEmpty()) {
                            Image(
                                painter = painterResource(R.drawable.home_banner),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = painterResource(R.drawable.home_banner),
                                error = painterResource(R.drawable.home_banner)
                            )
                        }
                    }

                    // --- TEXT & PROMO BUTTON LAYER (Parallax Fast Sliding Effect) ---
                    val textTranslationX = pageOffset * 200f
                    val textAlpha = lerp(start = 0f, stop = 1f, fraction = 1f - (absOffset * 1.5f).coerceIn(0f, 1f))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = textTranslationX
                                alpha = textAlpha
                            }
                    ) {
                        // Promo Button
                        if (item.isLink && item.link.isNotEmpty()) {
                            Box(Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(0.7f))
                                .clickable {
                                    if (isUrlAllowed(item.link)) {
                                        if (item.link.startsWith("https://play.google.com/")) {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.link)))
                                        } else {
                                            selectedLink = item.link; generateQuestion(); showMathDialog = true
                                        }
                                    }
                                }
                                .padding(14.dp, 8.dp)) { Text("ଅଧିକ ଜାଣନ୍ତୁ", color = Color.White, fontSize = 12.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold) }
                        }

                        // Gradient Block & Text
                        if (item.caption.isNotEmpty() || item.paragraph.isNotEmpty()) {
                            Box(Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.9f))))
                                .padding(16.dp)) {
                                Column {
                                    if (item.caption.isNotEmpty()) {
                                        Text(item.caption, color = Color.White, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    if (item.paragraph.isNotEmpty()) {
                                        Text(item.paragraph, color = Color.White.copy(0.85f), fontFamily = OdiaFont, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(dataList.size) { i ->
                val isSelected = pagerState.currentPage == i
                val width by animateDpAsState(
                    targetValue = if (isSelected) 28.dp else 8.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "width"
                )
                val color by animateColorAsState(
                    targetValue = if (isSelected) PinkGradientEnd else Color.LightGray.copy(0.6f),
                    animationSpec = tween(300),
                    label = "color"
                )

                Box(
                    Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun SankhyaSpecialCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val bg = remember { Color(0xFFE0F7FA) }
    val gradientBrush = remember { Brush.horizontalGradient(listOf(Color.Transparent, bg)) }

    val numberChunks = remember {
        (0..1000).chunked(50).map { chunk ->
            chunk.joinToString("      ") { convertToOdiaNum(it) }
        }
    }

    Box(
        modifier
            .shadow(10.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF006064).copy(alpha = 0.2f))
            .clip(RoundedCornerShape(28.dp))
            .background(bg)
            .bouncingClickable { onClick() }
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Text("ସଂଖ୍ୟା :", fontSize = 30.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Black, color = Color(0xFF006064), modifier = Modifier.padding(start = 24.dp, end = 5.dp))
            Box(Modifier
                .weight(1f)
                .fillMaxHeight()) {
                Row(Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(5.dp))
                    numberChunks.forEach { chunkText ->
                        Text(
                            text = chunkText + "      ",
                            fontSize = 30.sp,
                            fontFamily = OdiaFont,
                            color = Color(0xFF006064).copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                    Spacer(Modifier.width(40.dp))
                }
                Box(Modifier
                    .align(Alignment.CenterEnd)
                    .width(120.dp)
                    .fillMaxHeight()
                    .background(gradientBrush))
            }
        }
    }
}

@Composable
fun WritingEntryCard(onClick: () -> Unit) {
    val brush = remember {
        Brush.linearGradient(
            listOf(Color(0xFFFF8C00), Color(0xFFFF3D00)),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFFFF3D00).copy(alpha = 0.5f))
            .clip(RoundedCornerShape(28.dp))
            .background(brush)
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .bouncingClickable(onClick = onClick)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(44.dp)); Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("ଅକ୍ଷର ଲେଖି ଶିଖନ୍ତୁ", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.White)
            Text("ଅକ୍ଷର ଉପରେ ଲେଖନ୍ତୁ", fontSize = 14.sp, fontFamily = OdiaFont, color = Color.White.copy(0.9f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White)
    }
}

@Composable
fun MadhuBarnabodhaCard(downloadState: BookDownloadState, onDownload: () -> Unit, onRead: () -> Unit) {
    val brush = remember {
        Brush.linearGradient(
            listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }
    val isDownloading = downloadState is BookDownloadState.Downloading
    val isDownloaded = downloadState is BookDownloadState.Downloaded

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF764BA2).copy(alpha = 0.5f))
            .clip(RoundedCornerShape(28.dp))
            .background(brush)
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .bouncingClickable(enabled = !isDownloading, onClick = if (isDownloaded) onRead else onDownload)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Book, null, tint = Color.White, modifier = Modifier.size(44.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("ଛବିଳ ମଧୁ ବର୍ଣ୍ଣବୋଧ", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.White)
            val msg = when (downloadState) {
                is BookDownloadState.NotDownloaded -> "ପଢିବା ପାଇଁ ଡାଉନଲୋଡ୍ କରନ୍ତୁ"
                is BookDownloadState.Downloading -> {
                    val progress = (downloadState as? BookDownloadState.Downloading)?.progress ?: 0
                    "${convertToOdiaNum(progress)}% | ଡାଉନଲୋଡ୍ ହେଉଛି..."
                }
                is BookDownloadState.Downloaded -> "ପଢିବା ପାଇଁ କ୍ଲିକ୍ କରନ୍ତୁ"
                is BookDownloadState.Error -> "କିଛି ତ୍ରୁଟି ଘଟିଛି"
            }
            Text(msg, fontSize = 14.sp, fontFamily = OdiaFont, color = Color.White.copy(0.9f))
        }

        if (isDownloaded) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White)
        } else if (isDownloading) {
            CircularProgressIndicator(Modifier.size(26.dp), color = Color.White, strokeWidth = 3.dp)
        } else {
            Icon(Icons.Default.Download, null, tint = Color.White)
        }
    }
}

@Composable
fun ReadBooksCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .bouncingClickable(onClick = onClick)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.MenuBook, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp)); Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("ପାଠାଗାର", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold);
            Text("କାହାଣୀ ଓ କବିତା ପଢନ୍ତୁ", fontSize = 14.sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun OdiaCalendarCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .bouncingClickable(onClick = onClick)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp)); Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("କ୍ୟାଲେଣ୍ଡର", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold);
            Text("ପର୍ବପର୍ବାଣି ଓ ଛୁଟିଦିନ ଦେଖନ୍ତୁ", fontSize = 14.sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun CategoryCard(title: String, symbol: String, bgColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .bouncingClickable { onClick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp)) // Deeper Squircles for modern iOS feel
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(symbol, fontSize = 28.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        }
        Spacer(Modifier.height(14.dp))
        Text(title, fontSize = 14.sp, fontFamily = OdiaFont, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
fun BigCard(modifier: Modifier = Modifier, title: String, imageRes: Int, color: Color, onClick: () -> Unit) {
    Column(
        modifier = (if (modifier == Modifier) Modifier
            .width(230.dp)
            .height(270.dp) else modifier)
            .shadow(12.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.12f))
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .bouncingClickable { onClick() }
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(color)) {
            Image(painter = painterResource(imageRes), title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = OdiaFont);
            Text("ପଢନ୍ତୁ...", fontSize = 13.sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        }
    }
}

fun Modifier.bouncingClickable(enabled: Boolean = true, onClick: () -> Unit) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleState = animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "bouncingClickableScale"
    )

    // Added tactile alpha fade during press
    val alphaState = animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = tween(150),
        label = "bouncingClickableAlpha"
    )

    this.graphicsLayer {
        val scale = scaleState.value
        scaleX = scale
        scaleY = scale
        alpha = alphaState.value
    }.clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = onClick
    )
}

@Composable
fun AsaJanibaCard(onClick: () -> Unit) {
    val brush = remember {
        Brush.linearGradient(
            listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF2E7D32).copy(alpha = 0.5f))
            .clip(RoundedCornerShape(28.dp))
            .background(brush)
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .bouncingClickable(onClick = onClick)
            .padding(20.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Pets, null, tint = Color.White, modifier = Modifier.size(44.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text("ଆସ ଜାଣିବା", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.White)
            Text("ପଶୁପକ୍ଷୀ, ଫଳ ଓ ପନିପରିବା", fontSize = 14.sp, fontFamily = OdiaFont, color = Color.White.copy(0.9f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White)
    }
}
// --- END OF FILE HomeScreen.kt ---
