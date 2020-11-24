package uz.xdevelop.todo_uz.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_search_by_tag.*
import uz.xdevelop.contactonlinetrainingroommvp.ui.adapters.TodoFragmentAdapter
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.AppDatabase
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.utils.extensions.changeNavigationBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.changeStatusBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.toDarkenColor
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class SearchByTagActivity : AppCompatActivity() {
    private val ACTIVITY_INFO = 301
    private val executor = Executors.newSingleThreadExecutor()
    private val storage = LocalStorage.instance
    private val color = storage.currentColor
    private lateinit var adapter: TodoFragmentAdapter
    private var tag: String? = "none"
    private val todoDao = AppDatabase.getDatabase().todoDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_tag)
        tag = intent.getStringExtra("tag")
        loadViews()
    }

    private fun loadViews() {
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(storage.currentColor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        changeColors()
        title = "Tag bo'yicha qidirish"

        adapter = TodoFragmentAdapter(true)
        list.adapter = adapter
        val lm = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        list.layoutManager = lm
        initAdapterData()

        adapter.setOnItemClickListener { openInfoActivity(it) }
    }

    private fun openInfoActivity(it: TodoData) {
        val intent =
            Intent(this, AddTodoActivity::class.java).putExtra(
                "request",
                ACTIVITY_INFO
            )
        intent.putExtra("type", "info")
        intent.putExtra("search", 1)
        intent.putExtra("todo", it)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("DefaultLocale")
    private fun initAdapterData() {
        val query = tag!!.toLowerCase()
        runOnWorkerThread {
            val ls: List<TodoData> = todoDao.getAll()
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
            runOnUiThread {
                adapter.submitList(sorted.toList())
            }
        }
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