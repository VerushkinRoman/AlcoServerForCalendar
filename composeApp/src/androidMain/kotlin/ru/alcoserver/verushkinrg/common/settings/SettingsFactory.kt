package ru.alcoserver.verushkinrg.common.settings

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import ru.alcoserver.verushkinrg.common.core.platform.PlatformConfiguration

internal actual class SettingsFactory actual constructor(private val platformConfiguration: PlatformConfiguration) {

    actual fun createSettings(): Settings {
        val sharedPreferences = platformConfiguration.androidContext
            .getSharedPreferences(SHARED, Context.MODE_PRIVATE)

        return SharedPreferencesSettings(sharedPreferences)
    }
}