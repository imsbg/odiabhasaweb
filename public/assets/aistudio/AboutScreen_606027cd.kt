package `in`.sandeep.odiabhasa

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Copyright
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlin.random.Random

/*
* NOTE: To use the In-App Update feature, make sure you have added the Google Play Core library
* to your app's build.gradle (Module: app) file:
*
* implementation("com.google.android.play:app-update-ktx:2.1.0")
*
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity

    // -----------------------------------------------------------------
    // MOTION EFFECT LOGIC (ACCELEROMETER)
    // -----------------------------------------------------------------
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    // Stores the raw X-axis (Left/Right) and Y-axis (Up/Down) values
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.let {
                    // Axis X: Left/Right tilt (~9.8 to -9.8)
                    tiltX = it[0]
                    // Axis Y: Up/Down tilt (~9.8 vertical, ~0 flat)
                    tiltY = it[1]
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Logic for Zoom (Y-Axis) - Increased zoom effect
    // Lowered the base so it gets much smaller when flat, increased multiplier so it gets much bigger when raised
    val targetScale = 0.65f + (tiltY * 0.08f)

    // Logic for 3D Left/Right Tilt (X-Axis)
    // Multiplier determines how far it rotates based on tilt
    val targetRotationY = tiltX * 3.5f

    // Smooth & Slow animation for Scaling
    val animatedScale by animateFloatAsState(
        targetValue = targetScale.coerceIn(0.5f, 1.6f), // Expanded min/max bounds for deeper zoom
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow), // VeryLow stiffness makes it slow and fluid
        label = "LogoScaleMotion"
    )

    // Smooth & Slow animation for Left/Right 3D Tilt
    val animatedRotationY by animateFloatAsState(
        targetValue = targetRotationY.coerceIn(-40f, 40f), // Limit max rotation degrees
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow), // Slow fluid rotation
        label = "LogoRotationMotion"
    )

    // -----------------------------------------------------------------
    // IN-APP UPDATE LOGIC
    // -----------------------------------------------------------------
    val checkForUpdate = {
        if (activity != null) {
            val appUpdateManager = AppUpdateManagerFactory.create(context)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        activity,
                        123
                    )
                } else {
                    Toast.makeText(context, "ଆପଣ ସର୍ବଶେଷ ସଂସ୍କରଣ ବ୍ୟବହାର କରୁଛନ୍ତି", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "ଅପଡେଟ୍ ଯାଞ୍ଚ କରିବାରେ ବିଫଳ ହେଲା", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "କାର୍ଯ୍ୟ ବିଫଳ ହେଲା", Toast.LENGTH_SHORT).show()
        }
    }


    // -----------------------------------------------------------------
    // MATH CHALLENGE STATE
    // -----------------------------------------------------------------
    var showMathDialog by remember { mutableStateOf(false) }
    var pendingUrl by remember { mutableStateOf("") }
    var mathQuestion by remember { mutableStateOf("") }
    var expectedAnswer by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Helper to generate a new problem
    fun generateProblem() {
        val num1 = Random.nextInt(1, 21)
        val num2 = Random.nextInt(1, 11)
        val operation = Random.nextInt(0, 3)

        fun Int.toOdiaString(): String {
            return this.toString()
                .replace('0', '୦')
                .replace('1', '୧')
                .replace('2', '୨')
                .replace('3', '୩')
                .replace('4', '୪')
                .replace('5', '୫')
                .replace('6', '୬')
                .replace('7', '୭')
                .replace('8', '୮')
                .replace('9', '୯')
        }

        when (operation) {
            0 -> { // Addition
                expectedAnswer = num1 + num2
                mathQuestion = "${num1.toOdiaString()} + ${num2.toOdiaString()} = ?"
            }
            1 -> { // Subtraction
                val big = maxOf(num1, num2)
                val small = minOf(num1, num2)
                expectedAnswer = big - small
                mathQuestion = "${big.toOdiaString()} - ${small.toOdiaString()} = ?"
            }
            2 -> { // Multiplication
                expectedAnswer = num1 * num2
                mathQuestion = "${num1.toOdiaString()} × ${num2.toOdiaString()} = ?"
            }
        }
        userAnswer = ""
        isError = false
    }

    val openProtectedLink: (String) -> Unit = { url ->
        pendingUrl = url
        generateProblem()
        showMathDialog = true
    }

    val openDirectLink: (String) -> Unit = { url ->
        try {
            uriHandler.openUri(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 1. Get Version Name
    val rawVersionName = try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }

    // 2. Convert to Odia Digits
    val odiaVersionName = convertToOdiaDigits(rawVersionName)

    // Colors
    val ScreenBg = Color(0xFFF5F7FA)
    val TextBlack = Color(0xFF1D1D1D)
    val TextGray = Color.Gray

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ଆପ୍ ବିଷୟରେ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- LOGO SECTION (WITH MOTION) ---
            Image(
                painter = painterResource(id = R.drawable.logo_monochrome),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    // Apply both the zoom (scale) and 3D left/right tilt (rotationY) here
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                        rotationY = animatedRotationY // Applies the 3D left/right tilt
                        cameraDistance = 12f * density // Prevents perspective distortion for 3D rotation
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Name
            Text(
                text = "ଓଡ଼ିଆ ଭାଷା",
                fontSize = 24.sp,
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Version
            Text(
                text = "ସଂସ୍କରଣ $odiaVersionName",
                fontSize = 14.sp,
                color = TextGray,
                fontFamily = OdiaFont,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- NEW: APP UPDATE SECTION ---
            ModernSettingsGroup {
                ModernSettingsItem(
                    title = "ଅପଡେଟ୍ ଯାଞ୍ଚ କରନ୍ତୁ",
                    icon = Icons.Default.Update,
                    onClick = { checkForUpdate() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- LINKS SECTION ---

            // Group 1: Website (Protected)
            ModernSettingsGroup {
                ModernSettingsItem(
                    title = "ୱେବସାଇଟ୍ ଦେଖନ୍ତୁ",
                    icon = Icons.Default.Language,
                    onClick = { openProtectedLink("https://imsbg.github.io/odiabhasa") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Group 2: Contributors (Protected)
            SectionTitle("ସହଯୋଗୀ")
            ModernSettingsGroup {
                ModernSettingsItem(
                    title = "ସନ୍ଦୀପ୍ ବିଶ୍ୱାଳ ଜି (ନିର୍ମାତା)",
                    icon = Icons.Default.Person,
                    onClick = { openProtectedLink("https://www.instagram.com/sandeepbiswalg") }
                )
                SettingsDivider()
                ModernSettingsItem(
                    title = "ରୂପା",
                    icon = Icons.Default.Person,
                    onClick = { openProtectedLink("https://sbgb.pages.dev/rupa.html") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Group 3: Legal (Direct - No Question)
            SectionTitle("ଆଇନଗତ")
            ModernSettingsGroup {
                ModernSettingsItem(
                    title = "ଗୋପନୀୟତା ନୀତି",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { openDirectLink("https://imsbg.github.io/odiabhasa/privacy-policy?od=") }
                )
                SettingsDivider()
                ModernSettingsItem(
                    title = "ନିୟମ ଏବଂ ସର୍ତ୍ତାବଳୀ",
                    icon = Icons.Default.Description,
                    onClick = { openDirectLink("https://imsbg.github.io/odiabhasa/terms?od=") }
                )
                SettingsDivider()
                ModernSettingsItem(
                    title = "ଡିଜିଟାଲ୍ କପିରାଇଟ୍ ସୁରକ୍ଷା",
                    icon = Icons.Default.Copyright,
                    onClick = { openDirectLink("https://imsbg.github.io/odiabhasa/dmca?od=") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Group 4: Contact (Direct - No Question)
            SectionTitle("ଯୋଗାଯୋଗ")
            ModernSettingsGroup {
                ModernSettingsItem(
                    title = "ଆମକୁ ଇମେଲ୍ କରନ୍ତୁ",
                    icon = Icons.Default.Email,
                    onClick = { openDirectLink("mailto:message.sbg@gmail.com") }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Footer
            Text(
                text = "ଓଡ଼ିଆ ଭାଷା : ଆମ ଭାଷା, ଆମ ପରିଚୟ",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- PARENTAL GATE DIALOG ---
        if (showMathDialog) {
            AlertDialog(
                onDismissRequest = { showMathDialog = false },
                title = {
                    Text(
                        text = "ଅଭିଭାବକଙ୍କ ପାଇଁ ପ୍ରଶ୍ନ",
                        fontFamily = OdiaFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "ଏହି ଲିଙ୍କ୍‌ଟି ଆପଣଙ୍କୁ ଆମ ଆପ୍‌ ବାହାରେ ଏକ ଅନ୍ୟ ୱେବସାଇଟ୍‌କୁ ନେଇଯିବ । ଆପଣ କଣ ଆଗକୁ ବଢ଼ିବାକୁ ଚାହୁଁଛନ୍ତି? ତେବେ ଏହି ପ୍ରଶ୍ନ ର ଉତ୍ତର ଦିଅନ୍ତୁ :",
                            fontFamily = OdiaFont,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // The Math Question
                        Text(
                            text = mathQuestion,
                            fontFamily = OdiaFont,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = userAnswer,
                            onValueChange = { userAnswer = it },
                            label = { Text("ଉତ୍ତର", fontFamily = OdiaFont) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = isError
                        )

                        if (isError) {
                            Text(
                                text = "ଭୁଲ୍ ଉତ୍ତର, ଦୟାକରି ପୁନର୍ବାର ଚେଷ୍ଟା କରନ୍ତୁ",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                fontFamily = OdiaFont,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Normalize Odia digits to English digits if user types in Odia
                            val normalizedInput = userAnswer.map { char ->
                                when (char) {
                                    '୦' -> '0'; '୧' -> '1'; '୨' -> '2'; '୩' -> '3'; '୪' -> '4'
                                    '୫' -> '5'; '୬' -> '6'; '୭' -> '7'; '୭' -> '7'; '୮' -> '8'; '୯' -> '9'
                                    else -> char
                                }
                            }.joinToString("")

                            val inputInt = normalizedInput.toIntOrNull()

                            if (inputInt == expectedAnswer) {
                                showMathDialog = false
                                uriHandler.openUri(pendingUrl)
                            } else {
                                isError = true
                                userAnswer = ""
                            }
                        }
                    ) {
                        Text("ଠିକ୍ ଅଛି", fontFamily = OdiaFont)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showMathDialog = false }) {
                        Text("ବାତିଲ୍", fontFamily = OdiaFont, color = Color.Gray)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontFamily = OdiaFont,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, bottom = 8.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
fun ModernSettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
fun ModernSettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text
        Text(
            text = title,
            fontSize = 16.sp,
            fontFamily = OdiaFont,
            color = Color(0xFF1D1D1D),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // Arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        color = Color.LightGray.copy(alpha = 0.3f),
        thickness = 1.dp,
        modifier = Modifier.padding(start = 72.dp, end = 16.dp)
    )
}

/**
 * Converts English digits to Odia digits
 */
fun convertToOdiaDigits(input: String): String {
    val odiaDigits = arrayOf("୦", "୧", "୨", "୩", "୪", "୫", "୬", "୭", "୮", "୯")
    val sb = StringBuilder()
    for (char in input) {
        if (char.isDigit()) {
            val digit = char.toString().toInt()
            sb.append(odiaDigits[digit])
        } else {
            sb.append(char)
        }
    }
    return sb.toString()
}