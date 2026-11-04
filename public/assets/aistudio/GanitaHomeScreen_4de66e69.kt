package `in`.sandeep.odiabhasa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideogameAsset
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
import java.util.Calendar

// --- DATA CLASS ---
private data class CategoryItem(
    val title: String,
    val icon: ImageVector,
    val mainColor: Color,
    val shadowColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GanitaHomeScreen(
    onNavigateBack: () -> Unit,
    onNavToGames: () -> Unit,
    onNavToLearning: () -> Unit,
    onNavToUtility: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // Dynamic Greeting Logic (No Emojis)
    val greetingText = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 6..11 -> "ସୁପ୍ରଭାତ"
            in 12..16 -> "ଶୁଭ ଅପରାହ୍ଣ"
            in 17..19 -> "ଶୁଭ ସନ୍ଧ୍ୟା"
            else -> "ଶୁଭ ରାତ୍ରି"
        }
    }

    // Kids-Friendly Colors
    val itemGames = CategoryItem("ଗଣିତ ଖେଳ", Icons.Default.VideogameAsset, Color(0xFFFF6D00), Color(
        0xFFB74107
    )
    )
    val itemLearning = CategoryItem("ଶିକ୍ଷା", Icons.Default.School, Color(0xFF2979FF), Color(0xFF1565C0))
    val itemUtility = CategoryItem("ଉପଯୋଗିତା", Icons.Default.Construction, Color(0xFF06A4B9), Color(0xFF1B5E20))

    Scaffold(
        containerColor = Color(0xFFF0F4F8), // Soft background
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                    ) { -50 } + fadeIn()
                ) {
                    Column {
                        Text(
                            text = "ନମସ୍କାର,",
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = OdiaFont,
                            color = Color.Gray
                        )
                        Text(
                            text = greetingText,
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                            fontFamily = OdiaFont,
                            color = Color(0xFF2D3436)
                        )
                    }
                }
            }

            // 2. Categories Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    // Row 1: Games & Learning (Square Buttons - Vertical Layout inside)
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                            modifier = Modifier.weight(1f)
                        ) {
                            KidButton(
                                item = itemGames,
                                onClick = onNavToGames,
                                isHorizontal = false // Icon Top, Text Bottom
                            )
                        }
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                            modifier = Modifier.weight(1f)
                        ) {
                            KidButton(
                                item = itemLearning,
                                onClick = onNavToLearning,
                                isHorizontal = false // Icon Top, Text Bottom
                            )
                        }
                    }

                    // Row 2: Utility (Wide Button - Horizontal Layout inside)
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        ) { 100 } + fadeIn()
                    ) {
                        KidButton(
                            item = itemUtility,
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            onClick = onNavToUtility,
                            isHorizontal = true // Icon Left, Text Right
                        )
                    }
                }
            }

            // 3. Simple Footer (Gray Text only)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessVeryLow))
                ) {
                    Text(
                        text = "ଗଣିତ ଶିଖିବା ଏବେ\nଆହୁରି ସହଜ...",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        fontFamily = OdiaFont,
                        color = Color(0xFFB0BEC5), // Simple Gray
                        textAlign = TextAlign.Start,
                        lineHeight = 40.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun KidButton(
    item: CategoryItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isHorizontal: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Press animation logic
    val shadowHeight = 8.dp
    val verticalOffset = if (isPressed) 6.dp else 0.dp

    Box(
        modifier = modifier
            .padding(bottom = shadowHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = shadowHeight)
                .clip(RoundedCornerShape(24.dp))
                .background(item.shadowColor)
        )

        // Main Surface
        val surfaceModifier = Modifier
            .fillMaxSize()
            .offset(y = verticalOffset)
            .clip(RoundedCornerShape(24.dp))
            .background(item.mainColor)
            .padding(16.dp)

        // Content Layout Switcher
        if (isHorizontal) {
            // Horizontal Layout (Row) for Utility
            Row(
                modifier = surfaceModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    color = Color.White,
                    fontFamily = OdiaFont,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Vertical Layout (Column) for Games/Learning
            Column(
                modifier = surfaceModifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.title,
                    color = Color.White,
                    fontFamily = OdiaFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}