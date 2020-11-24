package uz.xdevelop.todo_uz.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData

@Dao
interface SubTasksDao : BaseDao<SubTaskData> {
    @Query("SELECT * FROM sub_tasks WHERE task_id=:id")
    fun getAll(id: Long): List<SubTaskData>

    @Query("SELECT * FROM sub_tasks")
    fun getAll(): List<SubTaskData>

    @Query("SELECT * FROM sub_tasks WHERE id=:id LIMIT 1")
    fun getById(id: Long): SubTaskData?
}