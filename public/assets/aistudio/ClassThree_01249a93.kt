// --- START OF FILE ClassThree.kt ---
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
fun SharedTransitionScope.ClassThreeScreen(
    onBack: () -> Unit,
    onReadBook: (title: String, path: String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedBook by remember { mutableStateOf<SchoolBookData?>(null) }
    // A trigger to refresh the list items (checkmarks) when a file is downloaded or deleted
    var refreshListTrigger by remember { mutableStateOf(0) }

    // Class Three Books Data
    val books = listOf(
        SchoolBookData(
            id = "class3_bhasha_mahaka_11",
            title = "ଭାଷା ମହକ ୧ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Class-3_Bhasa_Mahaka_11zon.pdf"
        ),
        SchoolBookData(
            id = "class3_bichitra_ama_pruthibi1",
            title = "ବିଚିତ୍ର ଆମ ପୃଥିବୀ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Bichitra_Pruthibi-III-P_1-176_Web_DRAFT_11zon.pdf"
        ),
        SchoolBookData(
            id = "class3_ganita_mela1",
            title = "ଗଣିତ ମେଳା (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Ganita_Mela-3-Final-216%20-Class-III-Web_11zon.pdf"
        ),
        SchoolBookData(
            id = "class3_indradhanu1",
            title = "ଇନ୍ଦ୍ରଧନୁ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Class-3_Indradhanu(1)_11zon.pdf"
        ),
        SchoolBookData(
            id = "class3_khela_o_joga1",
            title = "ଖେଳ ଓ ଯୋଗ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Khel_Yogo-III_Inn-P_1-144_Web_DRAFT_11zon.pdf"
        ),
        SchoolBookData(
            id = "class3_pallavi1",
            title = "Pallavi (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Pallavi-III_Eng-Final_168_Web_DRAFT_11zon.pdf"
        ),

        SchoolBookData(
            id = "class3_bhasha_mahaka_1",
            title = "ଭାଷା ମହକ ୧ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.Bhasa.MahakOdia.pdf"
        ),
        SchoolBookData(
            id = "class3_bichitra_ama_pruthibi",
            title = "ବିଚିତ୍ର ଆମ ପୃଥିବୀ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.Bichitra.Ama.Pruthibi.pdf"
        ),
        SchoolBookData(
            id = "class3_ganita_mela",
            title = "ଗଣିତ ମେଳା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.GANITA.MELA-3.pdf"
        ),
        SchoolBookData(
            id = "class3_indradhanu",
            title = "ଇନ୍ଦ୍ରଧନୁ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.INDRADHANU-3.pdf"
        ),
        SchoolBookData(
            id = "class3_khela_o_joga",
            title = "ଖେଳ ଓ ଯୋଗ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.KHELA.O.YOGA.pdf"
        ),
        SchoolBookData(
            id = "class3_pallavi",
            title = "Pallavi (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.Pallavi.English.pdf"
        ),
        SchoolBookData(
            id = "class3_ganitamaja",
            title = "ଗଣିତ ମଜା",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-III/Ganita-Moja-III-Inner-P-100.pdf"
        ),
        SchoolBookData(
            id = "class3_amabhasa",
            title = "ଆମ ଭାଷା ଆମ ସାହିତ୍ୟ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-III/Amo-Bhasa-Amo-Sahitya-III-112-Pagee.pdf"
        ),
        SchoolBookData(
            id = "class3_janaajana",
            title = "ଜଣା ଅଜଣା",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-III/Jana-Ajana-III-Inner-P-1-146page.pdf"
        ),
        SchoolBookData(
            id = "class3_english",
            title = "MY ENGLISH BOOK",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-III/My-English-Book-III-Final.pdf"
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
                        text = "ତୃତୀୟ ଶ୍ରେଣୀ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "class_title_ତୃତୀୟ ଶ୍ରେଣୀ"), // EXACTLY MATCHES THE TEXT IN THE LIST
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
// --- END OF FILE ClassThree.kt ---