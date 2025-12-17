package data.database

import androidx.room.*

@Database(
    entities = [
        StudentEntity::class,
        TeacherEntity::class,
        AttendanceLogEntity::class,
        CourseEntity::class
    ],
    version = 1,
)
@TypeConverters(Converters::class)
@ConstructedBy(ServerDatabaseConstructor::class)
abstract class ServerDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun attendanceLogDao(): AttendanceLogDao
    abstract fun courseDao(): CourseDao
}

@Suppress("KotlinNoActualForExpect")
expect object ServerDatabaseConstructor : RoomDatabaseConstructor<ServerDatabase> {
    override fun initialize(): ServerDatabase
}