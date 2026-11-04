// --- START OF FILE ClassTen.kt ---
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
fun SharedTransitionScope.ClassTenScreen(
    onBack: () -> Unit,
    onReadBook: (title: String, path: String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedBook by remember { mutableStateOf<SchoolBookData?>(null) }
    // A trigger to refresh the list items (checkmarks) when a file is downloaded or deleted
    var refreshListTrigger by remember { mutableStateOf(0) }

    // Class Ten Books Data
    val books = listOf(
        SchoolBookData("class10_sahityasindhu", "ସାହିତ୍ୟ ସିନ୍ଧୁ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/10th-01A-MIL_Odia_Class_X.pdf"),
        SchoolBookData("class10_madhyamikabyakarana", "ମାଧ୍ୟମିକ ବ୍ୟାକରଣ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/92%20-%20OD_ODIA_ODIAGRAMMAR_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_communicativeenglish", "Skills of Communicative English", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/10th-02A-English_Text_Class_X.pdf"),
        SchoolBookData("class10_englishgrammar", "Learn & Practice Grammer", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/94%20-%20OD_ENGLISH_ENGLISHGRAMMAR_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_sanskritasahitya", "ସଂସ୍କୃତ ସାହିତ୍ୟ ମଞ୍ଜରୀ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/95%20-%20OD_SANSKRIT_SANSKRITLANGUAGE_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_sanskritgrammar", "ସଂସ୍କୃତ ବ୍ୟାକରଣ ପ୍ରକାଶଃ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/96%20-%20OD_ODIA_SANSKRITGRAMMAR_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_bijaganita", "ମାଧ୍ୟମିକ ବୀଜଗଣିତ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/99%20-%20OD_ODIA_MATH_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_geometry", "ମାଧ୍ୟମିକ ଜ୍ୟାମିତି", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/100%20-%20OD_ODIA_GEOMETRY_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_physicalscience", "ଭୌତିକ ବିଜ୍ଞାନ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/101%20-%20OD_ODIA_PHYSICALSCIENCE_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_lifescience", "ଜୀବ ବିଜ୍ଞାନ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/102%20-%20OD_ODIA_LIFESCIENCE_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_historypolitics", "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/103%20-%20OD_ODIA_HISTORYCIVICS_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_geoeco", "ଭୂଗୋଳ ଓ ଅର୍ଥନୀତି", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/104%20-%20OD_ODIA_GEOGRAPHYECONOMICS_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_geoeco_addl", "ଭୂଗୋଳ ଓ ଅର୍ଥନୀତି (ଅତିରିକ୍ତ ପାଠ୍ୟକ୍ରମ)", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/10th-06B-Geography(addl)%20Class%20X.pdf"),
        SchoolBookData("class10_hindisourabh", "हिन्दी सौरभ", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/97%20-%20OD_HINDI_HINDI_CLASS10_TERM1.pdf"),
        SchoolBookData("class10_hindigrammar", "माध्यमिक हिन्दी व्याकरण", "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/98%20-%20OD_HINDI_HINDIGRAMMAR_CLASS10_TERM1.pdf"),
        SchoolBookData(
            id = "class10_paryatana_o_atithyara_parichaya",
            title = "ପର୍ଯ୍ୟଟନ ଓ ଆତିଥ୍ୟର ପରିଚୟ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.X.-.Vocational.Tourism.pdf"
        ),
        SchoolBookData(
            id = "class10_krushi_bigyana",
            title = "କୃଷି ବିଜ୍ଞାନ (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.X.-.Vocational.Agriculture.pdf"
        ),
        SchoolBookData(
            id = "class10_automotive",
            title = "AUTOMOTIVE (ନୂତନ)",
            url = "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.X.-.Vocational.Automotive.pdf"
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
                        text = "ଦଶମ ଶ୍ରେଣୀ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "class_title_ଦଶମ ଶ୍ରେଣୀ"), // EXACT MATCH TO LIST TITLE
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
// --- END OF FILE ClassTen.kt ---