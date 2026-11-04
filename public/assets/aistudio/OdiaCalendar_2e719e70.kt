package `in`.sandeep.odiabhasa

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// --- Data & Helper Functions (Unchanged) ---

private val DefaultFestivalColor = Color(0xFFB71C1C)
private val SelectedDateColor = Color(0xFF6750A4)
private val SundayColor = Color(0xFFD32F2F)

data class FestivalDetails(
    val name: String,
    val color: Color
)

data class DailyInfo(
    val festival: FestivalDetails?,
    val customText: String?
)

private val odiaDaysOfWeek = listOf("ରବି", "ସୋମ", "ମଙ୍ଗଳ", "ବୁଧ", "ଗୁରୁ", "ଶୁକ୍ର", "ଶନି")

private fun getTimePeriod(time: LocalTime): String {
    return when (time.hour) {
        in 4..5 -> "ପାହାନ୍ତା"; in 6..11 -> "ସକାଳ"; in 12..16 -> "ଅପରାହ୍ନ"
        in 17..20 -> "ସନ୍ଧ୍ୟା"; else -> "ରାତି"
    }
}

private fun getOdiaSeasonsForHeader(month: Month): String {
    return when (month) {
        Month.APRIL, Month.MAY -> "ଗ୍ରୀଷ୍ମ"; Month.JUNE -> "ଗ୍ରୀଷ୍ମ - ବର୍ଷା"; Month.JULY -> "ବର୍ଷା"
        Month.AUGUST -> "ବର୍ଷା - ଶରତ"; Month.SEPTEMBER -> "ଶରତ"; Month.OCTOBER -> "ଶରତ - ହେମନ୍ତ"
        Month.NOVEMBER -> "ହେମନ୍ତ"; Month.DECEMBER -> "ହେମନ୍ତ - ଶୀତ"; Month.JANUARY -> "ଶୀତ"
        Month.FEBRUARY -> "ଶୀତ - ବସନ୍ତ"; Month.MARCH -> "ବସନ୍ତ"
    }
}

private fun getOdiaMonthsForHeader(month: Month): String {
    return when (month) {
        Month.APRIL -> "ଚୈତ୍ର - ବୈଶାଖ"; Month.MAY -> "ବୈଶାଖ - ଜ୍ୟେଷ୍ଠ"; Month.JUNE -> "ଜ୍ୟେଷ୍ଠ - ଆଷାଢ଼"
        Month.JULY -> "ଆଷାଢ଼ - ଶ୍ରାବଣ"; Month.AUGUST -> "ଶ୍ରାବଣ - ଭାଦ୍ରବ"; Month.SEPTEMBER -> "ଭାଦ୍ରବ - ଆଶ୍ୱିନ"
        Month.OCTOBER -> "ଆଶ୍ୱିନ - କାର୍ତ୍ତିକ"; Month.NOVEMBER -> "କାର୍ତ୍ତିକ - ମାର୍ଗଶିର"; Month.DECEMBER -> "ମାର୍ଗଶିର - ପୌଷ"
        Month.JANUARY -> "ପୌଷ - ମାଘ"; Month.FEBRUARY -> "ମାଘ - ଫାଲ୍‌ଗୁନ"; Month.MARCH -> "ଫାଲ୍‌ଗୁନ - ଚୈତ୍ର"
    }
}

private fun getOdiaMonthForDate(date: LocalDate): String {
    return when (date.monthValue) {
        1 -> "ପୌଷ-ମାଘ"; 2 -> "ମାଘ-ଫାଲ୍‌ଗୁନ"; 3 -> "ଫାଲ୍‌ଗୁନ-ଚୈତ୍ର"; 4 -> "ଚୈତ୍ର-ବୈଶାଖ"
        5 -> "ବୈଶାଖ-ଜ୍ୟେଷ୍ଠ"; 6 -> "ଜ୍ୟେଷ୍ଠ-ଆଷାଢ଼"; 7 -> "ଆଷାଢ଼-ଶ୍ରାବଣ"; 8 -> "ଶ୍ରାବଣ-ଭାଦ୍ରବ"
        9 -> "ଭାଦ୍ରବ-ଆଶ୍ୱିନ"; 10 -> "ଆଶ୍ୱିନ-କାର୍ତ୍ତିକ"; 11 -> "କାର୍ତ୍ତିକ-ମାର୍ଗଶିର"; 12 -> "ମାର୍ଗଶିର-ପୌଷ"
        else -> ""
    }
}

private fun getOdiaGregorianMonthName(month: Month): String {
    return when (month) {
        Month.JANUARY -> "ଜାନୁଆରୀ"; Month.FEBRUARY -> "ଫେବୃଆରୀ"; Month.MARCH -> "ମାର୍ଚ୍ଚ"
        Month.APRIL -> "ଅପ୍ରେଲ"; Month.MAY -> "ମଇ"; Month.JUNE -> "ଜୁନ"; Month.JULY -> "ଜୁଲାଇ"
        Month.AUGUST -> "ଅଗଷ୍ଟ"; Month.SEPTEMBER -> "ସେପ୍ଟେମ୍ବର"; Month.OCTOBER -> "ଅକ୍ଟୋବର"
        Month.NOVEMBER -> "ନଭେମ୍ବର"; Month.DECEMBER -> "ଡିସେମ୍ବର"
    }
}

private fun convertToOdiaNumber(input: Any?): String {
    if (input == null) return ""
    val text = input.toString(); val odiaNumerals = ('୦'..'୯').toList()
    return text.map { char ->
        val index = ('0'..'9').indexOf(char)
        if (index != -1) odiaNumerals[index] else char
    }.joinToString("")
}

// --- Main Screen ---
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class
)
@Composable
fun OdiaCalendarScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("odia_calendar_cache", Context.MODE_PRIVATE) }

    var showFestivalList by remember { mutableStateOf(false) }
    var showMonthYearPicker by remember { mutableStateOf(false) }

    val dailyData = remember { mutableStateOf<Map<LocalDate, DailyInfo>>(emptyMap()) }
    val isLoading = remember { mutableStateOf(true) }
    val networkError = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val initialMonth = YearMonth.now(); val startMonth = initialMonth.minusYears(50)
    val totalMonths = ChronoUnit.MONTHS.between(startMonth, initialMonth.plusYears(50)).toInt()
    val initialPage = ChronoUnit.MONTHS.between(startMonth, initialMonth).toInt()
    val pagerState = rememberPagerState(initialPage = initialPage) { totalMonths }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isTablet = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact

    BackHandler(enabled = showFestivalList || showMonthYearPicker) {
        when {
            showFestivalList -> showFestivalList = false
            showMonthYearPicker -> showMonthYearPicker = false
        }
    }

    // Helper to parse the JSON content
    fun parseJsonContent(jsonString: String): Map<LocalDate, DailyInfo> {
        val dataMap = mutableMapOf<LocalDate, DailyInfo>()
        try {
            val root = JSONObject(jsonString)
            val jsonObject = root.getJSONObject("data")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            jsonObject.keys().forEach { dateString ->
                try {
                    val date = LocalDate.parse(dateString, formatter)
                    val dayJson = jsonObject.getJSONObject(dateString)
                    val customText = dayJson.optString("ct").takeIf { it.isNotBlank() }
                    var festivalDetails: FestivalDetails? = null
                    val name = dayJson.optString("name").takeIf { it.isNotBlank() }
                    if (name != null) {
                        val colorString = dayJson.optString("color", "")
                        val color = if (colorString.isNotBlank()) Color(android.graphics.Color.parseColor(colorString)) else DefaultFestivalColor
                        festivalDetails = FestivalDetails(name, color)
                    }
                    if (festivalDetails != null || customText != null) {
                        dataMap[date] = DailyInfo(festivalDetails, customText)
                    }
                } catch (_: Exception) { }
            }
        } catch (_: Exception) { }
        return dataMap
    }

    fun fetchDailyData() {
        coroutineScope.launch {
            // 1. First, check cache
            val cachedJson = sharedPrefs.getString("festival_data", null)
            if (cachedJson != null) {
                dailyData.value = parseJsonContent(cachedJson)
                isLoading.value = false
            } else {
                isLoading.value = true
            }

            networkError.value = false
            try {
                // 2. Fetch from network
                val jsonString = withContext(Dispatchers.IO) { URL("https://imsbg.github.io/odiabhasa/calendar/parba.json").readText() }
                val root = JSONObject(jsonString)
                val remoteVersion = root.optInt("version", 0)
                val localVersion = sharedPrefs.getInt("festival_version", -1)

                // 3. Update if version is newer or no cache
                if (remoteVersion > localVersion || cachedJson == null) {
                    dailyData.value = parseJsonContent(jsonString)
                    sharedPrefs.edit()
                        .putString("festival_data", jsonString)
                        .putInt("festival_version", remoteVersion)
                        .apply()
                }
            } catch (_: Exception) {
                // Only show network error if we have NO cached data
                if (dailyData.value.isEmpty()) {
                    networkError.value = true
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    LaunchedEffect(Unit) { fetchDailyData() }

    if (isTablet) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("କ୍ୟାଲେଣ୍ଡର", fontFamily = OdiaFont) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
        }) { paddingValues ->
            Row(Modifier.fillMaxSize().padding(paddingValues)) {
                Box(modifier = Modifier.weight(1.5f).fillMaxHeight()) {
                    FestivalListContent(dailyData = dailyData.value, onRefresh = { fetchDailyData() }, onFestivalSelected = { date ->
                        selectedDate = date
                        coroutineScope.launch {
                            val page = ChronoUnit.MONTHS.between(startMonth, YearMonth.from(date)).toInt()
                            pagerState.animateScrollToPage(page)
                        }
                    })
                }
                Box(modifier = Modifier.weight(2f).fillMaxHeight()) {
                    CalendarView(isTablet = true, onBack = onBack, onShowAllFestivals = { }, onShowMonthYearPicker = { showMonthYearPicker = true }, dailyData = dailyData.value, isLoading = isLoading.value, networkError = networkError.value, onRetry = { fetchDailyData() }, pagerState = pagerState, startMonth = startMonth, initialPage = initialPage, selectedDate = selectedDate, onDateSelected = { newDate -> selectedDate = newDate })
                }
            }
        }
        if (showMonthYearPicker) {
            MonthYearPickerScreen(isTablet = true, currentYearMonth = startMonth.plusMonths(pagerState.currentPage.toLong()), onMonthSelected = { selectedMonth ->
                val page = ChronoUnit.MONTHS.between(startMonth, selectedMonth).toInt()
                coroutineScope.launch { pagerState.animateScrollToPage(page) }
                showMonthYearPicker = false
            }, onBack = { showMonthYearPicker = false })
        }

    } else {
        Crossfade(targetState = when {
            showFestivalList -> "festivals"; showMonthYearPicker -> "picker"; else -> "calendar"
        }, label = "ScreenTransition") { screen ->
            when (screen) {
                "festivals" -> {
                    AllFestivalsScreen(dailyData = dailyData.value, onBack = { showFestivalList = false }, onRefresh = { fetchDailyData() }, onFestivalSelected = { date ->
                        showFestivalList = false; selectedDate = date
                        coroutineScope.launch {
                            val page = ChronoUnit.MONTHS.between(startMonth, YearMonth.from(date)).toInt()
                            pagerState.animateScrollToPage(page)
                        }
                    })
                }
                "picker" -> {
                    MonthYearPickerScreen(isTablet = false, currentYearMonth = startMonth.plusMonths(pagerState.currentPage.toLong()), onMonthSelected = { selectedMonth ->
                        val page = ChronoUnit.MONTHS.between(startMonth, selectedMonth).toInt()
                        coroutineScope.launch { pagerState.animateScrollToPage(page) }
                        showMonthYearPicker = false
                    }, onBack = { showMonthYearPicker = false })
                }
                else -> {
                    CalendarView(isTablet = false, onBack = onBack, onShowAllFestivals = { showFestivalList = true }, onShowMonthYearPicker = { showMonthYearPicker = true }, dailyData = dailyData.value, isLoading = isLoading.value, networkError = networkError.value, onRetry = { fetchDailyData() }, pagerState = pagerState, startMonth = startMonth, initialPage = initialPage, selectedDate = selectedDate, onDateSelected = { newDate -> selectedDate = newDate })
                }
            }
        }
    }
}

// --- Calendar View ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun CalendarView(
    isTablet: Boolean, onBack: () -> Unit, onShowAllFestivals: () -> Unit, onShowMonthYearPicker: () -> Unit,
    dailyData: Map<LocalDate, DailyInfo>, isLoading: Boolean, networkError: Boolean, onRetry: () -> Unit,
    pagerState: PagerState, startMonth: YearMonth, initialPage: Int,
    selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit
) {
    val currentMonth = startMonth.plusMonths(pagerState.currentPage.toLong())
    val coroutineScope = rememberCoroutineScope(); var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss")
        while (true) {
            val now = LocalTime.now(); val period = getTimePeriod(now); val formattedTime = now.format(timeFormatter)
            currentTime = "$period ${convertToOdiaNumber(formattedTime)}"; delay(1000)
        }
    }

    val content = @Composable { paddingValues: PaddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // CRASH FIX 1/2: Use LazyColumn as the main container instead of a scrollable Column.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = if (isTablet) 24.dp else 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    CalendarHeader(yearMonth = currentMonth, onPreviousMonth = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }, onNextMonth = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }, onClick = onShowMonthYearPicker)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    DayOfWeekHeader()
                }
                item {
                    HorizontalPager(state = pagerState) { page ->
                        val month = startMonth.plusMonths(page.toLong())
                        CalendarGrid(yearMonth = month, selectedDate = selectedDate, dailyData = dailyData, onDateSelected = { newDate -> if (YearMonth.from(newDate) == month) onDateSelected(newDate) })
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    FestivalDisplay(selectedDate = selectedDate, dailyData = dailyData, isLoading = isLoading, networkError = networkError, onRetry = onRetry)
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            if (!isTablet) {
                AnimatedVisibility(visible = !isLoading && !networkError && dailyData.any { it.value.festival != null }, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp), enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
                    FloatingActionButton(onClick = onShowAllFestivals, shape = RoundedCornerShape(16.dp)) { Icon(Icons.Default.List, "All Festivals") }
                }
            }
            AnimatedVisibility(visible = currentMonth != YearMonth.now() || selectedDate != LocalDate.now(), modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
                val todayOdiaDate = remember { convertToOdiaNumber(LocalDate.now().dayOfMonth) }
                FloatingActionButton(onClick = { coroutineScope.launch { pagerState.animateScrollToPage(initialPage) }; onDateSelected(LocalDate.now()) }, shape = RoundedCornerShape(16.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Icon(Icons.Default.Today, null); Spacer(modifier = Modifier.width(8.dp)); Text("ଆଜି $todayOdiaDate", fontFamily = OdiaFont)
                    }
                }
            }
        }
    }

    if (isTablet) {
        content(PaddingValues(top = 16.dp))
    } else {
        Scaffold(topBar = {
            TopAppBar(title = { Text("କ୍ୟାଲେଣ୍ଡର", fontFamily = OdiaFont) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }, actions = { Text(text = currentTime, modifier = Modifier.padding(end = 16.dp), fontFamily = OdiaFont, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
        }) { paddingValues ->
            content(paddingValues)
        }
    }
}

// --- Month/Year Picker Screen ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
private fun MonthYearPickerScreen(
    isTablet: Boolean, currentYearMonth: YearMonth, onMonthSelected: (YearMonth) -> Unit, onBack: () -> Unit
) {
    val startYear = 1970; val totalYears = 100
    val initialPage = currentYearMonth.year - startYear
    val pagerState = rememberPagerState(initialPage = initialPage) { totalYears }
    val coroutineScope = rememberCoroutineScope()
    val currentYear = Year.of(startYear + pagerState.currentPage)

    Scaffold(topBar = { TopAppBar(title = {}, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous Year") }
                AnimatedContent(targetState = currentYear, label = "YearAnimation", transitionSpec = { if (targetState.isAfter(initialState)) { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() } else { slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut() } }) { targetYear ->
                    Text(text = convertToOdiaNumber(targetYear.value), fontSize = 24.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next Year") }
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                val year = startYear + page
                LazyVerticalGrid(columns = if (isTablet) GridCells.Adaptive(minSize = 120.dp) else GridCells.Fixed(3), modifier = Modifier.padding(horizontal = if (isTablet) 32.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(Month.entries.size) { monthIndex ->
                        val month = Month.entries[monthIndex]
                        val isSelected = year == currentYearMonth.year && month == currentYearMonth.month
                        val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        Card(modifier = Modifier.aspectRatio(1.2f).clip(RoundedCornerShape(12.dp)).clickable { onMonthSelected(YearMonth.of(year, month)) }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = backgroundColor)) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text(text = getOdiaGregorianMonthName(month), fontFamily = OdiaFont, fontWeight = FontWeight.Medium, color = contentColor) }
                        }
                    }
                }
            }
        }
    }
}

// --- All Festivals Screens ---
@Composable
private fun AllFestivalsScreen(dailyData: Map<LocalDate, DailyInfo>, onBack: () -> Unit, onRefresh: () -> Unit, onFestivalSelected: (LocalDate) -> Unit) {
    Scaffold { padding ->
        Box(Modifier.padding(padding)) {
            FestivalListContent(dailyData = dailyData, onRefresh = onRefresh, onFestivalSelected = onFestivalSelected, onBack = onBack)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun FestivalListContent(
    dailyData: Map<LocalDate, DailyInfo>, onRefresh: () -> Unit, onFestivalSelected: (LocalDate) -> Unit, onBack: (() -> Unit)? = null
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()

    val festivalList = remember(dailyData, searchText) {
        dailyData.mapNotNull { (date, info) -> info.festival?.let { festival -> if (festival.name.contains(searchText, ignoreCase = true)) { date to festival } else null } }.sortedBy { it.first }
    }
    val initialIndex = remember(festivalList) { festivalList.indexOfFirst { it.first.isAfter(LocalDate.now().minusDays(1)) }.coerceAtLeast(0) }

    LaunchedEffect(isSearchActive) { if (isSearchActive) { focusRequester.requestFocus() } }
    BackHandler(enabled = isSearchActive) { isSearchActive = false; searchText = "" }
    LaunchedEffect(initialIndex) { lazyListState.scrollToItem(initialIndex) }

    Column {
        TopAppBar(title = { if (isSearchActive) { TextField(value = searchText, onValueChange = { searchText = it }, modifier = Modifier.fillMaxWidth().focusRequester(focusRequester), placeholder = { Text("ପର୍ବ ଖୋଜନ୍ତୁ...", fontFamily = OdiaFont) }, singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)) } else { Text("ପର୍ବପର୍ବାଣି ତାଲିକା", fontFamily = OdiaFont) } },
            navigationIcon = {
                if (onBack != null) {
                    if (isSearchActive) { IconButton(onClick = { isSearchActive = false; searchText = "" }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Close Search") } }
                    else { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
                }
            },
            actions = { if (isSearchActive) { if (searchText.isNotEmpty()) { IconButton(onClick = { searchText = "" }) { Icon(Icons.Default.Clear, "Clear Search") } } } else { IconButton(onClick = { isSearchActive = true }) { Icon(Icons.Default.Search, "Search") }; IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, "Refresh") } } }
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            LazyColumn(state = lazyListState, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (festivalList.isEmpty() && searchText.isNotEmpty()) {
                    item { Text(text = "କିଛି ଫଳାଫଳ ମିଳିଲା ନାହିଁ", fontFamily = OdiaFont, modifier = Modifier.fillMaxWidth().padding(top = 24.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) }
                } else {
                    val groupedByYear = festivalList.groupBy { it.first.year }
                    groupedByYear.forEach { (year, festivals) ->
                        stickyHeader { YearHeader(convertToOdiaNumber(year)) }
                        items(festivals) { (date, festival) ->
                            FestivalListItem(date = date, festival = festival, onClick = { onFestivalSelected(date) })
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

// --- UI Components ---

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    dailyData: Map<LocalDate, DailyInfo>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()

    val calendarDays = List(firstDayOfWeek) { null } + (1..daysInMonth).map { yearMonth.atDay(it) }
    val weeks = calendarDays.chunked(7)

    Column {
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (date != null) {
                            DayCell(
                                date = date,
                                isSelected = (date == selectedDate),
                                dayInfo = dailyData[date],
                                onClick = onDateSelected
                            )
                        } else {
                            // Spacer for empty cells at the start of the month
                            Spacer(modifier = Modifier.aspectRatio(1f))
                        }
                    }
                }
                // If the last week is not full, add spacers to maintain layout
                if (week.size < 7) {
                    for (i in 0 until (7 - week.size)) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}


@Composable
private fun FestivalListItem(date: LocalDate, festival: FestivalDetails, onClick: () -> Unit) {
    val odiaDayNum = convertToOdiaNumber(date.dayOfMonth)
    val odiaDayShort = odiaDaysOfWeek[date.dayOfWeek.value % 7]
    val odiaFullDate = "${convertToOdiaNumber(date.dayOfMonth)} ${getOdiaGregorianMonthName(date.month)}, ${convertToOdiaNumber(date.year)}"

    Card(onClick = onClick, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.width(80.dp).background(festival.color.copy(alpha = 0.1f)).clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)).padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = odiaDayNum, fontFamily = OdiaFont, style = MaterialTheme.typography.headlineSmall, color = festival.color, fontWeight = FontWeight.Bold)
                Text(text = odiaDayShort, fontFamily = OdiaFont, style = MaterialTheme.typography.bodyMedium, color = festival.color)
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = festival.name, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = odiaFullDate, fontFamily = OdiaFont, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun YearHeader(year: String) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background) {
        Text(text = year, fontFamily = OdiaFont, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CalendarHeader(yearMonth: YearMonth, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit, onClick: () -> Unit) {
    val seasonName = getOdiaSeasonsForHeader(yearMonth.month)
    val odiaMonthName = getOdiaMonthsForHeader(yearMonth.month)
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onPreviousMonth) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous") }
                AnimatedContent(targetState = yearMonth, label = "MonthYearAnimation", transitionSpec = { if (targetState.isAfter(initialState)) slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() else slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut() }) { targetMonth ->
                    Text(text = "${getOdiaGregorianMonthName(targetMonth.month)} ${convertToOdiaNumber(targetMonth.year)}", fontSize = 20.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onNextMonth) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next") }
            }
            AnimatedContent(targetState = Pair(seasonName, odiaMonthName), label = "OdiaSeasonAnimation") { (targetSeason, targetOdiaMonth) ->
                Text(text = "ଋତୁ: $targetSeason | ମାସ: $targetOdiaMonth", fontSize = 14.sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun DayOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
        odiaDaysOfWeek.forEachIndexed { index, dayName ->
            val color = if (index == 0) SundayColor else SelectedDateColor
            Text(text = dayName, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}


@Composable
private fun DayCell(date: LocalDate, isSelected: Boolean, dayInfo: DailyInfo?, onClick: (LocalDate) -> Unit) {
    val isToday = date == LocalDate.now()
    val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY
    val baseContentColor = if (isSunday && !isSelected) SundayColor else MaterialTheme.colorScheme.onSurface
    val contentColor = if (isSelected) Color.White else baseContentColor
    val backgroundColor = if (isSelected) SelectedDateColor else Color.Transparent
    val modifier = if (isToday && !isSelected) Modifier.border(1.5.dp, SelectedDateColor, CircleShape) else Modifier

    Box(modifier = Modifier.aspectRatio(1f).padding(4.dp).clip(CircleShape).background(backgroundColor).then(modifier).clickable { onClick(date) }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = convertToOdiaNumber(date.dayOfMonth), fontFamily = OdiaFont, fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal, color = contentColor, fontSize = 16.sp)
            if (dayInfo?.festival != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(if (isSelected) contentColor else dayInfo.festival.color))
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FestivalDisplay(selectedDate: LocalDate, dailyData: Map<LocalDate, DailyInfo>, isLoading: Boolean, networkError: Boolean, onRetry: () -> Unit) {
    val odiaFullDayName = selectedDate.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale("or", "IN"))
    val infoForDay = dailyData[selectedDate]
    val odiaMonth = getOdiaMonthForDate(selectedDate)
    Box(modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 120.dp).clip(RoundedCornerShape(16.dp)).border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "${convertToOdiaNumber(selectedDate.dayOfMonth)} ${getOdiaGregorianMonthName(selectedDate.month)}, $odiaFullDayName | ମାସ: $odiaMonth", fontFamily = OdiaFont, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            AnimatedContent(targetState = Triple(isLoading, networkError, infoForDay), label = "FestivalInfoAnimation") { (loading, error, info) ->
                when {
                    loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ଇଣ୍ଟରନେଟ୍ ସଂଯୋଗ ବିଫଳ ହେଲା", fontFamily = OdiaFont, color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = onRetry) { Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("ପୁଣି ଚେଷ୍ଟା କରନ୍ତୁ", fontFamily = OdiaFont) }
                    }
                    else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val hasFestival = info?.festival != null
                        val hasCustomText = !info?.customText.isNullOrBlank()
                        if (hasFestival) {
                            Text(text = info!!.festival!!.name, fontFamily = OdiaFont, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = info.festival.color)
                        } else if (hasCustomText) {
                            Text(text = info!!.customText!!, fontFamily = OdiaFont, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
                        } else {
                            Text(text = "କିଛି ବିଶେଷ ନାହିଁ", fontFamily = OdiaFont, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        }
                        if (hasFestival && hasCustomText) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = info!!.customText!!, fontFamily = OdiaFont, fontSize = 14.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}