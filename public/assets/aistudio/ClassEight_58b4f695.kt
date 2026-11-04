// --- START OF FILE ClassEight.kt ---
package `in`.sandeep.odiabhasa

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ClassEightScreen(
    onBack: () -> Unit,
    onReadBook: (title: String, path: String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedBook by remember { mutableStateOf<SchoolBookData?>(null) }
    // A trigger to refresh the list items (checkmarks) when a file is downloaded or deleted
    var refreshListTrigger by remember { mutableStateOf(0) }

    // Class Eight Books Data
    val books = listOf(
        SchoolBookData(
            id = "class8_sahitya_surabhi",
            title = "ସାହିତ୍ୟସୁରଭି (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Sahitya.Surabhi.pdf"
        ),
        SchoolBookData(
            id = "class8_ganita_prakasha",
            title = "ଗଣିତ ପ୍ରକାଶ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Math.pdf"
        ),
        SchoolBookData(
            id = "class8_jignyasa",
            title = "ଜିଜ୍ଞାସା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Jingyasha.Science.pdf"
        ),
        SchoolBookData(
            id = "class8_samajika_bigyana",
            title = "ସାମାଜିକ ବିଜ୍ଞାନ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Samajika.Bingyana.pdf"
        ),
        SchoolBookData(
            id = "class8_kruti",
            title = "କୃତି (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.KRUTI.pdf"
        ),
        SchoolBookData(
            id = "class8_kaushala_bodha",
            title = "କୌଶଳ ବୋଧ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Koushalabodha.pdf"
        ),
        SchoolBookData(
            id = "class8_jasmine",
            title = "Jasmine (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Jasmine.English.pdf"
        ),
        SchoolBookData(
            id = "class8_sanskrit_kalika",
            title = "संस्कृतकलिका (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.SANSKRUTA.KALIKA.pdf"
        ),
        SchoolBookData(
            id = "class8_hindi_kalika",
            title = "हिन्दी कलिका (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Hindi.Kalika.pdf"
        ),
        SchoolBookData("class8_sahitika", "ସାହିତ୍ୟିକା", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Sahitika-8th.pdf"),
        SchoolBookData("class8_odiabyakarana", "ଆମ ଓଡ଼ିଆ ବ୍ୟାକରଣ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Ama-Odia-Byakarana-Final-104.pdf"),
        SchoolBookData("class8_saralasahitya", "ସରଳ ସାହିତ୍ୟ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Sarala-Sahitya-VIII-P-1-104.pdf"),
        SchoolBookData("class8_bigyana", "ବିଜ୍ଞାନ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Science-(-1-240-)-pg.pdf"),
        SchoolBookData("class8_bijaganita", "ସରଳ ଗଣିତ (ବୀଜ ଗଣିତ)", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Sarala-Bija-Ganita-(-1-192-)-pg.pdf"),
        SchoolBookData("class8_geometry", "ସରଳ ଗଣିତ (ଜ୍ୟାମିତି)", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Sarala-Geometry.pdf"),
        SchoolBookData("class8_bhugola", "ଭୂଗୋଳ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Geography.pdf"),
        SchoolBookData("class8_historypolitics", "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/History-&-Political-Science-8.pdf"),
        SchoolBookData("class8_newapproachenglish", "A New Approach to English", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/A-New-Approach-to-English-(-1-168-).pdf"),
        SchoolBookData("class8_storiespastpresent", "Stories Past & Present", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Stories-Past-and-Present.pdf"),
        SchoolBookData("class8_bhasasouravam", "ଭାଷା ସୌରଭମ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Bhasa-Souravam-8.pdf"),
        SchoolBookData("class8_hindipatha", "ହିନ୍ଦୀ ପାଠ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Hindi-Patha-VIII-P-1-120.pdf"),
        SchoolBookData("class8_saralahindibyakarana", "ସରଳ ହିନ୍ଦୀ ବ୍ୟାକରଣ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Saral-Hindi-Byakaran-Class-8.pdf")
    )

// Reusing the upgraded BookDownloadDialog from ClassOne.kt
    if (selectedBook != null) {
        BookDownloadDialog(
            book = selectedBook!!,
            onDismiss = { selectedBook = null },
            onRead = { title, filePath ->
                selectedBook = null
                onReadBook(title, filePath) // Passes both Title and Path!
            },
            onFileChanged = {
                refreshListTrigger++ // Triggers re-composition for checkmarks
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // ANIMATION ADDED HERE!
                    Text(
                        text = "ଅଷ୍ଟମ ଶ୍ରେଣୀ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "class_title_ଅଷ୍ଟମ ଶ୍ରେଣୀ"), // EXACT MATCH TO LIST TITLE
                            animatedVisibilityScope = animatedVisibilityScope,
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ଫେରନ୍ତୁ")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp)
        ) {
            items(books.size) { index ->
                val book = books[index]
                val context = LocalContext.current
                val file = File(context.filesDir, "school_books/${book.id}.pdf")

                // Real-time check if the book is downloaded
                val isDownloaded = remember(book, refreshListTrigger) {
                    file.exists() && file.length() > 0
                }

                // Check if title contains "(ନୂତନ)" to give a unique visual style
                val isNew = book.title.contains("(ନୂତନ)")
                val containerBg = if (isNew) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                val outlineColor = if (isNew) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                val outlineWidth = if (isNew) 2.dp else 1.dp

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .border(outlineWidth, outlineColor, RoundedCornerShape(15.dp))
                        .background(containerBg)
                        .bouncingClickable {
                            selectedBook = book
                        }
                        .padding(16.dp, 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = if (isNew) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = book.title,
                        fontSize = 18.sp,
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // Display Checkmark if downloaded, otherwise normal arrow
                    if (isDownloaded) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "ଡାଉନଲୋଡ୍ ହୋଇଛି", tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }
        }
    }
}
// --- END OF FILE ClassEight.kt ---