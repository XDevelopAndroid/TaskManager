package uz.xdevelop.todo_uz.mvp.presenters

import android.util.Log
import uz.xdevelop.contactonlinetrainingroommvp.ui.presenters.ThreadsHelper
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.app.App
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.mvp.contracts.MainActivityContract

@Suppress("DEPRECATION")
class MainActivityPresenter(
    private val view: MainActivityContract.View,
    private val model: MainActivityContract.Model
) : MainActivityContract.Presenter, ThreadsHelper() {

    init {
        runOnWorkerThread {
            var list = model.getAll()
            if (list.isEmpty()) {
                model.insert(
                    TaskData(
                        id = 1L,
                        task_name = "TODO TASK",
                        color = App.instance.resources.getColor(R.color.colorPrimary)
                    )
                )
                model.insertSub(
                    SubTaskData(
                        task_name = "SUB TODO",
                        task_id = 1L
                    )
                )
                list = model.getAll()
            }
            runOnUIThread {
                view.initData(list, model.taskId)
            }
        }
    }

    override fun createTask(data: TaskData) {
        runOnWorkerThread {
            val i = model.insert(data)
            data.id = i

            model.insertSub(
                SubTaskData(
                    task_name = "SUB TODO",
                    task_id = i
                )
            )
            runOnUIThread {
                view.addTaskToNavigationMenu(data)
//                view.changeAllAttributes(data)
            }
        }
    }

    override fun editTask(data: TaskData) {
        runOnWorkerThread {
            model.update(data)
            runOnUIThread {
                view.update(data)
                view.changeAllAttributes(data)
                view.selectMenu(data.id)
            }
        }
    }

    override fun deleteTask(data: TaskData) {
        if (data.id != 1L) {
            runOnWorkerThread {
                model.delete(data)
                val list = model.getAll()
                runOnUIThread {
                    view.delete(data)
                    if (list.isNotEmpty()) {
                        view.changeAllAttributes(list[0])
                        view.selectMenu(list[0].id)
                    }
                }
            }
        } else {
            view.showMessage("Boshlang'ich task ni o'chirish mumkin emas!!")
        }
    }

    override fun openEditTaskDialog(id: Long) {
        runOnWorkerThread {
            val f = model.getById(id)
            if (f == null) {
                runOnUIThread { view.showMessage("Ro'yxatni tanlang!") }
            } else {
                runOnUIThread { view.openEditTaskDialog(f) }

            }
        }
    }

    override fun openAddTaskDialog() {
        view.openAddTaskDialog()
    }

    override fun openDeleteTaskDialog(id: Long) {
        Log.d("TTT", id.toString())
        runOnWorkerThread {
            val f = model.getById(id)
            if (model.getAll().isEmpty()) {
                runOnUIThread { view.showMessage("Ro'yxat bo'sh!") }
            } else {
                if (f == null) {
                    runOnUIThread { view.showMessage("Ro'yxatni tanlang!") }
                } else {
                    runOnUIThread { view.openDeleteTaskDialog(f) }
                }
            }
        }
    }

    override fun taskChanged(data: TaskData) {
        model.taskId = data.id
        view.openTodoTask(data)
        view.changeAllAttributes(data)
    }

    override fun addTodoListClicked() {
        runOnWorkerThread {
            val size = model.getSubTaskCount()
            runOnUIThread {
                if (size != 0) {
                    view.openAddTodoListActivity()
                } else
                    view.showMessage("Iltimos vazifani qo'shish uchun subtask yarating!")
            }
        }
    }

    override fun addTodoClicked() {
        runOnWorkerThread {
            val size = model.getSubTaskCount()
            runOnUIThread {
                if (size != 0) {
                    view.openAddTodoActivity()
                } else
                    view.showMessage("Iltimos vazifani qo'shish uchun subtask yarating!")
            }
        }
    }
}