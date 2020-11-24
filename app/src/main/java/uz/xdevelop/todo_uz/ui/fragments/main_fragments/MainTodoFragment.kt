package uz.xdevelop.todo_uz.ui.fragments.main_fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_main_tabs.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.mvp.contracts.TodoFragmentContract
import uz.xdevelop.todo_uz.mvp.presenters.TodoFragmentPresenter
import uz.xdevelop.todo_uz.mvp.repositories.TodoFragmentRepository
import uz.xdevelop.todo_uz.ui.adapters.MainPagerAdapter
import uz.xdevelop.todo_uz.ui.dialogs.SubTaskDialog
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.ui.screens.MainActivity
import uz.xdevelop.todo_uz.utils.extensions.hideKeyboard

@Suppress("DEPRECATION")
class MainTodoFragment(private val data: TaskData) : Fragment(), TodoFragmentContract.View {
    private lateinit var presenter: TodoFragmentContract.Presenter
    private lateinit var pagerAdapter: MainPagerAdapter
    private var pagerPosition = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = TodoFragmentPresenter(this, TodoFragmentRepository())

        loadView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_main_tabs, container, false)

    @SuppressLint("SetTextI18n")
    private fun loadView() {
        changeColorAttributes()

        pagerAdapter = MainPagerAdapter(this)
        mainPager.adapter = pagerAdapter
        mainTabs.isSmoothScrollingEnabled = true

        /*pagerAdapter.setOnItemEditListener {
            presenter.editTodo(it)
        }*/

        buttonAddSublist.setOnClickListener {
            presenter.openAddSubTaskDialog()
            MainActivity.f()
        }

        val tabMediator = TabLayoutMediator(mainTabs, mainPager) { tab, position ->
            tab.text = pagerAdapter.currentFragments[position].data.task_name
            tab.tag = pagerAdapter.currentFragments[position].data.id
        }
        tabMediator.attach()
        if (mainTabs.tabCount != 0) {
            mainTabs.selectTab(mainTabs.getTabAt(0))
        }
        presenter.init()

        buttonAddFastTodo.setOnClickListener {
            presenter.addFastTodoClicked()
            MainActivity.f()
        }
        mainPager.registerOnPageChangeCallback(pageChangeCallback)

        switchFilter.setOnCheckedChangeListener { _, b ->
            if (b) {
                switchFilter.text = "Filtered"
            } else {
                switchFilter.text = "All"
            }
            MainActivity.f()
            presenter.submitTodo(b)
        }
    }

    fun addTodoFromParentActivity(todoData: TodoData) {
        presenter.insertTodoData(todoData)
    }

    fun updateOnSearch(ls: String) {
        pagerAdapter.updateOnSearch(ls, pagerPosition)
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val tab = mainTabs.getTabAt(position)
            if (tab != null) {
                MainActivity.f()
                val id = tab.tag
                presenter.changeSubTaskTabPosition(id as Long)
                Log.d("TTTT", "$position - position $id")
                pagerPosition = position
            }
        }
    }

    override fun initData(data: List<SubTaskData>) {
        pagerAdapter.initFragments(data)
    }

    override fun showMessage(str: String) =
        Toast.makeText(activity!!, str, Toast.LENGTH_SHORT).show()

    @SuppressLint("SetTextI18n")
    override fun initSwitch(boolean: Boolean) {
        switchFilter.isChecked = boolean
        if (boolean) {
            switchFilter.text = "Filtered"
        } else {
            switchFilter.text = "All"
        }
    }

    override fun openEditSubTaskDialog(data: SubTaskData) {
        val dialog = SubTaskDialog(context!!, this.data.color)
        dialog.setSubTaskData(data)
        dialog.setOnClickListener { subTaskData -> presenter.editSubTask(subTaskData) }
        dialog.show()
    }

    override fun openAddSubTaskDialog() {
        val dialog = SubTaskDialog(context!!, data.color)
        dialog.setOnClickListener { subTaskData -> presenter.createSubTask(subTaskData) }
        dialog.show()
    }

    override fun openDeleteSubTaskDialog(data: SubTaskData) {
        TODO("Not yet implemented")
    }

    //TODO
    override fun updateSubTask(data: List<SubTaskData>) = pagerAdapter.addSubTask(data)

    //TODO
    override fun deleteSubTask(data: List<SubTaskData>) = pagerAdapter.addSubTask(data)

    override fun insertSubTask(data: List<SubTaskData>) {
        pagerAdapter.addSubTask(data)
//        mainPager.setCurrentItem(data.size, true)
        val tab = mainTabs.newTab().setText(data[0].task_name).setTag(data[0].id)
//        mainTabs.addTab(tab)
        mainTabs.selectTab(tab)
    }

    override fun submitTodoData(data: List<TodoData>) {
        pagerAdapter.submitTodoList(data, pagerPosition)
    }

    override fun insertTodoData(data: TodoData) {
        pagerAdapter.insertTodoList(data, pagerPosition)
    }

    override fun getHeaderText() = inputHeader.text.toString()

    override fun clearHeaderText() {
        inputHeader.text.clear()
        mainPager.requestFocus()
        hideKeyboard(context as Activity)
    }

    private fun changeColorAttributes() {
        // filtr langan rang
        val color =
            if (isColorDark(data.color)) android.R.color.white else android.R.color.black
        val colorFilter = PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_ATOP)

        //tepa layout itemlarining ranglarini moslashtirish
        layoutTop.forEach {
            if (it is ImageButton) {
                it.colorFilter = colorFilter
            }
            if (it is TextView) {
                it.setTextColor(resources.getColor(color))
            }
            if (it is TabLayout) {
                it.tabTextColors = ColorStateList.valueOf(resources.getColor(color))
            }
        }

        //past layout itemlarining ranglarini moslashtirish
        layoutBottom.forEach {
            if (it is ImageButton) {
                it.colorFilter = colorFilter
            }
            if (it is EditText) {
                it.setTextColor(resources.getColor(color))
                val colorHint =
                    if (isColorDark(data.color)) "#B3FFFFFF" else "#B3000000"
                it.setHintTextColor(Color.parseColor(colorHint))
            }
        }

        //icon lanring ranglarini o`zgartirish
        buttonAddSublist.colorFilter = colorFilter
        buttonAddFastTodo.colorFilter = colorFilter

        //tepa va past layout larning bacgroundlarini animatsion o`zgartirish
        val colorFrom = LocalStorage.instance.oldColor

        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, data.color)
        colorAnimator.duration = 600
        colorAnimator.addUpdateListener {
            layoutTop?.setBackgroundColor(it.animatedValue as Int)
            mainTabs?.setBackgroundColor(it.animatedValue as Int)
            layoutBottom?.setBackgroundColor(it.animatedValue as Int)
        }
        colorAnimator.start()
    }

    fun refreshCurrentPage() {
        pageChangeCallback.onPageSelected(pagerPosition)
    }
}