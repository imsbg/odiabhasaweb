package `in`.sandeep.odiabhasa.ganita

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
data class GameOption(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesCategoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    // Animation Trigger
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // --- POP ART COLORS ---
    val gamesList = remember {
        listOf(
            GameOption("ହିସାବ ଖେଳ", "game_route", Icons.Default.PlayArrow, Color(0xFFFFD700)), // Bright Yellow
            GameOption("ବେଲୁନ୍ ଖେଳ", "logic_game_route", Icons.Default.Psychology, Color(0xFF00BFFF)), // Deep Sky Blue
            GameOption("ଗାଣିତିକ କାହାଣୀ", "word_problem_route", Icons.Default.Calculate, Color(0xFFFF69B4)), // Hot Pink
            GameOption("ସୁଡୋକୁ", "sudoku_route", Icons.Default.ViewModule, Color(0xFFBA55D3)), // Medium Orchid
            GameOption("ନିଖୋଜ ସଂଖ୍ୟା", "ftmn_game_route", Icons.Default.HelpOutline, Color(0xFF32CD32)), // Lime Green
            GameOption("ସଂଖ୍ୟା ଖସଡ଼ା", "sliding_puzzle_route", Icons.Default.Dashboard, Color(0xFFFFA500))  // Orange
        )
    }

    Scaffold(
        containerColor = Color(0xFFE0F7FA), // Very light cartoon blue background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ଗଣିତ ଖେଳ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Black, // Extra Bold
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    // Custom Comic Back Button
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

            // Subtitle
            AnimatedVisibility(visible = isVisible, enter = fadeIn()) {
                Text(
                    text = "ଆସ ଖେଳିବା!", // "Come let's play!"
                    fontFamily = OdiaFont,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
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
                itemsIndexed(gamesList) { index, game ->
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
                        ComicGameCard(
                            game = game,
                            onClick = { onNavigateToRoute(game.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComicGameCard(
    game: GameOption,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Press Animation: The card moves DOWN to cover the shadow
    val offsetState by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        label = "offset"
    )

    // The maximum shadow depth
    val maxShadowDepth = 6.dp

    Box(
        modifier = Modifier
            .height(170.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // 1. Black Shadow Layer (Static at the bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = maxShadowDepth, start = maxShadowDepth) // Offset to create shadow effect
                .background(Color.Black, RoundedCornerShape(16.dp))
        )

        // 2. Main Color Card (Moves when pressed)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = maxShadowDepth, end = maxShadowDepth) // Room for movement
                .offset(x = offsetState, y = offsetState) // Move diagonally towards shadow
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp)) // Thick Comic Border
                .clip(RoundedCornerShape(16.dp))
                .background(game.color)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Right Decoration (White sheen/dot)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(modifier = Modifier.size(10.dp).background(Color.White.copy(alpha = 0.6f), CircleShape))
                }

                // Center Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black, CircleShape) // Black circle behind icon
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title Label
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = game.title,
                        fontFamily = OdiaFont,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}