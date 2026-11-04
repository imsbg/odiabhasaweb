// --- START OF FILE ClassSix.kt ---
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
fun SharedTransitionScope.ClassSixScreen(
    onBack: () -> Unit,
    onReadBook: (title: String, path: String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedBook by remember { mutableStateOf<SchoolBookData?>(null) }
    // A trigger to refresh the list items (checkmarks) when a file is downloaded or deleted
    var refreshListTrigger by remember { mutableStateOf(0) }

    // Class Six Books Data
    val books = listOf(
        SchoolBookData(
            id = "class6_sahitya_sudha",
            title = "ସାହିତ୍ୟସୁଧା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Sahitya.Sudha.pdf"
        ),
        SchoolBookData(
            id = "class6_ganita_prakasha",
            title = "ଗଣିତ ପ୍ରକାଶ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Math.pdf"
        ),
        SchoolBookData(
            id = "class6_samajika_bigyana",
            title = "ସାମାଜିକ ବିଜ୍ଞାନ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Social.Science.pdf"
        ),
        SchoolBookData(
            id = "class6_kaushala_bodha",
            title = "କୌଶଳ ବୋଧ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Koshalbodha.pdf"
        ),
        SchoolBookData(
            id = "class6_khela_shikshya",
            title = "ଖେଳ ଶିକ୍ଷା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Khela.Sahitya.pdf"
        ),
        SchoolBookData(
            id = "class6_kalakunja",
            title = "କଳାକୁଞ୍ଜ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Kalakunja.pdf"
        ),
        SchoolBookData(
            id = "class6_jignyasa",
            title = "ଜିଜ୍ଞାସା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Jingyasha.pdf"
        ),
        SchoolBookData(
            id = "class6_jasmine",
            title = "Jasmine (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.English.Jasmine.pdf"
        ),
        SchoolBookData(
            id = "class6_sanskrit_kalika",
            title = "संस्कृतकलिका (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Sanaskruta.Kalika.1.pdf"
        ),
        SchoolBookData(
            id = "class6_hindi_kalika",
            title = "हिन्दी कलिका (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Hindi.Kalika.pdf"
        ),
        SchoolBookData(
            id = "class6_amasahitya",
            title = "ଆମ ସାହିତ୍ୟ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Ama-Sahitya-Page-(-1-120-)-pg.pdf"
        ),
        SchoolBookData(
            id = "class6_ganita",
            title = "ଗଣିତ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Math-Page-6-(1-240-).pdf"
        ),
        SchoolBookData(
            id = "class6_bigyana",
            title = "ବିଜ୍ଞାନ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Science-(-1-104-)-pg.pdf"
        ),
        SchoolBookData(
            id = "class6_bhugola",
            title = "ଭୂଗୋଳ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Geography-Vi-104.pdf"
        ),
        SchoolBookData(
            id = "class6_itihasa",
            title = "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/History_Final_136.pdf"
        ),
        SchoolBookData(
            id = "class6_computer",
            title = "କମ୍ପ୍ୟୁଟର ଶିକ୍ଷା ଓ ପ୍ରୟୋଗ (ପ୍ରଥମ ଭାଗ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Computer-Sikshya-Part-1-(1-88page).pdf"
        ),
        SchoolBookData(
            id = "class6_english",
            title = "MY ENGLISH BOOK",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Eng-Vi-Page-1-152.pdf"
        ),
        SchoolBookData(
            id = "class6_bhasakalika",
            title = "ଭାଷା କଳିକା",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Bhasa-Kalika-VI-1-144-pg.pdf"
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
                        text = "ଷଷ୍ଠ ଶ୍ରେଣୀ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "class_title_ଷଷ୍ଠ ଶ୍ରେଣୀ"), // EXACT MATCH TO LIST TITLE
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
// --- END OF FILE ClassSix.kt ---