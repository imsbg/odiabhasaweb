// FILE: in/sandeep/odiabhasa/ganita/LogicPuzzleScreen.kt
package `in`.sandeep.odiabhasa.ganita

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.sandeep.odiabhasa.OdiaFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

// --- 1. DATA MODELS ---

data class LogicGameBalloon(
    val id: Int,
    val number: Int,
    val color: Color,
    var xPercent: Float,
    var yPercent: Float,
    val baseSpeed: Float,
    var isPopped: Boolean = false,
    var isWiggling: Boolean = false
)

data class GameCloud(
    val id: Int,
    var xPercent: Float,
    val yPercent: Float,
    val speed: Float,
    val size: Dp
)

data class LogicConfettiParticle(
    val id: Int, val color: Color, var x: Float, var y: Float,
    val angle: Double, val speed: Float, var alpha: Float = 1.0f
)

data class BalloonLogicState(
    val balloons: List<LogicGameBalloon> = emptyList(),
    val nextNumberToPop: Int = 0,
    val isLevelWon: Boolean = false,
    val correctClicks: Int = 0,
    val wrongClicks: Int = 0,
    val isGameFinished: Boolean = false,
    val clouds: List<GameCloud> = emptyList()
)

// --- 2. VIEW MODEL ---

class BalloonLogicViewModel : ViewModel() {
    private val PREFS_NAME = "OdiaBalloonGamePrefs_v3"
    private val KEY_CORRECT = "balloon_correct_clicks"
    private val KEY_WRONG = "balloon_wrong_clicks"
    private val WIN_THRESHOLD = 20
    private val BALLOON_WIDTH_PERCENT = 0.2f // Approx 80dp on a 400dp screen

    var logicState by mutableStateOf(BalloonLogicState())
        private set

    var confettiParticles = mutableStateListOf<LogicConfettiParticle>()

    fun initGame(context: Context) {
        loadData(context)
        if (logicState.balloons.isEmpty()) {
            startNewLevel()
        }
        if (logicState.clouds.isEmpty()) {
            initClouds()
        }
    }

    private fun initClouds() {
        val newClouds = List(4) { index ->
            GameCloud(
                id = index,
                xPercent = Random.nextFloat() * 1.2f, // Start some off-screen
                yPercent = 0.1f + (Random.nextFloat() * 0.5f),
                speed = 0.0005f + (Random.nextFloat() * 0.0005f),
                size = (80 + Random.nextInt(40)).dp
            )
        }
        logicState = logicState.copy(clouds = newClouds)
    }

    private fun startNewLevel() {
        val numbers = mutableSetOf<Int>()
        while (numbers.size < 5) {
            numbers.add(Random.nextInt(1, 50))
        }
        val sortedNumbers = numbers.sorted()

        val colors = CBalloonColors.shuffled()

        val newBalloons = numbers.mapIndexed { index, num ->
            LogicGameBalloon(
                id = index, number = num, color = colors[index % colors.size],
                xPercent = Random.nextFloat() * (1f - BALLOON_WIDTH_PERCENT),
                yPercent = 1.0f + (Random.nextFloat() * 0.5f),
                baseSpeed = 0.002f + (Random.nextFloat() * 0.0025f)
            )
        }

        logicState = logicState.copy(
            balloons = newBalloons,
            nextNumberToPop = sortedNumbers.first(),
            isLevelWon = false
        )
        confettiParticles.clear()
    }

    fun resetFullGame(context: Context) {
        logicState = logicState.copy(correctClicks = 0, wrongClicks = 0, isGameFinished = false)
        saveData(context)
        startNewLevel()
    }

    private fun getSpeedMultiplier(): Float {
        val totalTaps = logicState.correctClicks + logicState.wrongClicks
        return when {
            totalTaps > 100 -> 1.5f
            totalTaps > 50 -> 1.2f
            else -> 1.0f
        }
    }

    fun updateGameLoop() {
        if (logicState.isGameFinished) return

        val speedMultiplier = getSpeedMultiplier()

        // Update Balloons
        val updatedBalloons = logicState.balloons.map { b ->
            if (!b.isPopped) {
                var newY = b.yPercent - (b.baseSpeed * speedMultiplier)
                if (newY < -0.2f) { // Respawn
                    newY = 1.1f
                    b.xPercent = Random.nextFloat() * (1f - BALLOON_WIDTH_PERCENT)
                }
                b.copy(yPercent = newY)
            } else b
        }

        // Update Clouds
        val updatedClouds = logicState.clouds.map { c ->
            var newX = c.xPercent - c.speed
            if (newX < -0.3f) { // Wrap around
                newX = 1.1f
            }
            c.copy(xPercent = newX)
        }

        logicState = logicState.copy(balloons = updatedBalloons, clouds = updatedClouds)
    }

    fun updateParticles() {
        confettiParticles.removeAll { p ->
            p.x += (cos(p.angle) * p.speed).toFloat(); p.y += (sin(p.angle) * p.speed).toFloat()
            p.alpha -= 0.02f; p.alpha <= 0f
        }
    }

    fun onBalloonTap(context: Context, balloon: LogicGameBalloon, screenW: Float, screenH: Float) {
        if (logicState.isGameFinished || logicState.isLevelWon || balloon.isPopped) return

        if (balloon.number == logicState.nextNumberToPop) {
            // CORRECT
            val newCorrectCount = logicState.correctClicks + 1
            spawnConfetti(balloon.xPercent * screenW, balloon.yPercent * screenH, balloon.color)
            val updatedBalloons = logicState.balloons.map { if (it.id == balloon.id) it.copy(isPopped = true) else it }
            val remaining = updatedBalloons.filter { !it.isPopped }.map { it.number }.sorted()

            if (remaining.isEmpty()) { // Level Won
                if (newCorrectCount >= WIN_THRESHOLD) {
                    logicState = logicState.copy(balloons = updatedBalloons, isGameFinished = true, correctClicks = newCorrectCount)
                } else {
                    logicState = logicState.copy(balloons = updatedBalloons, isLevelWon = true, correctClicks = newCorrectCount)
                }
            } else { // Continue level
                logicState = logicState.copy(balloons = updatedBalloons, nextNumberToPop = remaining.first(), correctClicks = newCorrectCount)
            }
        } else {
            // WRONG
            val updatedBalloons = logicState.balloons.map { if (it.id == balloon.id) it.copy(isWiggling = true) else it }
            logicState = logicState.copy(balloons = updatedBalloons, wrongClicks = logicState.wrongClicks + 1)
        }
        saveData(context)
    }

    fun stopWiggle(id: Int) {
        val updatedBalloons = logicState.balloons.map { if (it.id == id) it.copy(isWiggling = false) else it }
        logicState = logicState.copy(balloons = updatedBalloons)
    }

    fun triggerNextLevel() { startNewLevel() }

    private fun spawnConfetti(x: Float, y: Float, color: Color) { for (i in 0..20) confettiParticles.add(LogicConfettiParticle(id = Random.nextInt(), color = color, x = x, y = y, angle = Random.nextDouble() * 2 * Math.PI, speed = 5f + Random.nextFloat() * 10f)) }
    private fun saveData(context: Context) { val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); prefs.edit().putInt(KEY_CORRECT, logicState.correctClicks).putInt(KEY_WRONG, logicState.wrongClicks).apply() }
    private fun loadData(context: Context) { val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); val correct = prefs.getInt(KEY_CORRECT, 0); val wrong = prefs.getInt(KEY_WRONG, 0); logicState = logicState.copy(correctClicks = correct, wrongClicks = wrong) }

    companion object {
        val CBalloonColors = listOf(Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFF81C784), Color(0xFFFFF176), Color(0xFFBA68C8), Color(0xFFFF8A65))
    }
}

// --- 3. HELPER ---
private fun Int.toOdiaDigitsLogic(): String { val odiaNumerals = arrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯'); return this.toString().map { char -> if (char.isDigit()) odiaNumerals[char.toString().toInt()] else char }.joinToString("") }

// --- 4. MAIN SCREEN ---
@Composable
fun LogicPuzzleScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: BalloonLogicViewModel = viewModel()

    LaunchedEffect(Unit) { viewModel.initGame(context) }
    LaunchedEffect(viewModel.logicState.isGameFinished) { while (isActive) { viewModel.updateGameLoop(); viewModel.updateParticles(); delay(16L) } }
    LaunchedEffect(viewModel.logicState.isLevelWon) { if (viewModel.logicState.isLevelWon) { delay(1500L); viewModel.triggerNextLevel() } }

    val state = viewModel.logicState

    Scaffold(
        containerColor = Color(0xFFE0F7FA),
        topBar = {
            BalloonGameTopBar(
                correctCount = state.correctClicks,
                wrongCount = state.wrongClicks,
                onBack = onBack
            )
        }
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            val screenW = maxWidth
            val screenH = maxHeight
            val density = LocalDensity.current

            // Top and Bottom Boundary Lines
            Divider(
                modifier = Modifier.align(Alignment.TopCenter),
                color = Color.Black.copy(alpha = 0.2f),
                thickness = 2.dp
            )
            Divider(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
                color = Color.Black.copy(alpha = 0.2f),
                thickness = 2.dp
            )

            // Clouds
            state.clouds.forEach { cloud ->
                MovingCloudItem(cloud = cloud, maxWidth = screenW)
            }

            // Background text
            if (!state.isGameFinished) {
                Text(
                    text = "ଛୋଟରୁ ବଡ଼\nଫୁଟାନ୍ତୁ",
                    modifier = Modifier.align(Alignment.Center).padding(bottom = 100.dp),
                    fontFamily = OdiaFont, fontSize = 32.sp, fontWeight = FontWeight.Black,
                    color = Color.Black.copy(alpha = 0.1f),
                    textAlign = TextAlign.Center, lineHeight = 40.sp
                )
            }

            // Balloons
            state.balloons.forEach { balloon ->
                if (!balloon.isPopped) {
                    AnimatedBalloonItem(
                        balloon = balloon, maxWidth = screenW, maxHeight = screenH,
                        onTap = { viewModel.onBalloonTap(context, balloon, with(density) { screenW.toPx() }, with(density) { screenH.toPx() }) },
                        onWiggleFinished = { viewModel.stopWiggle(balloon.id) }
                    )
                }
            }

            // Restart Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(50.dp)
                    .border(2.dp, Color.Black, CircleShape)
                    .background(Color.White, CircleShape)
                    .clip(CircleShape)
                    .clickable { viewModel.resetFullGame(context) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Restart Game",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }

            // Confetti
            viewModel.confettiParticles.forEach { p -> Box(modifier = Modifier.offset { IntOffset(p.x.roundToInt(), p.y.roundToInt()) }.size(10.dp).background(p.color.copy(alpha = p.alpha), CircleShape)) }

            // Final Win Overlay
            if (state.isGameFinished) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                    FinalCongratsCard(onPlayAgain = { viewModel.resetFullGame(context) })
                }
            }
        }
    }
}

// --- 5. UI COMPONENTS ---

@Composable
fun BalloonGameTopBar(correctCount: Int, wrongCount: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Box(
            modifier = Modifier.size(44.dp).border(2.dp, Color.Black, CircleShape)
                .background(Color.White, CircleShape).clip(CircleShape).clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black) }

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = "ବେଲୁନ୍ ଖେଳ", fontFamily = OdiaFont, fontSize = 22.sp,
            fontWeight = FontWeight.Black, color = Color.Black
        )

        // Spacer to push scores to the right
        Spacer(Modifier.weight(1f))

        // Correct Score (Green bar)
        Row(
            modifier = Modifier
                .height(40.dp)
                .widthIn(min = 70.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
                .background(Color(0xFFA5D6A7), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = correctCount.toOdiaDigitsLogic(), fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Wrong Score (White circle)
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(2.dp, Color.Black, CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = wrongCount.toOdiaDigitsLogic(),
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AnimatedBalloonItem(
    balloon: LogicGameBalloon, maxWidth: Dp, maxHeight: Dp,
    onTap: () -> Unit, onWiggleFinished: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    LaunchedEffect(balloon.isWiggling) { if (balloon.isWiggling) { repeat(3) { offsetX.animateTo(10f, tween(50)); offsetX.animateTo(-10f, tween(50)) }; offsetX.animateTo(0f); onWiggleFinished() } }

    val pxX = (balloon.xPercent * with(LocalDensity.current) { maxWidth.toPx() }) + offsetX.value
    val pxY = (balloon.yPercent * with(LocalDensity.current) { maxHeight.toPx() })

    Box(modifier = Modifier.offset { IntOffset(pxX.roundToInt(), pxY.roundToInt()) }.size(80.dp, 100.dp).clickable(remember { MutableInteractionSource() }, null, onClick = onTap)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width / 2, size.height)
                cubicTo(x1 = -10f, y1 = size.height * 0.4f, x2 = 0f, y2 = 0f, x3 = size.width / 2, y3 = 0f)
                cubicTo(x1 = size.width, y1 = 0f, x2 = size.width + 10f, y2 = size.height * 0.4f, x3 = size.width / 2, y3 = size.height)
                close()
            }
            drawPath(path = path, color = balloon.color, style = Fill)
            drawPath(path = path, color = Color.Black, style = Stroke(width = 4f))
            drawLine(color = Color.Black, start = Offset(size.width/2, size.height), end = Offset(size.width/2, size.height + 40f), strokeWidth = 4f)
            drawOval(color = Color.White.copy(0.5f), topLeft = Offset(15f, 15f), size = Size(20f, 30f))
        }
        Box(modifier = Modifier.fillMaxSize().padding(bottom = 10.dp), contentAlignment = Alignment.Center) {
            Text(text = balloon.number.toOdiaDigitsLogic(), fontFamily = OdiaFont, fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }
    }
}

@Composable
fun MovingCloudItem(cloud: GameCloud, maxWidth: Dp) {
    val pxX = (cloud.xPercent * with(LocalDensity.current) { maxWidth.toPx() })
    val pxY = (cloud.yPercent * with(LocalDensity.current) { LocalDensity.current.density * 1000 }) // Use a large fixed number for height reference

    Canvas(modifier = Modifier.offset { IntOffset(pxX.roundToInt(), pxY.roundToInt()) }.size(cloud.size)) {
        val cloudColor = Color.White.copy(alpha = 0.8f)
        drawCircle(cloudColor, radius = size.width * 0.3f, center = Offset(size.width * 0.2f, size.height * 0.5f))
        drawCircle(cloudColor, radius = size.width * 0.4f, center = Offset(size.width * 0.5f, size.height * 0.4f))
        drawCircle(cloudColor, radius = size.width * 0.35f, center = Offset(size.width * 0.8f, size.height * 0.55f))
    }
}

@Composable
fun FinalCongratsCard(onPlayAgain: () -> Unit) {
    Box(modifier = Modifier.padding(24.dp)) {
        Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(Color.Black, RoundedCornerShape(20.dp)))
        Column(
            modifier = Modifier.border(3.dp, Color.Black, RoundedCornerShape(20.dp)).background(Color.White, RoundedCornerShape(20.dp)).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("ଅଭିନନ୍ଦନ! 🎉", fontFamily = OdiaFont, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text("ଆପଣ ଖେଳ ଜିତିଗଲେ!", fontFamily = OdiaFont, fontSize = 20.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onPlayAgain,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Refresh, null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ପୁଣି ଖେଳନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            }
        }
    }
}