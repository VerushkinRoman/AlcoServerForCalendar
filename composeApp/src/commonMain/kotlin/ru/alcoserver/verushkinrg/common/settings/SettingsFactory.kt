package ru.alcoserver.verushkinrg.common.settings

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings

class SettingsFactory {
    fun createSettings(): Settings = PreferencesSettings.Factory().create(SHARED)
}

private const val SHARED = "common_shared_preferences"