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
// --- CHANGED: Use wildcard import to fix 'Unresolved reference' errors for update/updateAll ---
import androidx.glance.appwidget.*
// ---------------------------------------------------------------------------------------------
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
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

// Data class updated to include color
data class WidgetFestivalItem(
    val date: LocalDate,
    val name: String,
    val colorHex: String? // Store the color code (e.g., "#FF0000")
)

// 1. The Receiver (Updated for Midnight Auto-Update)
class OdiaCalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = OdiaCalendarWidget()

    private val coroutineScope = MainScope()

    // Handle the midnight update broadcast
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // If this is our custom midnight action, force update the widget
        if (intent.action == "in.sandeep.odiabhasa.ACTION_MIDNIGHT_UPDATE") {
            coroutineScope.launch {
                // updateAll should now work due to the wildcard import
                OdiaCalendarWidget().updateAll(context)
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
        val intent = Intent(context, OdiaCalendarWidgetReceiver::class.java).apply {
            action = "in.sandeep.odiabhasa.ACTION_MIDNIGHT_UPDATE"
        }

        // Create a PendingIntent that will trigger this Receiver
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate time until next midnight
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
        // Add 1 minute buffer (12:01 AM) to ensure the date has definitely changed
        val triggerTimeMillis = midnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 60000

        // Schedule the alarm
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Fallback for devices where exact alarm permission is denied
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
        }
    }

    private fun cancelMidnightUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, OdiaCalendarWidgetReceiver::class.java).apply {
            action = "in.sandeep.odiabhasa.ACTION_MIDNIGHT_UPDATE"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

// 2. The Main Widget Class
class OdiaCalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Fetch Data
        val sharedPrefs = context.getSharedPreferences("odia_calendar_cache", Context.MODE_PRIVATE)
        val jsonString = sharedPrefs.getString("festival_data", null)

        val today = LocalDate.now()
        var todayFestivalName: String? = null
        val upcomingFestivals = mutableListOf<WidgetFestivalItem>()

        // Parse Logic
        if (jsonString != null) {
            try {
                val root = JSONObject(jsonString)
                val dataObj = root.getJSONObject("data")

                // 1. Get Today's Info
                val todayStr = today.toString()
                if (dataObj.has(todayStr)) {
                    val dayJson = dataObj.getJSONObject(todayStr)
                    todayFestivalName = dayJson.optString("name").takeIf { it.isNotBlank() }
                        ?: dayJson.optString("ct").takeIf { it.isNotBlank() }
                }

                // 2. Get Upcoming List
                var count = 0
                for (i in 1..60) {
                    if (count >= 10) break
                    val nextDate = today.plusDays(i.toLong())
                    val nextDateStr = nextDate.toString()

                    if (dataObj.has(nextDateStr)) {
                        val nextJson = dataObj.getJSONObject(nextDateStr)
                        val name = nextJson.optString("name").takeIf { it.isNotBlank() }

                        val colorCode = nextJson.optString("color").takeIf { it.isNotBlank() }

                        if (name != null) {
                            upcomingFestivals.add(WidgetFestivalItem(nextDate, name, colorCode))
                            count++
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        provideContent {
            GlanceTheme {
                WidgetContent(
                    today = today,
                    todayFestival = todayFestivalName,
                    upcomingList = upcomingFestivals
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        today: LocalDate,
        todayFestival: String?,
        upcomingList: List<WidgetFestivalItem>
    ) {
        // Base Colors
        val defaultOrange = Color(0xFFD66A46)
        val textWhite = ColorProvider(Color.White)
        val textGrey = ColorProvider(Color(0xFFB0A8B5))
        val textOrange = ColorProvider(Color(0xFFFF8A65))

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                // Ensure widget_background_rounded.xml exists in res/drawable!
                .background(ImageProvider(R.drawable.widget_background_rounded))
                .padding(12.dp)
                // Add Click Action to Refresh the Widget manually
                .clickable(actionRunCallback<RefreshAction>())
        ) {

            // --- SECTION 1: TODAY (Top) ---
            val todayDayOdia = odiaDaysOfWeek[today.dayOfWeek.value % 7]
            val todayDateOdia = "${convertToOdiaNumber(today.dayOfMonth)} ${getOdiaGregorianMonthName(today.month)}"

            Column(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = "$todayDayOdia, $todayDateOdia",
                    style = TextStyle(color = textOrange, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = GlanceModifier.height(2.dp))

                val displayText = todayFestival ?: "ଆଜି କୌଣସି ପର୍ବ ନାହିଁ"
                Text(
                    text = displayText,
                    style = TextStyle(color = textWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Separator Line
            Box(modifier = GlanceModifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f))) {}

            Spacer(modifier = GlanceModifier.height(8.dp))

            // --- SECTION 2: UPCOMING LIST ---
            if (upcomingList.isEmpty()) {
                Text(
                    text = "କିଛି ଗୋଟେ ଅସୁବିଧା ହେଲା",
                    style = TextStyle(color = textGrey, fontSize = 14.sp)
                )
            } else {
                Text(
                    text = "ପରବର୍ତ୍ତୀ ପର୍ବ:",
                    style = TextStyle(color = textGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                LazyColumn {
                    items(upcomingList) { item ->
                        val itemDayOdia = odiaDaysOfWeek[item.date.dayOfWeek.value % 7]
                        val itemDateOdia = "${convertToOdiaNumber(item.date.dayOfMonth)} ${getOdiaGregorianMonthName(item.date.month)}"

                        val barColor = parseColorString(item.colorHex) ?: defaultOrange

                        Row(
                            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = GlanceModifier
                                    .width(3.dp)
                                    .height(30.dp)
                                    .background(barColor)
                            ) {}

                            Spacer(modifier = GlanceModifier.width(10.dp))

                            Column {
                                Text(
                                    text = "$itemDayOdia, $itemDateOdia",
                                    style = TextStyle(color = textGrey, fontSize = 11.sp)
                                )
                                Text(
                                    text = item.name,
                                    style = TextStyle(color = textWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- HELPER FUNCTIONS ---

    private fun parseColorString(hex: String?): Color? {
        if (hex.isNullOrBlank()) return null
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            null
        }
    }

    private val odiaDaysOfWeek = listOf("ରବି", "ସୋମ", "ମଙ୍ଗଳ", "ବୁଧ", "ଗୁରୁ", "ଶୁକ୍ର", "ଶନି")

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

// 3. The Callback Class for Manual Refresh
class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // This forces the widget to redraw and re-fetch the date
        // 'update' will now work because of the wildcard import
        OdiaCalendarWidget().update(context, glanceId)
    }
}