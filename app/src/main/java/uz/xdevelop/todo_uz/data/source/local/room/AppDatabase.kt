package uz.xdevelop.todo_uz.data.source.local.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.xdevelop.todo_uz.app.App
import uz.xdevelop.todo_uz.data.source.local.room.dao.SubTasksDao
import uz.xdevelop.todo_uz.data.source.local.room.dao.TasksDao
import uz.xdevelop.todo_uz.data.source.local.room.dao.TodoDao
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData

@Database(
    entities = [
        TaskData::class,
        SubTaskData::class,
        TodoData::class
    ]
    , version = 1
)

@TypeConverters(CustomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
    abstract fun subTasksDao(): SubTasksDao
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    App.instance,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}