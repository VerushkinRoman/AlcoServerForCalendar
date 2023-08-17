package ru.alcoserver.verushkinrg.common.settings

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import ru.alcoserver.verushkinrg.common.core.platform.PlatformConfiguration

internal actual class SettingsFactory actual constructor(platformConfiguration: PlatformConfiguration) {
    actual fun createSettings(): Settings = PreferencesSettings.Factory().create(SHARED)
}