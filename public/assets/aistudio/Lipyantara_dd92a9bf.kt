package `in`.sandeep.odiabhasa

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LipyantaraScreen(
    onBack: () -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)

    // State for font size with limits
    val minFontSize = 14.sp
    val maxFontSize = 26.sp
    var contentFontSize by remember { mutableStateOf(18.sp) }

    // --- A much simpler row for each letter ---
    @Composable
    fun AlphabetEntry(odiaChar: String, transliteration: String, ipa: String, fontSize: TextUnit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = odiaChar,
                // CORRECTED: Explicitly use .value for calculation
                fontSize = (fontSize.value + 8).sp,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(64.dp)
            )
            Column {
                Text(
                    text = transliteration,
                    fontSize = fontSize,
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "IPA: $ipa",
                    // CORRECTED: Explicitly use .value for calculation
                    fontSize = (fontSize.value - 4).sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }

    // --- A simple header for each group of letters ---
    @Composable
    fun SectionHeader(title: String, fontSize: TextUnit) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            // CORRECTED: Explicitly use .value for calculation
            fontSize = (fontSize.value + 4).sp,
            fontFamily = OdiaFont,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Divider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("ଲିପ୍ୟନ୍ତର", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Font Size Control with Limits
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ଅକ୍ଷର ଆକାର:", fontFamily = OdiaFont, fontSize = 14.sp)
                        IconButton(
                            onClick = { contentFontSize = (contentFontSize.value - 1).sp },
                            enabled = contentFontSize > minFontSize
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Font Size")
                        }
                        IconButton(
                            onClick = { contentFontSize = (contentFontSize.value + 1).sp },
                            enabled = contentFontSize < maxFontSize
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Font Size")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(16.dp)
                    .clip(cardShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lipyantara),
                    contentDescription = "Lipyantara",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = "ଲିପ୍ୟନ୍ତର କ'ଣ?",
                    fontSize = (contentFontSize.value + 6).sp,
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = contentFontSize, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f))) {
                        append("ଲିପ୍ୟନ୍ତର ଏକ ପ୍ରକ୍ରିୟା ଯେଉଁଥିରେ ଗୋଟିଏ ଲିପିର ଅକ୍ଷରକୁ ଅନ୍ୟ ଏକ ଲିପିରେ ଲେଖାଯାଏ, ଯାହା ଧ୍ୱନି ଉପରେ ଆଧାରିତ । ଉଦାହରଣ ସ୍ୱରୂପ, ଆମ ଭାଷାକୁ ପୂର୍ବରୁ ଇଂରାଜୀରେ '")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Oriya")
                        }
                        append("' କୁହାଯାଉଥିଲା, କିନ୍ତୁ ବର୍ତ୍ତମାନ ଏହାର ସଠିକ ନାମ ହେଉଛି '")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Odia")
                        }
                        append("' । ଏହି ପରିବର୍ତ୍ତନ ଲିପ୍ୟନ୍ତରର ଏକ ସୁନ୍ଦର ଉଦାହରଣ । ଡିଜିଟାଲ ଯୁଗରେ ଏହାର ଗୁରୁତ୍ୱ ବହୁତ ବଢିଯାଇଛି ।")
                    }
                }
                Text(
                    text = annotatedString,
                    fontFamily = OdiaFont,
                    lineHeight = contentFontSize * 1.6f
                )

                // --- Vowels Section ---
                SectionHeader("ସ୍ୱରବର୍ଣ୍ଣ (Vowels)", contentFontSize)
                AlphabetEntry("ଅ", "a", "/ɔ/", contentFontSize)
                AlphabetEntry("ଆ", "aa", "/aː/", contentFontSize)
                AlphabetEntry("ଇ", "i", "/i/", contentFontSize)
                AlphabetEntry("ଈ", "ee", "/iː/", contentFontSize)
                AlphabetEntry("ଉ", "u", "/u/", contentFontSize)
                AlphabetEntry("ଊ", "oo", "/uː/", contentFontSize)
                AlphabetEntry("ଋ", "ru", "/ru/", contentFontSize)
                AlphabetEntry("ଏ", "e", "/e/", contentFontSize)
                AlphabetEntry("ଐ", "ai", "/ɔi̯/", contentFontSize)
                AlphabetEntry("ଓ", "o", "/o/", contentFontSize)
                AlphabetEntry("ଔ", "ou", "/ɔu̯/", contentFontSize)

                // --- Combining Signs Section ---
                SectionHeader("ଯୋଗ ବାହକ (Combining Signs)", contentFontSize)
                AlphabetEntry("ଅଂ", "an", "/ɔŋ/, /ɔn/", contentFontSize)
                AlphabetEntry("ଅଃ", "ah", "/ɔh/", contentFontSize)
                AlphabetEntry("ଅଁ", "an / am̐ (nasal)", "/ɔ̃/", contentFontSize)

                // --- Consonants Section (Simple List) ---
                SectionHeader("ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ (Consonants)", contentFontSize)

                // Ka Series
                AlphabetEntry("କ", "ka", "/kɔ/", contentFontSize)
                AlphabetEntry("ଖ", "kha", "/kʰɔ/", contentFontSize)
                AlphabetEntry("ଗ", "ga", "/ɡɔ/", contentFontSize)
                AlphabetEntry("ଘ", "gha", "/ɡʱɔ/", contentFontSize)
                AlphabetEntry("ଙ", "una / ṅa", "/ŋɔ/", contentFontSize)
                Spacer(modifier = Modifier.height(16.dp))

                // Cha Series
                AlphabetEntry("ଚ", "cha", "/t͡ʃɔ/", contentFontSize)
                AlphabetEntry("ଛ", "chha", "/t͡ʃʰɔ/", contentFontSize)
                AlphabetEntry("ଜ", "ja", "/d͡ʒɔ/", contentFontSize)
                AlphabetEntry("ଝ", "jha", "/d͡ʒʱɔ/", contentFontSize)
                AlphabetEntry("ଞ", "nya / ña", "/ɲɔ/", contentFontSize)
                Spacer(modifier = Modifier.height(16.dp))

                // Ta (Retroflex) Series
                AlphabetEntry("ଟ", "Ta", "/ʈɔ/", contentFontSize)
                AlphabetEntry("ଠ", "Tha", "/ʈʰɔ/", contentFontSize)
                AlphabetEntry("ଡ", "Da", "/ɖɔ/", contentFontSize)
                AlphabetEntry("ଢ", "Dha", "/ɖʱɔ/", contentFontSize)
                AlphabetEntry("ଣ", "Na", "/ɳɔ/", contentFontSize)
                Spacer(modifier = Modifier.height(16.dp))

                // ta (Dental) Series
                AlphabetEntry("ତ", "ta", "/t̪ɔ/", contentFontSize)
                AlphabetEntry("ଥ", "tha", "/t̪ʰɔ/", contentFontSize)
                AlphabetEntry("ଦ", "da", "/d̪ɔ/", contentFontSize)
                AlphabetEntry("ଧ", "dha", "/d̪ʱɔ/", contentFontSize)
                AlphabetEntry("ନ", "na", "/n̪ɔ/", contentFontSize)
                Spacer(modifier = Modifier.height(16.dp))

                // pa Series
                AlphabetEntry("ପ", "pa", "/pɔ/", contentFontSize)
                AlphabetEntry("ଫ", "pha / fa", "/pʰɔ/", contentFontSize)
                AlphabetEntry("ବ", "ba", "/bɔ/", contentFontSize)
                AlphabetEntry("ଭ", "bha / va", "/bʱɔ/", contentFontSize)
                AlphabetEntry("ମ", "ma", "/mɔ/", contentFontSize)

                // --- Other Letters Section ---
                AlphabetEntry("ଯ", "ja", "/d͡ʒɔ/", contentFontSize)
                AlphabetEntry("ର", "ra", "/rɔ/", contentFontSize)
                AlphabetEntry("ଲ", "la", "/lɔ/", contentFontSize)
                AlphabetEntry("ଳ", "La", "/ɭɔ/", contentFontSize)
                AlphabetEntry("ୱ", "wa", "/wɔ/, /vɔ/", contentFontSize)
                AlphabetEntry("ଶ", "sha", "/sɔ/, /ʃɔ/", contentFontSize)
                AlphabetEntry("ଷ", "sha", "/sɔ/", contentFontSize)
                AlphabetEntry("ସ", "sa", "/sɔ/", contentFontSize)
                AlphabetEntry("ହ", "ha", "/hɔ/", contentFontSize)
                AlphabetEntry("କ୍ଷ", "ksha / khya", "/kʰjɔ/", contentFontSize)
                AlphabetEntry("ୟ", "ya", "/jɔ/", contentFontSize)
                AlphabetEntry("ଡ଼", "Ra (flapped)", "/ɽɔ/", contentFontSize)
                AlphabetEntry("ଢ଼", "Rha (flapped)", "/ɽʱɔ/", contentFontSize)

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}