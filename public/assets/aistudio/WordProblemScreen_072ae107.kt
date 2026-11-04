package `in`.sandeep.odiabhasa.ganita

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.sandeep.odiabhasa.R
import `in`.sandeep.odiabhasa.ganita.utils.toLocaleNumerals

// --- Comic Pop UI Theming ---
private val comicBlue = Color(0xFF3D5AFE)
private val comicGreen = Color(0xFF00C853)
private val comicRed = Color(0xFFD50000)
private val comicYellow = Color(0xFFFFD600)
private val comicBackground = Color(0xFFFFF8E1)
private val comicSurface = Color(0xFFFFFFFF)
private val comicOnSurface = Color(0xFF333333)

// 1. Define the Custom Odia Font Family
private val odiaFontFamily = FontFamily(
    Font(R.font.odia2, FontWeight.Normal) // Refers to res/font/odia1.ttf
)

// 2. Apply it to the Text Style
private val comicTextStyle = TextStyle(
    fontFamily = odiaFontFamily, // Changed from SansSerif to odiaFontFamily
    fontWeight = FontWeight.Bold, // Usually custom fonts look better with Bold or Normal
    color = comicOnSurface
)
// --- End Theming ---


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordProblemScreen(
    onBack: () -> Unit,
    viewModel: WordProblemViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val (questionText, solutionText) = remember(uiState.currentProblem, context) {
        uiState.currentProblem?.resolve(context) ?: Pair("ପ୍ରଶ୍ନ ଆସୁଛି...", "")
    }

    var showSlate by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = comicBackground,
        topBar = {
            TopAppBar(
                title = { Text("ଗାଣିତିକ କାହାଣୀ", fontWeight = FontWeight.Bold, style = comicTextStyle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ପଛକୁ ଯାଆନ୍ତୁ")
                    }
                },
                actions = {
                    // History Button
                    IconButton(onClick = { viewModel.toggleHistory() }) {
                        Icon(Icons.Default.History, contentDescription = "ଇତିହାସ", tint = comicBlue)
                    }

                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✔ ${uiState.correctAnswers.toLocaleNumerals(context)}",
                            color = comicGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            style = comicTextStyle // Apply Font
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✖ ${uiState.wrongAnswers.toLocaleNumerals(context)}",
                            color = comicRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            style = comicTextStyle // Apply Font
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSlate = true },
                shape = CircleShape,
                containerColor = comicBlue
            ) {
                Icon(Icons.Default.Edit, contentDescription = "ରଫ୍ ଖାତା", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Main Question Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isAnswerCorrect == null) comicSurface
                    else if (uiState.isAnswerCorrect == true) comicGreen
                    else comicRed
                ),
                border = BorderStroke(3.dp, comicOnSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isAnswerCorrect == null) {
                        // Show Question
                        if (uiState.currentProblem != null) {
                            AutoResizeText(
                                text = questionText,
                                // Apply font to question text
                                style = comicTextStyle.copy(fontSize = 32.sp, lineHeight = 42.sp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Show Feedback Message INSIDE the card
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (uiState.isAnswerCorrect == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (uiState.isAnswerCorrect == true) "ସଠିକ୍!" else "ଭୁଲ୍ ଉତ୍ତର",
                                style = comicTextStyle.copy(color = Color.White),
                                fontSize = 30.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ActionButtonsRow(
                onSkip = { viewModel.serveNextProblem(context) },
                onShowSolution = { viewModel.toggleSolution() }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // User Answer Display
            AnswerBox(userAnswer = uiState.userAnswer, isCorrect = uiState.isAnswerCorrect)
            Spacer(modifier = Modifier.height(16.dp))

            CustomKeypad(
                onNumberInput = { viewModel.onNumberInput(it) },
                onBackspace = { viewModel.onBackspace() },
                onSubmit = {
                    if (uiState.isAnswerCorrect == null) {
                        viewModel.checkAnswer(context, questionText)
                    } else {
                        viewModel.serveNextProblem(context)
                    }
                },
                isNextButton = uiState.isAnswerCorrect != null
            )

            // Solution Dialog
            if (uiState.showSolution) {
                AlertDialog(
                    onDismissRequest = { viewModel.toggleSolution() },
                    title = { Text("ସମାଧାନ", style = comicTextStyle, fontSize = 24.sp) },
                    text = { Text(solutionText, style = comicTextStyle.copy(fontWeight = FontWeight.Normal, fontSize = 18.sp)) },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.toggleSolution() },
                            colors = ButtonDefaults.buttonColors(containerColor = comicBlue)
                        ) { Text("ଠିକ୍ ଅଛି", color = Color.White, fontWeight = FontWeight.Bold, style = comicTextStyle) }
                    }
                )
            }

            // History Dialog
            if (uiState.showHistory) {
                HistoryDialog(
                    historyList = uiState.historyList,
                    onDismiss = { viewModel.toggleHistory() },
                    onClearHistory = { viewModel.clearHistory() }
                )
            }
        }
    }

    if (showSlate) {
        DigitalSlate(
            question = questionText,
            onDismiss = { showSlate = false }
        )
    }
}

@Composable
fun HistoryDialog(historyList: List<HistoryItem>, onDismiss: () -> Unit, onClearHistory: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = comicBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ସମାଧାନ କରିଥିବା ପ୍ରଶ୍ନ",
                        style = comicTextStyle,
                        fontSize = 24.sp
                    )
                    IconButton(onClick = onClearHistory) {
                        Icon(Icons.Default.Delete, contentDescription = "ସଫା କରନ୍ତୁ", tint = comicRed)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (historyList.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("କୌଣସି ତଥ୍ୟ ନାହିଁ", color = Color.Gray, style = comicTextStyle)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyList.reversed()) { item ->
                            HistoryItemRow(item)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = comicBlue)
                ) {
                    Text("ବନ୍ଦ କରନ୍ତୁ", color = Color.White, style = comicTextStyle)
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(item: HistoryItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = comicSurface),
        border = BorderStroke(1.dp, comicOnSurface.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = item.timestamp, fontSize = 12.sp, color = Color.Gray, style = comicTextStyle.copy(fontSize = 12.sp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.question, style = comicTextStyle.copy(fontWeight = FontWeight.Normal, fontSize = 14.sp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "ଉତ୍ତର: ${item.userAnswer}", color = comicBlue, fontWeight = FontWeight.Bold, style = comicTextStyle)
                Text(
                    text = if (item.result == "ସଠିକ୍") "✔ ସଠିକ୍" else if (item.result == "ଭୁଲ୍") "✖ ଭୁଲ୍" else "⚠ ଛାଡ଼ିଛନ୍ତି",
                    color = if (item.result == "ସଠିକ୍") comicGreen else if (item.result == "ଭୁଲ୍") comicRed else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    style = comicTextStyle
                )
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(onSkip: () -> Unit, onShowSolution: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = onSkip,
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = comicYellow)
        ) {
            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = null, modifier = Modifier.padding(end = 8.dp), tint = comicOnSurface)
            Text(text = "ଛାଡ଼ିଦିଅନ୍ତୁ", style = comicTextStyle)
        }
        Button(
            onClick = onShowSolution,
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = comicBlue)
        ) {
            Icon(Icons.Default.Lightbulb, contentDescription = "ସମାଧାନ ଦେଖନ୍ତୁ", tint = comicYellow)
        }
    }
}

@Composable
fun AutoResizeText(text: String, modifier: Modifier = Modifier, color: Color = comicOnSurface, textAlign: TextAlign? = null, style: TextStyle = LocalTextStyle.current,) {
    var scaledTextStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    Text(text = text, color = color, textAlign = textAlign, modifier = modifier.drawWithContent { if (readyToDraw) { drawContent() } }, onTextLayout = { textLayoutResult -> if (textLayoutResult.didOverflowHeight) { scaledTextStyle = scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.95) } else { readyToDraw = true } }, style = scaledTextStyle, softWrap = true, maxLines = 10)
}

@Composable
fun AnswerBox(userAnswer: String, isCorrect: Boolean?) {
    val context = LocalContext.current
    val borderColor = when (isCorrect) {
        true -> comicGreen
        false -> comicRed
        null -> comicBlue
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(64.dp)
            .background(comicSurface, RoundedCornerShape(16.dp))
            .border(4.dp, borderColor, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (userAnswer.isEmpty()) "_" else userAnswer.toLocaleNumerals(context),
            style = comicTextStyle,
            fontSize = 36.sp,
            color = if (isCorrect == null) comicOnSurface else borderColor
        )
    }
}

@Composable
fun CustomKeypad(onNumberInput: (String) -> Unit, onBackspace: () -> Unit, onSubmit: () -> Unit, isNextButton: Boolean) {
    val buttons = (1..9).map { it.toString() } + listOf("0")
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        buttons.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { number -> NumberButton(text = number, onClick = { onNumberInput(number) }) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            ActionButton(onClick = onBackspace, isSubmit = false, isNext = false)
            ActionButton(onClick = onSubmit, isSubmit = true, isNext = isNextButton)
        }
    }
}

@Composable
fun NumberButton(text: String, onClick: () -> Unit) {
    val context = LocalContext.current
    Button(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = comicSurface),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        border = BorderStroke(2.dp, comicOnSurface)
    ) {
        Text(text.toLocaleNumerals(context), style = comicTextStyle, fontSize = 28.sp)
    }
}

@Composable
fun ActionButton(onClick: () -> Unit, isSubmit: Boolean, isNext: Boolean) {
    val (bgColor, contentColor) = when {
        isNext -> comicGreen to Color.White
        isSubmit -> comicBlue to Color.White
        else -> comicRed to Color.White
    }
    val modifier = if (isSubmit) Modifier.width(152.dp).height(72.dp) else Modifier.size(72.dp)

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        when {
            isNext -> Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "ପରବର୍ତ୍ତୀ", modifier = Modifier.size(36.dp), tint = contentColor)
            isSubmit -> Icon(Icons.Default.Check, contentDescription = "ଦାଖଲ କରନ୍ତୁ", modifier = Modifier.size(36.dp), tint = contentColor)
            else -> Icon(Icons.Default.Backspace, contentDescription = "କାଟିଦିଅନ୍ତୁ", tint = contentColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DigitalSlate(question: String, onDismiss: () -> Unit) {
    val completedPaths = remember { mutableStateListOf<Path>() }
    val undonePaths = remember { mutableStateListOf<Path>() }
    var currentPathPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    val drawColor = comicOnSurface
    val slateBackgroundColor = comicBackground

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ରଫ୍ ଖାତା", style = comicTextStyle) },
                    navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "ସ୍ଲେଟ୍ ବନ୍ଦ କରନ୍ତୁ") } },
                    actions = {
                        IconButton(onClick = { if (completedPaths.isNotEmpty()) undonePaths.add(completedPaths.removeLast()) }, enabled = completedPaths.isNotEmpty()) { Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "ପୂର୍ବବତ୍ କରନ୍ତୁ") }
                        IconButton(onClick = { if (undonePaths.isNotEmpty()) completedPaths.add(undonePaths.removeLast()) }, enabled = undonePaths.isNotEmpty()) { Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "ପୁନଃ କରନ୍ତୁ") }
                        IconButton(onClick = { completedPaths.clear(); undonePaths.clear() }, enabled = completedPaths.isNotEmpty()) { Icon(Icons.Default.DeleteOutline, contentDescription = "ସବୁ ମିଟାନ୍ତୁ") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = comicYellow)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(max = 120.dp).verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = question, textAlign = TextAlign.Center, style = comicTextStyle.copy(fontSize = 18.sp), color = comicOnSurface)
                }
                HorizontalDivider(thickness = 1.dp, color = comicOnSurface.copy(alpha = 0.5f))
                Canvas(
                    modifier = Modifier.fillMaxWidth().weight(1f).background(slateBackgroundColor).pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset -> undonePaths.clear(); currentPathPoints = listOf(offset) },
                            onDrag = { change, _ -> change.consume(); currentPathPoints = currentPathPoints + change.position },
                            onDragEnd = {
                                if (currentPathPoints.size > 1) {
                                    val path = Path().apply { moveTo(currentPathPoints.first().x, currentPathPoints.first().y); currentPathPoints.drop(1).forEach { lineTo(it.x, it.y) } }
                                    completedPaths.add(path)
                                }
                                currentPathPoints = emptyList()
                            }
                        )
                    }
                ) {
                    completedPaths.forEach { path -> drawPath(path = path, color = drawColor, style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
                    if (currentPathPoints.size > 1) {
                        val currentDrawPath = Path().apply { moveTo(currentPathPoints.first().x, currentPathPoints.first().y); currentPathPoints.drop(1).forEach { lineTo(it.x, it.y) } }
                        drawPath(path = currentDrawPath, color = drawColor, style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                }
            }
        }
    }
}