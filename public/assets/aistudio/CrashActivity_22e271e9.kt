package `in`.sandeep.odiabhasa

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.sandeep.odiabhasa.ui.theme.OdiaBhasaTheme

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Try to get logs from the Intent (Fastest)
        var crashLogs = intent.getStringExtra("CRASH_DATA")

        // 2. If Intent is empty, fetch from SharedPreferences (Fallback)
        if (crashLogs.isNullOrEmpty()) {
            val prefs = getSharedPreferences("AppCrashPrefs", MODE_PRIVATE)
            crashLogs = prefs.getString("CRASH_LOG", "No Crash Details Found.")
        }

        setContent {
            OdiaBhasaTheme {
                CrashScreen(
                    errorMessage = crashLogs ?: "Unknown Error",
                    onRestartClick = {
                        restartApp()
                    }
                )
            }
        }
    }

    private fun restartApp() {
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            val componentName = intent.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }
    }
}

@Composable
fun CrashScreen(errorMessage: String, onRestartClick: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFFFFF8F8) // Light Red/Pink background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Icon
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ଆପ୍ ରେ କିଛି ଅସୁବିଧା ହୋଇଛି", // "Something went wrong in the app"
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "କୌଣସି ଏକ କାରଣ ପାଇଁ ଆପ୍ ଟି ବନ୍ଦ ହୋଇଗଲା",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Log Container
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f) // Takes up remaining middle space
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = errorMessage,
                        fontFamily = FontFamily.Monospace, // Looks like code
                        fontSize = 12.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(scrollState) // Makes text scrollable
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Copy Button
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Crash Log", errorMessage)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "ଲଗ୍ ଟିପି ହୋଇଗଲା", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ଲଗ୍ ଟିପନ୍ତୁ")
                }

                // Restart Button
                Button(
                    onClick = onRestartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ପୁନଃ ଆରମ୍ଭ କରନ୍ତୁ")
                }
            }
        }
    }
}