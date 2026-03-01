/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
import shub39.rpi_attendance.client.viewmodels.AttendanceLogScreenViewModel
import shub39.rpi_attendance.client.viewmodels.RpcServiceWrapper
import shub39.rpi_attendance.client.viewmodels.SessionsViewModel
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
    viewModelOf(::SessionsViewModel)
    viewModelOf(::AttendanceLogScreenViewModel)
}
