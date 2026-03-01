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
package data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

@Database(
    entities = [StudentEntity::class, TeacherEntity::class, AttendanceLogEntity::class],
    version = 1,
)
@TypeConverters(Converters::class)
@ConstructedBy(ServerDatabaseConstructor::class)
abstract class ServerDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao

    abstract fun teacherDao(): TeacherDao

    abstract fun attendanceLogDao(): AttendanceLogDao
}

@Suppress("KotlinNoActualForExpect")
expect object ServerDatabaseConstructor : RoomDatabaseConstructor<ServerDatabase> {
    override fun initialize(): ServerDatabase
}
