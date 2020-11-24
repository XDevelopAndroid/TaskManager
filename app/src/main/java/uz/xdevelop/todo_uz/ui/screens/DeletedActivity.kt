package uz.xdevelop.todo_uz.ui.screens

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_all_todo.*
import kotlinx.android.synthetic.main.activity_deleted.*
import kotlinx.android.synthetic.main.activity_deleted.toolbar
import uz.xdevelop.contactonlinetrainingroommvp.ui.adapters.DeletedAdapter
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.AppDatabase
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.ui.adapters.TabFragmentAdapter
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.ui.screens.AddTodoActivity.Companion.DELETE_FOREVER
import uz.xdevelop.todo_uz.ui.screens.AddTodoActivity.Companion.RESTORE
import uz.xdevelop.todo_uz.utils.extensions.changeNavigationBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.changeStatusBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.toDarkenColor
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class DeletedActivity : AppCompatActivity() {
    private val ACTIVITY_INFO = 301
    private val executor = Executors.newSingleThreadExecutor()
    private val storage = LocalStorage.instance
    private val color = storage.currentColor
    private lateinit var adapter: DeletedAdapter
    private val todoDao = AppDatabase.getDatabase().todoDao()
    private val subTaskDao = AppDatabase.getDatabase().subTasksDao()
    private val taskDao = AppDatabase.getDatabase().tasksDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deleted)
        loadViews()
    }

    private fun loadViews() {
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(storage.currentColor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        changeColors()
        title = "O'chirilgan vazifalar"

        adapter = DeletedAdapter(this)
        list.adapter = adapter
        val lm = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        list.layoutManager = lm
        initAdapterData()

        adapter.setOnItemClickListener { openInfoActivity(it) }
        adapter.setOnItemDeleteListener { deleteItem(it) }
        adapter.setOnItemEditListener { restoreItem(it) }
    }

    private fun openInfoActivity(it: TodoData) {
        val intent =
            Intent(this, AddTodoActivity::class.java).putExtra(
                "request",
                ACTIVITY_INFO
            )
        intent.putExtra("type", "info")
        intent.putExtra("todo", it)
        startActivityForResult(intent, ACTIVITY_INFO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val dd = data?.getSerializableExtra("data")
        if (dd != null) {
            val d = dd as TodoData
            if (requestCode == ACTIVITY_INFO && resultCode == RESTORE) {
                restoreItem(d)
            }
            if (requestCode == ACTIVITY_INFO && resultCode == DELETE_FOREVER) {
                deleteItem(d)
            }
        }
    }

    // TODO: 7/23/2020 restore qilinganda vaqti o'tgan bo'lsa toast ni almashtirish kerak
    private fun restoreItem(it: TodoData) {
        runOnWorkerThread {
            val sub = subTaskDao.getById(it.sub_task_id)!!
            val task = taskDao.getById(sub.task_id)!!

            runOnUiThread {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Vazifani qaytarish!")
                dialog.setPositiveButton("Ha") { _, _ ->
                    it.todo_deleted = false
                    runOnWorkerThread { todoDao.update(it) }
                    adapter.restoreItem(it)
                    Toast.makeText(
                        this,
                        "Vazifa quyidagi joyga muvaffaqiyatli qaytarildi: ${sub.task_name} in ${task.task_name} task.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                dialog.setNegativeButton("Yo'q") { _, _ -> }
                dialog.setIcon(R.drawable.ic_baseline_restore_24)
                dialog.setMessage("Vazifani quyidagi joyga qaytarishni istaysizmi?: ${sub.task_name} in ${task.task_name} task?")
                dialog.show()
            }
        }
    }

    private fun deleteItem(it: TodoData) {
        runOnWorkerThread {
            todoDao.delete(it)
            runOnUiThread {
                adapter.restoreItem(it)
                Toast.makeText(this, "Muvaffaqiyatli o'chirildi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAdapterData() {
        runOnWorkerThread {
            val ls = todoDao.getAllDeleted()
            runOnUiThread { adapter.submitList(ls) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_all, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Barcha vazifalarni o'chirish")
            dialog.setPositiveButton("Ha") { _, _ ->
                val str = "Bazadan muvaffaqiyatli o'chirildi!"
                runOnWorkerThread {
                    val ls = todoDao.getAllDeleted()
                    todoDao.deleteAll(ls)
                    runOnUiThread {
                        adapter.clear()
                        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialog.setNegativeButton("Yo'q") { _, _ -> }
            dialog.setIcon(R.drawable.ic_trash)
            dialog.setMessage("Ushbu sahifadagi barcha vazifalarni o'chirmoqchimisiz?")
            dialog.show()
        }
        return true
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun changeColors() {
        changeStatusBarColorWithAnimation(color.toDarkenColor())
        changeNavigationBarColorWithAnimation(color.toDarkenColor())

        val color =
            if (isColorDark(color)) android.R.color.white else android.R.color.black
        val colorFilter = PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_ATOP)

        toolbar.forEach {
            if (it is ImageView) {
                it.colorFilter = colorFilter
            }
            if (it is TextView) {
                it.setTextColor(resources.getColor(color))
            }
        }
    }

    private fun runOnWorkerThread(f: () -> Unit) {
        executor.execute(f)
    }
}