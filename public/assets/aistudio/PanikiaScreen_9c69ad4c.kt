
package `in`.sandeep.odiabhasa.ganita

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * MAIN ENTRY POINT
 */
@Composable
fun PanikiaScreen(onBack: () -> Unit) {
    // State to track if we are viewing the list or a specific table
    var selectedTable by remember { mutableStateOf<Int?>(null) }

    // HANDLES HARDWARE BACK BUTTON
    // If a table is selected, go back to null (List View).
    // If null (List View), let the system handle back (Exit screen).
    BackHandler(enabled = selectedTable != null) {
        selectedTable = null
    }

    if (selectedTable == null) {
        // 1. GRID LIST VIEW
        PanikiaListScreen(
            onTableClick = { number -> selectedTable = number },
            onNavigateBack = {
                // If on Grid screen, back button exits to Learning Category
                onBack()
            }
        )
    } else {
        // 2. DETAIL TABLE VIEW
        PanikiaDetailScreen(
            tableNumber = selectedTable!!,
            initialView = null,
            onNavigateBack = {
                // If on Detail screen, back button goes to Grid List
                selectedTable = null
            }
        )
    }
}

// --- DATA & LOGIC ---

data class PanikiaRow(
    val numericalExpression: String,
    val numericalResult: String,
    val scriptPart1: String,
    val scriptPart2: String,
    val scriptPart3: String
)

val odiaDigits = listOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')

val odiaNumberWords = listOf(
    "ଶୂନ", "ଏକ", "ଦୁଇ", "ତିନି", "ଚାରି", "ପାଞ୍ଚ", "ଛଅ", "ସାତ", "ଆଠ", "ନଅ", "ଦଶ",
    "ଏଗାର", "ବାର", "ତେର", "ଚଉଦ", "ପନ୍ଦର", "ଷୋହଳ", "ସତର", "ଅଠର", "ଉଣେଇଶି", "କୋଡ଼ିଏ",
    "ଏକୋଇଶି", "ବାଇଶି", "ତେଇଶି", "ଚବିଶି", "ପଚିଶି", "ଛବିଶି", "ସତେଇଶି", "ଅଠେଇଶି", "ଅଣତିରିଶି", "ତିରିଶି",
    "ଏକତିରିଶି", "ବତିଶି", "ତେତିଶି", "ଚଉତିରିଶି", "ପଞ୍ଚତିରିଶି", "ଛତିଶି", "ସଇଁତିରିଶି", "ଅଠତିରିଶି", "ଅଣଚାଳିଶି", "ଚାଳିଶି",
    "ଏକଚାଳିଶି", "ବୟାଳିଶି", "ତେୟାଳିଶି", "ଚଉରାଳିଶି", "ପଞ୍ଚଚାଳିଶି", "ଛୟାଳିଶି", "ସତଚାଳିଶି", "ଅଠଚାଳିଶି", "ଅଣଚାଶ", "ପଚାଶ",
    "ଏକାବନ", "ବାଉନ", "ତେପନ", "ଚଉବନ", "ପଞ୍ଚାବନ", "ଛପନ", "ସତାବନ", "ଅଠାବନ", "ଅଣଷଠି", "ଷାଠିଏ",
    "ଏକଷଠି", "ବାଷଠି", "ତେଷଠି", "ଚଉଷଠି", "ପଞ୍ଚଷଠି", "ଛଅଷଠି", "ସତଷଠି", "ଅଠଷଠି", "ଅଣସ୍ତରି", "ସତୁରି",
    "ଏକସ୍ତରି", "ବାସ୍ତରି", "ତେସ୍ତରି", "ଚଉସ୍ତରି", "ପଞ୍ଚସ୍ତରି", "ଛଅସ୍ତରି", "ସତସ୍ତରି", "ଅଠସ୍ତରି", "ଅଣାଅଶୀ", "ଅଶୀ",
    "ଏକାଅଶୀ", "ବୟାଅଶୀ", "ତେୟାଅଶୀ", "ଚଉରାଅଶୀ", "ପଞ୍ଚାଅଶୀ", "ଛୟାଅଶୀ", "ସତାଅଶୀ", "ଅଠାଅଶୀ", "ଅଣାନବେ", "ନବେ",
    "ଏକାନବେ", "ବୟାନବେ", "ତେୟାନବେ", "ଚଉରାନବେ", "ପଞ୍ଚାନବେ", "ଛୟାନବେ", "ସତାନବେ", "ଅଠାନବେ", "ଅନେଶୋତ", "ଶହେ"
)

val panikiaTerms = listOf("କେ", "ଦୁଗୁଣେ", "ତିରି", "ଚଉ", "ପଞ୍ଚା", "ଷୋ", "ସତା", "ଅଷ୍ଟା", "ନୂଆଁ", "ଦଶା")

fun Int.toOdiaNumerals(): String {
    return this.toString().map { char ->
        if (char.isDigit()) odiaDigits[char.digitToInt()] else char
    }.joinToString("")
}

fun getOdiaNumberWord(number: Int): String {
    if (number <= 100) return odiaNumberWords.getOrElse(number) { "" }
    val hundreds = number / 100
    val remainder = number % 100
    val hundredsPart = if (hundreds == 1) "ଶହେ" else "${odiaNumberWords[hundreds]} ଶହ"
    if (remainder == 0) return hundredsPart
    val remainderPart = odiaNumberWords.getOrElse(remainder) { "" }
    return "$hundredsPart $remainderPart"
}

fun getPanikiaTableData(number: Int): List<PanikiaRow> {
    val list = mutableListOf<PanikiaRow>()
    for (i in 1..10) {
        val result = number * i
        list.add(
            PanikiaRow(
                numericalExpression = "${number.toOdiaNumerals()} x ${i.toOdiaNumerals()}",
                numericalResult = result.toOdiaNumerals(),
                scriptPart1 = getOdiaNumberWord(number),
                scriptPart2 = panikiaTerms.getOrElse(i - 1) { "" },
                scriptPart3 = getOdiaNumberWord(result)
            )
        )
    }
    return list
}

object ComicColors {
    val Background = Color(0xFFFFF8E1)
    val PrimaryOrange = Color(0xFFFFC107)
    val PopGreen = Color(0xFFAED581) // Light Green
    val PopCyan = Color(0xFF26C6DA)  // Cyan/Blue
    val PopPurple = Color(0xFFAB47BC) // Purple
}

// --- SCREENS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanikiaListScreen(onTableClick: (Int) -> Unit, onNavigateBack: () -> Unit) {
    Scaffold(
        containerColor = ComicColors.Background,
        // Header is handled by ComicTopBar with status bar padding
        topBar = { ComicTopBar(title = "ପଣିକିଆ", onBack = onNavigateBack) }
    ) { padding ->
        val tableNumbers = (2..25).toList()

        // Fixed 4 columns to ensure the row-by-row coloring works perfectly
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(tableNumbers) { index, number ->
                // Row logic: 4 items per row.
                // Row 0 = Green, Row 1 = Cyan, Row 2 = Purple, repeat.
                val rowIndex = index / 4
                val color = when (rowIndex % 3) {
                    0 -> ComicColors.PopGreen
                    1 -> ComicColors.PopCyan
                    else -> ComicColors.PopPurple
                }

                ComicGridItem(number = number, color = color, onClick = { onTableClick(number) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PanikiaDetailScreen(tableNumber: Int, initialView: String?, onNavigateBack: () -> Unit) {
    val totalTablesToDisplay = 25 - tableNumber + 1
    val pageCount = totalTablesToDisplay * 2
    val initialPage = if (initialView == "word") 1 else 0
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { pageCount })

    val currentTableForTitle by remember { derivedStateOf { tableNumber + (pagerState.currentPage / 2) } }
    val titleText = "${currentTableForTitle.toOdiaNumerals()}କ ପଣିକିଆ"

    Scaffold(
        containerColor = ComicColors.Background,
        topBar = { ComicTopBar(title = titleText, onBack = onNavigateBack) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                val currentTableNumber = tableNumber + (page / 2)
                val isNumericalView = page % 2 == 0
                val panikiaTableData = getPanikiaTableData(currentTableNumber)

                if (isNumericalView) NumericalTableView(panikiaTableData)
                else ScriptTableView(panikiaTableData)
            }
            if (pagerState.currentPage < pageCount - 1) SwipeUpHint()
        }
    }
}

// --- UI COMPONENTS ---

@Composable
fun ComicGridItem(number: Int, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Makes them square
            .clickable(onClick = onClick)
    ) {
        // Shadow (Black layer behind)
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp, start = 5.dp)
            .background(Color.Black, RoundedCornerShape(12.dp))
        )
        // Main Card (Colored layer)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 5.dp, end = 5.dp)
                .background(color, RoundedCornerShape(12.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toOdiaNumerals(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                // White text with small black shadow for readability
                style = TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black,
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 0f
                    )
                )
            )
        }
    }
}

@Composable
fun NumericalTableView(panikiaTable: List<PanikiaRow>) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(12.dp))
        // Header
        Box(modifier = Modifier.fillMaxWidth().height(48.dp).background(ComicColors.PopCyan, RoundedCornerShape(8.dp)).border(2.dp, Color.Black, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Text(text = "ସଂଖ୍ୟା ତାଲିକା", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Rows
        panikiaTable.forEach { row ->
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(Color.White, RoundedCornerShape(8.dp)).border(2.dp, Color.Black, RoundedCornerShape(8.dp))) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = row.numericalExpression, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                    Text(text = "=", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = ComicColors.PopPurple, modifier = Modifier.padding(horizontal = 8.dp))
                    Text(text = row.numericalResult, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFD84315), textAlign = TextAlign.End)
                }
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun ScriptTableView(panikiaTable: List<PanikiaRow>) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(12.dp))
        // Header
        Box(modifier = Modifier.fillMaxWidth().height(48.dp).background(ComicColors.PopPurple, RoundedCornerShape(8.dp)).border(2.dp, Color.Black, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Text(text = "ଶବ୍ଦ ତାଲିକା", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Rows
        panikiaTable.forEach { row ->
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                // Shadow
                Box(modifier = Modifier.matchParentSize().padding(top = 3.dp, start = 3.dp).background(Color.Black, RoundedCornerShape(10.dp)))

                // Content
                Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(10.dp)).border(2.dp, Color.Black, RoundedCornerShape(10.dp)).padding(vertical = 8.dp, horizontal = 6.dp), contentAlignment = Alignment.Center) {
                    AutoSizingText(
                        text = "${row.scriptPart1} ${row.scriptPart2} ${row.scriptPart3}",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp, textAlign = TextAlign.Center, color = Color.Black),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun ComicTopBar(title: String, onBack: () -> Unit) {
    // Top Bar Container
    Column {
        // This spacer pushes the content down to avoid the status bar/camera
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .background(ComicColors.PrimaryOrange)
            .windowInsetsTopHeight(WindowInsets.statusBars)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp) // Height of the bar itself
                .background(ComicColors.PrimaryOrange)
                .drawBehind {
                    // Thick bottom border
                    drawLine(
                        color = Color.Black,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = 3.dp.toPx() * 2
                    )
                }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center // Center content in the box
        ) {
            // Back Button (Aligned to Start)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            // Title (Centered)
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                color = Color.Black,
                style = LocalTextStyle.current.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.White,
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 0f
                    )
                )
            )
        }
    }
}

@Composable
fun SwipeUpHint() {
    Box(modifier = Modifier.fillMaxSize().padding(bottom = 16.dp), contentAlignment = Alignment.BottomCenter) {
        Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(text = "ଉପରକୁ ସ୍କ୍ରୋଲ କରନ୍ତୁ", style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun AutoSizingText(text: String, style: TextStyle, modifier: Modifier = Modifier, fontWeight: FontWeight? = null) {
    var scaledTextStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    Text(
        text = text, modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        style = scaledTextStyle, fontWeight = fontWeight, softWrap = false, maxLines = 1,
        onTextLayout = { if (it.didOverflowWidth) scaledTextStyle = scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.95) else readyToDraw = true }
    )
}