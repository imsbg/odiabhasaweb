package `in`.sandeep.odiabhasa

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A dedicated repository to handle all data persistence for the writing practice feature.
 * This is the single source of truth for saving and loading data to/from SharedPreferences.
 */
object PracticeRepository {
    private const val PREF_NAME = "OdiaWritingPrefs"
    private const val KEY_LAST_CATEGORY = "last_category"
    private const val KEY_LAST_INDEX = "last_index"
    private const val KEY_DRAWINGS = "drawings"
    private const val KEY_STATS = "practice_stats"

    /**
     * Saves all relevant practice data synchronously to ensure it persists even on sudden app close.
     */
    suspend fun saveData(
        context: Context,
        lastCategory: String,
        lastIndex: Int,
        drawings: Map<String, List<StrokeData>>,
        stats: PracticeStats
    ) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_LAST_CATEGORY, lastCategory)
        editor.putInt(KEY_LAST_INDEX, lastIndex)

        // Save drawings
        val drawingsSb = StringBuilder()
        for ((char, strokes) in drawings) {
            if (strokes.isEmpty()) continue
            if (drawingsSb.isNotEmpty()) drawingsSb.append("%")
            drawingsSb.append(char).append("~")
            val strokeStrings = strokes.map { stroke ->
                val colorInt = stroke.color.toArgb()
                val pointsStr = stroke.points.joinToString(";") { "${it.first},${it.second}" }
                // NEW FORMAT: Save color, strokeWidth, and points
                "$colorInt|${stroke.strokeWidth}|$pointsStr"
            }
            drawingsSb.append(strokeStrings.joinToString("$"))
        }
        editor.putString(KEY_DRAWINGS, drawingsSb.toString())

        // Save stats
        val statsSb = StringBuilder()
        statsSb.append("${stats.totalUndo},${stats.totalRedo},${stats.totalClears}")
        if (stats.perCharStats.isNotEmpty()) {
            statsSb.append("%")
            val charStatsStr = stats.perCharStats.map { (char, charStat) ->
                "$char~${charStat.undoCount},${charStat.redoCount},${charStat.clearCount}"
            }.joinToString("$")
            statsSb.append(charStatsStr)
        }
        editor.putString(KEY_STATS, statsSb.toString())

        // CRITICAL FIX: Use commit() instead of apply().
        // commit() is synchronous and writes the data to disk immediately,
        // preventing data loss if the app is closed right after saving.
        editor.commit()
    }

    /**
     * Loads all practice data from SharedPreferences.
     */
    suspend fun loadData(context: Context): Pair<Triple<String, Int, Map<String, List<StrokeData>>>, PracticeStats> = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastCategory = prefs.getString(KEY_LAST_CATEGORY, "Swara") ?: "Swara"
        val lastIndex = prefs.getInt(KEY_LAST_INDEX, 0)

        // Load drawings
        val loadedDrawings = mutableMapOf<String, List<StrokeData>>()
        val drawingsStr = prefs.getString(KEY_DRAWINGS, "") ?: ""
        if (drawingsStr.isNotEmpty()) {
            drawingsStr.split("%").forEach { letterSection ->
                val parts = letterSection.split("~")
                if (parts.size == 2) {
                    val char = parts[0]
                    val strokesData = parts[1]
                    val restoredStrokes = mutableListOf<StrokeData>()
                    strokesData.split("$").forEach { singleStrokeStr ->
                        try {
                            val strokeParts = singleStrokeStr.split("|")
                            // Check for new format (3 parts) or old format (2 parts) for backward compatibility
                            if (strokeParts.size >= 2) {
                                val color = Color(strokeParts[0].toInt())

                                val (strokeWidth, pointsStr) = if (strokeParts.size == 3) {
                                    // New format: color|width|points
                                    (strokeParts[1].toFloatOrNull() ?: 25f) to strokeParts[2]
                                } else {
                                    // Old format: color|points
                                    25f to strokeParts[1] // Use a default stroke width for old data
                                }

                                val points = pointsStr.split(";").mapNotNull { pt ->
                                    val coords = pt.split(",")
                                    if (coords.size == 2) Pair(coords[0].toFloat(), coords[1].toFloat()) else null
                                }

                                if (points.isNotEmpty()) {
                                    val path = Path().apply {
                                        moveTo(points[0].first, points[0].second)
                                        points.drop(1).forEach { lineTo(it.first, it.second) }
                                    }
                                    // FIX: Pass the parsed or default strokeWidth to the StrokeData constructor
                                    restoredStrokes.add(StrokeData(path, color, points, strokeWidth))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (restoredStrokes.isNotEmpty()) {
                        loadedDrawings[char] = restoredStrokes
                    }
                }
            }
        }
        val drawingData = Triple(lastCategory, lastIndex, loadedDrawings)

        // Load stats
        val statsStr = prefs.getString(KEY_STATS, "") ?: ""
        var loadedStats = PracticeStats()
        if (statsStr.isNotEmpty()) {
            try {
                val parts = statsStr.split("%")
                val totals = parts[0].split(",")
                val totalUndo = totals.getOrNull(0)?.toInt() ?: 0
                val totalRedo = totals.getOrNull(1)?.toInt() ?: 0
                val totalClears = totals.getOrNull(2)?.toInt() ?: 0
                val perCharMap = mutableMapOf<String, CharStats>()
                if (parts.size > 1 && parts[1].isNotEmpty()) {
                    parts[1].split("$").forEach { charPart ->
                        val charData = charPart.split("~")
                        if (charData.size == 2) {
                            val char = charData[0]
                            val counts = charData[1].split(",")
                            val undo = counts.getOrNull(0)?.toInt() ?: 0
                            val redo = counts.getOrNull(1)?.toInt() ?: 0
                            val clear = counts.getOrNull(2)?.toInt() ?: 0
                            perCharMap[char] = CharStats(undo, redo, clear)
                        }
                    }
                }
                loadedStats = PracticeStats(totalUndo, totalRedo, totalClears, perCharMap)
            } catch (e: Exception) {
                e.printStackTrace()
                loadedStats = PracticeStats()
            }
        }
        Pair(drawingData, loadedStats)
    }

    /**
     * Clears all data from the preferences file.
     */
    suspend fun clearAll(context: Context) = withContext(Dispatchers.IO) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }
}