package uz.xdevelop.todo_uz.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData

@Dao
interface TasksDao : BaseDao<TaskData> {
    @Query("SELECT * FROM tasks")
    fun getAll(): List<TaskData>

    @Query("SELECT * FROM tasks WHERE id=:id")
    fun getById(id: Long): TaskData?
}