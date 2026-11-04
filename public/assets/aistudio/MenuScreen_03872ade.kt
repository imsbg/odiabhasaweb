package `in`.sandeep.odiabhasa

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onNavBack: () -> Unit,
    onNavToAbout: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current // Added for Share Intent
    val scrollState = rememberScrollState()

    // --- MATH CHALLENGE STATE ---
    var showMathDialog by remember { mutableStateOf(false) }
    var pendingUrl by remember { mutableStateOf("") }
    var mathQuestion by remember { mutableStateOf("") }
    var expectedAnswer by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Share Message
    val shareMessage = """
        ନମସ୍କାର !
        ଆମ ମାତୃଭାଷା ଓଡ଼ିଆ ଶିଖିବା ଏବେ ହେବ ଆହୁରି ସହଜ ଏବଂ ମଜାଦାର । ମୁଁ ଏକ ସୁନ୍ଦର ଆପ୍ ବ୍ୟବହାର କରୁଛି, ଯାହା ପିଲାମାନଙ୍କୁ ଓଡ଼ିଆ ଅକ୍ଷର ଲେଖିବା ଓ ପଢ଼ିବାରେ ବହୁତ ସାହାଯ୍ୟ କରୁଛି ।

        ● ଏହି ଆପ୍‌ର କିଛି ବିଶେଷତ୍ୱ :
        ➜ ଅଫ୍‌ଲାଇନ୍ ଏବଂ ସମ୍ପୂର୍ଣ୍ଣ ମାଗଣା : ଏଥିପାଇଁ କୌଣସି ଇଣ୍ଟରନେଟ୍ ବା ଟଙ୍କାର ଆବଶ୍ୟକତା ନାହିଁ ।
        ➜ ବିଜ୍ଞାପନ ମୁକ୍ତ : ପଢ଼ିବା ସମୟରେ କୌଣସି ବିରକ୍ତିକର ବିଜ୍ଞାପନ ଆସିବ ନାହିଁ ।
        ➜ ଲେଖିବା ଅଭ୍ୟାସ : ଏଥିରେ ସୁନ୍ଦର ଡିଜିଟାଲ୍ ସ୍ଲେଟ୍ ଅଛି , ଯେଉଁଥିରେ ପିଲାମାନେ ଅକ୍ଷର ଲେଖିବା ଅଭ୍ୟାସ କରିପାରିବେ ।
        ➜ ପାଠ୍ୟପୁସ୍ତକ : ଏଠାରେ ପ୍ରଥମ ଶ୍ରେଣୀରୁ ଦଶମ ଶ୍ରେଣୀ ପର୍ଯ୍ୟନ୍ତ ସମସ୍ତ ବହି ମଧ୍ୟ ଉପଲବ୍ଧ ।
        ➜ ଓଡ଼ିଆ ଲାଇବ୍ରେରୀ : ଏଠାରେ ପଢ଼ିବା ପାଇଁ ଅନେକ ଓଡ଼ିଆ ବିଷୟ ମଧ୍ୟ ଅଛି ।

        ଆପଣ ବି ଏହାକୁ ଥରେ ନିଶ୍ଚୟ ବ୍ୟବହାର କରନ୍ତୁ ଏବଂ ନିଜ ପିଲାମାନଙ୍କୁ ଓଡ଼ିଆ ଶିଖିବାରେ ସାହାଯ୍ୟ କରନ୍ତୁ ।

        ➜ ଡାଉନଲୋଡ୍ କରିବା ପାଇଁ ଏହି ଲିଙ୍କ୍ କୁ କ୍ଲିକ୍ କରନ୍ତୁ :https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa
        ଆମ ଭାଷା, ଆମ ପରିଚୟ 
    """.trimIndent()

    // Helper to generate a new problem
    fun generateProblem() {
        val num1 = Random.nextInt(1, 21) // 1 to 20
        val num2 = Random.nextInt(1, 11) // Keep second number smaller for easier multiplication
        val operation = Random.nextInt(0, 3) // 0: Add, 1: Sub, 2: Mul

        // Helper to convert Int to Odia String
        fun Int.toOdia(): String {
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
                mathQuestion = "${num1.toOdia()} + ${num2.toOdia()} = ?"
            }
            1 -> { // Subtraction (Ensure positive result)
                val big = maxOf(num1, num2)
                val small = minOf(num1, num2)
                expectedAnswer = big - small
                mathQuestion = "${big.toOdia()} - ${small.toOdia()} = ?"
            }
            2 -> { // Multiplication
                expectedAnswer = num1 * num2
                mathQuestion = "${num1.toOdia()} × ${num2.toOdia()} = ?"
            }
        }
        userAnswer = ""
        isError = false
    }

    // Function to trigger the flow
    val initiateExternalLink: (String) -> Unit = { url ->
        pendingUrl = url
        generateProblem()
        showMathDialog = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("ଅଧିକ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            // --- GENERAL SECTION ---
            Text(text = "ସାଧାରଣ", fontSize = 18.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    MenuActionItem("ଆପ୍ ବିଷୟରେ") { onNavToAbout() } // Internal nav, no math needed
                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    // Added Rate App Menu Item (With Parental Gate)
                    MenuActionItem("ଆପ୍‌କୁ ମୂଲ୍ୟାଙ୍କନ କରନ୍ତୁ") {
                        initiateExternalLink("https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa")
                    }
                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    // Added Contact / Update Menu Item (With Parental Gate)
                    MenuActionItem("ଯୋଗାଯୋଗ / ସୂଚନା ପାଆନ୍ତୁ") {
                        initiateExternalLink("https://instagram.com/sbgapps/")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- COLOURFUL SHARE BUTTON ---
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareMessage)
                    }
                    context.startActivity(Intent.createChooser(intent, "ସାଙ୍ଗମାନଙ୍କୁ ଜଣାନ୍ତୁ"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50), // Colourful Green for Share
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share Icon")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ସାଙ୍ଗମାନଙ୍କୁ ଜଣାନ୍ତୁ",
                    fontSize = 18.sp,
                    fontFamily = OdiaFont,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // --- MORE APPS SECTION ---
            Text(text = "ସନ୍ଦୀପ୍ ବିଶ୍ୱାଳ ଜି'ଙ୍କ ଅନ୍ୟ ଆପ୍", fontSize = 18.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(10.dp))

            MoreAppCard(
                name = "ଆଟମିକ୍ ଗୁରୁ",
                desc = "ଇଣ୍ଟରାକ୍ଟିଭ୍ ପିରିଓଡିକ୍ ଟେବୁଲ୍ ଶିକ୍ଷା ଆପ୍",
                url = "https://play.google.com/store/apps/details?id=com.sandeep.atomicguru",
                onLinkClick = initiateExternalLink
            )
            Spacer(modifier = Modifier.height(10.dp))
            MoreAppCard(
                name = "ପ୍ଲିକ୍ସୋ ଗେମ୍ସ୍",
                desc = "ଯେଉଁଠି ପ୍ରତିଟି ଚାଲ୍ ଗୁରୁତ୍ୱପୂର୍ଣ୍ଣ",
                url = "https://play.google.com/store/apps/details?id=in.sandeep.plyxogames",
                onLinkClick = initiateExternalLink
            )

            Spacer(modifier = Modifier.height(25.dp))

            // --- FOLLOW ME SECTION ---
            Text(text = "ମୋତେ ଫଲୋ କରନ୍ତୁ", fontSize = 18.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SocialButton("ଇନ୍\u200Cଷ୍ଟାଗ୍ରାମ୍", "https://www.instagram.com/sandeepbiswalg", initiateExternalLink)
                SocialButton("ଏକ୍ସ", "https://x.com/SandeepBiswalG", initiateExternalLink)
                SocialButton("ସବୁ ପ୍ରୋଫାଇଲ୍", "https://imsbg.github.io/", initiateExternalLink)
            }

            Spacer(modifier = Modifier.height(40.dp))
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
                            // Normalize Odia digits to English digits for parsing if user types in Odia keyboard
                            val normalizedInput = userAnswer.map { char ->
                                when (char) {
                                    '୦' -> '0'; '୧' -> '1'; '୨' -> '2'; '୩' -> '3'; '୪' -> '4'
                                    '୫' -> '5'; '୬' -> '6'; '୭' -> '7'; '୮' -> '8'; '୯' -> '9'
                                    else -> char
                                }
                            }.joinToString("")

                            val inputInt = normalizedInput.toIntOrNull()

                            if (inputInt == expectedAnswer) {
                                showMathDialog = false
                                uriHandler.openUri(pendingUrl)
                            } else {
                                isError = true
                                userAnswer = "" // Clear wrong answer
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

// --- Helpers ---

@Composable
fun MenuActionItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 16.sp, fontFamily = OdiaFont, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun MoreAppCard(
    name: String,
    desc: String,
    url: String,
    onLinkClick: (String) -> Unit // Changed: Takes a function instead of using handler directly
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLinkClick(url) }, // Call the gate function
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, fontSize = 18.sp, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = PinkGradientEnd)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, fontSize = 14.sp, fontFamily = OdiaFont, color = Color.Gray)
        }
    }
}

@Composable
fun SocialButton(
    name: String,
    url: String,
    onLinkClick: (String) -> Unit // Changed: Takes a function
) {
    Button(
        onClick = { onLinkClick(url) }, // Call the gate function
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = ButtonDefaults.buttonElevation(2.dp),
        shape = RoundedCornerShape(50)
    ) {
        Text(text = name, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontFamily = OdiaFont)
    }
}