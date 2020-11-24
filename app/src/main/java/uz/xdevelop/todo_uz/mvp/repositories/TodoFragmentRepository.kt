package uz.xdevelop.todo_uz.mvp.repositories

import uz.xdevelop.todo_uz.data.source.local.room.AppDatabase
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.mvp.contracts.TodoFragmentContract

class TodoFragmentRepository : TodoFragmentContract.Model {
    private val dao = AppDatabase.getDatabase().subTasksDao()
    private val daoTodo = AppDatabase.getDatabase().todoDao()
    private val storage = LocalStorage.instance
    override var taskId: Long = storage.lastTaskId
    override var subTaskId: Long = storage.subTaskId

    override fun insertSubTask(data: SubTaskData) = dao.insert(data)

    override fun updateSubTask(data: SubTaskData) = dao.update(data)

    override fun deleteSubTask(data: SubTaskData) = dao.delete(data)

    override fun getAllSubTasks() = dao.getAll(taskId)

    override fun getSubTaskById(id: Long): SubTaskData? {
        TODO("Not yet implemented")
    }

    override fun changeFilterType(type: Boolean) {
        storage.filter = type
    }

    override fun insertTodo(data: TodoData) = daoTodo.insert(data)
    override fun updateTodo(data: TodoData) = daoTodo.update(data)

    override fun getAllTodo(): List<TodoData> = when (storage.filter) {
        true -> {
            daoTodo.getAllNotDeletedFForFilter(subTaskId)
        }
        false -> {
            daoTodo.getAllNotDeleted(subTaskId)
        }
    }

    override fun getTodoById(id: Long): TodoData? {
        TODO("Not yet implemented")
    }
}