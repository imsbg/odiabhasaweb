// --- START OF FILE Harmonium.kt ---
package `in`.sandeep.odiabhasa

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.audiofx.PresetReverb
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

// --- CONFIGURATION ---
private val odiaNumeralsMap = charArrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
fun Int.toOdiaNum(): String = this.toString().map { if (it.isDigit()) odiaNumeralsMap[it - '0'] else it }.joinToString("")

val noteNames = listOf("ସା", "ସା#", "ରେ", "ରେ#", "ଗା", "ମା", "ମା#", "ପା", "ପା#", "ଧା", "ଧା#", "ନି")

data class PianoKeyData(val id: Int, val midi: Int, val type: String, val odiaLabel: String, val engLabel: String)

val KEYBOARD_LAYOUT = listOf(
    // White Keys (14)
    PianoKeyData(0, 55, "white", "ଞ", "`"), PianoKeyData(2, 57, "white", "ଖ", "Q"),
    PianoKeyData(4, 59, "white", "ୱ", "W"), PianoKeyData(5, 60, "white", "ଏ", "E"),
    PianoKeyData(7, 62, "white", "ର", "R"), PianoKeyData(9, 64, "white", "ତ", "T"),
    PianoKeyData(10, 65, "white", "ୟ", "Y"), PianoKeyData(12, 67, "white", "ଉ", "U"),
    PianoKeyData(14, 69, "white", "ଇ", "I"), PianoKeyData(16, 71, "white", "ଓ", "O"),
    PianoKeyData(17, 72, "white", "ପ", "P"), PianoKeyData(19, 74, "white", "ଡ", "["),
    PianoKeyData(21, 76, "white", "ଢ", "]"), PianoKeyData(22, 77, "white", "ଣ", "\\"),

    // Black Keys (9)
    PianoKeyData(1, 56, "black", "୧", "1"), PianoKeyData(3, 58, "black", "୨", "2"),
    PianoKeyData(6, 61, "black", "୪", "4"), PianoKeyData(8, 63, "black", "୫", "5"),
    PianoKeyData(11, 66, "black", "୭", "7"), PianoKeyData(13, 68, "black", "୮", "8"),
    PianoKeyData(15, 70, "black", "୯", "9"), PianoKeyData(18, 73, "black", "-", "-"),
    PianoKeyData(20, 75, "black", "=", "=")
)

val HW_KEY_MAP_CHARS = mapOf(
    '`' to 55, '1' to 56, 'q' to 57, '2' to 58, 'w' to 59,
    'e' to 60, '4' to 61, 'r' to 62, '5' to 63, 't' to 64,
    'y' to 65, '7' to 66, 'u' to 67, '8' to 68, 'i' to 69,
    '9' to 70, 'o' to 71, 'p' to 72, '-' to 73, '[' to 74,
    '=' to 75, ']' to 76, '\\' to 77
)

// ==========================================
// CUSTOM SYNTH ENGINE (Voice Pool Architecture)
// ==========================================
class HarmoniumEngine(private val context: Context) {
    private var pcmData: ShortArray = ShortArray(0)
    private var baseSampleRate = 44100
    private var channels = 1

    private val engineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var reverb: PresetReverb? = null

    var volume = 0.3f
    var octave = 4
    var transpose = 0
    var isReverbOn = true

    // Voice Pool for polyphony without memory crashes
    private val MAX_VOICES = 12
    private val voices = mutableListOf<Voice>()

    init {
        engineScope.launch {
            loadWavPcm()
            setupReverb()
            initializeVoices()
        }
    }

    private fun loadWavPcm() {
        try {
            val stream = context.resources.openRawResource(R.raw.harmonium_sample)
            val bytes = stream.readBytes()
            stream.close()

            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
            var dataOffset = 12

            while (dataOffset < bytes.size - 8) {
                val chunkId = String(bytes, dataOffset, 4)
                val chunkSize = buffer.getInt(dataOffset + 4)

                if (chunkId == "fmt ") {
                    channels = buffer.getShort(dataOffset + 10).toInt()
                    baseSampleRate = buffer.getInt(dataOffset + 12)
                } else if (chunkId == "data") {
                    val shortCount = chunkSize / 2
                    pcmData = ShortArray(shortCount)
                    buffer.position(dataOffset + 8)
                    buffer.asShortBuffer().get(pcmData, 0, shortCount)
                    break
                }
                dataOffset += 8 + chunkSize
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun setupReverb() {
        try {
            reverb = PresetReverb(0, 0).apply {
                preset = PresetReverb.PRESET_LARGEHALL
                enabled = true
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun initializeVoices() {
        if (pcmData.isEmpty()) return
        val channelConfig = if (channels == 2) AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_OUT_MONO

        for (i in 0 until MAX_VOICES) {
            try {
                val track = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    baseSampleRate,
                    channelConfig,
                    AudioFormat.ENCODING_PCM_16BIT,
                    pcmData.size * 2,
                    AudioTrack.MODE_STATIC
                )
                track.write(pcmData, 0, pcmData.size)

                val frameCount = pcmData.size / channels
                if (frameCount > 0) {
                    val loopStart = (frameCount * 0.8).toInt().coerceAtMost(frameCount - 1)
                    track.setLoopPoints(loopStart, frameCount, -1)
                }

                reverb?.let {
                    track.attachAuxEffect(it.id)
                    track.setAuxEffectSendLevel(if (isReverbOn) 1.0f else 0.0f)
                }

                voices.add(Voice(track))
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun setReverbEnabled(enabled: Boolean) {
        isReverbOn = enabled
        voices.forEach { it.track.setAuxEffectSendLevel(if (enabled) 1.0f else 0.0f) }
    }

    @Synchronized
    fun noteOn(midiNote: Int) {
        if (voices.isEmpty()) return

        // Prevent double trigger of the exact same note
        if (voices.any { it.activeMidi == midiNote && !it.isFading }) return

        // 1. Find a completely free voice
        var voice = voices.find { it.activeMidi == null }
        // 2. If none, steal a fading voice
        if (voice == null) voice = voices.find { it.isFading }
        // 3. If still none (heavy chords), steal the oldest active voice
        if (voice == null) voice = voices.minByOrNull { it.lastUsedTime }

        val actualMidi = midiNote + ((octave - 4) * 12) + transpose
        val rate = 2.0.pow((actualMidi - 60) / 12.0).toFloat()
        val playbackSampleRate = (baseSampleRate * rate).toInt().coerceIn(4000, 192000)

        voice?.play(midiNote, playbackSampleRate, volume)
    }

    @Synchronized
    fun noteOff(midiNote: Int) {
        val voice = voices.find { it.activeMidi == midiNote && !it.isFading }
        voice?.stopFade(volume, isReverbOn)
    }

    fun release() {
        engineScope.cancel()
        voices.forEach {
            try { it.track.stop(); it.track.release() } catch (e: Exception) {}
        }
        voices.clear()
        reverb?.release()
    }

    // Individual synth voice manager
    inner class Voice(val track: AudioTrack) {
        var activeMidi: Int? = null
        var isFading = false
        var lastUsedTime = 0L
        private var fadeJob: Job? = null

        fun play(midi: Int, sampleRate: Int, targetVolume: Float) {
            fadeJob?.cancel()
            activeMidi = midi
            isFading = false
            lastUsedTime = System.currentTimeMillis()

            try {
                track.playbackRate = sampleRate
                track.setVolume(targetVolume)
                track.stop()
                track.reloadStaticData() // Instantly resets playhead to start
                track.play()
            } catch (e: Exception) {}
        }

        fun stopFade(currentVolume: Float, reverbOn: Boolean) {
            isFading = true
            fadeJob = engineScope.launch {
                val fadeDuration = if (reverbOn) 800L else 150L
                val steps = 15
                val delayTime = fadeDuration / steps

                for (i in steps downTo 0) {
                    if (!isActive) break
                    val curvedVolume = currentVolume * (i.toFloat() / steps).pow(1.5f)
                    try { track.setVolume(curvedVolume) } catch (e: Exception) {}
                    delay(delayTime)
                }

                try { track.stop() } catch (e: Exception) {}
                activeMidi = null
                isFading = false
            }
        }
    }
}

// ==========================================
// MAIN SCREEN COMPOSABLE
// ==========================================
@Composable
fun HarmoniumScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity

    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        onDispose { activity?.requestedOrientation = originalOrientation }
    }

    val engine = remember { HarmoniumEngine(context) }
    DisposableEffect(Unit) { onDispose { engine.release() } }

    var volume by remember { mutableFloatStateOf(30f) }
    var isReverbOn by remember { mutableStateOf(true) }
    var labelMode by remember { mutableIntStateOf(0) }
    var rootNoteIndex by remember { mutableIntStateOf(0) }
    var currentOctave by remember { mutableIntStateOf(4) }

    val activeKeys = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(volume) { engine.volume = volume / 100f }
    LaunchedEffect(isReverbOn) { engine.setReverbEnabled(isReverbOn) }
    LaunchedEffect(currentOctave, rootNoteIndex) {
        engine.octave = currentOctave
        engine.transpose = rootNoteIndex
    }

    fun handleNoteOn(midi: Int) {
        if (activeKeys[midi] != true) {
            activeKeys[midi] = true
            engine.noteOn(midi)
        }
    }

    fun handleNoteOff(midi: Int) {
        if (activeKeys[midi] == true) {
            activeKeys[midi] = false
            engine.noteOff(midi)
        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    BackHandler { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                val char = event.utf16CodePoint.toChar().lowercaseChar()
                val mappedMidi = HW_KEY_MAP_CHARS[char]
                if (mappedMidi != null) {
                    if (event.type == KeyEventType.KeyDown) {
                        handleNoteOn(mappedMidi)
                    } else if (event.type == KeyEventType.KeyUp) {
                        handleNoteOff(mappedMidi)
                    }
                    true
                } else false
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(0.4f).background(Color(0xFF1E1E1E))
                    .border(1.dp, Color(0xFF333333)).padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ପଛକୁ ଫେରନ୍ତୁ", tint = Color.White)
                }

                DashboardCard("ଧ୍ୱନି", Modifier.weight(1.5f)) {
                    Text("${volume.toInt().toOdiaNum()}%", color = Color(0xFF4CAF50), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Slider(
                        value = volume, onValueChange = { volume = it }, valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF4CAF50), activeTrackColor = Color(0xFF4CAF50))
                    )
                }

                DashboardCard("ପ୍ରତିଧ୍ୱନି", Modifier.weight(1f)) {
                    Switch(
                        checked = isReverbOn, onCheckedChange = { isReverbOn = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF4CAF50), checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f))
                    )
                    Text(if (isReverbOn) "ଚାଲୁ" else "ବନ୍ଦ", color = Color.Gray, fontSize = 12.sp)
                }

                DashboardCard("ଚିହ୍ନ", Modifier.weight(1.2f)) {
                    val labelNames = listOf("ଲୁକ୍କାୟିତ", "ଓଡ଼ିଆ", "English")
                    Button(
                        onClick = { labelMode = (labelMode + 1) % 3 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                        shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.fillMaxWidth().height(32.dp)
                    ) {
                        Text(labelNames[labelMode], color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                DashboardCard("ମୂଳ ସ୍ୱର (${noteNames[(rootNoteIndex % 12 + 12) % 12]})", Modifier.weight(1.2f)) {
                    Text(rootNoteIndex.toOdiaNum(), color = Color(0xFF4CAF50), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallButton("-") { rootNoteIndex-- }
                        SmallButton("+") { rootNoteIndex++ }
                    }
                }

                DashboardCard("ସପ୍ତକ", Modifier.weight(1.2f)) {
                    Text(currentOctave.toOdiaNum(), color = Color(0xFF4CAF50), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallButton("-") { if (currentOctave > 1) currentOctave-- }
                        SmallButton("+") { if (currentOctave < 7) currentOctave++ }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(0.6f).background(Color(0xFF222222))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight().shadow(20.dp, RoundedCornerShape(8.dp))
                        .background(Color.Black, RoundedCornerShape(8.dp)).padding(8.dp)
                ) {
                    KeyboardLayout(
                        labelMode = labelMode, activeKeys = activeKeys,
                        onNoteOn = { handleNoteOn(it) }, onNoteOff = { handleNoteOff(it) }
                    )
                }
            }
        }
    }
}

private data class KeyRect(val midi: Int, val rect: Rect, val isBlack: Boolean)

// ==========================================
// PIANO KEYBOARD UI (SLIDE & MULTI-TOUCH)
// ==========================================
@Composable
fun KeyboardLayout(
    labelMode: Int,
    activeKeys: Map<Int, Boolean>,
    onNoteOn: (Int) -> Unit,
    onNoteOff: (Int) -> Unit
) {
    val whiteKeys = KEYBOARD_LAYOUT.filter { it.type == "white" }
    val blackKeys = KEYBOARD_LAYOUT.filter { it.type == "black" }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val layoutWidth = constraints.maxWidth.toFloat()
        val layoutHeight = constraints.maxHeight.toFloat()
        val parentMaxWidth = this.maxWidth
        val parentMaxHeight = this.maxHeight

        val keyRects = remember(layoutWidth, layoutHeight) {
            val rects = mutableListOf<KeyRect>()
            val keyW = layoutWidth / whiteKeys.size
            val blackKeyW = keyW * 0.65f
            val blackKeyH = layoutHeight * 0.55f

            var wCount = 0
            whiteKeys.forEach {
                val left = wCount * keyW
                rects.add(KeyRect(it.midi, Rect(left, 0f, left + keyW, layoutHeight), false))
                wCount++
            }

            blackKeys.forEach { bk ->
                val wIdxBefore = when (bk.midi) {
                    56 -> 0; 58 -> 1; 61 -> 3; 63 -> 4; 66 -> 6; 68 -> 7; 70 -> 8; 73 -> 10; 75 -> 11
                    else -> 0
                }
                val center = (wIdxBefore + 1) * keyW
                val left = center - (blackKeyW / 2f)
                rects.add(KeyRect(bk.midi, Rect(left, 0f, left + blackKeyW, blackKeyH), true))
            }
            rects
        }

        // FLAWLESS TRACKING MAP: PointerID -> Currently Pressed Key MIDI
        val pointerToKeyMap = remember { mutableMapOf<Long, Int>() }

        val pointerModifier = Modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()

                    event.changes.forEach { change ->
                        val pointerId = change.id.value

                        if (change.pressed) {
                            val pos = change.position
                            val keyMidi = keyRects.firstOrNull { it.isBlack && it.rect.contains(pos) }?.midi
                                ?: keyRects.firstOrNull { !it.isBlack && it.rect.contains(pos) }?.midi

                            val previousKeyMidi = pointerToKeyMap[pointerId]

                            // Finger moved to a new key
                            if (keyMidi != previousKeyMidi) {
                                if (previousKeyMidi != null) onNoteOff(previousKeyMidi)
                                if (keyMidi != null) {
                                    onNoteOn(keyMidi)
                                    pointerToKeyMap[pointerId] = keyMidi
                                } else {
                                    // Finger slid off the keys entirely
                                    pointerToKeyMap.remove(pointerId)
                                }
                            }
                        } else {
                            // Finger Lifted up
                            val previousKeyMidi = pointerToKeyMap.remove(pointerId)
                            if (previousKeyMidi != null) {
                                onNoteOff(previousKeyMidi)
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = pointerModifier.fillMaxSize()) {
            val keyWidthDp = parentMaxWidth / whiteKeys.size
            val blackKeyWidthDp = keyWidthDp * 0.65f
            val blackKeyHeightDp = parentMaxHeight * 0.55f

            Row(Modifier.fillMaxSize()) {
                whiteKeys.forEach { keyData ->
                    PianoKey(
                        keyData = keyData, labelMode = labelMode, isBlack = false,
                        isActive = activeKeys[keyData.midi] == true, modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }

            blackKeys.forEach { keyData ->
                val wIdxBefore = when (keyData.midi) {
                    56 -> 0; 58 -> 1; 61 -> 3; 63 -> 4; 66 -> 6; 68 -> 7; 70 -> 8; 73 -> 10; 75 -> 11
                    else -> 0
                }
                val offsetDp = (keyWidthDp * (wIdxBefore + 1)) - (blackKeyWidthDp / 2)

                PianoKey(
                    keyData = keyData, labelMode = labelMode, isBlack = true,
                    isActive = activeKeys[keyData.midi] == true,
                    modifier = Modifier.width(blackKeyWidthDp).height(blackKeyHeightDp).absoluteOffset(x = offsetDp, y = 0.dp)
                )
            }
        }
    }
}

@Composable
fun PianoKey(keyData: PianoKeyData, labelMode: Int, isBlack: Boolean, isActive: Boolean, modifier: Modifier) {
    val baseColor = if (isBlack) Color(0xFF222222) else Color(0xFFFFFFFF)
    val activeColor = Color(0xFFFFCC00)
    val borderColor = if (isBlack) Color.Black else Color(0xFFCCCCCC)
    val textColor = if (isActive && !isBlack) Color.Black else if (isBlack) Color.White else Color.Gray

    Box(
        modifier = modifier
            .padding(horizontal = if (isBlack) 0.dp else 1.dp)
            .shadow(if (isActive) 0.dp else 4.dp, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(
                color = if (isActive) activeColor else baseColor,
                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
            )
            .border(1.dp, borderColor, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (labelMode != 0) {
            val textToShow = if (labelMode == 1) keyData.odiaLabel else keyData.engLabel
            Text(
                text = textToShow, color = textColor, fontSize = if (isBlack) 10.sp else 14.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = if (isBlack) 8.dp else 16.dp)
            )
        }
    }
}

@Composable
fun DashboardCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252525)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = Color(0xFF777777), fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
fun SmallButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(40.dp, 32.dp)
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}
// --- END OF FILE Harmonium.kt ---