package ru.alcoserver.verushkinrg.common.settings

interface SettingsRepository {
    var dbCleanupDate: Long
    var serviceAccountPath: String
}