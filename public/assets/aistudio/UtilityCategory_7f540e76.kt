package `in`.sandeep.odiabhasa.ganita

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Title
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

// --- DATA CLASS ---
data class UtilityOption(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityCategoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    // Animation Trigger
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // --- UTILITY LIST ---
    val utilityList = remember {
        listOf(
            // The "Calculator" option has been removed from this list.
            UtilityOption("ଡିଜିଟାଲ୍ ବଜାର", "utility_digital_bajara", Icons.Default.ShoppingCart, Color(0xFF4CAF50)), // Green
            UtilityOption("ଏକକ ପରିବର୍ତ୍ତନ", "utility_converter", Icons.Default.SwapHoriz, Color(0xFF7E57C2)), // Purple
            UtilityOption("ସଂଖ୍ୟାରୁ ଶବ୍ଦ", "utility_num_to_text", Icons.Default.Title, Color(0xFFFFB74D))    // Orange
        )
    }

    Scaffold(
        containerColor = Color(0xFFE0F2F1), // Very light teal background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ଉପଯୋଗିତା", // "Utility"
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
                    text = "ଦରକାରୀ ଯନ୍ତ୍ରପାତି", // "Useful Tools"
                    fontFamily = OdiaFont,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 24.dp, bottom = 10.dp, top = 8.dp)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(utilityList) { index, item ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        ) { 100 * (index + 1) } + fadeIn()
                    ) {
                        ComicUtilityCard(
                            item = item,
                            onClick = { onNavigateToRoute(item.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComicUtilityCard(
    item: UtilityOption,
    onClick: () -> Unit
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
            .fillMaxWidth()
            .height(110.dp) // Taller row for utility
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
                .background(Color.Black, RoundedCornerShape(24.dp))
        )

        // 2. Main Color Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = maxShadowDepth, end = maxShadowDepth)
                .offset(x = offsetState, y = offsetState)
                .border(2.dp, Color.Black, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(item.color)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Text(
                    text = item.title,
                    fontFamily = OdiaFont,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Icon Bubble
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color, // Icon matches the card color
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}