package `in`.sandeep.odiabhasa

import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- GENERIC DATA MODELS FOR ANY HISTORY SCREEN ---
@Keep
data class GenericHistoryScreenData(
    val version: Int = 1, // <--- ADDED THIS FIELD TO FIX THE ERROR
    val title: String? = null,
    val imageUrl: String? = null,
    val wikipediaUrl: String? = null,
    val disclaimer: String? = null,
    val content: List<GenericContentItem>? = emptyList()
)

@Keep
data class GenericContentItem(
    val type: String?,
    val text: String? = null,
    val height: Int? = null,
    val data: GenericTableData? = null
)

@Keep
data class GenericTableData(
    val headers: List<String>? = emptyList(),
    val columnWeights: List<Float>? = emptyList(),
    val rows: List<List<String>>? = emptyList()
)

sealed interface GenericHistoryUiState {
    data object Loading : GenericHistoryUiState
    data class Success(val data: GenericHistoryScreenData) : GenericHistoryUiState
    data object Error : GenericHistoryUiState
}


// --- ALL SHARED UI COMPONENTS ARE NOW IN THIS FILE ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopAppBar(
    title: String, isSearchActive: Boolean, searchQuery: String,
    onSearchQueryChange: (String) -> Unit, onSearchActivated: () -> Unit,
    onSearchDeactivated: () -> Unit, onFontClick: () -> Unit, onBack: () -> Unit
) {
    if (isSearchActive) {
        TextField(
            value = searchQuery, onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            placeholder = { Text("ଏଠାରେ ଖୋଜନ୍ତୁ...", fontFamily = OdiaFont) },
            textStyle = TextStyle(fontSize = 18.sp, fontFamily = OdiaFont),
            singleLine = true,
            leadingIcon = { IconButton(onClick = onSearchDeactivated) { Icon(Icons.Default.ArrowBack, "ପଛକୁ ଯାଆନ୍ତୁ") } },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) { Icon(Icons.Default.Clear, "ଲିଭାନ୍ତୁ") }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary, unfocusedIndicatorColor = Color.Transparent
            )
        )
    } else {
        TopAppBar(
            title = { Text(title, fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
            actions = {
                IconButton(onClick = onSearchActivated) { Icon(Icons.Default.Search, "ଖୋଜନ୍ତୁ") }
                IconButton(onClick = onFontClick) { Icon(Icons.Default.TextFields, "ଫଣ୍ଟ ଆକାର ବଦଳାନ୍ତୁ") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun FontAdjustmentDialog(currentAdjustment: Int, onDismiss: () -> Unit, onAdjust: (Int) -> Unit) {
    val minAdjustment = -4; val maxAdjustment = 8
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ଫଣ୍ଟ ଆକାର", fontFamily = OdiaFont) },
        text = {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
                FilledTonalIconButton(onClick = { onAdjust(currentAdjustment - 1) }, enabled = currentAdjustment > minAdjustment) {
                    Icon(Icons.Default.Remove, "କମାନ୍ତୁ")
                }
                Text("ଅ", fontFamily = OdiaFont, fontSize = (18 + currentAdjustment).sp)
                FilledTonalIconButton(onClick = { onAdjust(currentAdjustment + 1) }, enabled = currentAdjustment < maxAdjustment) {
                    Icon(Icons.Default.Add, "ବଢାନ୍ତୁ")
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("ଠିକ୍ ଅଛି", fontFamily = OdiaFont) } }
    )
}

@Composable
fun HighlightedText(text: String, searchQuery: String, style: TextStyle, modifier: Modifier = Modifier) {
    if (searchQuery.isBlank()) {
        Text(text = text, style = style, modifier = modifier); return
    }
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        while (lastIndex < text.length) {
            val startIndex = text.indexOf(searchQuery, lastIndex, ignoreCase = true)
            if (startIndex == -1) { append(text.substring(lastIndex)); break }
            append(text.substring(lastIndex, startIndex))
            withStyle(style = SpanStyle(background = MaterialTheme.colorScheme.primaryContainer)) {
                append(text.substring(startIndex, startIndex + searchQuery.length))
            }
            lastIndex = startIndex + searchQuery.length
        }
    }
    Text(annotatedString, style = style, modifier = modifier)
}

@Composable
fun ErrorScreen(onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("କୌଣସି ତଥ୍ୟ ମିଳିଲା ନାହିଁ", fontFamily = OdiaFont, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("ଦୟାକରି ଆପଣଙ୍କର ୱାଇଫାଇ କିମ୍ବା ମୋବାଇଲ୍ ଡାଟା ଅନ୍ କରନ୍ତୁ", fontFamily = OdiaFont, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, "Refresh"); Spacer(Modifier.width(8.dp)); Text("ପୁଣି ଚେଷ୍ଟା କରନ୍ତୁ", fontFamily = OdiaFont)
        }
    }
}

@Composable
fun DisclaimerBox(text: String, searchQuery: String, fontSizeAdjustment: Int) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)).padding(16.dp)) {
        val style = LocalTextStyle.current.copy(fontFamily = OdiaFont, fontSize = (14 + fontSizeAdjustment).sp, lineHeight = (22 + fontSizeAdjustment).sp, color = MaterialTheme.colorScheme.onErrorContainer, textAlign = TextAlign.Center)
        HighlightedText(text, searchQuery, style)
    }
}
@Composable
fun SectionTitle(text: String, searchQuery: String, fontSizeAdjustment: Int) {
    val style = LocalTextStyle.current.copy(fontSize = (22 + fontSizeAdjustment).sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    HighlightedText(text, searchQuery, style, Modifier.padding(vertical = 8.dp))
}
@Composable
fun SubHeading(text: String, searchQuery: String, fontSizeAdjustment: Int) {
    val style = LocalTextStyle.current.copy(fontSize = (20 + fontSizeAdjustment).sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    HighlightedText(text, searchQuery, style, Modifier.padding(top = 16.dp, bottom = 8.dp))
}
@Composable
fun MinorHeading(text: String, searchQuery: String, fontSizeAdjustment: Int) {
    val style = LocalTextStyle.current.copy(fontSize = (18 + fontSizeAdjustment).sp, fontFamily = OdiaFont, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f))
    HighlightedText(text, searchQuery, style, Modifier.padding(top = 12.dp, bottom = 4.dp))
}
@Composable
fun BodyText(text: String, searchQuery: String, fontSizeAdjustment: Int) {
    val style = LocalTextStyle.current.copy(fontSize = (17 + fontSizeAdjustment).sp, fontFamily = OdiaFont, lineHeight = (28 + fontSizeAdjustment).sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
    HighlightedText(text, searchQuery, style)
}

@Composable
fun SimpleTable(tableData: GenericTableData, searchQuery: String, fontSizeAdjustment: Int) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp).border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), RoundedCornerShape(8.dp))) {
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).padding(horizontal = 8.dp, vertical = 12.dp)) {
            tableData.headers?.forEachIndexed { index, header ->
                val style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold, fontSize = (15 + fontSizeAdjustment).sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HighlightedText(header, searchQuery, style, Modifier.weight(tableData.columnWeights?.getOrNull(index) ?: 1f))
            }
        }
        tableData.rows?.forEach { rowData ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                rowData.forEachIndexed { index, cellData ->
                    val style = LocalTextStyle.current.copy(fontSize = (15 + fontSizeAdjustment).sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), lineHeight = (22 + fontSizeAdjustment).sp)
                    HighlightedText(cellData, searchQuery, style, Modifier.weight(tableData.columnWeights?.getOrNull(index) ?: 1f))
                }
            }
        }
    }
}