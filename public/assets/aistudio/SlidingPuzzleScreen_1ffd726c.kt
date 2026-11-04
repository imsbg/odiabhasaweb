// FILE: in/sandeep/odiabhasa/ganita/SlidingPuzzleScreen.kt
package `in`.sandeep.odiabhasa.ganita

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.sandeep.odiabhasa.OdiaFont
import kotlin.math.abs

// --- 1. STATE & DATA ---

data class PuzzleState(
    val gridSize: Int = 3, // 3, 4, 5, 6
    val tiles: List<Int> = listOf(), // 0 represents the empty space
    val moves: Int = 0,
    val isWon: Boolean = false
)

// --- 2. VIEW MODEL ---

class SlidingPuzzleViewModel : ViewModel() {
    private val PREFS_NAME = "OdiaSlidingPuzzlePrefs"
    private val KEY_GRID_SIZE = "grid_size"
    private val KEY_TILES = "tile_layout"
    private val KEY_MOVES = "move_count"

    var state by mutableStateOf(PuzzleState())
        private set

    fun initGame(context: Context) {
        loadGame(context)
    }

    fun changeGridSize(context: Context, newSize: Int) {
        startNewGame(newSize)
        saveGame(context)
    }

    fun restartGame(context: Context) {
        startNewGame(state.gridSize)
        saveGame(context)
    }

    private fun startNewGame(size: Int) {
        val totalTiles = size * size
        val solvedTiles = (1 until totalTiles).toList() + 0

        val currentTiles = solvedTiles.toMutableList()
        var emptyIndex = totalTiles - 1
        var lastMoveIndex = -1

        val shuffleSteps = when(size) {
            3 -> 100
            4 -> 200
            5 -> 300
            else -> 400
        }

        for (i in 0 until shuffleSteps) {
            val validMoves = getValidMoves(emptyIndex, size)
                .filter { it != lastMoveIndex }

            if (validMoves.isNotEmpty()) {
                val moveIndex = validMoves.random()
                currentTiles[emptyIndex] = currentTiles[moveIndex]
                currentTiles[moveIndex] = 0

                lastMoveIndex = emptyIndex
                emptyIndex = moveIndex
            }
        }

        state = state.copy(
            gridSize = size,
            tiles = currentTiles,
            moves = 0,
            isWon = false
        )
    }

    fun onTileClick(context: Context, index: Int) {
        if (state.isWon) return

        val emptyIndex = state.tiles.indexOf(0)

        if (isAdjacent(index, emptyIndex, state.gridSize)) {
            val newTiles = state.tiles.toMutableList()
            newTiles[emptyIndex] = newTiles[index]
            newTiles[index] = 0

            val newMoves = state.moves + 1
            val isWon = checkWin(newTiles)

            state = state.copy(
                tiles = newTiles,
                moves = newMoves,
                isWon = isWon
            )
            saveGame(context)
        }
    }

    private fun getValidMoves(emptyIndex: Int, size: Int): List<Int> {
        val moves = mutableListOf<Int>()
        val row = emptyIndex / size
        val col = emptyIndex % size

        if (row > 0) moves.add(emptyIndex - size)
        if (row < size - 1) moves.add(emptyIndex + size)
        if (col > 0) moves.add(emptyIndex - 1)
        if (col < size - 1) moves.add(emptyIndex + 1)

        return moves
    }

    private fun isAdjacent(index1: Int, index2: Int, size: Int): Boolean {
        val row1 = index1 / size
        val col1 = index1 % size
        val row2 = index2 / size
        val col2 = index2 % size
        return (abs(row1 - row2) + abs(col1 - col2)) == 1
    }

    private fun checkWin(tiles: List<Int>): Boolean {
        val total = tiles.size
        for (i in 0 until total - 1) {
            if (tiles[i] != i + 1) return false
        }
        return tiles[total - 1] == 0
    }

    private fun saveGame(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(KEY_GRID_SIZE, state.gridSize)
        editor.putInt(KEY_MOVES, state.moves)
        editor.putString(KEY_TILES, state.tiles.joinToString(","))
        editor.apply()
    }

    private fun loadGame(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val size = prefs.getInt(KEY_GRID_SIZE, 3)
        val moves = prefs.getInt(KEY_MOVES, 0)
        val tilesString = prefs.getString(KEY_TILES, "")

        if (!tilesString.isNullOrEmpty()) {
            val tiles = tilesString.split(",").mapNotNull { it.toIntOrNull() }
            if (tiles.size == size * size) {
                state = state.copy(gridSize = size, tiles = tiles, moves = moves, isWon = checkWin(tiles))
                return
            }
        }
        startNewGame(size)
    }
}

// --- 3. HELPER: ODIA DIGITS ---
private fun Int.toOdia(): String {
    val odiaNumerals = arrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    val s = this.toString()
    val sb = StringBuilder()
    for (char in s) {
        if (char.isDigit()) {
            sb.append(odiaNumerals[char.toString().toInt()])
        } else {
            sb.append(char)
        }
    }
    return sb.toString()
}

// --- 4. UI SCREEN ---

@Composable
fun SlidingPuzzleScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: SlidingPuzzleViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.initGame(context)
    }

    val state = viewModel.state

    Scaffold(
        containerColor = Color(0xFFF0F4F8),
        topBar = {
            ComicPuzzleTopBar(
                moves = state.moves,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // --- GRID SIZE SELECTOR ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                SizeSelectorBtn("୩x୩", state.gridSize == 3) { viewModel.changeGridSize(context, 3) }
                SizeSelectorBtn("୪x୪", state.gridSize == 4) { viewModel.changeGridSize(context, 4) }
                SizeSelectorBtn("୫x୫", state.gridSize == 5) { viewModel.changeGridSize(context, 5) }
                SizeSelectorBtn("୬x୬", state.gridSize == 6) { viewModel.changeGridSize(context, 6) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PUZZLE AREA ---
            // Parent Box for the Grid and the Overlay
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Layer 1: Main Puzzle Board Background
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(Color(0xFF263238), RoundedCornerShape(16.dp)) // Dark Slate BG
                        .border(4.dp, Color.Black, RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(state.gridSize),
                        userScrollEnabled = false,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(state.tiles) { index, number ->
                            PuzzleTile(
                                number = number,
                                gridSize = state.gridSize,
                                onClick = { viewModel.onTileClick(context, index) }
                            )
                        }
                    }
                }

                // Layer 2: WON OVERLAY
                // Using explicit package name to avoid compiler ambiguity
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.isWon,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.matchParentSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "ଅଭିନନ୍ଦନ!", // Congratulations
                                fontFamily = OdiaFont,
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp,
                                color = Color.White
                            )
                            Text(
                                "ଆପଣ ଜିତିଗଲେ", // You won
                                fontFamily = OdiaFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.restartGame(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(2.dp, Color.White)
                            ) {
                                Text(
                                    "ପୁନଃ ଆରମ୍ଭ", // Play Again
                                    fontFamily = OdiaFont,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // --- BOTTOM CONTROLS ---
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier
                .padding(bottom = 32.dp)
                .clickable { viewModel.restartGame(context) }
            ) {
                ComicButton(
                    text = "ପୁନଃ ଆରମ୍ଭ", // Restart
                    color = Color(0xFFEF5350), // Red
                    icon = Icons.Default.Refresh
                )
            }
        }
    }
}

// --- 5. COMIC COMPONENTS ---

@Composable
fun ComicPuzzleTopBar(moves: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .statusBarsPadding() // FIX: Added this to push content down
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .size(44.dp)
                .border(2.dp, Color.Black, CircleShape)
                .background(Color.White, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            "ସଂଖ୍ୟା ଖସଡ଼ା", // Sliding Puzzle
            fontFamily = OdiaFont,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        // Moves Counter
        Box(modifier = Modifier.height(44.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 2.dp, start = 2.dp)
                    .background(Color.Black, RoundedCornerShape(12.dp))
            )
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 2.dp, end = 2.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .background(Color(0xFF80DEEA), RoundedCornerShape(12.dp)) // Cyan
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "ଚାଲ୍: ", // Moves:
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    moves.toOdia(),
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun PuzzleTile(number: Int, gridSize: Int, onClick: () -> Unit) {
    if (number == 0) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .background(Color.Transparent)
        )
        return
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(targetValue = if (isPressed) 2.dp else 0.dp)

    val fontSize = when(gridSize) {
        3 -> 32.sp
        4 -> 24.sp
        5 -> 20.sp
        else -> 16.sp
    }

    val tileColor = Color(0xFFFFB74D)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp, start = 2.dp)
                .background(Color.Black, RoundedCornerShape(8.dp))
        )
        // Tile Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 2.dp, end = 2.dp)
                .offset(x = offset, y = offset)
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .background(tileColor, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toOdia(),
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RowScope.SizeSelectorBtn(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color(0xFFA5D6A7) else Color.White

    Box(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 3.dp, start = 3.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 3.dp, end = 3.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .background(bgColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = text,
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ComicButton(text: String, color: Color, icon: ImageVector) {
    Box(modifier = Modifier.height(56.dp).width(160.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp, start = 4.dp)
                .background(Color.Black, RoundedCornerShape(16.dp))
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp, end = 4.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(color, RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}