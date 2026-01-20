package shub39.rpi_attendance.client.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import shub39.rpi_attendance.client.data.datastore.AppDatastoreImpl
import shub39.rpi_attendance.client.data.datastore.DataStoreFactory
import shub39.rpi_attendance.client.domain.AppDatastore
import shub39.rpi_attendance.client.viewmodels.AppViewModel
import shub39.rpi_attendance.client.viewmodels.RpcServiceWrapper
import shub39.rpi_attendance.client.viewmodels.StudentsScreenViewModel
import shub39.rpi_attendance.client.viewmodels.TeachersScreenViewModel

expect val platformModules: Module

val modules = module {
    single { get<DataStoreFactory>().getPreferencesDataStore() }
    singleOf(::AppDatastoreImpl).bind<AppDatastore>()
    singleOf(::RpcServiceWrapper)

    viewModelOf(::AppViewModel)
    viewModelOf(::StudentsScreenViewModel)
    viewModelOf(::TeachersScreenViewModel)
}