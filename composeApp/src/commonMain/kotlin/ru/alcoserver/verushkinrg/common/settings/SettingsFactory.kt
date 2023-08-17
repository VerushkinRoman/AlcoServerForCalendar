package ru.alcoserver.verushkinrg.common.settings

import com.russhwolf.settings.Settings
import ru.alcoserver.verushkinrg.common.core.platform.PlatformConfiguration

internal expect class SettingsFactory(platformConfiguration: PlatformConfiguration) {
    fun createSettings(): Settings
}

internal const val SHARED = "common_shared_preferences"