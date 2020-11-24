package uz.xdevelop.todo_uz.ui.screens

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.forEach
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_all_todo.*
import kotlinx.android.synthetic.main.activity_deleted.toolbar
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.AppDatabase
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.ui.adapters.TabFragmentAdapter
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.utils.extensions.changeNavigationBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.changeStatusBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.toDarkenColor
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class AllTodoActivity : AppCompatActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private val storage = LocalStorage.instance
    private val color = storage.currentColor
    private lateinit var adapter: TabFragmentAdapter
    private val todoDao = AppDatabase.getDatabase().todoDao()
    private val subTaskDao = AppDatabase.getDatabase().subTasksDao()
    private val taskDao = AppDatabase.getDatabase().tasksDao()
    private var indicatorWidth = 0

    companion object {
        var f: () -> Unit = {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_todo)

        loadViews()
    }

    private fun loadViews() {
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(storage.currentColor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        changeColors()
        title = "Barcha vazifalar"

        initAdapterData()

    }

    private fun initAdapterData() {
        adapter = TabFragmentAdapter(supportFragmentManager, lifecycle)

        viewPager.adapter = adapter
        tab.isSmoothScrollingEnabled = true

        val tabMediator = TabLayoutMediator(tab, viewPager) { tab, position ->
            tab.text = when (position) {
                3 -> {
                    "OutDated"
                }
                1 -> {
                    "Done"
                }
                2 -> {
                    "Canceled"
                }
                else -> {
                    "Not Finished"
                }
            }
        }
        tabMediator.attach()

        viewPager.registerOnPageChangeCallback(pageChangeCallback)

        tab.post {
            indicatorWidth = tab.width / tab.tabCount
            //Assign new width
            val indicatorParams = indicator.layoutParams as FrameLayout.LayoutParams
            indicatorParams.width = indicatorWidth
            indicator.layoutParams = indicatorParams
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
            dialog.setTitle("Barcha vazifani o'chirish!")
            dialog.setPositiveButton("Ha") { _, _ ->
                val i = tab.selectedTabPosition
                var str = "Bazadan muvaffaqiyatli o'chirildi!"

                runOnWorkerThread {
                    when (i) {
                        0 -> {
                            val ls = todoDao.getNotFinishedTodo().toMutableList()
                            ls.forEach {
                                it.todo_deleted = true
                            }
                            todoDao.updateAll(ls)
                            str = "Muvaffaqiyatli o'chirildi!"
                        }
                        1 -> {
                            val ls = todoDao.getDoneTodo()
                            todoDao.deleteAll(ls)
                        }
                        2 -> {
                            val ls = todoDao.getCanceledTodo()
                            todoDao.deleteAll(ls)
                        }
                        else -> {
                            val ls = todoDao.getOutDatedTodo()
                            todoDao.deleteAll(ls)
                        }
                    }

                    runOnUiThread {
                        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
                        f()
                        /*adapter = TabFragmentAdapter(supportFragmentManager, lifecycle)
                        viewPager.adapter = adapter*/
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

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {

        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val params = indicator.layoutParams as FrameLayout.LayoutParams
            val translationOffset = (positionOffset + position) * indicatorWidth
            params.leftMargin = translationOffset.toInt()
            indicator.layoutParams = params
        }
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