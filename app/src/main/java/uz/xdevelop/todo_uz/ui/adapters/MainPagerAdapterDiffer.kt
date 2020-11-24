package uz.xdevelop.todo_uz.ui.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ListAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.ui.fragments.TodoFragment

class MainPagerAdapterDiffer(activity: Fragment) :
    FragmentStateAdapter(activity) {
    //    private var fragments = mutableListOf<TodoFragment>()
    private var fragments: ArrayList<TodoFragment> = ArrayList()
//    private val differ = AsyncListDiffer(this, ITEM_DIFF)
    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
    /*TodoFragment()
        .apply {
            fragments.add(this)
            Log.d("TTTT", "$position - Fragment")
        }*/
    /*.putArguments {
        putSerializable("DATA", differ.currentList[position])
    }*/

//    fun submitList(ls: List<SubTaskData>) = differ.submitList(ls)
//    val currentList: MutableList<SubTaskData> get() = differ.currentList

    fun submitTodoList(ls: List<TodoData>, position: Int) {
        fragments[position].initTodoData(ls)
    }

    fun insertTodoList(ls: TodoData, position: Int) {
        fragments[position].insertTodoData(ls)
    }
    /*private var listener: ((Int) -> Unit)? = null
    fun setButtonClickListener(block: (Int) -> Unit) {
        listener = block
    }*/
}