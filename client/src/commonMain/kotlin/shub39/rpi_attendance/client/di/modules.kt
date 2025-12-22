package shub39.rpi_attendance.client.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import shub39.rpi_attendance.client.viewmodels.AppViewModel
import shub39.rpi_attendance.client.viewmodels.RpcServiceWrapper
import shub39.rpi_attendance.client.viewmodels.StateLayer

val modules = module {
    singleOf(::RpcServiceWrapper)
    singleOf(::StateLayer)

    viewModelOf(::AppViewModel)
}