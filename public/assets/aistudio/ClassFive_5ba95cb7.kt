// --- START OF FILE ClassFive.kt ---
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
fun SharedTransitionScope.ClassFiveScreen(
    onBack: () -> Unit,
    onReadBook: (title: String, path: String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedBook by remember { mutableStateOf<SchoolBookData?>(null) }
    // A trigger to refresh the list items (checkmarks) when a file is downloaded or deleted
    var refreshListTrigger by remember { mutableStateOf(0) }

    // Class Five Books Data
    val books = listOf(
        SchoolBookData(
            id = "class5_bhashamahaka_31", // added '1' to make ID unique for revised vs new if needed, assuming uniqueness is handled
            title = "ଭାଷାମହକ ୩ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/bhasa-mahak-v.pdf"
        ),
        SchoolBookData(
            id = "class5_ama_bichitra_biswa1",
            title = "ଆମ ବିଚିତ୍ର ବିଶ୍ଵ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/our-earth-v.pdf"
        ),
        SchoolBookData(
            id = "class5_khela_o_joga1",
            title = "ଖେଳ ଓ ଯୋଗ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/play-v.pdf"
        ),
        SchoolBookData(
            id = "class5_ganita_mela1",
            title = "ଗଣିତ ମେଳା (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/math-v.pdf"
        ),
        SchoolBookData(
            id = "class5_nabarasa1",
            title = "ନବରସ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/nabaras-v.pdf"
        ),
        SchoolBookData(
            id = "class5_pallavi1",
            title = "Pallavi (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Pallavi_(Class-V).pdf"
        ),

        SchoolBookData(
            id = "class5_bhashamahaka_3",
            title = "ଭାଷାମହକ ୩ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.BHASHA.MAHAKOdia.pdf"
        ),
        SchoolBookData(
            id = "class5_ama_bichitra_biswa",
            title = "ଆମ ବିଚିତ୍ର ବିଶ୍ଵ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.Ama.Bichitra.Biswa.pdf"
        ),
        SchoolBookData(
            id = "class5_khela_o_joga",
            title = "ଖେଳ ଓ ଯୋଗ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.Khela.o.joga.pdf"
        ),
        SchoolBookData(
            id = "class5_ganita_mela",
            title = "ଗଣିତ ମେଳା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.Ganita.MelaMath.pdf"
        ),
        SchoolBookData(
            id = "class5_nabarasa",
            title = "ନବରସ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.Nabarasa.pdf"
        ),
        SchoolBookData(
            id = "class5_pallavi",
            title = "Pallavi (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.ENGLISH.PALLAVI.pdf"
        ),
        SchoolBookData(
            id = "class5_sahityakusum",
            title = "ସାହିତ୍ୟ କୁସୁମ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-V/Sahitya-Kusum-Cl-V-(B)-P-1-144.pdf"
        ),
        SchoolBookData(
            id = "class5_ganitadhara",
            title = "ଗଣିତ ଧାରା",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-V/Ganita-Dhara-V-P-1-216.pdf"
        ),
        SchoolBookData(
            id = "class5_english",
            title = "MY ENGLISH BOOK",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-V/English-V-P-1-128.pdf"
        ),
        SchoolBookData(
            id = "class5_paribesabigyan",
            title = "ପରିବେଶ ବିଜ୍ଞାନ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-V/Paribesa-Bigyan-V-P-1-208.pdf"
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
                        text = "ପଞ୍ଚମ ଶ୍ରେଣୀ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "class_title_ପଞ୍ଚମ ଶ୍ରେଣୀ"), // EXACT MATCH TO LIST TITLE
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

                // Identify if book is "New" or "Revised"
                val isNew = book.title.contains("(ନୂତନ)")
                val isRevised = book.title.contains("(ସଂଶୋଧିତ)")

                // Styling logic based on the tag
                val containerBg = when {
                    isNew -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    isRevised -> Color(0xFF4CAF50).copy(alpha = 0.15f) // Light Green background
                    else -> MaterialTheme.colorScheme.surface
                }

                val outlineColor = when {
                    isNew -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    isRevised -> Color(0xFF4CAF50).copy(alpha = 0.6f) // Green Outline
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                }

                val outlineWidth = if (isNew || isRevised) 2.dp else 1.dp

                val iconTint = when {
                    isNew -> MaterialTheme.colorScheme.primary
                    isRevised -> Color(0xFF2E7D32) // Dark Green Icon
                    else -> MaterialTheme.colorScheme.secondary
                }

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
                        tint = iconTint,
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
// --- END OF FILE ClassFive.kt ---