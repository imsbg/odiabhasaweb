package `in`.sandeep.odiabhasa

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CrashScreen() {
    val context = LocalContext.current

    // Read the crash data from SharedPreferences
    val crashInfo = remember {
        val prefs = context.getSharedPreferences("AppCrashPrefs", Context.MODE_PRIVATE)
        val log = prefs.getString("CRASH_LOG", "କୌଣସି ଲଗ୍ ମିଳିଲା ନାହିଁ ।") ?: "ଲଗ୍ ଖାଲି ଅଛି ।"
        val timeMillis = prefs.getLong("CRASH_TIME", 0)
        val formattedTime = if (timeMillis > 0) {
            val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale("or", "IN"))
            sdf.format(Date(timeMillis))
        } else {
            "ସମୟ ଅଜ୍ଞାତ"
        }
        Pair(log, formattedTime)
    }

    val crashLog = crashInfo.first
    val crashTime = crashInfo.second

    Scaffold(
        containerColor = MaterialTheme.colorScheme.errorContainer
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ଆପ୍ କ୍ରାଶ୍ ହୋଇଛି",
                fontFamily = OdiaFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ଦୁଃଖିତ, ଏକ ଅପ୍ରତ୍ୟାଶିତ ତ୍ରୁଟି ଦେଖାଦେଇଛି । ଆପଣ ଏହି ତ୍ରୁଟି ରିପୋର୍ଟ କରି ଆମକୁ ସାହାଯ୍ୟ କରିପାରିବେ ।",
                fontFamily = OdiaFont,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Crash Log Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Takes up remaining space
            ) {
                Text(
                    text = "କ୍ରାଶ୍ ସମୟ: $crashTime",
                    fontFamily = OdiaFont,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, MaterialTheme.colorScheme.onError, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = crashLog,
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        fontFamily = FontFamily.Monospace, // Best for logs
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Email Button
            Button(
                onClick = {
                    sendCrashReportByEmail(context, crashTime, crashLog)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(Icons.Default.Email, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ଇମେଲ୍ ମାଧ୍ୟମରେ ରିପୋର୍ଟ କରନ୍ତୁ",
                    fontFamily = OdiaFont,
                    fontSize = 16.sp
                )
            }
        }
    }
}

private fun sendCrashReportByEmail(context: Context, time: String, log: String) {
    val recipient = "message.sbg@gmail.com"
    val subject = "Odia Bhasa App - କ୍ରାଶ୍ ରିପୋର୍ଟ"

    val body = """
        ନମସ୍କାର,
        
        ମୋର ଆପ୍ ବ୍ୟବହାର କରିବା ସମୟରେ ଏକ କ୍ରାଶ୍ ହୋଇଛି ।
        ଏହାର ବିବରଣୀ ନିମ୍ନରେ ଦିଆଗଲା:
        
        --------------------
        କ୍ରାଶ୍ ସମୟ: $time
        --------------------
        
        କ୍ରାଶ୍ ଲଗ୍:
        $log
        --------------------
        
        ଦୟାକରି ଏହି ସମସ୍ୟାର ସମାଧାନ କରନ୍ତୁ ।
        ଧନ୍ୟବାଦ ।
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // Only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        context.startActivity(Intent.createChooser(intent, "ଇମେଲ୍ ପଠାନ୍ତୁ..."))
    } catch (e: Exception) {
        // Handle case where no email app is installed
    }
}