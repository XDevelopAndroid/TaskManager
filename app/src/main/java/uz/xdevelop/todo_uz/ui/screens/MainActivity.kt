package uz.xdevelop.todo_uz.ui.screens

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.withIcon
import com.mikepenz.materialdrawer.model.interfaces.withIdentifier
import com.mikepenz.materialdrawer.model.interfaces.withName
import com.mikepenz.materialdrawer.util.addStickyFooterItem
import com.mikepenz.materialdrawer.util.getDrawerItem
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_activity_main.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.mvp.contracts.MainActivityContract
import uz.xdevelop.todo_uz.mvp.presenters.MainActivityPresenter
import uz.xdevelop.todo_uz.mvp.repositories.MainActivityRepository
import uz.xdevelop.todo_uz.ui.dialogs.TaskDialog
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.ui.fragments.main_fragments.MainTodoFragment
import uz.xdevelop.todo_uz.utils.extensions.changeNavigationBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.changeStatusBarColorWithAnimation
import uz.xdevelop.todo_uz.utils.extensions.toDarkenColor
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NAME_SHADOWING")
class MainActivity : AppCompatActivity(), MainActivityContract.View {
    private lateinit var presenter: MainActivityContract.Presenter
    private var currentTodoFragment: MainTodoFragment? = null
    private var lastSelectedTask = -1L
    private val MENU_NEW_TODO = 1001L
    private val MENU_EDIT_TODO = 1002L
    private val MENU_ALL = 1003L
    private val MENU_HISTORY = 1004L
    private val MENU_DELETE_SELECTED = 1005L
    private val MENU_DELETED = 1006L
    private val MENU_SHARE = 1010L
    private val MENU_INSTRUCTION = 1007L
    private val MENU_TERMS = 1008L
    private val MENU_CONNECT = 1009L
    private val ACTIVITY_ADD_TODO_FROM_MAIN = 100
    private val ACTIVITY_ADD_TODO_LIST_FROM_MAIN = 101
    private val ACTIVITY_DELETED = 201
    private val ACTIVITY_ALL = 202

    companion object {
        var f: () -> Unit = {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadViews()
    }

    private fun loadViews() {
        presenter = MainActivityPresenter(this, MainActivityRepository())

        with(toolbar) {
            title = ""
            setTitleTextColor(Color.parseColor("#FFFFFF"))
        }
        setSupportActionBar(toolbar)

        val headerView = AccountHeaderView(this).apply {
            attachToSliderView(slider)
            accountHeaderBackground.setPadding(15, 0, 15, 0)
            accountHeaderBackground.scaleType = ImageView.ScaleType.CENTER_INSIDE
            accountHeaderBackground.setImageDrawable(resources.getDrawable(R.drawable.ic_drawer_header2))
        }
        headerView.accountHeaderBackground.alpha = 0.5f

        fillDrawerMenuItems()

        slider.onDrawerItemClickListener = navItemSelected

        val toggle = ActionBarDrawerToggle(
            this,
            root,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = resources.getColor(android.R.color.white)
        toggle.syncState()

        initFloatingActionButtonMenu()
        initSearchV()
    }

    private fun initFloatingActionButtonMenu() {
        multiple_menu.setOnFloatingActionsMenuUpdateListener(object :
            FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuExpanded() {
                floating_menu_background.visibility = View.VISIBLE
                closeSearchView()
            }

            override fun onMenuCollapsed() {
                floating_menu_background.visibility = View.GONE
            }
        })

        floating_menu_background.setOnClickListener {
            it.visibility = View.GONE
            multiple_menu.collapse()
        }

        buttonAddTodo.setOnClickListener {
            closeSearchView()
            presenter.addTodoClicked()
            multiple_menu.collapse()
        }

        buttonAddList.setOnClickListener {
            closeSearchView()
            presenter.addTodoListClicked()
            multiple_menu.collapse()
        }
    }

    private fun closeSearchView() {
        searchV.onActionViewCollapsed()
        currentTodoFragment?.updateOnSearch("")
    }

    private fun initSearchV() {
        searchV.setOnCloseListener {
            closeSearchView()
            true
        }
        val searchPlate =
            searchV.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchPlate.hint = "Qidiruv"
        val searchPlateView: View =
            searchV.findViewById(androidx.appcompat.R.id.search_plate)
        searchPlateView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )

        // TODO: 7/27/2020
        searchV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // do your logic here                Toast.makeText(applicationContext, query, Toast.LENGTH_SHORT).show()
//                currentTodoFragment?.updateOnSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                currentTodoFragment?.updateOnSearch(newText)
                return false
            }
        })

        searchV.setOnQueryTextFocusChangeListener { _, b ->
            if (!b) {
                searchV.onActionViewCollapsed()
                textHeader.visibility = View.VISIBLE
                searchV.setQuery("", false)
            } else {
                textHeader.visibility = View.INVISIBLE
            }
        }
        f = { searchV.onActionViewCollapsed() }

        val searchManager =
            getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchV.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    }

    override fun onBackPressed() {
        if (!searchV.isIconified) {
            searchV.onActionViewCollapsed()
            currentTodoFragment?.updateOnSearch("")
        } else {
            val intent = Intent()
            setResult(2, intent)
            finish()
        }
    }

    private val navItemSelected: ((v: View?, item: IDrawerItem<*>, position: Int) -> Boolean) =
        { _, drawerItem, _ ->
            when (drawerItem.identifier) {
                MENU_NEW_TODO -> presenter.openAddTaskDialog()
                MENU_EDIT_TODO -> presenter.openEditTaskDialog(lastSelectedTask)
                MENU_ALL -> {
                    val intent =
                        Intent(this, AllTodoActivity::class.java)
                    startActivityForResult(intent, ACTIVITY_ALL)
                }
                MENU_HISTORY -> {
                    val intent =
                        Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                }
                MENU_DELETE_SELECTED -> presenter.openDeleteTaskDialog(lastSelectedTask)
                MENU_DELETED -> {
                    val intent =
                        Intent(this, DeletedActivity::class.java)
                    startActivityForResult(intent, ACTIVITY_DELETED)
                }
                MENU_INSTRUCTION -> {
                    openPdf("instruction.pdf")
                }
                MENU_SHARE -> {
                    sendApkFile(this)
                }
                MENU_TERMS -> {
                    openPdf("terms.pdf")
                }
                MENU_CONNECT -> {
                }
                else -> {
                    val data = drawerItem.tag as TaskData
                    presenter.taskChanged(data)
                    lastSelectedTask = data.id
                }
            }
            false
        }

    /*private fun setVisibilityFloatingButton(b: Boolean = true) {
        multiple_menu.visibility = if (b) View.VISIBLE else View.GONE
    }*/

    private fun openPdf(name: String) {
        // Open the PDF file from raw folder
        val inputStream =
            when (name) {
                "instruction.pdf" -> resources.openRawResource(R.raw.instruction)
                else -> resources.openRawResource(R.raw.terms)
            }
        // Copy the file to the cache folder
        inputStream.use { inputStream ->
            val file = File(cacheDir, name)
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }

        val cacheFile = File(cacheDir, name)

        // Get the URI of the cache file from the FileProvider
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", cacheFile)
        if (uri != null) {
            // Create an intent to open the PDF in a third party app
            val pdfViewIntent = Intent(Intent.ACTION_VIEW)
            pdfViewIntent.data = uri
            pdfViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(pdfViewIntent, "Choose PDF viewer"))
        }
    }

    private fun sendApkFile(context: Context) {
        try {
            val pm = context.packageManager
            val ai = pm.getApplicationInfo(context.packageName, 0)
            val srcFile = File(ai.publicSourceDir)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "*/*"
            val uri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", srcFile)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.grantUriPermission(
                context.packageManager.toString(),
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun openTodoTask(data: TaskData) {
        textHeader.text = data.task_name
        textHeader.visibility = View.VISIBLE
        searchV.onActionViewCollapsed()

        val fragment = MainTodoFragment(data)
        currentTodoFragment = fragment
        LocalStorage.instance.currentColor = data.color
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.toggle) {
            searchV.onActionViewCollapsed()
        }
        return true
    }

    override fun initData(data: List<TaskData>, lastSelected: Long) {
        data.forEach {
            addTaskToNavigationMenu(it)
        }
        if (lastSelected != -1L)
            slider.setSelection(lastSelected)
    }

    override fun showMessage(str: String) = Toast.makeText(this, str, Toast.LENGTH_SHORT).show()

    override fun openEditTaskDialog(data: TaskData) {
        root.close()
        val dialog = TaskDialog(this)
        dialog.setTaskData(data)
        dialog.setOnClickListener { taskData -> presenter.editTask(taskData) }
        dialog.show()
    }

    override fun openAddTaskDialog() {
        root.close()
        val dialog = TaskDialog(this)
        dialog.setOnClickListener { taskData -> presenter.createTask(taskData) }
        dialog.show()
    }

    override fun openDeleteTaskDialog(data: TaskData) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("O'chirish")
            .setMessage("Ushbu ro'yxatni haqiqatdan ham o'chirmoqchimisiz?")
            .setIcon(R.drawable.ic_clear)
            .setNegativeButton("Bekor qilish", null)
            .setPositiveButton("Ha") { _, _ ->
                presenter.deleteTask(data)
            }
            .create()
        dialog.show()
    }

    override fun update(data: TaskData) {
        val item =
            slider.getDrawerItem(data.id) as PrimaryDrawerItem
        item.name = StringHolder(data.task_name)

        val drawableOriginal = AppCompatResources.getDrawable(this, R.drawable.ic_list)?.mutate()
        val drawableWrapped = drawableOriginal?.let { DrawableCompat.wrap(it) }
        if (drawableWrapped != null) {
            DrawableCompat.setTint(drawableWrapped, data.color)
        }
        item.withIcon(drawableWrapped)

        item.tag = data
        slider.itemAdapter[slider.adapter.getPosition(item.identifier)] = item
    }

    override fun delete(data: TaskData) {
        slider.itemAdapter.removeByIdentifier(data.id)
    }

    override fun selectMenu(id: Long) {
        slider.setSelection(id)
    }

    override fun openAddTodoActivity() {
        val intent =
            Intent(this, AddTodoActivity::class.java).putExtra(
                "request",
                ACTIVITY_ADD_TODO_FROM_MAIN
            )
        startActivityForResult(intent, ACTIVITY_ADD_TODO_FROM_MAIN)
    }

    override fun openAddTodoListActivity() {
        val intent =
            Intent(this, AddTodoActivity::class.java).putExtra(
                "request",
//                ACTIVITY_ADD_TODO_LIST_FROM_MAIN
                ACTIVITY_ADD_TODO_FROM_MAIN
            )
//        startActivityForResult(intent, ACTIVITY_ADD_TODO_LIST_FROM_MAIN)
        startActivityForResult(intent, ACTIVITY_ADD_TODO_FROM_MAIN)
    }

    @SuppressLint("NewApi")
    override fun addTaskToNavigationMenu(taskData: TaskData) {
//        setVisibilityFloatingButton(true)
        val index = slider.itemAdapter.itemList.items.indexOfFirst { it is DividerDrawerItem }

        val drawableOriginal = AppCompatResources.getDrawable(this, R.drawable.ic_list)?.mutate()
        val drawableWrapped = drawableOriginal?.let { DrawableCompat.wrap(it) }
        if (drawableWrapped != null) {
            DrawableCompat.setTint(drawableWrapped, taskData.color)
        }
        val item = PrimaryDrawerItem().withName(taskData.task_name).withIcon(drawableWrapped)
            .withIdentifier(taskData.id)
        slider.itemAdapter.add(
            index + 1,
            item
        )
        item.tag = taskData
        slider.setSelection(taskData.id)
    }

    override fun changeAllAttributes(taskData: TaskData) {
        changeStatusBarColorWithAnimation(taskData.color.toDarkenColor())
        changeNavigationBarColorWithAnimation(taskData.color.toDarkenColor())

        val color =
            if (isColorDark(taskData.color)) android.R.color.white else android.R.color.black
        val colorFilter = PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_ATOP)

        toolbar.forEach {
            if (it is ImageView) {
                it.colorFilter = colorFilter
            }
            if (it is TextView) {
                it.setTextColor(resources.getColor(color))
//                it.text = taskData.task_name
            }
        }

        textHeader.setTextColor(resources.getColor(color))

        if (isColorDark(taskData.color)) {
            val searchIcon = searchV.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
            searchIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_search_white
                )
            )

            val closeIcon =
                searchV.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_close_white
                )
            )

            val searchAutoComplete =
                searchV.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
            searchAutoComplete.setHintTextColor(Color.parseColor("#B3FFFFFF"))
            searchAutoComplete.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            val searchIcon = searchV.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
            searchIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_search_black
                )
            )

            val closeIcon =
                searchV.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_close_black
                )
            )

            val searchAutoComplete =
                searchV.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
            searchAutoComplete.setHintTextColor(Color.parseColor("#B3000000"))
            searchAutoComplete.setTextColor(Color.parseColor("#000000"))
        }
        changeCurrentBackgroundColorWithAnimation(taskData.color, toolbar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_ADD_TODO_FROM_MAIN && resultCode == Activity.RESULT_OK) {
            addTodoDataToFragment(data?.getSerializableExtra("todo") as TodoData)
        }
        if ((requestCode == ACTIVITY_DELETED || requestCode == ACTIVITY_ALL) && resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    private fun refreshData() {
        currentTodoFragment?.refreshCurrentPage()
    }

    override fun addTodoDataToFragment(todoData: TodoData) {
        currentTodoFragment?.addTodoFromParentActivity(todoData)
    }

    private fun changeCurrentBackgroundColorWithAnimation(
        colorTo: Int,
        toolbar: Toolbar
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val viewColor = toolbar.background as ColorDrawable
            val colorFrom = viewColor.color
            val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimator.duration = 600
            colorAnimator.addUpdateListener {
                toolbar.setBackgroundColor(it.animatedValue as Int)
            }
            colorAnimator.start()

            LocalStorage.instance.oldColor = colorFrom
        }
    }

    private fun fillDrawerMenuItems() {
        slider.itemAdapter.add(
            DividerDrawerItem().apply { isSelectable = false },
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_NEW_TODO)
                .withName(R.string.drawer_item_add_todo)
                .withIcon(R.drawable.ic_add_circle),
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_EDIT_TODO)
                .withName(R.string.drawer_item_edit_todo)
                .withIcon(R.drawable.ic_edit),
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_ALL)
                .withName(R.string.drawer_item_all_todo)
                .withIcon(R.drawable.ic_all),
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_HISTORY)
                .withName(R.string.drawer_item_history_todo)
                .withIcon(R.drawable.ic_history),
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_DELETE_SELECTED)
                .withName(R.string.drawer_item_delete)
                .withIcon(R.drawable.ic_clear),
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_DELETED)
                .withName(R.string.drawer_item_deleted_todo)
                .withIcon(R.drawable.ic_delete),
            DividerDrawerItem().apply { isSelectable = false },
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_INSTRUCTION)
                .withName(R.string.drawer_item_instr_todo)
                .withIcon(R.drawable.ic_baseline_chrome_reader_mode_24),
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_SHARE)
                .withName(R.string.drawer_item_share)
                .withIcon(R.drawable.ic_baseline_share),
            PrimaryDrawerItem().apply { isSelectable = false }
                .withIdentifier(MENU_TERMS)
                .withName(R.string.drawer_item_terms)
                .withIcon(R.drawable.ic_baseline_verified_user_24)
        )
        slider.addStickyFooterItem(
            SecondaryDrawerItem().apply { isSelectable = false }
                .withName(R.string.drawer_item_connect)
                .withIcon(R.drawable.ic_info)
                .withIdentifier(MENU_CONNECT)
        )
    }
}
