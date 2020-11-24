package uz.xdevelop.todo_uz.ui.screens

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_all_todo.*
import kotlinx.android.synthetic.main.activity_deleted.toolbar
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.ui.adapters.TabFragmentAdapter
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.utils.extensions.changeNavigationBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.changeStatusBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.toDarkenColor

@Suppress("DEPRECATION")
class HistoryActivity : AppCompatActivity() {
    private val storage = LocalStorage.instance
    private val color = storage.currentColor
    private lateinit var adapter: TabFragmentAdapter
    private var indicatorWidth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        loadViews()
    }

    private fun loadViews() {
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(storage.currentColor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        changeColors()
        title = "Vazifalar tarixi"

        initAdapterData()

    }

    private fun initAdapterData() {
        adapter = TabFragmentAdapter(supportFragmentManager, lifecycle, "history")

        viewPager.adapter = adapter
        tab.isSmoothScrollingEnabled = true

        val tabMediator = TabLayoutMediator(tab, viewPager) { tab, position ->
            tab.text = when (position) {
                1 -> {
                    "Canceled"
                }
                0 -> {
                    "Done"
                }
                else -> {
                    "OutDated"
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
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
}