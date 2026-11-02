package ru.gorinih.instaimagecopy

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.gorinih.instaimagecopy.di.appModule
import ru.gorinih.instaimagecopy.di.networkModule

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

class ImageCopyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ImageCopyApplication)
            modules(
                appModule,
                networkModule
            )
        }

        SingletonImageLoader.setSafe {
            get<ImageLoader>()
        }
    }
}