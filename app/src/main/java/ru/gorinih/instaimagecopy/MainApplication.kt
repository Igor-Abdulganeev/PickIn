package ru.gorinih.instaimagecopy

import android.app.Application
import org.koin.core.context.startKoin
import ru.gorinih.instaimagecopy.di.appModule
import ru.gorinih.instaimagecopy.di.networkModule

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                appModule,
                networkModule
            )
        }
    }
}