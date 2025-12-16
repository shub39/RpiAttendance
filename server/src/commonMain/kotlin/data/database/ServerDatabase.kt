package data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [StudentEntity::class, TeacherEntity::class, AttendanceLogEntity::class],
    version = 1,
)
@ConstructedBy(ServerDatabaseConstructor::class)
abstract class ServerDatabase: RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun attendanceLogDao(): AttendanceLogDao
}

@Suppress("KotlinNoActualForExpect")
expect object ServerDatabaseConstructor: RoomDatabaseConstructor<ServerDatabase> {
    override fun initialize(): ServerDatabase
}