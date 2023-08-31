package ru.alcoserver.verushkinrg.common.core

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.provider
import org.kodein.di.singleton
import ru.alcoserver.verushkinrg.common.core.di.Inject
import ru.alcoserver.verushkinrg.common.data.Repository
import ru.alcoserver.verushkinrg.common.data.RepositoryFirestoreImpl
import ru.alcoserver.verushkinrg.common.settings.SettingsFactory
import ru.alcoserver.verushkinrg.common.settings.SettingsRepository
import ru.alcoserver.verushkinrg.common.settings.SettingsRepositoryImpl
import ru.alcoserver.verushkinrg.common.utils.CoroutinesDispatchers
import ru.alcoserver.verushkinrg.common.utils.CoroutinesDispatchersImpl

object PlatformSDK {
    fun init() {
        val commonModule = DI.Module("commonModule") {
            bind<SettingsRepository>() with singleton {
                SettingsRepositoryImpl(SettingsFactory().createSettings())
            }

            bind<CoroutinesDispatchers>() with singleton { CoroutinesDispatchersImpl() }

            bind<Repository>() with provider { RepositoryFirestoreImpl() }
        }

        Inject.createDependencies(
            DI {
                importAll(
                    commonModule
                )
            }.direct
        )
    }
}