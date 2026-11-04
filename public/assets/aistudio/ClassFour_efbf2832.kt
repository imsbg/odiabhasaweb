// --- START OF FILE ClassFour.kt ---
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
fun SharedTransitionScope.ClassFourScreen(
    onBack: () -> Unit,
    onReadBook: (title: String, path: String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedBook by remember { mutableStateOf<SchoolBookData?>(null) }
    // A trigger to refresh the list items (checkmarks) when a file is downloaded or deleted
    var refreshListTrigger by remember { mutableStateOf(0) }

    // Class Four Books Data
    val books = listOf(

        SchoolBookData(
            id = "class4_bhasha_mahaka_21",
            title = "ଭାଷା ମହକ ୨ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/BHASA-MAHAKA-CLASS-4-S-DRAFT_11zon.pdf"
        ),
        SchoolBookData(
            id = "class4_ama_bichitra_biswa1",
            title = "ଆମ ବିଚିତ୍ର ବିଶ୍ଵ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Our_Wondrous-IV-P_1-176_Web_DRAFT_11zon.pdf"
        ),
        SchoolBookData(
            id = "class4_ganita_mela1",
            title = "ଗଣିତ ମେଳା (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Mathe_Mela-Final-240-Class-4-Web_11zon.pdf"
        ),
        SchoolBookData(
            id = "class4_indradhanu1",
            title = "ଇନ୍ଦ୍ରଧନୁ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Indradhanu_Class-4_11zon.pdf"
        ),
        SchoolBookData(
            id = "class4_krida_o_joga1",
            title = "କ୍ରିଡ଼ା ଓ ଯୋଗ (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Physcial_Class_4_Final_Web_2_11zon.pdf"
        ),
        SchoolBookData(
            id = "class4_pallavi1",
            title = "Pallavi (ସଂଶୋଧିତ)",
            url = "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Pallavi_English_Class-4_11zon.pdf"
        ),

        SchoolBookData(
            id = "class4_ama_bichitra_biswa",
            title = "ଆମ ବିଚିତ୍ର ବିଶ୍ଵ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Ama.Bichitra.Biswa.pdf"
        ),
        SchoolBookData(
            id = "class4_bhasha_mahaka_2",
            title = "ଭାଷା ମହକ ୨ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Bhasa.Mahak.pdf"
        ),
        SchoolBookData(
            id = "class4_ganita_mela",
            title = "ଗଣିତ ମେଳା (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.GANITA.MELAMath.pdf"
        ),
        SchoolBookData(
            id = "class4_indradhanu",
            title = "ଇନ୍ଦ୍ରଧନୁ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Indradhanu.pdf"
        ),
        SchoolBookData(
            id = "class4_krida_o_joga",
            title = "କ୍ରିଡ଼ା ଓ ଯୋଗ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Krida.o.Yoga.pdf"
        ),
        SchoolBookData(
            id = "class4_pallavi",
            title = "Pallavi (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Pallavi.English.pdf"
        ),
        SchoolBookData(
            id = "class4_ganitakuhuka",
            title = "ଗଣିତ କୁହୁକ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IV/Ganita-Kuhuka-IV-P-1-208.pdf"
        ),
        SchoolBookData(
            id = "class4_sahityakalika",
            title = "ସାହିତ୍ୟ କଳିକା",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IV/Sahitya-Kalika-IV-P-1-112.pdf"
        ),
        SchoolBookData(
            id = "class4_ameoamaparibesa",
            title = "ଆମେ ଓ ଆମ ପରିବେଶ",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IV/Ame-O-Ama-Paribesa-IV-P-1-208.pdf"
        ),
        SchoolBookData(
            id = "class4_english",
            title = "MY ENGLISH BOOK",
            url = "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IV/English-IV-P-1-136.pdf"
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
                        text = "ଚତୁର୍ଥ ଶ୍ରେଣୀ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "class_title_ଚତୁର୍ଥ ଶ୍ରେଣୀ"), // EXACT MATCH TO LIST TITLE
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
// --- END OF FILE ClassFour.kt ---