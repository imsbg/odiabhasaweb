package `in`.sandeep.odiabhasa

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Set our custom crash handler as the default handler
        Thread.setDefaultUncaughtExceptionHandler(AppCrashHandler(this))
    }
}