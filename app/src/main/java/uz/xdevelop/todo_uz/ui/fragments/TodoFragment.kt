package uz.xdevelop.todo_uz.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_todo_page.*
import uz.xdevelop.contactonlinetrainingroommvp.ui.adapters.TodoFragmentAdapter
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.AppDatabase
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.ui.screens.AddTodoActivity
import uz.xdevelop.todo_uz.ui.screens.MainActivity
import uz.xdevelop.todo_uz.ui.screens.SearchByTagActivity
import java.util.concurrent.Executors

class TodoFragment : Fragment(R.layout.fragment_todo_page) {
    private val executor = Executors.newSingleThreadExecutor()
    private val handle = Handler(Looper.getMainLooper())
    private var adapter: TodoFragmentAdapter = TodoFragmentAdapter()
    private val todoDao = AppDatabase.getDatabase().todoDao()
    private var id: Long = 0
    private val ACTIVITY_ADD_TODO_FROM_FRAGMENT = 102
    private val ACTIVITY_SEARCH_TAG = 105
    private val ACTIVITY_ADD_TODO_LIST_FROM_FRAGMENT = 103

    companion object {
        lateinit var context: Context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        TodoFragment.context = context!!
        id = LocalStorage.instance.subTaskId

        list.adapter = adapter
        val lm = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        list.layoutManager = lm

        adapter.setOnItemClickListener { openAddTodoActivity(it) }
        adapter.setOnItemEditListener {
            runOnWorkerThread {
                todoDao.update(it)
            }
        }
        adapter.setOnItemDeleteListener {
            runOnWorkerThread {
                todoDao.update(it)
            }
        }
        adapter.setOnItemTagListener {
            MainActivity.f()
            val intent =
                Intent(context, SearchByTagActivity::class.java).putExtra(
                    "request",
                    ACTIVITY_SEARCH_TAG
                )
            intent.putExtra("tag", it)
            startActivityForResult(intent, ACTIVITY_SEARCH_TAG)
        }
    }

    private fun openAddTodoActivity(it: TodoData) {
        val intent =
            Intent(context, AddTodoActivity::class.java).putExtra(
                "request",
                ACTIVITY_ADD_TODO_FROM_FRAGMENT
            )
        intent.putExtra("todo", it)
        startActivityForResult(intent, ACTIVITY_ADD_TODO_FROM_FRAGMENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_ADD_TODO_FROM_FRAGMENT && (resultCode == Activity.RESULT_OK || resultCode == AddTodoActivity.CANCEL || resultCode == AddTodoActivity.DELETE)) {
            val d = data?.getSerializableExtra("todo") as TodoData
            adapter.update(d)
            runOnWorkerThread {
                todoDao.update(d)
            }
        }
    }

    fun initTodoData(ls: List<TodoData>) {
        adapter.submitList(ls)
    }

    @SuppressLint("DefaultLocale")
    fun updateOnSearch(d: String, id: Long) {
        val query = d.toLowerCase()
        runOnWorkerThread {
            val ls: List<TodoData> = if (!LocalStorage.instance.filter) {
                todoDao.getAllNotDeleted(id)
            } else {
                todoDao.getAllNotDeletedFForFilter(id)
            }
            val sorted: HashSet<TodoData> = hashSetOf()
            ls.forEach {
                if (it.todo_text.toLowerCase().contains(query)) {
                    sorted.add(it)
                }
                it.tags?.forEach { t ->
                    if (t.toLowerCase().contains(query)) {
                        sorted.add(it)
                    }
                }
            }
            runOnUIThread {
                adapter.submitList(sorted.toList())
            }
        }
    }

    fun insertTodoData(ls: TodoData) {
        if (ls.todo_finished != 1) {
            if (LocalStorage.instance.filter) {
                if (ls.todo_date != 0L && ls.todo_date > System.currentTimeMillis()) {
                    val l = adapter.insert(ls)
                    list.scrollToPosition(l)
                } else if (ls.todo_date < System.currentTimeMillis() && ls.todo_date == 0L)
                    showMessage("Yaratilgan todo shu bo'limning saralanmagan qismiga qo'shildi!")
            } else {
                if (ls.todo_date > System.currentTimeMillis() || ls.todo_date == 0L) {
                    val l = adapter.insert(ls)
                    list.scrollToPosition(l)
                } else
                    showMessage("Todo eskirgan vazifalar bo'limiga o'tkazildi!")
            }
        } else if (ls.todo_date != 0L && ls.todo_date < System.currentTimeMillis()) {
            showMessage("Todo eskirgan vazifalar bo'limiga o'tkazildi!")
        } else
            showMessage("Yaratilgan todo Bajarilgan vazifalar bo'limiga qo'shildi!")
    }

    private fun showMessage(string: String) {
        Toast.makeText(
            context,
            string,
            Toast.LENGTH_LONG
        ).show()
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