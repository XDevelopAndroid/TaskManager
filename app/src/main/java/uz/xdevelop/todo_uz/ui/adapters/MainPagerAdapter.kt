package uz.xdevelop.todo_uz.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.ui.fragments.TodoFragment

class MainPagerAdapter(activity: Fragment) :
    FragmentStateAdapter(activity) {
    private var fragments: ArrayList<TodoFragmentHybrid> = ArrayList()

    //    private var listenerEdit: SingleBlock<TodoData>? = null
    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position].fragment

    fun initFragments(ls: List<SubTaskData>) {
        ls.forEach {
            val fr = TodoFragment()
            /*fr.setOnItemEditListener { t ->
                listenerEdit?.invoke(t)
            }*/
            fragments.add(
                TodoFragmentHybrid(
                    fr,
                    it
                )
            )
        }
        notifyDataSetChanged()
    }

    val currentFragments: MutableList<TodoFragmentHybrid> get() = fragments

    fun addSubTask(ls: List<SubTaskData>) {
        fragments.add(
            TodoFragmentHybrid(
                TodoFragment(),
                ls.last()
            )
        )
        notifyItemInserted(ls.size - 1)
    }

    fun submitTodoList(ls: List<TodoData>, position: Int) {
        fragments[position].fragment.initTodoData(ls)
    }

    fun updateOnSearch(ls: String, position: Int) {
        val d = fragments[position]
        d.fragment.updateOnSearch(ls, d.data.id)
    }

    fun insertTodoList(ls: TodoData, position: Int) {
        fragments[position].fragment.insertTodoData(ls)
    }

    /*fun setOnItemEditListener(block: SingleBlock<TodoData>) {
        listenerEdit = block
    }*/
    /*private var listener: ((Int) -> Unit)? = null
    fun setButtonClickListener(block: (Int) -> Unit) {
        listener = block
    }*/
}