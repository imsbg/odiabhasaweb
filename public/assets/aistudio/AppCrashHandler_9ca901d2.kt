package `in`.sandeep.odiabhasa

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class AppCrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppCrashPrefs", Context.MODE_PRIVATE)

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // 1. Get the crash log
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        val crashLog = stringWriter.toString()

        // 2. Save to SharedPreferences synchronously
        // CRITICAL FIX: Use commit() instead of apply().
        // apply() is async and the app might die before it saves.
        sharedPreferences.edit()
            .putString("CRASH_LOG", crashLog)
            .putLong("CRASH_TIME", System.currentTimeMillis())
            .commit()

        // 3. Launch CrashActivity
        val intent = Intent(context, CrashActivity::class.java).apply {
            // Pass the log via Intent as well (faster retrieval)
            putExtra("CRASH_DATA", crashLog)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        context.startActivity(intent)

        // 4. Kill the process
        // We kill it to prevent the "App keeps stopping" system dialog
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}