// FILE: in/sandeep/odiabhasa/ganita/MathGameScreen.kt
package `in`.sandeep.odiabhasa.ganita

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.sandeep.odiabhasa.OdiaFont
import kotlin.random.Random

// --- 1. DATA & STATE ---

enum class GameMode {
    ADD, SUB, MUL, DIV, MIXED
}

data class MathQuestion(
    val text: String,
    val correctAnswer: Int,
    val options: List<Int>
)

data class SolvedItem(
    val question: String,
    val answer: String,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class MathGameState(
    val currentQuestion: MathQuestion? = null,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val solvedHistory: List<SolvedItem> = emptyList(),
    val isAnswered: Boolean = false,
    val selectedOption: Int? = null,
    val isCorrectLast: Boolean = false,
    val gameMode: GameMode = GameMode.MIXED
)

// --- 2. VIEW MODEL ---

class MathGameViewModel : ViewModel() {
    private val PREFS_NAME = "OdiaMathGamePrefs"
    private val KEY_CORRECT = "correct_count"
    private val KEY_WRONG = "wrong_count"
    private val KEY_HISTORY = "history_v2"
    private val KEY_MODE = "game_mode"

    var state by mutableStateOf(MathGameState())
        private set

    fun initGame(context: Context) {
        loadData(context)
        if (state.currentQuestion == null) {
            generateNewQuestion()
        }
    }

    fun setGameMode(context: Context, mode: GameMode) {
        state = state.copy(gameMode = mode)
        saveData(context)
        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        val op = when (state.gameMode) {
            GameMode.ADD -> 0
            GameMode.SUB -> 1
            GameMode.MUL -> 2
            GameMode.DIV -> 3
            GameMode.MIXED -> Random.nextInt(4)
        }

        var num1 = Random.nextInt(1, 50)
        var num2 = Random.nextInt(1, 20)
        var answer = 0
        var symbol = ""

        when (op) {
            0 -> { // Add
                symbol = "+"
                answer = num1 + num2
            }
            1 -> { // Subtract
                if (num1 < num2) { val temp = num1; num1 = num2; num2 = temp }
                symbol = "-"
                answer = num1 - num2
            }
            2 -> { // Multiply
                num1 = Random.nextInt(1, 15)
                num2 = Random.nextInt(1, 10)
                symbol = "×"
                answer = num1 * num2
            }
            3 -> { // Divide
                num2 = Random.nextInt(2, 10)
                answer = Random.nextInt(1, 10)
                num1 = num2 * answer
                symbol = "÷"
            }
        }

        val options = mutableSetOf<Int>()
        options.add(answer)
        while (options.size < 4) {
            val offset = Random.nextInt(-10, 10)
            val wrong = answer + offset
            if (wrong >= 0 && wrong != answer) options.add(wrong)
        }

        val questionText = "${num1.toString().toOdiaDigits()} $symbol ${num2.toString().toOdiaDigits()}"

        state = state.copy(
            currentQuestion = MathQuestion(questionText, answer, options.toList().shuffled()),
            isAnswered = false,
            selectedOption = null
        )
    }

    fun submitAnswer(context: Context, selected: Int) {
        if (state.isAnswered) return

        val isCorrect = selected == state.currentQuestion?.correctAnswer
        val currentQ = state.currentQuestion ?: return

        val newCorrect = if (isCorrect) state.correctCount + 1 else state.correctCount
        val newWrong = if (!isCorrect) state.wrongCount + 1 else state.wrongCount

        val historyItem = SolvedItem(
            question = currentQ.text,
            answer = selected.toString().toOdiaDigits(),
            isCorrect = isCorrect
        )
        val newHistory = listOf(historyItem) + state.solvedHistory

        state = state.copy(
            correctCount = newCorrect,
            wrongCount = newWrong,
            solvedHistory = newHistory,
            isAnswered = true,
            selectedOption = selected,
            isCorrectLast = isCorrect
        )

        saveData(context)
    }

    fun nextQuestion() {
        generateNewQuestion()
    }

    fun clearHistory(context: Context) {
        state = state.copy(correctCount = 0, wrongCount = 0, solvedHistory = emptyList())
        saveData(context)
        generateNewQuestion()
    }

    private fun saveData(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(KEY_CORRECT, state.correctCount)
        editor.putInt(KEY_WRONG, state.wrongCount)
        editor.putString(KEY_MODE, state.gameMode.name)

        val historyString = state.solvedHistory.take(50).joinToString("#") {
            "${it.question}|${it.answer}|${it.isCorrect}"
        }
        editor.putString(KEY_HISTORY, historyString)
        editor.apply()
    }

    private fun loadData(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedCorrect = prefs.getInt(KEY_CORRECT, 0)
        val savedWrong = prefs.getInt(KEY_WRONG, 0)
        val modeString = prefs.getString(KEY_MODE, "MIXED")
        val savedMode = try { GameMode.valueOf(modeString ?: "MIXED") } catch (e: Exception) { GameMode.MIXED }

        val historyString = prefs.getString(KEY_HISTORY, "") ?: ""

        val historyList = if (historyString.isNotEmpty()) {
            historyString.split("#").mapNotNull {
                val parts = it.split("|")
                if (parts.size >= 3) {
                    SolvedItem(parts[0], parts[1], parts[2].toBoolean())
                } else null
            }
        } else emptyList()

        state = state.copy(
            correctCount = savedCorrect,
            wrongCount = savedWrong,
            solvedHistory = historyList,
            gameMode = savedMode
        )
    }
}

// --- 3. HELPER: STRING EXTENSIONS ---

fun String.toOdiaDigits(): String {
    val odiaNumerals = arrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    val sb = StringBuilder()
    for (char in this) {
        if (char.isDigit()) {
            sb.append(odiaNumerals[char.toString().toInt()])
        } else {
            sb.append(char)
        }
    }
    return sb.toString()
}

// --- 4. MAIN SCREEN COMPOSABLE ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathGameScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: MathGameViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.initGame(context)
    }

    val state = viewModel.state
    var showSlate by remember { mutableStateOf(false) }
    var showModeSheet by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) } // Added state for Clear Dialog

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = Color(0xFFF0F4F8),
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Left Button (Settings/Mode)
                Box(modifier = Modifier.align(Alignment.BottomStart)) {
                    ComicFloatingButton(
                        icon = Icons.Default.Tune,
                        bgColor = Color(0xFF29B6F6),
                        onClick = { showModeSheet = true }
                    )
                }

                // Right Button (Slate/Pencil)
                Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                    ComicFloatingButton(
                        icon = Icons.Default.Edit,
                        bgColor = Color(0xFFFF9800),
                        onClick = { showSlate = true }
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. FIXED TOP BAR
            ComicTopBar(
                title = "ଗଣିତ ଖେଳ",
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                onBack = onBack
            )

            // 2. FIXED QUESTION & OPTIONS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                ComicQuestionCard(
                    question = state.currentQuestion?.text ?: "...",
                    isAnswered = state.isAnswered,
                    isCorrect = state.isCorrectLast,
                    onNext = { viewModel.nextQuestion() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                state.currentQuestion?.options?.let { options ->
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val chunkedOptions = options.chunked(2)
                        chunkedOptions.forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowItems.forEach { option ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ComicOptionButton(
                                            text = option.toString().toOdiaDigits(),
                                            isSelected = state.isAnswered && state.selectedOption == option,
                                            isCorrectAnswer = state.isAnswered && state.currentQuestion.correctAnswer == option,
                                            isAnswered = state.isAnswered,
                                            onClick = { viewModel.submitAnswer(context, option) }
                                        )
                                    }
                                }
                                if (rowItems.size < 2) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // 3. SCROLLABLE HISTORY AREA
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(16.dp)
            ) {
                // Header (Triggers Confirmation Dialog)
                ComicHistoryHeader(onClear = {
                    if(state.solvedHistory.isNotEmpty() || state.correctCount > 0) {
                        showClearDialog = true
                    }
                })

                Spacer(modifier = Modifier.height(8.dp))

                if (state.solvedHistory.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("କୌଣସି ତଥ୍ୟ ନାହିଁ", fontFamily = OdiaFont, color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(state.solvedHistory) { item ->
                            ComicHistoryItem(item)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // --- BOTTOM SHEET FOR MODES ---
        if (showModeSheet) {
            ModalBottomSheet(
                onDismissRequest = { showModeSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFFFFF9C4)
            ) {
                ComicModeSelectionSheet(
                    currentMode = state.gameMode,
                    onSelect = { mode ->
                        viewModel.setGameMode(context, mode)
                        showModeSheet = false
                    }
                )
            }
        }

        // --- CLEAR CONFIRMATION DIALOG ---
        if (showClearDialog) {
            ComicConfirmationDialog(
                onConfirm = {
                    viewModel.clearHistory(context)
                    showClearDialog = false
                },
                onDismiss = { showClearDialog = false }
            )
        }

        // --- SLATE DIALOG ---
        if (showSlate) {
            DigitalSlateDialog(
                question = state.currentQuestion?.text,
                onDismiss = { showSlate = false }
            )
        }
    }
}

// --- 5. COMIC UI COMPONENTS ---

@Composable
fun ComicConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            // Shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 6.dp, start = 6.dp)
                    .background(Color.Black, RoundedCornerShape(20.dp))
            )
            // Main Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp, end = 6.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(48.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ଆପଣ ନିଶ୍ଚିତ କି?", // Are you sure?
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ସମସ୍ତ ପୂର୍ବ ସମାଧାନ ଲିଭିଯିବ ।", // All previous solutions will be deleted.
                    fontFamily = OdiaFont,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cancel Button (Green)
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)), // Green
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text("ନାଁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
                    }

                    // Confirm Button (Red)
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)), // Red
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text("ହଁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ComicTopBar(title: String, correctCount: Int, wrongCount: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
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

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontFamily = OdiaFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        ComicScoreBox(
            count = correctCount,
            color = Color(0xFFA5D6A7),
            icon = Icons.Default.Check
        )

        Spacer(modifier = Modifier.width(8.dp))

        ComicScoreBox(
            count = wrongCount,
            color = Color(0xFFEF9A9A),
            icon = Icons.Default.Close
        )
    }
}

@Composable
fun ComicScoreBox(count: Int, color: Color, icon: ImageVector) {
    Box(modifier = Modifier.height(40.dp).width(70.dp)) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 2.dp, start = 2.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 2.dp, end = 2.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .background(color, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count.toString().toOdiaDigits(),
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ComicQuestionCard(
    question: String,
    isAnswered: Boolean,
    isCorrect: Boolean,
    onNext: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 6.dp, start = 6.dp)
                .background(Color.Black, RoundedCornerShape(20.dp))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 6.dp, end = 6.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isAnswered) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if(isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if(isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCorrect) "ଠିକ୍ ଉତ୍ତର!" else "ଭୁଲ ଉତ୍ତର!",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if(isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("ପରବର୍ତ୍ତୀ", fontFamily = OdiaFont)
                }
            } else {
                Text(
                    text = question,
                    fontFamily = OdiaFont,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ComicOptionButton(
    text: String,
    isSelected: Boolean,
    isCorrectAnswer: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrectAnswer -> Color(0xFFA5D6A7)
        isSelected && !isCorrectAnswer -> Color(0xFFEF9A9A)
        else -> Color(0xFFE1F5FE)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(targetValue = if (isPressed && !isAnswered) 4.dp else 0.dp)

    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isAnswered,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 4.dp, start = 4.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp, end = 4.dp)
                .offset(x = offset, y = offset)
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .background(backgroundColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontFamily = OdiaFont,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ComicFloatingButton(icon: ImageVector, bgColor: Color, onClick: () -> Unit) {
    Box(modifier = Modifier.size(60.dp).clickable(onClick = onClick)) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top=4.dp, start=4.dp)
                .background(Color.Black, CircleShape)
        )
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom=4.dp, end=4.dp)
                .border(2.dp, Color.Black, CircleShape)
                .background(bgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun ComicHistoryHeader(onClear: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "ପୂର୍ବ ସମାଧାନ",
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
        TextButton(onClick = onClear) {
            Text("ସଫା କରନ୍ତୁ", fontFamily = OdiaFont, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ComicHistoryItem(item: SolvedItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.question,
            fontFamily = OdiaFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "= ${item.answer}",
                fontFamily = OdiaFont,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                if(item.isCorrect) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if(item.isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ComicModeSelectionSheet(currentMode: GameMode, onSelect: (GameMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "ଗଣିତ ପ୍ରକାର ବାଛନ୍ତୁ",
            fontFamily = OdiaFont,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val modes = listOf(
            Triple(GameMode.ADD, "ମିଶାଣ (+)", Color(0xFFFFCC80)),
            Triple(GameMode.SUB, "ଫେଡାଣ (-)", Color(0xFFEF9A9A)),
            Triple(GameMode.MUL, "ଗୁଣନ (×)", Color(0xFFA5D6A7)),
            Triple(GameMode.DIV, "ହରଣ (÷)", Color(0xFF90CAF9)),
            Triple(GameMode.MIXED, "ମିଶ୍ରିତ", Color(0xFFE1BEE7))
        )

        modes.forEach { (mode, title, color) ->
            ComicModeItem(
                title = title,
                color = color,
                isSelected = currentMode == mode,
                onClick = { onSelect(mode) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ComicModeItem(title: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top=4.dp, start=4.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom=4.dp, end=4.dp)
                .border(
                    width = if(isSelected) 3.dp else 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(color, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                if(isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                }
            }
        }
    }
}

// --- 6. DIGITAL SLATE ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalSlateDialog(question: String?, onDismiss: () -> Unit) {
    val completedPaths = remember { mutableStateListOf<Path>() }
    val undonePaths = remember { mutableStateListOf<Path>() }
    var currentPathPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("ରଫ୍ ଖାତା", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (completedPaths.isNotEmpty()) undonePaths.add(completedPaths.removeLast())
                        }) { Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo") }
                        IconButton(onClick = {
                            completedPaths.clear(); undonePaths.clear()
                        }) { Icon(Icons.Default.DeleteOutline, contentDescription = "Clear") }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFFFF9C4)
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF263238))
            ) {
                if (question != null) {
                    Text(
                        text = "ପ୍ରଶ୍ନ: $question",
                        color = Color.White,
                        fontFamily = OdiaFont,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                    HorizontalDivider(color = Color.Gray)
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    undonePaths.clear()
                                    currentPathPoints = listOf(offset)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPathPoints = currentPathPoints + change.position
                                },
                                onDragEnd = {
                                    if (currentPathPoints.size > 1) {
                                        val path = Path().apply {
                                            moveTo(currentPathPoints.first().x, currentPathPoints.first().y)
                                            currentPathPoints.drop(1).forEach { lineTo(it.x, it.y) }
                                        }
                                        completedPaths.add(path)
                                    }
                                    currentPathPoints = emptyList()
                                }
                            )
                        }
                ) {
                    val strokeStyle = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)

                    completedPaths.forEach { path ->
                        drawPath(path, Color.White, style = strokeStyle)
                    }
                    if (currentPathPoints.size > 1) {
                        val tempPath = Path().apply {
                            moveTo(currentPathPoints.first().x, currentPathPoints.first().y)
                            currentPathPoints.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(tempPath, Color.White, style = strokeStyle)
                    }
                }
            }
        }
    }
}