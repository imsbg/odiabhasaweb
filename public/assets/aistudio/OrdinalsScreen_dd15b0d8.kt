// FILE: app/src/main/java/in/sandeep/odiabhasa/ganita/OrdinalsScreen.kt

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Making OrdinalEntry public as it's used in a public Composable function
data class OrdinalEntry(val number: String, val word: String, val symbol: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OrdinalsScreen(onBack: () -> Unit) {

    // --- Hardcoded Odia Data (Unchanged) ---
    val numbers = listOf("୧", "୨", "୩", "୪", "୫", "୬", "୭", "୮", "୯", "୧୦", "୧୧", "୧୨", "୧୩", "୧୪", "୧୫", "୧୬", "୧୭", "୧୮", "୧୯", "୨୦", "୨୧", "୨୨", "୨୩", "୨୪", "୨୫", "୨୬", "୨୭", "୨୮", "୨୯", "୩୦", "୩୧", "୩୨", "୩୩", "୩୪", "୩୫", "୩୬", "୩୭", "୩୮", "୩୯", "୪୦", "୪୧", "୪୨", "୪୩", "୪୪", "୪୫", "୪୬", "୪୭", "୪୮", "୪୯", "୫୦", "୫୧", "୫୨", "୫୩", "୫୪", "୫୫", "୫୬", "୫୭", "୫୮", "୫୯", "୬୦", "୬୧", "୬୨", "୬୩", "୬୪", "୬୫", "୬୬", "୬୭", "୬୮", "୬୯", "୭୦", "୭୧", "୭୨", "୭୩", "୭୪", "୭୫", "୭୬", "୭୭", "୭୮", "୭୯", "୮୦", "୮୧", "୮୨", "୮୩", "୮୪", "୮୫", "୮୬", "୮୭", "୮୮", "୮୯", "୯୦", "୯୧", "୯୨", "୯୩", "୯୪", "୯୫", "୯୬", "୯୭", "୯୮", "୯୯", "୧୦୦", "୨୦୦", "୩୦୦", "୪୦୦", "୫୦୦", "୬୦୦", "୭୦୦", "୮୦୦", "୯୦୦", "୧୦୦୦", "୧୦୦୦୦୦")
    val words = listOf("ପ୍ରଥମ", "ଦ୍ୱିତୀୟ", "ତୃତୀୟ", "ଚତୁର୍ଥ", "ପଞ୍ଚମ", "ଷଷ୍ଠ", "ସପ୍ତମ", "ଅଷ୍ଟମ", "ନବମ", "ଦଶମ", "ଏକାଦଶ", "ଦ୍ୱାଦଶ", "ତ୍ରୟୋଦଶ", "ଚତୁର୍ଦ୍ଦଶ", "ପଞ୍ଚଦଶ", "ଷୋଡ଼ଶ", "ସପ୍ତଦଶ", "ଅଷ୍ଟାଦଶ", "ଊନବିଂଶ", "ବିଂଶ", "ଏକବିଂଶ", "ଦ୍ୱାବିଂଶ", "ତ୍ରୟୋବିଂଶ", "ଚତୁବିଂଶ", "ପଞ୍ଚବିଂଶ", "ଷଡବିଂଶ", "ସପ୍ତବିଂଶ", "ଅଷ୍ଟବିଂଶ", "ଊନତ୍ରିଂଶ", "ତ୍ରିଂଶ", "ଏକତ୍ରିଂଶ", "ଦ୍ୱାତ୍ରିଂଶ", "ତ୍ରୟତ୍ରିଂଶ", "ଚତୁତ୍ରିଂଶ", "ପଞ୍ଚତ୍ରିଂଶ", "ଷଟ୍ ତ୍ରିଂଶ", "ସପ୍ତତ୍ରିଂଶ", "ଅଷ୍ଟତ୍ରିଂଶ", "ଊନଚତ୍ୱାରିଂଶ", "ଚତ୍ୱାରିଂଶ", "ଏକଚତ୍ୱାରିଂଶ", "ଦ୍ୱିଚତ୍ୱାରିଂଶ", "ତ୍ରିଚତ୍ୱାରିଂଶ", "ଚତୁଃଚତ୍ୱାରିଂଶ", "ପଞ୍ଚଚତ୍ୱାରିଂଶ", "ଷଟ୍ ଚତ୍ୱାରିଂଶ", "ସପ୍ତଚତ୍ୱାରିଂଶ", "ଅଷ୍ଟଚତ୍ୱାରିଂଶ", "ଊନପଞ୍ଚାଶତ୍", "ପଞ୍ଚାଶତ୍", "ଏକପଞ୍ଚାଶତ୍", "ଦ୍ୱିପଞ୍ଚାଶତ୍", "ତ୍ରିପଞ୍ଚାଶତ୍", "ଚତୁପଞ୍ଚାଶତ୍", "ପଞ୍ଚପଞ୍ଚାଶତ୍", "ଷଡପଞ୍ଚାଶତ୍", "ସପ୍ତପଞ୍ଚାଶତ୍", "ଅଷ୍ଟପଞ୍ଚାଶତ୍", "ଊନଷଷ୍ଠୀତମ", "ଷଷ୍ଠୀତମ", "ଏକଷଷ୍ଠୀତମ", "ଦ୍ୱିଷଷ୍ଠୀତମ", "ତ୍ରିଷଷ୍ଠୀତମ", "ଚତୁଷଷ୍ଠୀତମ", "ପଞ୍ଚଷଷ୍ଠୀତମ", "ଷଟ୍ ଷଷ୍ଠୀତମ", "ସପ୍ତଷଷ୍ଠୀତମ", "ଅଷ୍ଟଷଷ୍ଠୀତମ", "ଊନସପ୍ତତିତମ", "ସପ୍ତତିତମ", "ଏକସପ୍ତତିତମ", "ଦ୍ୱିସପ୍ତତିତମ", "ତ୍ରିସପ୍ତତିତମ", "ଚତୁସପ୍ତତିତମ", "ପଞ୍ଚସପ୍ତତିତମ", "ଷଟ୍ ସପ୍ତତିତମ", "ସତସପ୍ତତିତମ", "ଅଷ୍ଟସପ୍ତତିତମ", "ଊନଶୀତିତମ", "ଅଶୀତିତମ", "ଏକାଶୀତିତମ", "ଦ୍ଵାଶୀତିତମ", "ତ୍ରିଶୀତିତମ", "ଚତୁଶୀତିତମ", "ପଞ୍ଚଶୀତିତମ", "ଷଟ୍ ଶୀତିତମ", "ସପ୍ତଶୀତିତମ", "ଅଷ୍ଟଶୀତିତମ", "ଊନନବତିତମ", "ନବତିତମ", "ଏକନବତିତମ", "ଦ୍ୱିନବତିତମ", "ତ୍ରିନବତିତମ", "ଚତୁନବତିତମ", "ପଞ୍ଚନବତିତମ", "ଷଟ୍ ନବତିତମ", "ସପ୍ତନବତିତମ", "ଅଷ୍ଟନବତିତମ", "ନବନବତିତମ", "ଶତତମ", "ଦ୍ୱିଶତତମ", "ତ୍ରିଶତତମ", "ଚତୁଶତତମ", "ପଞ୍ଚଶତତମ", "ଷଟ୍ ତତମ", "ସପ୍ତଶତତମ", "ଅଷ୍ଟଶତତମ", "ନବଶତତମ", "ସହସ୍ରତମ", "ଲକ୍ଷତମ")
    val symbols = listOf("୧ମ", "୨ୟ", "୩ୟ", "୪ର୍ଥ", "୫ମ", "୬ଷ୍ଠ", "୭ମ", "୮ମ", "୯ମ", "୧୦ମ", "୧୧ଶ", "୧୨ଶ", "୧୩ଶ", "୧୪ଶ", "୧୫ଶ", "୧୬ଶ", "୧୭ଶ", "୧୮ଶ", "୧୯ଶ", "୨୦ଶ", "୨୧ଶ", "୨୨ଶ", "୨୩ଶ", "୨୪ଶ", "୨୫ଶ", "୨୬ଶ", "୨୭ଶ", "୨୮ଶ", "୨୯ଶ", "୩୦ଶ", "୩୧ଶ", "୩୨ଶ", "୩୩ଶ", "୩୪ଶ", "୩୫ଶ", "୩୬ଶ", "୩୭ଶ", "୩୮ଶ", "୩୯ଶ", "୪୦ଶ", "୪୧ଶ", "୪୨ଶ", "୪୩ଶ", "୪୪ଶ", "୪୫ଶ", "୪୬ଶ", "୪୭ଶ", "୪୮ଶ", "୪୯ଶ", "୫୦ମ", "୫୧ମ", "୫୨ମ", "୫୩ମ", "୫୪ମ", "୫୫ମ", "୫୬ମ", "୫୭ମ", "୫୮ମ", "୫୯ମ", "୬୦ମ", "୬୧ମ", "୬୨ମ", "୬୩ମ", "୬୪ମ", "୬୫ମ", "୬୬ମ", "୬୭ମ", "୬୮ମ", "୬୯ମ", "୭୦ମ", "୭୧ମ", "୭୨ମ", "୭୩ମ", "୭୪ମ", "୭୫ମ", "୭୬ମ", "୭୭ମ", "୭୮ମ", "୭୯ମ", "୮୦ମ", "୮୧ମ", "୮୨ମ", "୮୩ମ", "୮୪ମ", "୮୫ମ", "୮୬ମ", "୮୭ମ", "୮୮ମ", "୮୯ମ", "୯୦ମ", "୯୧ମ", "୯୨ମ", "୯୩ମ", "୯୪ମ", "୯୫ମ", "୯୬ମ", "୯୭ମ", "୯୮ମ", "୯୯ମ", "୧୦୦ମ", "୨୦୦ମ", "୩୦୦ମ", "୪୦୦ମ", "୫୦୦ମ", "୬୦୦ମ", "୭୦୦ମ", "୮୦୦ମ", "୯୦୦ମ", "୧୦୦୦ତମ", "୧୦୦୦୦୦ତମ")
    val ordinalData = numbers.zip(words).zip(symbols) { (num, word), sym -> OrdinalEntry(num, word, sym) }

    // Matches your requested background color
    val mainBackgroundColor = Color(0xFFF3E5F5)

    Scaffold(
        containerColor = mainBackgroundColor,
        topBar = {
            // --- HEADER 1: Matches the Comic Pop UI Reference ---
            TopAppBar(
                title = {
                    Text(
                        "ପୂରଣବାଚକ ସଂଖ୍ୟା",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(44.dp)
                            .border(2.dp, Color.Black, CircleShape)
                            .background(Color.White, CircleShape)
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            stickyHeader {
                // --- HEADER 2: FIXED and Comic Pop Style ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(mainBackgroundColor)
                        .padding(bottom = 8.dp)
                ) {
                    ComicStickyHeader()
                }
            }

            items(ordinalData) { entry ->
                ComicOrdinalRow(entry)
            }
        }
    }
}

// --- COMIC UI COMPONENTS ---

@Composable
fun ComicStickyHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cyan Header
        ComicHeaderChip(
            text = "ସଂଖ୍ୟା",
            color = Color(0xFF4DD0E1),
            modifier = Modifier.weight(1.2f)
        )
        // Purple Header
        ComicHeaderChip(
            text = "ଶବ୍ଦ",
            color = Color(0xFFBA68C8),
            modifier = Modifier.weight(2f)
        )
        // Orange Header
        ComicHeaderChip(
            text = "ଚିହ୍ନ",
            color = Color(0xFFFF8A65),
            modifier = Modifier.weight(1.2f)
        )
    }
}

@Composable
fun ComicHeaderChip(text: String, color: Color, modifier: Modifier = Modifier) {
    // Style similar to the cards in your reference: Black border, solid color
    Box(
        modifier = modifier
            .height(44.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
            .background(color, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ComicOrdinalRow(entry: OrdinalEntry) {
    val cornerRadius = 12.dp
    val shadowOffset = 4.dp

    // The Comic "Panel" Container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Necessary for vertical dividers to stretch
    ) {
        // 1. Shadow Layer (Hard black offset)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = shadowOffset, start = shadowOffset)
                .background(Color.Black, RoundedCornerShape(cornerRadius))
        )

        // 2. Main Content Layer (White background)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = shadowOffset, end = shadowOffset)
                .background(Color.White, RoundedCornerShape(cornerRadius))
                .border(2.dp, Color.Black, RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- Column 1: Number (NO CIRCLES, just bold text) ---
                Text(
                    text = entry.number,
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(8.dp),
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                // Vertical Comic Divider
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color.Black)
                )

                // --- Column 2: Word ---
                Text(
                    text = entry.word,
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Vertical Comic Divider
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color.Black)
                )

                // --- Column 3: Symbol ---
                Text(
                    text = entry.symbol,
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(8.dp),
                    color = Color(0xFFD84315), // Deep orange for contrast
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}