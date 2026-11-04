package `in`.sandeep.odiabhasa.ganita

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.sandeep.odiabhasa.OdiaFont

// --- MODIFIED DATA CLASS ---
// Now supports either an icon or text for the circle content
data class LearningOption(
    val title: String,
    val route: String,
    val color: Color,
    val icon: ImageVector? = null, // Made nullable
    val circleContentText: String? = null // New field for text
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningCategoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    // Animation Trigger
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // --- UPDATED LIST WITH TEXT CONTENT ---
    val learningList = remember {
        listOf(
            // 1. Panikia (Purple) - Keeps its icon
            LearningOption(
                title = "ପଣିକିଆ",
                route = "learning_panikia",
                icon = Icons.Default.MenuBook,
                color = Color(0xFFBA68C8)
            ),
            // 2. Ordinals (Deep Orange) - Uses text "୧ମ" instead of an icon
            LearningOption(
                title = "କ୍ରମିକ ସଂଖ୍ୟା",
                route = "learning_ordinals",
                circleContentText = "୧ମ",
                color = Color(0xFFFF8A65)
            ),
            // 3. Counting (Light Green) - Uses text "ଏକ" instead of an icon
            LearningOption(
                title = "ଗଣନା ପ୍ରଣାଳୀ",
                route = "learning_counting",
                circleContentText = "ଏକ",
                color = Color(0xFFAED581)
            )
        )
    }

    Scaffold(
        containerColor = Color(0xFFF3E5F5), // Very light purple background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ଗଣିତ ଶିକ୍ଷା",
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
                            .clickable(onClick = onNavigateBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {

            AnimatedVisibility(visible = isVisible, enter = fadeIn()) {
                Text(
                    text = "ଆସ ପଢିବା ଓ ଶିଖିବା!", // "Come let's read and learn!"
                    fontFamily = OdiaFont,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 24.dp, bottom = 10.dp, top = 8.dp)
                )
            }

            // Grid Layout
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = learningList,
                    span = { index, _ ->
                        // If it's the 3rd item (Index 2 -> Counting), make it span 2 columns (Full Width)
                        if (index == 2) GridItemSpan(2) else GridItemSpan(1)
                    }
                ) { index, item ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialScale = 0.5f
                        )
                    ) {
                        ComicLearningCard(
                            item = item,
                            onClick = { onNavigateToRoute(item.route) },
                            isFullWidth = index == 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComicLearningCard(
    item: LearningOption,
    onClick: () -> Unit,
    isFullWidth: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val offsetState by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        label = "offset"
    )

    val maxShadowDepth = 6.dp

    Box(
        modifier = Modifier
            .height(160.dp) // Height remains constant
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // 1. Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = maxShadowDepth, start = maxShadowDepth)
                .background(Color.Black, RoundedCornerShape(16.dp))
        )

        // 2. Main Color Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = maxShadowDepth, end = maxShadowDepth)
                .offset(x = offsetState, y = offsetState)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(item.color)
        ) {
            // Layout changes slightly if full width to look better
            if (isFullWidth) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // --- MODIFICATION FOR FULL-WIDTH CARD ---
                        if (item.circleContentText != null) {
                            Text(
                                text = item.circleContentText,
                                fontFamily = OdiaFont,
                                color = Color.Black,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        } else if (item.icon != null) {
                            Icon(item.icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = item.title,
                        fontFamily = OdiaFont,
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                // Original Vertical Layout for half-width cards
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(Color.Black.copy(alpha = 0.2f), CircleShape))
                            Box(modifier = Modifier.size(8.dp).background(Color.Black.copy(alpha = 0.2f), CircleShape))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // --- MODIFICATION FOR HALF-WIDTH CARD ---
                        if (item.circleContentText != null) {
                            Text(
                                text = item.circleContentText,
                                fontFamily = OdiaFont,
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        } else if (item.icon != null) {
                            Icon(item.icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(30.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.title,
                            fontFamily = OdiaFont,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}