package `in`.sandeep.odiabhasa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// --- 1. FONT CONFIG ---
private val OdiaSplashFont = FontFamily(
    Font(R.font.odia1, FontWeight.Normal)
)

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {

    // Standard Google Apps usually show the splash screen for just 1 to 1.5 seconds,
    // or however long it takes the app to load its initial data.
    LaunchedEffect(key1 = true) {
        delay(1200L) // Short, snappy delay
        onNavigateToHome()
    }

    // Colors are now hardcoded to Light Mode values so it always stays white
    val backgroundColor = Color(0xFFFFFFFF)
    val titleColor = Color(0xFF202124) // Google's standard dark gray so it's visible on white
    val subtitleColor = Color(0xFF60656B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        // --- CENTER: App Logo (Like Gmail / YouTube) ---
        Image(
            painter = painterResource(id = R.drawable.logo_monochrome),
            contentDescription = "Odia Bhasa Logo",
            modifier = Modifier
                .size(120.dp) // Standard Android splash icon size
                .align(Alignment.Center)
        )

        // --- BOTTOM: Branding Text (Like "from Google Workspace") ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // Padding pushes it up safely above the navigation bar
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "ଓଡ଼ିଆ ଭାଷା",
                fontFamily = OdiaSplashFont,
                fontSize = 24.sp, // Smaller, cleaner text
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ଆମ ଭାଷା, ଆମ ପରିଚୟ",
                fontFamily = OdiaSplashFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = subtitleColor
            )
        }
    }
}