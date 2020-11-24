package uz.xdevelop.todo_uz.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.xdevelop.todo_uz.data.source.models.IntoData
import uz.xdevelop.todo_uz.ui.fragments.IntoFragment
import uz.xdevelop.todo_uz.utils.extensions.putArguments

class IntoPagerAdapter(private val data: List<IntoData>, activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = data.size
    override fun createFragment(position: Int): Fragment = IntoFragment()
        .putArguments {
            putSerializable("DATA", data[position])
        }
}