package uz.xdevelop.todo_uz.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_outdated.*
import uz.xdevelop.contactonlinetrainingroommvp.ui.adapters.AllTodoFragmentAdapter
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.AppDatabase
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.ui.dialogs.SubTaskChooserDialog
import uz.xdevelop.todo_uz.ui.screens.AddTodoActivity
import uz.xdevelop.todo_uz.ui.screens.AllTodoActivity
import java.util.concurrent.Executors

class FragmentAllTodo : Fragment(R.layout.fragment_outdated) {
    private val ACTIVITY_EDIT_FROM_FRAGMENT = 401
    private val ACTIVITY_COPY_FROM_FRAGMENT = 402

    //    private lateinit var data: List<TodoData>
    private val handle = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var adapter: AllTodoFragmentAdapter
    private val todoDao = AppDatabase.getDatabase().todoDao()
    private val subTaskDao = AppDatabase.getDatabase().subTasksDao()
    private val taskDao = AppDatabase.getDatabase().tasksDao()
    private var position: Int = -1
    private var type: String = "DEF"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = requireArguments()
//        Log.d("TT", "created")
        position = bundle.getInt("position")
        type = bundle.getString("type").toString()

        adapter = AllTodoFragmentAdapter(context!!, position, type)
        list.layoutManager = GridLayoutManager(context!!, 2, GridLayoutManager.VERTICAL, false)
        list.adapter = adapter

        adapter.setOnItemClickListener { openAddTodoActivity(it) }
        if (type != "history") {
            adapter.setOnItemCloneListener { openCloneActivity(it) }
            adapter.setOnItemDeleteListener {
                if (position == 0) {
                    it.todo_deleted = true
                    runOnWorkerThread {
                        todoDao.update(it)
                    }
                    showMessage("Ro'yxatdan muvaffaqiyatli o'chirildi. Vazifani o'chirilgan vazifalar bo'limidan topishingiz mumkin!")
                } else {
                    runOnWorkerThread {
                        todoDao.delete(it)
                    }
                    showMessage("Bazadan muvaffaqiyatli o'chirildi!")
                }
            }
            adapter.setOnItemEditListener {
                runOnWorkerThread {
                    todoDao.update(it)
                }
            }
        } else {
            runOnWorkerThread {
                val ls = when (position) {
                    1 -> {
                        todoDao.getCanceledTodo()
                    }
                    0 -> {
                        todoDao.getDoneTodo()
                    }
                    else -> {
                        todoDao.getOutDatedTodo()
                    }
                }

                runOnUIThread {
                    adapter.submitList(ls)
                }
            }
        }
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
//        Log.d("TT", "resume")
        if (type != "history") {
            AllTodoActivity.f = { this.onResume() }
            initData()
        }
    }

    private fun initData() {
        runOnWorkerThread {
            val ls = when (position) {
                3 -> {
                    todoDao.getOutDatedTodo()
                }
                1 -> {
                    todoDao.getDoneTodo()
                }
                2 -> {
                    todoDao.getCanceledTodo()
                }
                else -> {
                    todoDao.getNotFinishedTodo()
                }
            }

            runOnUIThread {
                adapter.submitList(ls)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_EDIT_FROM_FRAGMENT && (resultCode == Activity.RESULT_OK || resultCode == AddTodoActivity.CANCEL || resultCode == AddTodoActivity.DELETE)) {
            val d = data?.getSerializableExtra("todo") as TodoData
            adapter.update(d)
            runOnWorkerThread {
                todoDao.update(d)
            }
//            listenerEdit?.invoke(d)
        }
        if (requestCode == ACTIVITY_COPY_FROM_FRAGMENT && resultCode == Activity.RESULT_OK) {
            val d = data?.getSerializableExtra("todo") as TodoData
            adapter.insert(d)
            showMessage("Vazifa muvaffaqiyatli ko'chirildi!")
            runOnWorkerThread {
                todoDao.insert(d)
            }
        }
    }

    private fun openCloneActivity(it: TodoData) {
        it.cancelled = false
        it.todo_finished = 0
        it.todo_date = 0L
        it.id = 0L

        runOnWorkerThread {
            val ls = subTaskDao.getAll()
            val taskList = taskDao.getAll()
            runOnUIThread {
                val dialog = SubTaskChooserDialog(context!!, ls, taskList)
                dialog.show()
                dialog.setOnClickListener { id ->
                    it.sub_task_id = id

                    val intent =
                        Intent(context, AddTodoActivity::class.java).putExtra(
                            "request",
                            ACTIVITY_COPY_FROM_FRAGMENT
                        )
                    intent.putExtra("todo", it)
                    startActivityForResult(intent, ACTIVITY_COPY_FROM_FRAGMENT)
                }
            }
        }
    }

    private fun openAddTodoActivity(it: TodoData) {
        val intent =
            Intent(context, AddTodoActivity::class.java).putExtra(
                "request",
                ACTIVITY_EDIT_FROM_FRAGMENT
            )
        if (position != 0 && type == "DEF") {
            intent.putExtra("type", "info")
            intent.putExtra("position", position)
        }
        if (type != "DEF") {
            intent.putExtra("type", "info")
            intent.putExtra("position", position)
        }
        intent.putExtra("todo", it)
        startActivityForResult(intent, ACTIVITY_EDIT_FROM_FRAGMENT)
    }

    private fun runOnWorkerThread(f: () -> Unit) {
        executor.execute(f)
    }

    private fun runOnUIThread(f: () -> Unit) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            f()
        } else {
            handle.post { f() }
        }
    }
}