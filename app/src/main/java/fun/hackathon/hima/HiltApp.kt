package `fun`.hackathon.hima

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class HiltApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            // Timber.d("{Your Message}")でログに出力できる
            Timber.plant(Timber.DebugTree())
        }
    }
}