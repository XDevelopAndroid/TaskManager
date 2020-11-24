package uz.xdevelop.contactonlinetrainingroommvp.ui.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_todo.view.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.utils.SingleBlock
import uz.xdevelop.todo_uz.utils.extensions.bindItem
import uz.xdevelop.todo_uz.utils.extensions.inflate
import uz.xdevelop.todo_uz.utils.extensions.toFormattedDateString
import java.util.*

@Suppress("DEPRECATION")
class AllTodoFragmentAdapter(val context: Context, val position: Int, val type: String = "DEF") :
    RecyclerView.Adapter<AllTodoFragmentAdapter.ViewHolder>() {
    private val currentColor = LocalStorage.instance.currentColor
    val color =
        if (isColorDark(currentColor)) android.R.color.white else android.R.color.black
    private var colorFilter: PorterDuffColorFilter

    private var listenerItem: SingleBlock<TodoData>? = null
    private var listenerClone: SingleBlock<TodoData>? = null
    private var listenerDelete: SingleBlock<TodoData>? = null
    private var listenerEdit: SingleBlock<TodoData>? = null

    private val adapterCallback = object : SortedListAdapterCallback<TodoData>(this) {
        override fun areItemsTheSame(item1: TodoData, item2: TodoData): Boolean {
            return item1.id == item2.id
        }

        override fun compare(o1: TodoData, o2: TodoData): Int = o1.todo_date.compareTo(o2.todo_date)

        override fun areContentsTheSame(oldItem: TodoData, newItem: TodoData): Boolean {
            return oldItem == newItem
        }
    }

    val sortedList = SortedList(TodoData::class.java, adapterCallback)

    init {
        colorFilter = PorterDuffColorFilter(
            context.resources.getColor(color),
            PorterDuff.Mode.SRC_ATOP
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_todo))
    }

    override fun getItemCount(): Int = sortedList.size()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    fun submitList(ls: List<TodoData>) {
        sortedList.replaceAll(ls)
        notifyDataSetChanged()
    }

    fun insert(person: TodoData): Int {
        return sortedList.add(person)
    }

    fun setOnItemClickListener(block: SingleBlock<TodoData>) {
        listenerItem = block
    }

    fun setOnItemCloneListener(block: SingleBlock<TodoData>) {
        listenerClone = block
    }

    fun setOnItemDeleteListener(block: SingleBlock<TodoData>) {
        listenerDelete = block
    }

    fun setOnItemEditListener(block: SingleBlock<TodoData>) {
        listenerEdit = block
    }

    fun update(person: TodoData) {
        for (i in 0 until sortedList.size()) {
            if (sortedList.get(i).id == person.id) {
                if (person.todo_finished == 1) {
                    sortedList.removeItemAt(i)
                    showMessage("Yaratilgan vazifa Bajarilgan vazifalar bo'limiga qo'shildi!")
                    return
                }
                if (person.cancelled) {
                    sortedList.removeItemAt(i)
                    showMessage("Vazifa Bekor qilingan vazifalar bo'limiga o'tkazildi!")
                    return
                }
                if (person.todo_date == 0L && LocalStorage.instance.filter) {
                    sortedList.removeItemAt(i)
                    showMessage("Yaratilgan vazifa shu bo'limning saralanmagan qismiga qo'shildi!")
                    return
                }
                if (person.todo_date < System.currentTimeMillis() && person.todo_date != 0L) {
                    sortedList.removeItemAt(i)
                    showMessage("Vazifa eskirgan vazifalar bo'limiga o'tkazildi!")
                    return
                }
                sortedList.updateItemAt(i, person)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.apply {
                val cColor = LocalStorage.instance.currentColor
                mainLayout.setBackgroundColor(cColor)
                // filtr langan rang
                val color =
                    if (isColorDark(cColor)) android.R.color.white else android.R.color.black
//                val colorFilter = PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_ATOP)

                //itemlarining ranglarini moslashtirish
                mainLayout.forEach {
                    if (it is TextView)
                        it.setTextColor(resources.getColor(color))
                }

                action_view.setOnClickListener { listenerItem?.invoke(sortedList[adapterPosition]) }

                if (type == "DEF")
                    action_view.setOnLongClickListener {
                        popupMenu(adapterPosition, it)
                        true
                    }
            }
        }

        @SuppressLint("RestrictedApi")
        private fun popupMenu(pos: Int, view: View) {
            val menuBuilder = MenuBuilder(context)
            val menuInflater = MenuInflater(context)
            if (this@AllTodoFragmentAdapter.position == 0)
                menuInflater.inflate(R.menu.menu_popup, menuBuilder)
            else
                menuInflater.inflate(R.menu.menu_popup_all_simple, menuBuilder)

            val menu = MenuPopupHelper(context, menuBuilder, view)
            menu.setForceShowIcon(true)
            menu.show(0, 0)
//            menu.show()
            menuBuilder.setCallback(object : MenuBuilder.Callback {
                override fun onMenuModeChange(menu: MenuBuilder?) {

                }

                override fun onMenuItemSelected(menu: MenuBuilder?, item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.popup_menu_delete -> {
                            val dialog = AlertDialog.Builder(context)
                            dialog.setTitle("O'chirish")
                            dialog.setPositiveButton("Ha") { _, _ ->
                                listenerDelete?.invoke(sortedList[pos])
                                sortedList.removeItemAt(pos)
                            }
                            dialog.setNegativeButton("Yo'q") { _, _ -> }
                            dialog.setIcon(R.drawable.ic_trash)
                            dialog.setMessage("Ushbu vazifani o'chirishni istaysizmi?")
                            dialog.show()
                        }
                        R.id.popup_menu_edit -> {
                            listenerItem?.invoke(sortedList[pos])
                        }
                        R.id.popup_menu_copy -> {
                            listenerClone?.invoke(sortedList[pos])
                        }
                        R.id.popup_menu_finish -> {
                            sortedList[pos].todo_finished = 1
                            listenerEdit?.invoke(sortedList[pos])
                            showMessage("Vazifa Bajarilgan vazifalar bo'limiga o'tkazildi!")
                            sortedList.removeItemAt(pos)
                        }
                        R.id.popup_menu_cancel -> {
                            val dialog = AlertDialog.Builder(context)
                            dialog.setTitle("Bekor qilish")
                            dialog.setPositiveButton("Ha") { _, _ ->
                                sortedList[pos].cancelled = true
                                listenerEdit?.invoke(sortedList[pos])
                                showMessage("Vazifa Bekor qilingan vazifalar bo'limiga o'tkazildi!")
                                sortedList.removeItemAt(pos)
                            }
                            dialog.setNegativeButton("Yo'q") { _, _ -> }
                            dialog.setIcon(R.drawable.ic_cancelled)
                            dialog.setMessage("Ushbu vazifani bekor qilmoqchimisiz?")
                            dialog.show()
                        }
                    }
                    return true
                }

            })

        }

        @SuppressLint("SetTextI18n", "NewApi")
        fun bind() = bindItem {
            val d = sortedList[adapterPosition]
            layoutList.visibility = View.GONE
            text_info.text = d.todo_text

            if (this@AllTodoFragmentAdapter.position == 0 && type == "DEF") {
                when (checkDate(d.todo_date)) {
                    1 -> {
                        layoutBottom.setBackgroundColor(resources.getColor(R.color.itemColorSoon))
                    }
                    2 -> {
                        layoutBottom.setBackgroundColor(resources.getColor(R.color.itemColorMiddle))
                    }
                    3 -> {
                        layoutBottom.setBackgroundColor(resources.getColor(R.color.itemColorFar))
                    }
                    4 -> {
                        layoutBottom.setBackgroundColor(resources.getColor(R.color.itemB))
                    }
                }
            }

            action_view.setOnClickListener { listenerItem?.invoke(d) }
            if (d.todo_date != 0L) {
                textAlarm.text = d.todo_date.toFormattedDateString()
                img_alarm.clearColorFilter()
                img_alarm.setImageDrawable(resources.getDrawable(R.drawable.clock1))
            } else {
                img_alarm.colorFilter = colorFilter
                img_alarm.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_alarm))
                textAlarm.text = ""
            }
            image_emotions.setImageResource(d.emotion_id)
            if (d.sticker_id == 0) {
                image_sticker.visibility = View.GONE
            } else {
                image_sticker.visibility = View.VISIBLE
            }
            image_sticker.setImageResource(d.sticker_id)
            ratingBar.rating = d.todo_priority.toFloat()
            if (d.todo_finished == 1) {
                checkbox.clearColorFilter()
                checkbox.setImageDrawable(resources.getDrawable(R.drawable.checked))
            } else {
                checkbox.colorFilter = colorFilter
                checkbox.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_check_circle))
            }

            if (d.todo_favourite) {
                img_love.clearColorFilter()
                img_love.setImageDrawable(resources.getDrawable(R.drawable.love))
            } else {
                img_love.colorFilter = colorFilter
                img_love.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24))
            }

            if (d.tags != null) {
                chipsLayout.visibility = View.VISIBLE
                chip_group.removeAllViews()
                d.tags!!.forEach {
                    val chip = Chip(context)
                    chip.text = "#$it"
                    chip.gravity = Gravity.CENTER
                    chip.textSize = 12f
                    chip.setTextColor(Color.parseColor("#F57C00"))
                    chip_group.addView(chip)
                    chip.chipBackgroundColor =
                        ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                }
            } else {
                chipsLayout.visibility = View.GONE
            }
        }
    }

    private fun checkDate(long: Long): Int {
        if (long == 0L)
            return 4
        val check = Calendar.getInstance()
        check.timeInMillis = long

        val now = Calendar.getInstance()
        now.timeInMillis = System.currentTimeMillis()

        val soon = Calendar.getInstance()
        soon.timeInMillis = now.timeInMillis
        soon.add(Calendar.DATE, 1)

        val middle = Calendar.getInstance()
        middle.timeInMillis = now.timeInMillis
        middle.add(Calendar.DATE, 4)

        if (check.after(soon) && check.before(middle))
            return 2

        if (check.before(soon) && check.after(now))
            return 1

        if (check.after(middle))
            return 3
        return 4
    }

    private fun showMessage(string: String) {
        Toast.makeText(
            context,
            string,
            Toast.LENGTH_LONG
        ).show()
    }
}