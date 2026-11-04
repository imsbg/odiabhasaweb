// FILE: in/sandeep/odiabhasa/ganita/UnitConverterScreen.kt
package `in`.sandeep.odiabhasa.ganita

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.sandeep.odiabhasa.OdiaFont
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.pow

// --- Data Models (Using direct String instead of StringRes) ---
data class UnitItem(val name: String, val symbol: String, val toBase: (Double) -> Double, val fromBase: (Double) -> Double)
data class ConversionCategory(val name: String, val icon: ImageVector, val colors: List<Color>, val units: List<UnitItem>)

// --- Helper function to convert English digits to Odia digits ---
fun String.toOdiaNumerals(): String {
    val odiaNumerals = arrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    val sb = StringBuilder()
    for (char in this) {
        if (char.isDigit()) {
            sb.append(odiaNumerals[char.toString().toInt()])
        } else {
            sb.append(char)
        }
    }
    return sb.toString()
}

// --- Main Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    onBack: () -> Unit // CHANGED: Renamed from onNavigateBack to onBack to match MainActivity
) {
    val categories = remember { getConversionCategories() }
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    var inputValue by remember { mutableStateOf("1") }

    var fromUnit by remember { mutableStateOf(selectedCategory.units.first()) }
    var toUnit by remember { mutableStateOf(selectedCategory.units.getOrNull(1) ?: selectedCategory.units.first()) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var isSelectingFromUnit by remember { mutableStateOf(true) }

    // Reset input when category changes
    LaunchedEffect(selectedCategory) {
        inputValue = "1"
        fromUnit = selectedCategory.units.first()
        toUnit = selectedCategory.units.getOrNull(1) ?: selectedCategory.units.first()
    }

    val outputValue = remember(inputValue, fromUnit, toUnit) {
        if (inputValue.isEmpty() || inputValue == "-") return@remember ""
        val input = inputValue.toDoubleOrNull() ?: 0.0
        val baseValue = fromUnit.toBase(input)
        val result = toUnit.fromBase(baseValue)
        val df = DecimalFormat("#.##########")
        df.format(result)
    }

    Scaffold(
        containerColor = Color(0xFFF0F4F8), // Light grayish blue
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ଏକକ ରୂପାନ୍ତରଣ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Black,
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
                            .clickable(onClick = onBack), // CHANGED: Uses onBack here
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Category Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    ComicCategoryChip(
                        category = category,
                        isSelected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            // 2. Display Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ComicUnitDisplayCard(
                    label = "ଏଠାରୁ",
                    value = inputValue.toOdiaNumerals(),
                    unitSymbol = fromUnit.symbol,
                    onUnitClicked = {
                        isSelectingFromUnit = true
                        showBottomSheet = true
                    },
                    themeColor = selectedCategory.colors.first()
                )
                Spacer(modifier = Modifier.height(16.dp))
                ComicSwapButton(onClick = {
                    val temp = fromUnit
                    fromUnit = toUnit
                    toUnit = temp
                }, color = selectedCategory.colors.last())
                Spacer(modifier = Modifier.height(16.dp))
                ComicUnitDisplayCard(
                    label = "ଏହାକୁ",
                    value = outputValue.toOdiaNumerals(),
                    unitSymbol = toUnit.symbol,
                    onUnitClicked = {
                        isSelectingFromUnit = false
                        showBottomSheet = true
                    },
                    themeColor = Color(0xFFB0BEC5), // Gray for inactive
                    isReadOnly = true
                )
            }

            // 3. Custom Comic Keyboard
            ComicKeyboard(
                onKeyPressed = { key ->
                    when (key) {
                        "C" -> inputValue = ""
                        "⌫" -> if (inputValue.isNotEmpty()) inputValue = inputValue.dropLast(1)
                        "." -> if (!inputValue.contains(".")) inputValue += "."
                        "-" -> {
                            if (inputValue.startsWith("-")) inputValue = inputValue.substring(1)
                            else inputValue = "-$inputValue"
                        }
                        else -> inputValue += key
                    }
                },
                themeColor = selectedCategory.colors.last(),
                showMinus = selectedCategory.name == "ତାପମାତ୍ରା"
            )
        }

        // Bottom Sheet for Unit Selection
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Text(
                    text = if (isSelectingFromUnit) "ଏଠାରୁ" else "ଏହାକୁ",
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )
                LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
                    items(selectedCategory.units) { unit ->
                        ListItem(
                            headlineContent = { Text(unit.name, fontFamily = OdiaFont) },
                            trailingContent = { Text(unit.symbol, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.clickable {
                                if (isSelectingFromUnit) fromUnit = unit else toUnit = unit
                                scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- COMIC UI COMPONENTS ---

@Composable
private fun ComicUnitDisplayCard(
    label: String, value: String, unitSymbol: String, onUnitClicked: () -> Unit,
    themeColor: Color, isReadOnly: Boolean = false
) {
    val cardColor = if (isReadOnly) Color.White else Color(0xFFFFFDE7)
    Box {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(top = 6.dp, start = 6.dp)
                .background(Color.Black, RoundedCornerShape(24.dp))
        )
        // Main Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(bottom = 6.dp, end = 6.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(24.dp))
                .background(cardColor, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label, style = MaterialTheme.typography.labelLarge,
                    fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.7f)
                )
                val fontSize = calculateFontSize(value.length)
                Text(
                    text = if(value.isEmpty()) "୦" else value, fontSize = fontSize, fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold, color = if (isReadOnly) Color.Gray else themeColor, maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Unit Selector Button
            Box(modifier = Modifier.clickable(onClick = onUnitClicked)) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp)
                        .background(Color.Black, RoundedCornerShape(16.dp))
                )
                Row(
                    modifier = Modifier
                        .padding(bottom = 4.dp, end = 4.dp)
                        .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = unitSymbol, fontWeight = FontWeight.Bold,
                        fontFamily = OdiaFont, style = MaterialTheme.typography.titleMedium
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
                }
            }
        }
    }
}

private fun calculateFontSize(length: Int): TextUnit {
    return when {
        length > 15 -> 22.sp
        length > 10 -> 28.sp
        length > 7 -> 36.sp
        else -> 44.sp
    }
}

@Composable
private fun ComicCategoryChip(category: ConversionCategory, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(targetValue = if (isPressed || isSelected) 6.dp else 0.dp, label = "offset")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    ) {
        Box(modifier = Modifier.width(70.dp).height(74.dp)) {
            // Shadow
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .padding(top = 6.dp, start = 6.dp)
                    .background(Color.Black, CircleShape)
                    .align(Alignment.TopStart)
            )
            // Main Icon
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .offset(x = offset, y = offset)
                    .border(2.dp, Color.Black, CircleShape)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = category.colors))
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = category.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name, fontFamily = OdiaFont, fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            color = Color.Black.copy(alpha = if (isSelected) 1f else 0.7f)
        )
    }
}

@Composable
private fun ComicSwapButton(onClick: () -> Unit, color: Color) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .padding(top = 6.dp, start = 6.dp)
                .background(Color.Black, CircleShape)
                .align(Alignment.TopStart)
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .border(2.dp, Color.Black, CircleShape)
                .background(color, CircleShape)
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SwapVert, contentDescription = "Swap", tint = Color.White)
        }
    }
}

@Composable
fun ComicKeyboard(onKeyPressed: (String) -> Unit, themeColor: Color, showMinus: Boolean) {
    val keys = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"))
    val bottomRow = if (showMinus) listOf(".", "0", "⌫", "-") else listOf(".", "0", "⌫")

    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFE8EAF6)).padding(8.dp)) {
        keys.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().height(64.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    ComicKeyboardKey(key = key, weight = 1f, onClick = onKeyPressed)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(modifier = Modifier.fillMaxWidth().height(64.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            bottomRow.forEach { key ->
                ComicKeyboardKey(key = key, weight = 1f, isAction = key == "⌫" || key == "-", actionColor = themeColor, onClick = onKeyPressed)
            }
        }
    }
}

@Composable
fun RowScope.ComicKeyboardKey(
    key: String, weight: Float, onClick: (String) -> Unit,
    isAction: Boolean = false, actionColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp, label = "offset")
    val bgColor = if(isAction) actionColor.copy(alpha = 0.8f) else Color.White

    Box(
        modifier = Modifier.weight(weight).fillMaxHeight().clickable(
            interactionSource = interactionSource, indication = null, onClick = { onClick(key) }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 4.dp, start = 4.dp).background(Color.Black, RoundedCornerShape(16.dp))
        )
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 4.dp, end = 4.dp).offset(x = offset, y = offset)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(bgColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (key) {
                "⌫" -> Icon(Icons.Default.Backspace, contentDescription = "Backspace", tint = if(isAction) Color.White else Color.Black)
                "-" -> Text("±", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                else -> Text(
                    text = key.toOdiaNumerals(), fontFamily = OdiaFont, fontSize = 28.sp,
                    fontWeight = FontWeight.Bold, color = Color.Black
                )
            }
        }
    }
}

// --- Data Source with Odia Text ---
private fun getConversionCategories(): List<ConversionCategory> {
    return listOf(
        ConversionCategory("ଲମ୍ବ", Icons.Default.Straighten, listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)), listOf(
            UnitItem("ମିଲିମିଟର", "mm", { it / 1000 }, { it * 1000 }),
            UnitItem("ସେଣ୍ଟିମିଟର", "cm", { it / 100 }, { it * 100 }),
            UnitItem("ମିଟର", "m", { it }, { it }),
            UnitItem("କିଲୋମିଟର", "km", { it * 1000 }, { it / 1000 }),
            UnitItem("ଇଞ୍ଚ", "in", { it * 0.0254 }, { it / 0.0254 }),
            UnitItem("ଫୁଟ", "ft", { it * 0.3048 }, { it / 0.3048 }),
            UnitItem("ଗଜ", "yd", { it * 0.9144 }, { it / 0.9144 }),
            UnitItem("ମାଇଲ", "mi", { it * 1609.34 }, { it / 1609.34 })
        )),
        ConversionCategory("ଓଜନ", Icons.Default.Scale, listOf(Color(0xFFAED581), Color(0xFF689F38)), listOf(
            UnitItem("ମିଲିଗ୍ରାମ", "mg", { it / 1000 }, { it * 1000 }),
            UnitItem("ଗ୍ରାମ", "g", { it }, { it }),
            UnitItem("କିଲୋଗ୍ରାମ", "kg", { it * 1000 }, { it / 1000 }),
            UnitItem("କ୍ୱିଣ୍ଟାଲ", "q", { it * 100000 }, { it / 100000 }),
            UnitItem("ଟନ୍", "t", { it * 1000000 }, { it / 1000000 }),
            UnitItem("ପାଉଣ୍ଡ", "lb", { it * 453.592 }, { it / 453.592 })
        )),
        ConversionCategory("ଧାରଣ କ୍ଷମତା", Icons.Default.Opacity, listOf(Color(0xFFCE93D8), Color(0xFF8E24AA)), listOf(
            UnitItem("ମିଲିଲିଟର", "ml", { it / 1000 }, { it * 1000 }),
            UnitItem("ଲିଟର", "l", { it }, { it }),
            UnitItem("କିଲୋଲିଟର", "kl", { it * 1000 }, { it / 1000 }),
            UnitItem("ଘନ ସେଣ୍ଟିମିଟର", "cc", { it / 1000 }, { it * 1000 })
        )),
        ConversionCategory("ତାପମାତ୍ରା", Icons.Default.Thermostat, listOf(Color(0xFFEF9A9A), Color(0xFFD32F2F)), listOf(
            UnitItem("ସେଲସିୟସ", "°C", { it }, { it }),
            UnitItem("ଫାରେନହାଇଟ୍", "°F", { (it - 32) * 5 / 9 }, { (it * 9 / 5) + 32 }),
            UnitItem("କେଲଭିନ", "K", { it - 273.15 }, { it + 273.15 })
        )),
        ConversionCategory("ସମୟ", Icons.Default.Schedule, listOf(Color(0xFFFFCC80), Color(0xFFF57C00)), listOf(
            UnitItem("ସେକେଣ୍ଡ", "sec", { it }, { it }),
            UnitItem("ମିନିଟ୍", "min", { it * 60 }, { it / 60 }),
            UnitItem("ଘଣ୍ଟା", "hr", { it * 3600 }, { it / 3600 }),
            UnitItem("ଦିନ", "d", { it * 86400 }, { it / 86400 }),
            UnitItem("ସପ୍ତାହ", "wk", { it * 604800 }, { it / 604800 }),
            UnitItem("ମାସ", "mo", { it * 2628000 }, { it / 2628000 }),
            UnitItem("ବର୍ଷ", "yr", { it * 31536000 }, { it / 31536000 })
        )),
        ConversionCategory("ଷ୍ଟୋରେଜ୍", Icons.Default.SdStorage, listOf(Color(0xFF607D8B), Color(0xFF455A64)), listOf(
            UnitItem("ବିଟ୍", "b", { it }, { it }),
            UnitItem("ବାଇଟ୍", "B", { it * 8 }, { it / 8 }),
            UnitItem("କିଲୋବିଟ୍", "Kb", { it * 1024 }, { it / 1024 }),
            UnitItem("କିଲୋବାଇଟ୍", "KB", { it * 8 * 1024 }, { it / (8 * 1024) }),
            UnitItem("ମେଗାବିଟ୍", "Mb", { it * 1024.0.pow(2) }, { it / 1024.0.pow(2) }),
            UnitItem("ମେଗାବାଇଟ୍", "MB", { it * 8 * 1024.0.pow(2) }, { it / (8 * 1024.0.pow(2)) }),
            UnitItem("ଗିଗାବିଟ୍", "Gb", { it * 1024.0.pow(3) }, { it / 1024.0.pow(3) }),
            UnitItem("ଗିଗାବାଇଟ୍", "GB", { it * 8 * 1024.0.pow(3) }, { it / (8 * 1024.0.pow(3)) }),
            UnitItem("ଟେରାବିଟ୍", "Tb", { it * 1024.0.pow(4) }, { it / 1024.0.pow(4) }),
            UnitItem("ଟେରାବାଇଟ୍", "TB", { it * 8 * 1024.0.pow(4) }, { it / (8 * 1024.0.pow(4)) })
        ))
    )
}