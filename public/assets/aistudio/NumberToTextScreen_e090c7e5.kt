// FILE: in/sandeep/odiabhasa/ganita/NumberToTextScreen.kt
package `in`.sandeep.odiabhasa.ganita

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.sandeep.odiabhasa.OdiaFont
import java.text.NumberFormat
import java.util.Locale

// --- HARDCODED DATA (No XML) ---

private val ODIA_NUMBER_WORDS = listOf(
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

private const val WORD_HUNDRED_SUFFIX = "ଶହ"
private const val WORD_HUNDRED_STANDALONE = "ଶହେ"
private const val WORD_THOUSAND = "ହଜାର"
private const val WORD_LAKH = "ଲକ୍ଷ"
private const val WORD_CRORE = "କୋଟି"

// --- COMPOSABLE ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberToTextScreen(
    onBack: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var odiaResult by remember { mutableStateOf("") }
    var englishResult by remember { mutableStateOf("") }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Number Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "ନକଲ ହେଲା (Copied)", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = Color(0xFFF0F4F8), // Light grayish blue background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ସଂଖ୍ୟାରୁ ଅକ୍ଷର",
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
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            // --- COMIC INPUT FIELD ---
            Text(
                "ସଂଖ୍ୟା ଲେଖନ୍ତୁ",
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            ComicInputField(
                value = inputText,
                onValueChange = { newValue ->
                    // Limit to 10 digits
                    if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                        inputText = newValue
                        if (inputText.isNotEmpty()) {
                            val number = inputText.toLongOrNull() ?: 0L
                            odiaResult = convertToOdia(number)
                            englishResult = convertToEnglish(number)
                        } else {
                            odiaResult = ""
                            englishResult = ""
                        }
                    }
                },
                placeholder = "୧୨୩...",
                focusManager = focusManager
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (inputText.isNotEmpty()) {
                // Odia Result
                ComicResultCard(
                    label = "ଓଡ଼ିଆ",
                    nativeNum = inputText.toLocalOdiaNumerals(),
                    text = odiaResult,
                    cardColor = Color(0xFFFFF176), // Yellow
                    textColor = Color.Black,
                    fontFamily = OdiaFont,
                    onCopy = { copyToClipboard(odiaResult) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // English Result
                ComicResultCard(
                    label = "ଇଂରାଜୀ (English)",
                    nativeNum = inputText,
                    text = englishResult,
                    cardColor = Color(0xFF4FC3F7), // Light Blue
                    textColor = Color.Black,
                    fontFamily = null,
                    onCopy = { copyToClipboard(englishResult) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Cheque Format
                ComicChequeCard(
                    number = inputText.toLongOrNull() ?: 0L,
                    odiaText = odiaResult,
                    englishText = englishResult
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// --- COMIC UI COMPONENTS ---

@Composable
fun ComicInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Box(modifier = Modifier.fillMaxWidth().height(64.dp)) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp, start = 4.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
        )
        // Main Input
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = OdiaFont,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            visualTransformation = OdiaDigitsVisualTransformation(), // Shows Odia digits
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 4.dp, end = 4.dp)
                        .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontFamily = OdiaFont,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun ComicResultCard(
    label: String,
    nativeNum: String,
    text: String,
    cardColor: Color,
    textColor: Color,
    fontFamily: androidx.compose.ui.text.font.FontFamily?,
    onCopy: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .padding(top = 4.dp, start = 4.dp)
                .background(Color.Black, RoundedCornerShape(16.dp))
        )
        // Card Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp, end = 4.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(cardColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = OdiaFont,
                        color = textColor.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        lineHeight = 30.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    // Number Badge
                    Box(
                        modifier = Modifier
                            .background(Color.Black, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = nativeNum,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = OdiaFont,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Copy Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .border(2.dp, Color.Black, CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable(onClick = onCopy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComicChequeCard(
    number: Long,
    odiaText: String,
    englishText: String
) {
    var showInEnglish by remember { mutableStateOf(false) }

    val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedNumber = numberFormat.format(number)

    val displayAmount = if (showInEnglish) formattedNumber else formattedNumber.toLocalOdiaNumerals()
    val displayText = if (showInEnglish) englishText else odiaText

    val label = "ବ୍ୟାଙ୍କ ଫର୍ମାଟ୍"
    val symbol = "₹"
    val suffix = "/-"
    val wordRupees = if (showInEnglish) "Rupees" else "ଟଙ୍କା"
    val wordOnly = if (showInEnglish) "Only" else "ମାତ୍ର"

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .padding(top = 4.dp, start = 4.dp)
                .background(Color.Black, RoundedCornerShape(16.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp, end = 4.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = OdiaFont,
                    color = Color.Black,
                    fontWeight = FontWeight.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (showInEnglish) "Eng" else "Odia",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = showInEnglish,
                        onCheckedChange = { showInEnglish = it },
                        modifier = Modifier.scale(0.7f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color.Black,
                            uncheckedThumbColor = Color.Black,
                            uncheckedTrackColor = Color(0xFFE0E0E0),
                            uncheckedBorderColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "$symbol $displayAmount $suffix",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = if (showInEnglish) null else OdiaFont,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$displayText $wordRupees $wordOnly",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = if (showInEnglish) null else OdiaFont,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                lineHeight = 26.sp
            )
        }
    }
}

// --- LOGIC: ODIA CONVERSION ---

private fun convertToOdia(n: Long): String {
    if (n in 0..100) {
        return ODIA_NUMBER_WORDS.getOrElse(n.toInt()) { "" }
    }
    if (n < 1000) {
        val hundredPart = n / 100
        val remainder = n % 100
        val prefix = if (hundredPart == 1L) WORD_HUNDRED_STANDALONE else "${ODIA_NUMBER_WORDS[hundredPart.toInt()]} $WORD_HUNDRED_SUFFIX"
        val rest = if (remainder != 0L) " " + convertToOdia(remainder) else ""
        return "$prefix$rest"
    }
    if (n < 100000) {
        val thousandPart = n / 1000
        val remainder = n % 1000
        val prefix = convertToOdia(thousandPart)
        val rest = if (remainder != 0L) " " + convertToOdia(remainder) else ""
        return "$prefix $WORD_THOUSAND$rest"
    }
    if (n < 10000000) {
        val lakhPart = n / 100000
        val remainder = n % 100000
        val prefix = convertToOdia(lakhPart)
        val rest = if (remainder != 0L) " " + convertToOdia(remainder) else ""
        return "$prefix $WORD_LAKH$rest"
    }
    val crorePart = n / 10000000
    val remainder = n % 10000000
    val prefix = convertToOdia(crorePart)
    val rest = if (remainder != 0L) " " + convertToOdia(remainder) else ""
    return "$prefix $WORD_CRORE$rest"
}

// --- LOGIC: ENGLISH CONVERSION ---
private fun convertToEnglish(n: Long): String {
    if (n == 0L) return "Zero"
    val units = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
    val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

    if (n < 0) return "Minus " + convertToEnglish(-n)
    if (n < 20) return units[n.toInt()]
    if (n < 100) return tens[n.toInt() / 10] + (if (n % 10 != 0L) " " + units[n.toInt() % 10] else "")
    if (n < 1000) return units[n.toInt() / 100] + " Hundred" + (if (n % 100 != 0L) " " + convertToEnglish(n % 100) else "")
    if (n < 100000) return convertToEnglish(n / 1000) + " Thousand" + (if (n % 1000 != 0L) " " + convertToEnglish(n % 1000) else "")
    if (n < 10000000) return convertToEnglish(n / 100000) + " Lakh" + (if (n % 100000 != 0L) " " + convertToEnglish(n % 100000) else "")
    return convertToEnglish(n / 10000000) + " Crore" + (if (n % 10000000 != 0L) " " + convertToEnglish(n % 10000000) else "")
}

// --- HELPER: ODIA DIGITS ---

private fun String.toLocalOdiaNumerals(): String {
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

// --- VISUAL TRANSFORMATION ---
// This makes the standard numbers (0-9) look like Odia numbers (୦-୯) in the TextField
class OdiaDigitsVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = text.text.toLocalOdiaNumerals()
        return TransformedText(
            AnnotatedString(transformed),
            OffsetMapping.Identity
        )
    }
}