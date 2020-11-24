package uz.xdevelop.todo_uz.mvp.presenters

import android.util.Log
import uz.xdevelop.contactonlinetrainingroommvp.ui.presenters.ThreadsHelper
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.mvp.contracts.TodoFragmentContract
import java.util.*

class TodoFragmentPresenter(
    private val view: TodoFragmentContract.View,
    private val model: TodoFragmentContract.Model
) : TodoFragmentContract.Presenter, ThreadsHelper() {

    override fun init() {
        runOnWorkerThread {
            val list = model.getAllSubTasks()
            /*list.forEach {
                Log.d("TTT", it.toString())
            }*/
            runOnUIThread {
                view.initData(list)
            }
        }
        view.initSwitch(LocalStorage.instance.filter)
    }

    override fun submitTodo(type: Boolean) {
        model.changeFilterType(type)
        if (model.subTaskId != -1L)
            changeSubTaskTabPosition(model.subTaskId)
    }

    override fun createSubTask(data: SubTaskData) {
        runOnWorkerThread {
            data.task_id = model.taskId
            val i = model.insertSubTask(data)
//            data.id = i
            val list = model.getAllSubTasks()
            Log.d("TTT", i.toString())
            runOnUIThread {
                view.insertSubTask(list)
            }
        }
    }

    override fun editSubTask(data: SubTaskData) {
        TODO("Not yet implemented")
    }

    override fun deleteSubTask(data: SubTaskData) {
        TODO("Not yet implemented")
    }

    override fun editTodo(data: TodoData) {
        runOnWorkerThread {
            model.updateTodo(data)
        }
    }

    override fun openEditSubTaskDialog(id: Long) {
        TODO("Not yet implemented")
    }

    override fun openAddSubTaskDialog() {
        view.openAddSubTaskDialog()
    }

    override fun openDeleteSubTaskDialog(id: Long) {
        TODO("Not yet implemented")
    }

    override fun addFastTodoClicked() {
        if (view.getHeaderText().trim().isEmpty()) {
            view.showMessage("Matn maydoni bo'sh bo'lishi mumkin emas!")
            return
        }
        val d = GregorianCalendar.getInstance()
        val data = TodoData(
            sub_task_id = model.subTaskId,
            todo_created = d.timeInMillis,
            todo_priority = 0,
            todo_date = 0L,
            todo_text = view.getHeaderText()
        )
//        Log.d("TTTT", model.subTaskId.toString())
        runOnWorkerThread {
            val size = model.getAllSubTasks().size
            if (size != 0) {
                val id = model.insertTodo(data)
                data.id = id
                runOnUIThread {
                    view.insertTodoData(data)
                    view.clearHeaderText()
                }
            } else
                runOnUIThread {
                    view.showMessage("Iltimos todo qo'shish uchun subtask yarating!")
                }
        }
    }

    override fun insertTodoData(todoData: TodoData) {
        todoData.sub_task_id = model.subTaskId
        runOnWorkerThread {
            val id = model.insertTodo(todoData)
            todoData.id = id
            runOnUIThread {
                view.insertTodoData(todoData)
            }
        }
    }

    override fun changeSubTaskTabPosition(id: Long) {
        runOnWorkerThread {
            val ls = model.getAllSubTasks()
            val t = ls.filter { it.id == id }
            if (t.isNotEmpty()) {
                model.subTaskId = t[0].id
                val list = model.getAllTodo()
//            Log.d("LLL", list.size.toString())
                runOnUIThread { view.submitTodoData(list) }
            }
        }
    }
}