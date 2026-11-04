// FILE: in/sandeep/odiabhasa/ganita/FTMNScreen.kt
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
import kotlin.math.abs
import kotlin.random.Random

// --- 1. DATA & STATE ---

enum class FTMNDifficulty {
    EASY, MEDIUM, HARD
}

data class FTMNQuestion(
    val displaySequence: String, // e.g., "2, 4, ?, 8"
    val fullSequence: String,    // e.g., "2, 4, 6, 8" for history
    val missingNumber: Int,
    val options: List<Int>,
    val patternType: String = "" // For internal debugging or hints if needed
)

data class FTMNSolvedItem(
    val sequence: String,
    val userAnswer: String,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class FTMNState(
    val currentQuestion: FTMNQuestion? = null,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val solvedHistory: List<FTMNSolvedItem> = emptyList(),
    val isAnswered: Boolean = false,
    val selectedOption: Int? = null,
    val isCorrectLast: Boolean = false,
    val difficulty: FTMNDifficulty = FTMNDifficulty.EASY
)

// --- 2. VIEW MODEL ---

class FTMNViewModel : ViewModel() {
    private val PREFS_NAME = "OdiaFTMNPrefs"
    private val KEY_CORRECT = "ftmn_correct_count"
    private val KEY_WRONG = "ftmn_wrong_count"
    private val KEY_HISTORY = "ftmn_history_v2"
    private val KEY_DIFF = "ftmn_difficulty"

    var state by mutableStateOf(FTMNState())
        private set

    fun initGame(context: Context) {
        loadData(context)
        if (state.currentQuestion == null) {
            generateNewQuestion()
        }
    }

    fun setDifficulty(context: Context, diff: FTMNDifficulty) {
        state = state.copy(difficulty = diff)
        saveData(context)
        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        // Decide length: 4, 5, or 6
        val length = Random.nextInt(4, 7) // 4, 5, 6
        val sequence = mutableListOf<Int>()
        var patternName = ""

        when (state.difficulty) {
            FTMNDifficulty.EASY -> {
                // Logic: Simple Addition/Subtraction (AP) with small numbers
                val start = Random.nextInt(1, 20)
                val diff = Random.nextInt(1, 6)
                for (i in 0 until length) {
                    sequence.add(start + (i * diff))
                }
                patternName = "Easy AP"
            }
            FTMNDifficulty.MEDIUM -> {
                // Logic: Larger AP or Multiplication table steps
                val start = Random.nextInt(10, 50)
                val diff = Random.nextInt(2, 15)
                for (i in 0 until length) {
                    sequence.add(start + (i * diff))
                }
                patternName = "Medium AP"
            }
            FTMNDifficulty.HARD -> {
                // 1. Increasing Gap, 2. Multiplier, 3. Alternating, 4. Fibonacci
                val type = Random.nextInt(1, 5)

                when (type) {
                    1 -> { // Increasing Gap (+2, +3, +4...)
                        var current = Random.nextInt(5, 20)
                        var gap = Random.nextInt(1, 4)
                        sequence.add(current)
                        for (i in 1 until length) {
                            current += gap
                            sequence.add(current)
                            gap++ // Increase gap
                        }
                        patternName = "Increasing Gap"
                    }
                    2 -> { // Multiplier (x2 or x3)
                        var current = Random.nextInt(2, 6)
                        val mult = Random.nextInt(2, 4) // x2 or x3
                        sequence.add(current)
                        for (i in 1 until length) {
                            current *= mult
                            sequence.add(current)
                        }
                        patternName = "Multiplier x$mult"
                    }
                    3 -> { // Alternating (+A, -B)
                        var current = Random.nextInt(10, 30)
                        val addVal = Random.nextInt(5, 15)
                        val subVal = Random.nextInt(2, 5)
                        sequence.add(current)
                        for (i in 1 until length) {
                            if (i % 2 != 0) current += addVal else current -= subVal
                            sequence.add(current)
                        }
                        patternName = "Alternating"
                    }
                    4 -> { // Fibonacci Style (Sum of previous two)
                        var n1 = Random.nextInt(1, 10)
                        var n2 = Random.nextInt(1, 10)
                        sequence.add(n1)
                        sequence.add(n2)
                        for (i in 2 until length) {
                            val next = n1 + n2
                            sequence.add(next)
                            n1 = n2
                            n2 = next
                        }
                        patternName = "Fibonacci"
                    }
                }
            }
        }

        // Pick one to hide
        val missingIndex = Random.nextInt(sequence.size)
        val missingNumber = sequence[missingIndex]

        // Build Strings
        val displayParts = sequence.mapIndexed { index, num ->
            if (index == missingIndex) "?" else num.toString().toOdiaDigitsFTMN()
        }
        val displayString = displayParts.joinToString(", ")
        val fullSeqString = sequence.joinToString(", ") { it.toString().toOdiaDigitsFTMN() }

        // Options Generation
        val options = mutableSetOf<Int>()
        options.add(missingNumber)

        while (options.size < 4) {
            // Generate wrong answers based on magnitude of correct answer
            val range = if (missingNumber < 10) 5 else (missingNumber / 3)
            val offset = Random.nextInt(-range, range + 2)
            val wrong = missingNumber + offset

            // Ensure no duplicates and positive numbers (unless sequence went negative)
            if (wrong != missingNumber && (wrong >= 0 || missingNumber < 0)) {
                options.add(wrong)
            }
        }

        state = state.copy(
            currentQuestion = FTMNQuestion(
                displaySequence = displayString,
                fullSequence = fullSeqString,
                missingNumber = missingNumber,
                options = options.toList().shuffled(),
                patternType = patternName
            ),
            isAnswered = false,
            selectedOption = null
        )
    }

    fun submitAnswer(context: Context, selected: Int) {
        if (state.isAnswered) return

        val currentQ = state.currentQuestion ?: return
        val isCorrect = selected == currentQ.missingNumber

        val newCorrect = if (isCorrect) state.correctCount + 1 else state.correctCount
        val newWrong = if (!isCorrect) state.wrongCount + 1 else state.wrongCount

        val historyItem = FTMNSolvedItem(
            sequence = currentQ.displaySequence,
            userAnswer = selected.toString().toOdiaDigitsFTMN(),
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
        editor.putString(KEY_DIFF, state.difficulty.name)

        val historyString = state.solvedHistory.take(50).joinToString("#") {
            "${it.sequence}|${it.userAnswer}|${it.isCorrect}"
        }
        editor.putString(KEY_HISTORY, historyString)
        editor.apply()
    }

    private fun loadData(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedCorrect = prefs.getInt(KEY_CORRECT, 0)
        val savedWrong = prefs.getInt(KEY_WRONG, 0)
        val diffString = prefs.getString(KEY_DIFF, "EASY")
        val savedDiff = try { FTMNDifficulty.valueOf(diffString ?: "EASY") } catch (e: Exception) { FTMNDifficulty.EASY }

        val historyString = prefs.getString(KEY_HISTORY, "") ?: ""

        val historyList = if (historyString.isNotEmpty()) {
            historyString.split("#").mapNotNull {
                val parts = it.split("|")
                if (parts.size >= 3) {
                    FTMNSolvedItem(parts[0], parts[1], parts[2].toBoolean())
                } else null
            }
        } else emptyList()

        state = state.copy(
            correctCount = savedCorrect,
            wrongCount = savedWrong,
            solvedHistory = historyList,
            difficulty = savedDiff
        )
    }
}

// --- 3. HELPER: STRING EXTENSIONS ---

private fun String.toOdiaDigitsFTMN(): String {
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
fun FTMNScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: FTMNViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.initGame(context)
    }

    val state = viewModel.state
    var showSlate by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showDifficultySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = Color(0xFFF0F4F8),
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Left Button (Settings/Difficulty)
                Box(modifier = Modifier.align(Alignment.BottomStart)) {
                    FTMNComicFloatingButton(
                        icon = Icons.Default.Tune,
                        bgColor = Color(0xFF29B6F6),
                        onClick = { showDifficultySheet = true }
                    )
                }

                // Right Button (Slate/Pencil)
                Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                    FTMNComicFloatingButton(
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
            // 1. TOP BAR
            FTMNComicTopBar(
                title = "ନିଖୋଜ ସଂଖ୍ୟା", // "Missing Number"
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                onBack = onBack
            )

            // 2. QUESTION & OPTIONS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Question Card (Displays Sequence)
                FTMNComicQuestionCard(
                    questionText = state.currentQuestion?.displaySequence ?: "...",
                    isAnswered = state.isAnswered,
                    isCorrect = state.isCorrectLast,
                    correctFullSequence = state.currentQuestion?.fullSequence,
                    onNext = { viewModel.nextQuestion() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                state.currentQuestion?.options?.let { options ->
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val chunkedOptions = options.chunked(2)
                        chunkedOptions.forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowItems.forEach { option ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        FTMNComicOptionButton(
                                            text = option.toString().toOdiaDigitsFTMN(),
                                            isSelected = state.isAnswered && state.selectedOption == option,
                                            isCorrectAnswer = state.isAnswered && state.currentQuestion.missingNumber == option,
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

            // 3. HISTORY AREA
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(16.dp)
            ) {
                FTMNComicHistoryHeader(onClear = {
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
                            FTMNComicHistoryItem(item)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // --- BOTTOM SHEET FOR DIFFICULTY ---
        if (showDifficultySheet) {
            ModalBottomSheet(
                onDismissRequest = { showDifficultySheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFFFFF9C4)
            ) {
                FTMNDifficultySheet(
                    currentDiff = state.difficulty,
                    onSelect = { diff ->
                        viewModel.setDifficulty(context, diff)
                        showDifficultySheet = false
                    }
                )
            }
        }

        // --- DIALOGS ---
        if (showClearDialog) {
            FTMNComicConfirmationDialog(
                onConfirm = {
                    viewModel.clearHistory(context)
                    showClearDialog = false
                },
                onDismiss = { showClearDialog = false }
            )
        }

        if (showSlate) {
            FTMNDigitalSlateDialog(
                question = state.currentQuestion?.displaySequence,
                onDismiss = { showSlate = false }
            )
        }
    }
}

// --- 5. COMIC UI COMPONENTS ---

@Composable
fun FTMNComicTopBar(title: String, correctCount: Int, wrongCount: Int, onBack: () -> Unit) {
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

        FTMNScoreBox(
            count = correctCount,
            color = Color(0xFFA5D6A7),
            icon = Icons.Default.Check
        )

        Spacer(modifier = Modifier.width(8.dp))

        FTMNScoreBox(
            count = wrongCount,
            color = Color(0xFFEF9A9A),
            icon = Icons.Default.Close
        )
    }
}

@Composable
fun FTMNScoreBox(count: Int, color: Color, icon: ImageVector) {
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
                text = count.toString().toOdiaDigitsFTMN(),
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun FTMNComicQuestionCard(
    questionText: String,
    isAnswered: Boolean,
    isCorrect: Boolean,
    correctFullSequence: String?,
    onNext: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
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

                Text(
                    text = correctFullSequence ?: "",
                    fontFamily = OdiaFont,
                    fontSize = 22.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

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
                    text = questionText,
                    fontFamily = OdiaFont,
                    fontSize = 28.sp, // Reduced font size to accommodate longer sequences
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )
                Text(
                    text = "ନିଖୋଜ ସଂଖ୍ୟା ବାଛନ୍ତୁ",
                    fontFamily = OdiaFont,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun FTMNComicOptionButton(
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
fun FTMNComicFloatingButton(icon: ImageVector, bgColor: Color, onClick: () -> Unit) {
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
fun FTMNComicHistoryHeader(onClear: () -> Unit) {
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
fun FTMNComicHistoryItem(item: FTMNSolvedItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.sequence,
                fontFamily = OdiaFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "ଉତ୍ତର: ${item.userAnswer}",
                fontFamily = OdiaFont,
                fontSize = 16.sp,
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
fun FTMNComicConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 6.dp, start = 6.dp)
                    .background(Color.Black, RoundedCornerShape(20.dp))
            )
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
                    text = "ଆପଣ ନିଶ୍ଚିତ କି?",
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ସମସ୍ତ ତଥ୍ୟ ଲିଭିଯିବ ।",
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
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text("ନାଁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
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
fun FTMNDifficultySheet(currentDiff: FTMNDifficulty, onSelect: (FTMNDifficulty) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "କଠିନତା ସ୍ତର ବାଛନ୍ତୁ", // Select Difficulty Level
            fontFamily = OdiaFont,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val levels = listOf(
            Triple(FTMNDifficulty.EASY, "ସହଜ", Color(0xFFA5D6A7)),
            Triple(FTMNDifficulty.MEDIUM, "ମଧ୍ୟମ", Color(0xFFFFCC80)),
            Triple(FTMNDifficulty.HARD, "କଠିନ", Color(0xFFEF9A9A))
        )

        levels.forEach { (diff, title, color) ->
            FTMNModeItem(
                title = title,
                color = color,
                isSelected = currentDiff == diff,
                onClick = { onSelect(diff) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FTMNModeItem(title: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
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

// --- 6. DIGITAL SLATE (Reused) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FTMNDigitalSlateDialog(question: String?, onDismiss: () -> Unit) {
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