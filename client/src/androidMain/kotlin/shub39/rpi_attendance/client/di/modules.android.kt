package shub39.rpi_attendance.client.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import shub39.rpi_attendance.client.data.datastore.DataStoreFactory

actual val platformModules: Module = module {
    singleOf(::DataStoreFactory)
}