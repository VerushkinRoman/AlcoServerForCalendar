package ru.alcoserver.verushkinrg.common.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class SettingsRepositoryImpl(
    private val settings: Settings
) : SettingsRepository {
    override var dbCleanupDate: Long
        get() = settings[CLEANUP_TIME] ?: 0
        set(value) {
            settings[CLEANUP_TIME] = value
        }

    override var serviceAccountPath: String
        get() = settings[SERVICE_ACCOUNT_PATH] ?: ""
        set(value) {
            settings[SERVICE_ACCOUNT_PATH] = value
        }

    companion object {
        private const val CLEANUP_TIME = "cleanup time"
        private const val SERVICE_ACCOUNT_PATH = "service account path"
    }
}