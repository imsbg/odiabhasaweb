// FILE: app/src/main/java/in/sandeep/odiabhasa/ganita/CountingScreen.kt

package `in`.sandeep.odiabhasa.ganita

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- DATA MODEL ---
private data class CountingEntry(val number: String, val name: String)

// --- THEME COLORS ---
private object CountingThemeColors {
    val Background = Color(0xFFE8EAF6) // Light Indigo background
    val HeaderBar = Color(0xFF3949AB)  // Deep Indigo top bar
    val PopPink = Color(0xFFD81B60)    // Pink for 'Names'
    val PopTeal = Color(0xFF00ACC1)    // Teal for 'Numbers'
    val PanelBlue = Color(0xFFE3F2FD)  // Light blue for number background
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountingScreen(onBack: () -> Unit) {

    // --- HARDCODED ODIA DATA ---
    val numbers = listOf(
        "୧", "୧୦", "୧୦୦", "୧୦୦୦", "୧୦୦୦୦", "୧୦୦୦୦୦", "୧୦୦୦୦୦୦",
        "୧୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦",
        "୧୦୦୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦୦୦୦",
        "୧୦୦୦୦୦୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦",
        "୧୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦"
    )

    val names = listOf(
        "ଏକ", "ଦଶ", "ଶହ", "ହଜାର", "ଅୟୁତ", "ଲକ୍ଷ", "ନିୟୁତ",
        "କୋଟି", "ଅର୍ବୁଦ", "ବୃନ୍ଦ", "ଖର୍ବ", "ନିଖର୍ବ", "ଶଙ୍ଖ",
        "ପଦ୍ମ", "ସାଗର", "ଅନ୍ତ୍ୟ", "ମଧ୍ୟ", "ପରାର୍ଦ୍ଧ"
    )

    val gananaData = numbers.zip(names) { num, name ->
        CountingEntry(num, name)
    }

    Scaffold(
        containerColor = CountingThemeColors.Background,
        topBar = {
            CountingTopBar(title = "ଗଣନା ପ୍ରଣାଳୀ", onBack = onBack)
        }
    ) { paddingValues ->

        // Use a Column to hold the Fixed Header and the List
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 1. FIXED HEADER (Outside the LazyColumn)
            // It will stay at the top and never scroll
            CountingFixedHeader()

            // 2. SCROLLING LIST
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Takes up remaining space
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(gananaData) { entry ->
                    CountingRowItem(entry)
                }
            }
        }
    }
}

// --- RENAMED UI COMPONENTS ---

@Composable
fun CountingFixedHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CountingThemeColors.Background)
            .padding(horizontal = 16.dp, vertical = 12.dp) // Add padding around the header
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header for Number (Matches row weight 1.8f)
            CountingHeaderChip(
                text = "ସଂଖ୍ୟା",
                color = CountingThemeColors.PopTeal,
                modifier = Modifier.weight(1.8f)
            )
            // Header for Name (Matches row weight 1f)
            CountingHeaderChip(
                text = "ନାମ",
                color = CountingThemeColors.PopPink,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CountingHeaderChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(color, RoundedCornerShape(12.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black,
                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                    blurRadius = 0f
                )
            )
        )
    }
}

@Composable
private fun CountingRowItem(entry: CountingEntry) {
    // Container with Shadow
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Important for divider height
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 4.dp, start = 4.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
        )

        // Main Content Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- COLUMN 1: NUMBER (Weight 1.8f) ---
                Box(
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight()
                        .background(CountingThemeColors.PanelBlue) // Light blue bg for numbers
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    CountingAutoText(
                        text = entry.number,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            letterSpacing = 2.sp
                        )
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.Black)
                )

                // --- COLUMN 2: NAME (Weight 1f) ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = entry.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = CountingThemeColors.PopPink,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CountingTopBar(title: String, onBack: () -> Unit) {
    Column {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .background(CountingThemeColors.HeaderBar)
            .windowInsetsTopHeight(WindowInsets.statusBars)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(CountingThemeColors.HeaderBar)
                .drawBehind {
                    drawLine(
                        color = Color.Black,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = 3.dp.toPx() * 2
                    )
                }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Back Button
            Box(
                modifier = Modifier
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

            // Title
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = Color.White,
                style = LocalTextStyle.current.copy(
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

// --- UTILS ---

@Composable
private fun CountingAutoText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var scaledTextStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style = scaledTextStyle,
        softWrap = false,
        maxLines = 1,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle = scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.90)
            } else {
                readyToDraw = true
            }
        }
    )
}