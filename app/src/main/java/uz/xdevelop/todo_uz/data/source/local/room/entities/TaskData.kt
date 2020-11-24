package uz.xdevelop.todo_uz.data.source.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskData(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var task_name: String = "",
    var color: Int = 0
)