package uz.xdevelop.todo_uz.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.ui.fragments.FragmentAllTodo
import uz.xdevelop.todo_uz.utils.extensions.putArguments

class TabFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val type: String = "DEF"
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = if (type == "DEF") 4 else 3
    override fun createFragment(position: Int): Fragment = FragmentAllTodo()
        .putArguments {
            putInt("position", position)
            putString("type", type)
        }
}