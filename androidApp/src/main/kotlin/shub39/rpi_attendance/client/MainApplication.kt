package shub39.rpi_attendance.client

import android.app.Application
import org.koin.android.ext.koin.androidContext
import shub39.rpi_attendance.client.di.initKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@MainApplication)
        }

    }
}