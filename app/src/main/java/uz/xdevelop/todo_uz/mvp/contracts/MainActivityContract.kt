package uz.xdevelop.todo_uz.mvp.contracts

import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData

interface MainActivityContract {
    interface Model {
        var taskId: Long
        fun insert(taskData: TaskData): Long
        fun insertSub(sub: SubTaskData): Long
        fun update(data: TaskData): Int
        fun delete(data: TaskData): Int
        fun getAll(): List<TaskData>
        fun getById(id: Long): TaskData?
        fun getSubTaskCount(): Int
    }

    interface View {
        fun openTodoTask(data: TaskData)
        fun initData(data: List<TaskData>, lastSelected: Long = -1L)
        fun showMessage(str: String)
        fun openEditTaskDialog(data: TaskData)
        fun openAddTaskDialog()
        fun openDeleteTaskDialog(data: TaskData)
        fun update(data: TaskData)
        fun delete(data: TaskData)
        fun addTaskToNavigationMenu(taskData: TaskData)
        fun changeAllAttributes(taskData: TaskData)
        fun selectMenu(id: Long)

        fun openAddTodoActivity()
        fun openAddTodoListActivity()
        fun addTodoDataToFragment(todoData: TodoData)
    }

    interface Presenter {
        fun createTask(data: TaskData)
        fun editTask(data: TaskData)
        fun deleteTask(data: TaskData)
        fun openEditTaskDialog(id: Long)
        fun openAddTaskDialog()
        fun openDeleteTaskDialog(id: Long)
        fun taskChanged(data: TaskData)
        fun addTodoListClicked()
        fun addTodoClicked()
    }
}