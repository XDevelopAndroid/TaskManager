package uz.xdevelop.todo_uz.mvp.contracts

import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData

interface TodoFragmentContract {
    interface Model {
        var taskId: Long
        var subTaskId: Long
        fun insertSubTask(data: SubTaskData): Long
        fun updateSubTask(data: SubTaskData): Int
        fun deleteSubTask(data: SubTaskData): Int
        fun getAllSubTasks(): List<SubTaskData>
        fun getSubTaskById(id: Long): SubTaskData?

        fun changeFilterType(type: Boolean)
        fun insertTodo(data: TodoData): Long
        fun updateTodo(data: TodoData): Int
        fun getAllTodo(): List<TodoData>
        fun getTodoById(id: Long): TodoData?
    }

    interface View {
        fun initData(data: List<SubTaskData>)
        fun showMessage(str: String)
        fun initSwitch(boolean: Boolean)
        fun openEditSubTaskDialog(data: SubTaskData)
        fun openAddSubTaskDialog()
        fun openDeleteSubTaskDialog(data: SubTaskData)
        fun updateSubTask(data: List<SubTaskData>)
        fun deleteSubTask(data: List<SubTaskData>)
        fun insertSubTask(data: List<SubTaskData>)

        fun submitTodoData(data: List<TodoData>)
        fun insertTodoData(data: TodoData)
        fun getHeaderText(): String
        fun clearHeaderText()
    }

    interface Presenter {
        fun init()
        fun submitTodo(type: Boolean)
        fun createSubTask(data: SubTaskData)
        fun editSubTask(data: SubTaskData)
        fun deleteSubTask(data: SubTaskData)
        fun editTodo(data: TodoData)
        fun openEditSubTaskDialog(id: Long)
        fun openAddSubTaskDialog()
        fun openDeleteSubTaskDialog(id: Long)
        fun addFastTodoClicked()
        fun insertTodoData(todoData: TodoData)
        fun changeSubTaskTabPosition(id: Long)
    }
}