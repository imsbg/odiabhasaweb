package `in`.sandeep.odiabhasa

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
// Wildcard import to ensure update() works
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

// 1. Receiver (NOW WITH AUTO-UPDATE LOGIC)
class OdiaCalendarUniqueWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = OdiaCalendarUniqueWidget()

    private val coroutineScope = MainScope()

    // Handle the midnight update broadcast
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Check for our specific Unique Widget Action
        if (intent.action == "in.sandeep.odiabhasa.ACTION_MIDNIGHT_UPDATE_UNIQUE") {
            coroutineScope.launch {
                OdiaCalendarUniqueWidget().updateAll(context)
            }
            // Reschedule for the next night
            scheduleMidnightUpdate(context)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleMidnightUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelMidnightUpdate(context)
    }

    private fun scheduleMidnightUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Note: We use a UNIQUE action string for this widget so it doesn't clash with the other one
        val intent = Intent(context, OdiaCalendarUniqueWidgetReceiver::class.java).apply {
            action = "in.sandeep.odiabhasa.ACTION_MIDNIGHT_UPDATE_UNIQUE"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2002, // Unique Request Code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
        // Wake up at 12:01 AM
        val triggerTimeMillis = midnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 60000

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
        }
    }

    private fun cancelMidnightUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, OdiaCalendarUniqueWidgetReceiver::class.java).apply {
            action = "in.sandeep.odiabhasa.ACTION_MIDNIGHT_UPDATE_UNIQUE"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

// 2. The Widget Class (Eye-Catchy Design)
class OdiaCalendarUniqueWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val sharedPrefs = context.getSharedPreferences("odia_calendar_cache", Context.MODE_PRIVATE)
        val jsonString = sharedPrefs.getString("festival_data", null)
        val today = LocalDate.now()

        var todayFestivalName: String? = null
        var nextFestivalName: String? = null
        var nextFestivalDate: String? = null

        if (jsonString != null) {
            try {
                val root = JSONObject(jsonString)
                val dataObj = root.getJSONObject("data")

                // Today
                val todayStr = today.toString()
                if (dataObj.has(todayStr)) {
                    val dayJson = dataObj.getJSONObject(todayStr)
                    todayFestivalName = dayJson.optString("name").takeIf { it.isNotBlank() }
                        ?: dayJson.optString("ct").takeIf { it.isNotBlank() }
                }

                // Next Event
                for (i in 1..30) {
                    val nextDate = today.plusDays(i.toLong())
                    val nextDateStr = nextDate.toString()
                    if (dataObj.has(nextDateStr)) {
                        val nextJson = dataObj.getJSONObject(nextDateStr)
                        val name = nextJson.optString("name").takeIf { it.isNotBlank() }
                        if (name != null) {
                            nextFestivalName = name
                            nextFestivalDate = "${convertToOdiaNumber(nextDate.dayOfMonth)} ${getOdiaGregorianMonthName(nextDate.month)}"
                            break
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        provideContent {
            GlanceTheme {
                UniqueContent(today, todayFestivalName, nextFestivalName, nextFestivalDate)
            }
        }
    }

    @Composable
    private fun UniqueContent(
        today: LocalDate,
        todayFestival: String?,
        nextFestName: String?,
        nextFestDate: String?
    ) {
        // --- NEW EYE-CATCHY PALETTE ---
        val eyeCatchyYellow = Color(0xFFFFC107)

        val textBlack = ColorProvider(Color.Black)
        val textWhite = ColorProvider(Color.White)
        val textGrey = ColorProvider(Color(0xFFCCCCCC))
        val textYellowAccent = ColorProvider(eyeCatchyYellow)

        // Main Container
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.widget_background_rounded))
                .clickable(actionRunCallback<UniqueRefreshAction>()),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- LEFT PANEL: VIBRANT DATE STRIP ---
            Column(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .width(95.dp)
                    .background(eyeCatchyYellow)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Month
                Text(
                    text = getOdiaGregorianMonthName(today.month),
                    style = TextStyle(color = textBlack, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                )

                // Big Date
                Text(
                    text = convertToOdiaNumber(today.dayOfMonth),
                    style = TextStyle(color = textBlack, fontSize = 46.sp, fontWeight = FontWeight.Bold)
                )

                // Day of Week
                val dayName = odiaDaysOfWeek[today.dayOfWeek.value % 7]
                Text(
                    text = dayName,
                    style = TextStyle(color = textBlack, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                )
            }

            // --- RIGHT PANEL: DARK INFO AREA ---
            Column(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // "Today" Label
                Text(
                    text = "ଆଜି କ’ଣ ଅଛି?",
                    style = TextStyle(color = textYellowAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Festival Name
                val festText = todayFestival ?: "ଆଜି କିଛି ବିଶେଷ ନାହିଁ"
                Text(
                    text = festText,
                    style = TextStyle(color = textWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold),
                    maxLines = 2
                )

                Spacer(modifier = GlanceModifier.height(10.dp))

                // Divider Line
                Box(
                    modifier = GlanceModifier
                        .width(30.dp)
                        .height(3.dp)
                        .background(eyeCatchyYellow)
                ) {}

                Spacer(modifier = GlanceModifier.height(10.dp))

                // Next Festival Footer
                if (nextFestName != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = GlanceModifier.width(4.dp).height(4.dp).background(Color.White)) {}
                        Spacer(modifier = GlanceModifier.width(6.dp))

                        Text(
                            text = "ପରବର୍ତ୍ତୀ: $nextFestName",
                            style = TextStyle(color = textGrey, fontSize = 12.sp),
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "   $nextFestDate",
                        style = TextStyle(color = textYellowAccent, fontSize = 11.sp)
                    )
                }
            }
        }
    }

    // --- Helpers ---
    private val odiaDaysOfWeek = listOf("ରବିବାର", "ସୋମବାର", "ମଙ୍ଗଳବାର", "ବୁଧବାର", "ଗୁରୁବାର", "ଶୁକ୍ରବାର", "ଶନିବାର")

    private fun getOdiaGregorianMonthName(month: Month): String {
        return when (month) {
            Month.JANUARY -> "ଜାନୁଆରୀ"; Month.FEBRUARY -> "ଫେବୃଆରୀ"; Month.MARCH -> "ମାର୍ଚ୍ଚ"
            Month.APRIL -> "ଅପ୍ରେଲ"; Month.MAY -> "ମଇ"; Month.JUNE -> "ଜୁନ"; Month.JULY -> "ଜୁଲାଇ"
            Month.AUGUST -> "ଅଗଷ୍ଟ"; Month.SEPTEMBER -> "ସେପ୍ଟେମ୍ବର"; Month.OCTOBER -> "ଅକ୍ଟୋବର"
            Month.NOVEMBER -> "ନଭେମ୍ବର"; Month.DECEMBER -> "ଡିସେମ୍ବର"
        }
    }

    private fun convertToOdiaNumber(input: Any?): String {
        if (input == null) return ""
        val text = input.toString(); val odiaNumerals = ('୦'..'୯').toList()
        return text.map { char ->
            val index = ('0'..'9').indexOf(char)
            if (index != -1) odiaNumerals[index] else char
        }.joinToString("")
    }
}

// 3. Callback
class UniqueRefreshAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        OdiaCalendarUniqueWidget().update(context, glanceId)
    }
}