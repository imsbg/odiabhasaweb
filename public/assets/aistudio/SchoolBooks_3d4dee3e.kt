// --- START OF FILE SchoolBooks.kt ---
package `in`.sandeep.odiabhasa

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Cache variables to remember scroll state across screen changes
private var savedScrollIndex = 0
private var savedScrollOffset = 0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SchoolBooksScreen(
    onBack: () -> Unit,
    onClassClick: (Screen) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val classes = listOf(
        "ପ୍ରଥମ ଶ୍ରେଣୀ" to Screen.CLASS_1,
        "ଦ୍ଵିତୀୟ ଶ୍ରେଣୀ" to Screen.CLASS_2,
        "ତୃତୀୟ ଶ୍ରେଣୀ" to Screen.CLASS_3,
        "ଚତୁର୍ଥ ଶ୍ରେଣୀ" to Screen.CLASS_4,
        "ପଞ୍ଚମ ଶ୍ରେଣୀ" to Screen.CLASS_5,
        "ଷଷ୍ଠ ଶ୍ରେଣୀ" to Screen.CLASS_6,
        "ସପ୍ତମ ଶ୍ରେଣୀ" to Screen.CLASS_7,
        "ଅଷ୍ଟମ ଶ୍ରେଣୀ" to Screen.CLASS_8,
        "ନବମ ଶ୍ରେଣୀ" to Screen.CLASS_9,
        "ଦଶମ ଶ୍ରେଣୀ" to Screen.CLASS_10
    )

    var showInfoDialog by remember { mutableStateOf(false) }
    val backgroundColor = Color(0xFFFAF8F5)

    // Restore the lazy list state from our saved variables
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = savedScrollIndex,
        initialFirstVisibleItemScrollOffset = savedScrollOffset
    )

    // Continuously save the scroll position so it is never lost
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                savedScrollIndex = index
                savedScrollOffset = offset
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "ବିଦ୍ୟାଳୟ ବହି",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(42.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .clip(CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(42.dp)
                            .background(Color(0xFFFFD54F), CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .clip(CircleShape)
                            .clickable { showInfoDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        LazyColumn(
            state = listState, // Attach the state here!
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            items(classes.size) { index ->
                val (title, screen) = classes[index]
                ClassCard(
                    title = title,
                    index = index,
                    onClick = { onClassClick(screen) },
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }

        // Info Dialog
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                modifier = Modifier.border(4.dp, Color.Black, RoundedCornerShape(20.dp)),
                containerColor = Color(0xFFFFFDF0),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "ସୂଚନା",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "ଏହି ଆପ୍ ଓଡ଼ିଆ ଭାଷାର ପ୍ରସାର ପାଇଁ ସନ୍ଦୀପ୍ ବିଶ୍ୱାଳ ଜି'ଙ୍କ ଦ୍ୱାରା ଏକ ସ୍ୱତନ୍ତ୍ର ଶିକ୍ଷାଗତ ପ୍ରୟାସ । ଏହା ଓଡ଼ିଶା ବିଦ୍ୟାଳୟ ଶିକ୍ଷା କାର୍ଯ୍ୟକ୍ରମ ପ୍ରାଧିକରଣ (OSEPA) କିମ୍ବା ଓଡ଼ିଶା ସରକାରଙ୍କ କୌଣସି ଅଫିସିଆଲ୍ ଆପ୍ ନୁହେଁ ।\n\nଏଠାରେ ଉପଲବ୍ଧ ପାଠ୍ୟପୁସ୍ତକଗୁଡ଼ିକ OSEPA ର ଅଫିସିଆଲ୍ ପୋର୍ଟାଲ୍ (osepa.odisha.gov.in) ରୁ ସଂଗ୍ରହ କରାଯାଇଛି । ଏହି ପାଠ୍ୟପୁସ୍ତକ ବିଷୟବସ୍ତୁର ସମସ୍ତ କପିରାଇଟ୍ ଏବଂ ମାଲିକାନା ବିଦ୍ୟାଳୟ ଓ ଗଣଶିକ୍ଷା ବିଭାଗ, ଓଡ଼ିଶା ସରକାରଙ୍କ ଅଧୀନରେ ଅଛି । ଏହି ଆପ୍ ସମ୍ପୂର୍ଣ୍ଣ ମାଗଣା ଏବଂ କେବଳ ଛାତ୍ରଛାତ୍ରୀଙ୍କ ଶିକ୍ଷା ଉଦ୍ଦେଶ୍ୟରେ ନିର୍ମିତ ।",
                        fontFamily = OdiaFont,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp,
                        color = Color.DarkGray
                    )
                },
                confirmButton = {
                    val btnInteractionSource = remember { MutableInteractionSource() }
                    val isBtnPressed by btnInteractionSource.collectIsPressedAsState()
                    val btnPressOffset by animateDpAsState(targetValue = if (isBtnPressed) 4.dp else 0.dp)

                    Box(
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 8.dp)
                            .clickable(interactionSource = btnInteractionSource, indication = null, onClick = { showInfoDialog = false })
                    ) {
                        Box(modifier = Modifier.matchParentSize().offset(x = 4.dp, y = 4.dp).background(Color.Black, RoundedCornerShape(12.dp)))
                        Box(
                            modifier = Modifier
                                .offset(x = btnPressOffset, y = btnPressOffset)
                                .background(Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                                .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                .padding(horizontal = 30.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "ବୁଝିଗଲି", fontFamily = OdiaFont, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                        }
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ClassCard(
    title: String,
    index: Int,
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val comicColors = listOf(
        Color(0xFF00E5FF), Color(0xFFFFE135), Color(0xFFFF66CC), Color(0xFFB2FF59), Color(0xFFFF9D00)
    )

    val cardColor = comicColors[index % comicColors.size]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(targetValue = if (isPressed) 6.dp else 0.dp, label = "pressAnimation")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, end = 6.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Box(modifier = Modifier.matchParentSize().offset(x = 6.dp, y = 6.dp).background(Color.Black, RoundedCornerShape(16.dp)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = pressOffset, y = pressOffset)
                .background(cardColor, RoundedCornerShape(16.dp))
                .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
                    .rotate(-10f),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color.Black, modifier = Modifier.size(26.dp))
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Text with Shared Element Bounds Transition
            Text(
                text = title,
                fontSize = 24.sp,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "class_title_$title"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
            )

            Box(
                modifier = Modifier.size(36.dp).background(Color.White, CircleShape).border(2.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
            }
        }
    }
}
// --- END OF FILE SchoolBooks.kt ---