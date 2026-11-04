// --- START OF FILE ClassSeven.kt ---
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
fun SharedTransitionScope.ClassSevenScreen(
    onBack: () -> Unit,
    onReadBook: (title: String, path: String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedBook by remember { mutableStateOf<SchoolBookData?>(null) }
    // A trigger to refresh the list items (checkmarks) when a file is downloaded or deleted
    var refreshListTrigger by remember { mutableStateOf(0) }

    // Class Seven Books Data
    val books = listOf(
        SchoolBookData(
            id = "class7_sahityasumana",
            title = "ସାହିତ୍ୟସୁମନ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Odia.pdf"
        ),
        SchoolBookData(
            id = "class7_jignyasa",
            title = "ଜିଜ୍ଞାସା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Jingyasha.pdf"
        ),
        SchoolBookData(
            id = "class7_samajika_bigyana",
            title = "ସାମାଜିକ ବିଜ୍ଞାନ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Social.Science.pdf"
        ),
        SchoolBookData(
            id = "class7_khela_shikshya",
            title = "ଖେଳ ଶିକ୍ଷା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Khela.Shikhya.pdf"
        ),
        SchoolBookData(
            id = "class7_kaushala_bodha",
            title = "କୌଶଳ ବୋଧ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Koshalbodha.pdf"
        ),
        SchoolBookData(
            id = "class7_ganita_prakasha",
            title = "ଗଣିତ ପ୍ରକାଶ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Ganita.Prakash.pdf"
        ),
        SchoolBookData(
            id = "class7_kalakruti",
            title = "କଳାକୃତି (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Kalakruti.pdf"
        ),
        SchoolBookData(
            id = "class7_jasmine",
            title = "Jasmine (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII.-.JASMINE-ENGLISH.pdf"
        ),
        SchoolBookData(
            id = "class7_sanskrit_kalika",
            title = "संस्कृतकलिका (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Sanskruta.Kalika.2.pdf"
        ),
        SchoolBookData(
            id = "class7_hindi_kalika",
            title = "हिन्दी कलिका (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII.-.Hindi.Kalika.2.pdf"
        ),
        SchoolBookData(
            id = "class7_sahityasaurabh",
            title = "ସାହିତ୍ୟ ସୌରଭ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Sahitya-Saurav-VII-P-1-160_compressed.pdf"
        ),
        SchoolBookData(
            id = "class7_ganita",
            title = "ଗଣିତ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Mathematics-VII-P-1-248_compressed.pdf"
        ),
        SchoolBookData(
            id = "class7_itihasa",
            title = "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Hist-&-Pol-VII-P-1-128_compressed.pdf"
        ),
        SchoolBookData(
            id = "class7_bhugola",
            title = "ଭୂଗୋଳ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Geography-VII-P-1-120_compressed.pdf"
        ),
        SchoolBookData(
            id = "class7_bigyana",
            title = "ବିଜ୍ଞାନ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Science-VII-P-1-228_compressed.pdf"
        ),
        SchoolBookData(
            id = "class7_english",
            title = "ENGLISH READER",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/English-VII-P-1-144_compressed.pdf"
        ),
        SchoolBookData(
            id = "class7_bhasakalika2",
            title = "ଭାଷା କଳିକା ଭାଗ - ୨",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Bhasa-Kalika-VII-P-1-112_compressed.pdf"
        ),
        SchoolBookData(
            id = "class7_computer2",
            title = "କମ୍ପ୍ୟୁଟର ଶିକ୍ଷା ଓ ପ୍ରୟୋଗ (ଦ୍ଵିତୀୟ ଭାଗ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Computer-Book-1-120page_compressed.pdf"
        )
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
                        text = "ସପ୍ତମ ଶ୍ରେଣୀ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "class_title_ସପ୍ତମ ଶ୍ରେଣୀ"), // EXACT MATCH TO LIST TITLE
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
// --- END OF FILE ClassSeven.kt ---