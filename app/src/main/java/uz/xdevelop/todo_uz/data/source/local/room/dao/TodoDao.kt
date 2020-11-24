package uz.xdevelop.todo_uz.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import java.util.*

@Dao
interface TodoDao : BaseDao<TodoData> {
    @Query("SELECT * FROM todo WHERE sub_task_id=:id AND todo_deleted=0 AND todo_finished=0 AND (todo_date>:date OR todo_date=0) AND cancelled=0")
    fun getAllNotDeleted(
        id: Long,
        date: Long = Calendar.getInstance().timeInMillis
    ): List<TodoData>

    @Query("SELECT * FROM todo WHERE sub_task_id=:id AND todo_deleted=0 AND todo_finished=0 AND (todo_date>:date) AND cancelled=0")
    fun getAllNotDeletedFForFilter(
        id: Long,
        date: Long = Calendar.getInstance().timeInMillis
    ): List<TodoData>

    @Query("SELECT * FROM todo WHERE todo_deleted=1")
    fun getAllDeleted(): List<TodoData>

    @Query("SELECT * FROM todo")
    fun getAll(): List<TodoData>

    @Query("SELECT * FROM todo WHERE sub_task_id =:id")
    fun getAllSearch(id: Long): List<TodoData>

    @Query("SELECT * FROM todo WHERE id=:id LIMIT 1")
    fun getById(id: Long): TodoData?

    @Query("SELECT * FROM todo WHERE (todo_date!=0) AND todo_date <:time AND todo_finished = 0")
    fun getOutDatedTodo(time: Long = System.currentTimeMillis()): List<TodoData>

    @Query("SELECT * FROM todo WHERE todo_finished=1")
    fun getDoneTodo(): List<TodoData>

    @Query("SELECT * FROM todo WHERE cancelled=1")
    fun getCanceledTodo(): List<TodoData>

    @Query("SELECT * FROM todo WHERE  todo_deleted=0 AND todo_finished=0 AND (todo_date>:time OR todo_date=0) AND cancelled=0")
    fun getNotFinishedTodo(time: Long = System.currentTimeMillis()): List<TodoData>
}