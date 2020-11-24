package uz.xdevelop.todo_uz.mvp.repositories

import uz.xdevelop.todo_uz.data.source.local.room.AppDatabase
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.mvp.contracts.MainActivityContract

class MainActivityRepository : MainActivityContract.Model {
    private val dao = AppDatabase.getDatabase().tasksDao()
    private val subTaskDao = AppDatabase.getDatabase().subTasksDao()

    /*private val subTaskDao = AppDatabase.getDatabase().subTasksDao()
    private val todoDao = AppDatabase.getDatabase().todoDao()*/
    private val storage = LocalStorage.instance
    override var taskId: Long
        get() = storage.lastTaskId
        set(value) {
            storage.lastTaskId = value
        }

    override fun insert(taskData: TaskData): Long = dao.insert(taskData)

    override fun insertSub(sub: SubTaskData): Long = subTaskDao.insert(sub)

    override fun update(data: TaskData): Int = dao.update(data)

    override fun delete(data: TaskData): Int {
        /*Log.d("BBB", dao.getAll().size.toString())
        Log.d("BBB", subTaskDao.getAll().size.toString())
        Log.d("BBB", todoDao.getAll().size.toString())*/
        /*Log.d("BBB", dao.getAll().size.toString())
        Log.d("BBB", subTaskDao.getAll().size.toString())
        Log.d("BBB", todoDao.getAll().size.toString())*/
        return dao.delete(data)
    }

    override fun getAll(): List<TaskData> = dao.getAll()

    override fun getById(id: Long) = dao.getById(id)
    override fun getSubTaskCount(): Int = subTaskDao.getAll(taskId).size
}