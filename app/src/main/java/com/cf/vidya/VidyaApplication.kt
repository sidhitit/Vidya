package com.cf.vidya

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.cf.vidya.shared.di.sharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VidyaApplication : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VidyaApplication)
            modules(sharedModules)
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
}
