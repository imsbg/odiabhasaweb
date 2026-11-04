// FILE: in/sandeep/odiabhasa/ganita/SudokuScreen.kt
package `in`.sandeep.odiabhasa.ganita

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.PlayArrow
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
import kotlin.random.Random

// --- 1. UNIQUE DATA CLASSES & ENUMS ---

enum class SudokuScreenState { MENU, GAME }

enum class SudokuDifficulty(val label: String, val removeCount: Int, val color: Color) {
    EASY("ସହଜ", 30, Color(0xFFA5D6A7)),    // Green
    MEDIUM("ମଧ୍ୟମ", 45, Color(0xFFFFF176)), // Yellow
    HARD("କଠିନ", 55, Color(0xFFEF9A9A))     // Red
}

data class SudokuCell(
    val row: Int,
    val col: Int,
    val value: Int,         // 0 means empty
    val isInitial: Boolean, // True if part of the puzzle (immutable)
    val isError: Boolean = false
)

data class SudokuGameState(
    val screenState: SudokuScreenState = SudokuScreenState.MENU,
    val grid: List<SudokuCell> = List(81) { i -> SudokuCell(i / 9, i % 9, 0, false) },
    val selectedIndex: Int = -1,
    val difficulty: SudokuDifficulty = SudokuDifficulty.EASY,
    val hasSavedGame: Boolean = false,
    val isCompleted: Boolean = false
)

// --- 2. UNIQUE VIEW MODEL ---

class SudokuGameViewModel : ViewModel() {
    private val PREFS_NAME = "OdiaSudokuGamePrefs"
    private val KEY_GRID = "sudoku_grid_vals"
    private val KEY_INITIAL = "sudoku_grid_init"
    private val KEY_DIFF = "sudoku_diff_level"
    private val KEY_HAS_SAVE = "sudoku_has_save"

    var state by mutableStateOf(SudokuGameState())
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hasSave = prefs.getBoolean(KEY_HAS_SAVE, false)
        state = state.copy(hasSavedGame = hasSave)
    }

    fun startNewGame(context: Context, diff: SudokuDifficulty) {
        val (solvedGrid, puzzleGrid) = generateSudokuGrid(diff.removeCount)

        val newGrid = puzzleGrid.mapIndexed { index, value ->
            SudokuCell(
                row = index / 9,
                col = index % 9,
                value = value,
                isInitial = value != 0
            )
        }

        state = state.copy(
            screenState = SudokuScreenState.GAME,
            grid = newGrid,
            difficulty = diff,
            selectedIndex = -1,
            isCompleted = false,
            hasSavedGame = true
        )
        saveGameData(context)
    }

    fun resumeGame(context: Context) {
        loadGameData(context)
        state = state.copy(screenState = SudokuScreenState.GAME)
    }

    fun onCellClicked(index: Int) {
        state = state.copy(selectedIndex = index)
    }

    fun onInput(context: Context, number: Int) {
        val idx = state.selectedIndex
        if (idx == -1) return

        val currentCell = state.grid[idx]
        if (currentCell.isInitial) return // Cannot change initial cells

        val newGrid = state.grid.toMutableList()
        newGrid[idx] = currentCell.copy(value = number, isError = false)

        if (number != 0 && !isMoveValid(newGrid, idx, number)) {
            newGrid[idx] = newGrid[idx].copy(isError = true)
        }

        val isWon = checkSudokuWin(newGrid)

        state = state.copy(grid = newGrid, isCompleted = isWon)
        saveGameData(context)
    }

    fun goBackToMenu() {
        state = state.copy(screenState = SudokuScreenState.MENU)
    }

    // --- SUDOKU LOGIC ---
    private fun generateSudokuGrid(removeCount: Int): Pair<List<Int>, List<Int>> {
        val board = IntArray(81) { 0 }
        fillSudokuDiagonal(board)
        solveSudokuBoard(board)
        val fullBoard = board.toList()

        val puzzleBoard = board.clone()
        var attempts = removeCount
        while (attempts > 0) {
            val idx = Random.nextInt(81)
            if (puzzleBoard[idx] != 0) {
                puzzleBoard[idx] = 0
                attempts--
            }
        }
        return Pair(fullBoard, puzzleBoard.toList())
    }

    private fun fillSudokuDiagonal(board: IntArray) {
        for (boxStartIndex in 0 until 9 step 3) {
            // **FIXED**: The call should be to fillSudokuBox(board, boxStartIndex, boxStartIndex)
            // The previous code had `box * 3` which was incorrect.
            fillSudokuBox(board, boxStartIndex, boxStartIndex)
        }
    }

    private fun fillSudokuBox(board: IntArray, startRow: Int, startCol: Int) {
        var num: Int
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                do {
                    num = Random.nextInt(1, 10)
                } while (!isSafeInBox(board, startRow, startCol, num))
                board[(startRow + i) * 9 + (startCol + j)] = num
            }
        }
    }

    private fun isSafeInBox(board: IntArray, startRow: Int, startCol: Int, num: Int): Boolean {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[(startRow + i) * 9 + (startCol + j)] == num) return false
            }
        }
        return true
    }

    private fun solveSudokuBoard(board: IntArray): Boolean {
        for (i in 0 until 81) {
            if (board[i] == 0) {
                val row = i / 9
                val col = i % 9
                val numbers = (1..9).shuffled()
                for (num in numbers) {
                    if (isSafeToPlace(board, row, col, num)) {
                        board[i] = num
                        if (solveSudokuBoard(board)) return true
                        board[i] = 0
                    }
                }
                return false
            }
        }
        return true
    }

    private fun isSafeToPlace(board: IntArray, row: Int, col: Int, num: Int): Boolean {
        for (x in 0 until 9) {
            if (board[row * 9 + x] == num) return false
            if (board[x * 9 + col] == num) return false
        }
        val startRow = row - row % 3
        val startCol = col - col % 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[(startRow + i) * 9 + (startCol + j)] == num) return false
            }
        }
        return true
    }

    private fun isMoveValid(grid: List<SudokuCell>, index: Int, num: Int): Boolean {
        val row = index / 9
        val col = index % 9
        for (i in 0 until 9) {
            if (i != col && grid[row * 9 + i].value == num) return false
            if (i != row && grid[i * 9 + col].value == num) return false
        }
        val startRow = row - row % 3
        val startCol = col - col % 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val r = startRow + i
                val c = startCol + j
                if ((r != row || c != col) && grid[r * 9 + c].value == num) return false
            }
        }
        return true
    }

    private fun checkSudokuWin(grid: List<SudokuCell>): Boolean {
        return grid.none { it.value == 0 || it.isError }
    }

    // --- PERSISTENCE ---
    private fun saveGameData(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val values = state.grid.joinToString(",") { it.value.toString() }
        val initials = state.grid.joinToString(",") { if(it.isInitial) "1" else "0" }

        editor.putString(KEY_GRID, values)
        editor.putString(KEY_INITIAL, initials)
        editor.putString(KEY_DIFF, state.difficulty.name)
        editor.putBoolean(KEY_HAS_SAVE, true)
        editor.apply()
    }

    private fun loadGameData(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val valStr = prefs.getString(KEY_GRID, "") ?: ""
        val initStr = prefs.getString(KEY_INITIAL, "") ?: ""
        val diffStr = prefs.getString(KEY_DIFF, "EASY") ?: "EASY"

        if (valStr.isNotEmpty()) {
            val vals = valStr.split(",").map { it.toInt() }
            val inits = initStr.split(",").map { it == "1" }

            val loadedGrid = vals.zip(inits).mapIndexed { idx, (v, i) ->
                SudokuCell(idx / 9, idx % 9, v, i)
            }

            state = state.copy(
                grid = loadedGrid,
                difficulty = try { SudokuDifficulty.valueOf(diffStr) } catch(e: Exception) { SudokuDifficulty.EASY },
                hasSavedGame = true
            )
        }
    }
}

// --- 3. HELPER: ODIA DIGITS ---
private fun Int.toOdiaSudoku(): String {
    if (this == 0) return ""
    val odiaNumerals = arrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    return odiaNumerals[this].toString()
}

// --- 4. MAIN SCREEN ---

@Composable
fun SudokuScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: SudokuGameViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.init(context)
    }

    val state = viewModel.state

    BackHandler {
        if (state.screenState == SudokuScreenState.GAME) {
            viewModel.goBackToMenu()
        } else {
            onBack()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF0F4F8)
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            if (state.screenState == SudokuScreenState.MENU) {
                SudokuMenuUi(
                    hasSave = state.hasSavedGame,
                    onResume = { viewModel.resumeGame(context) },
                    onNewGame = { viewModel.startNewGame(context, it) },
                    onBack = onBack
                )
            } else {
                SudokuGameUi(
                    state = state,
                    onInput = { viewModel.onInput(context, it) },
                    onCellClick = { viewModel.onCellClicked(it) },
                    onBack = { viewModel.goBackToMenu() }
                )
            }
        }
    }
}

// --- 5. MENU UI ---

@Composable
fun SudokuMenuUi(
    hasSave: Boolean,
    onResume: () -> Unit,
    onNewGame: (SudokuDifficulty) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SudokuComicTopBar(title = "ସୁଡୋକୁ", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "ନୂତନ ଖେଳ", // New Game
                fontFamily = OdiaFont,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (hasSave) {
                SudokuComicMenuButton(
                    text = "ପୁଣି ଖେଳନ୍ତୁ",
                    color = Color(0xFF80DEEA), // Cyan
                    icon = Icons.Default.PlayArrow,
                    onClick = onResume
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            SudokuComicMenuButton(
                text = SudokuDifficulty.EASY.label,
                color = SudokuDifficulty.EASY.color,
                onClick = { onNewGame(SudokuDifficulty.EASY) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            SudokuComicMenuButton(
                text = SudokuDifficulty.MEDIUM.label,
                color = SudokuDifficulty.MEDIUM.color,
                onClick = { onNewGame(SudokuDifficulty.MEDIUM) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            SudokuComicMenuButton(
                text = SudokuDifficulty.HARD.label,
                color = SudokuDifficulty.HARD.color,
                onClick = { onNewGame(SudokuDifficulty.HARD) }
            )
        }
    }
}

// --- 6. GAME UI ---

@Composable
fun SudokuGameUi(
    state: SudokuGameState,
    onInput: (Int) -> Unit,
    onCellClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SudokuComicTopBar(title = "ସୁଡୋକୁ: ${state.difficulty.label}", onBack = onBack)

        AnimatedVisibility(visible = state.isCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "ଅଭିନନ୍ଦନ! ଆପଣ ଜିତିଗଲେ",
                    fontFamily = OdiaFont,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .aspectRatio(1f)
                .border(3.dp, Color.Black)
                .background(Color.Black)
                .padding(2.dp)
        ) {
            Column {
                for (blockRow in 0 until 3) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (blockCol in 0 until 3) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(1.dp, Color.Black)
                                    .padding(1.dp)
                            ) {
                                Sudoku3x3BlockUi(
                                    grid = state.grid,
                                    blockRow = blockRow,
                                    blockCol = blockCol,
                                    selectedIndex = state.selectedIndex,
                                    onCellClick = onCellClick
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        SudokuComicKeypad(onInput = onInput)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun Sudoku3x3BlockUi(
    grid: List<SudokuCell>,
    blockRow: Int,
    blockCol: Int,
    selectedIndex: Int,
    onCellClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (i in 0 until 3) {
            Row(modifier = Modifier.weight(1f)) {
                for (j in 0 until 3) {
                    val globalRow = blockRow * 3 + i
                    val globalCol = blockCol * 3 + j
                    val index = globalRow * 9 + globalCol
                    val cell = grid[index]

                    val isSelected = index == selectedIndex

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(0.5.dp, Color.Gray)
                            .background(
                                when {
                                    isSelected -> Color(0xFFBBDEFB)
                                    cell.isError -> Color(0xFFFFCDD2)
                                    cell.isInitial -> Color(0xFFEEEEEE)
                                    else -> Color.White
                                }
                            )
                            .clickable { onCellClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cell.value.toOdiaSudoku(),
                            fontFamily = OdiaFont,
                            fontWeight = if(cell.isInitial) FontWeight.Black else FontWeight.Normal,
                            fontSize = 20.sp,
                            color = if(cell.isInitial) Color.Black else Color(0xFF1565C0)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuComicKeypad(onInput: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 1..5) {
                SudokuComicNumButton(number = i, modifier = Modifier.weight(1f)) { onInput(i) }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 6..9) {
                SudokuComicNumButton(number = i, modifier = Modifier.weight(1f)) { onInput(i) }
            }
            SudokuComicNumButton(number = 0, isIcon = true, modifier = Modifier.weight(1f)) { onInput(0) }
        }
    }
}

// --- 7. UNIQUE COMIC COMPONENTS ---

@Composable
fun SudokuComicTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        Text(
            text = title,
            fontFamily = OdiaFont,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = Color.Black
        )
    }
}

@Composable
fun SudokuComicMenuButton(
    text: String,
    color: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp, label = "offset")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp, start = 4.dp)
                .background(Color.Black, RoundedCornerShape(16.dp))
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp, end = 4.dp)
                .offset(x = offset, y = offset)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(color, RoundedCornerShape(16.dp))
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun SudokuComicNumButton(
    number: Int,
    isIcon: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(targetValue = if (isPressed) 2.dp else 0.dp, label = "offset")

    Box(
        modifier = modifier
            .height(50.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 3.dp, start = 3.dp)
                .background(Color.Black, RoundedCornerShape(8.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 3.dp, end = 3.dp)
                .offset(x = offset, y = offset)
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isIcon) {
                Icon(Icons.Default.Backspace, contentDescription = "Erase", tint = Color.Red)
            } else {
                Text(
                    text = number.toOdiaSudoku(),
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
        }
    }
}