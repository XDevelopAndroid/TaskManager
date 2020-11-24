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
import uz.xdevelop.todo_uz.app.App
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.utils.SingleBlock
import uz.xdevelop.todo_uz.utils.extensions.bindItem
import uz.xdevelop.todo_uz.utils.extensions.inflate
import uz.xdevelop.todo_uz.utils.extensions.toFormattedDateString

@Suppress("DEPRECATION")
class DeletedAdapter(val context: Context) :
    RecyclerView.Adapter<DeletedAdapter.ViewHolder>() {
    private val currentColor = LocalStorage.instance.currentColor
    val color =
        if (isColorDark(currentColor)) android.R.color.white else android.R.color.black
    private var colorFilter: PorterDuffColorFilter

    private var listenerItem: SingleBlock<TodoData>? = null
    private var listenerRestore: SingleBlock<TodoData>? = null
    private var listenerDelete: SingleBlock<TodoData>? = null

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
            App.instance.resources.getColor(color),
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
    }

    fun clear() {
        sortedList.clear()
    }

    fun setOnItemClickListener(block: SingleBlock<TodoData>) {
        listenerItem = block
    }

    fun setOnItemEditListener(block: SingleBlock<TodoData>) {
        listenerRestore = block
    }

    fun setOnItemDeleteListener(block: SingleBlock<TodoData>) {
        listenerDelete = block
    }

    fun restoreItem(todoData: TodoData) {
        sortedList.remove(todoData)
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
            menuInflater.inflate(R.menu.menu_popup_deleted, menuBuilder)
            val menu = MenuPopupHelper(context, menuBuilder, view)
            menu.setForceShowIcon(true)
            menu.show(0, 0)
//            menu.show()
            menuBuilder.setCallback(object : MenuBuilder.Callback {
                override fun onMenuModeChange(menu: MenuBuilder?) {

                }

                override fun onMenuItemSelected(menu: MenuBuilder?, item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.popup_menu_delete_forever -> {
                            val dialog = AlertDialog.Builder(context)
                            dialog.setTitle("Butunlay o'chirish")
                            dialog.setPositiveButton("Ha") { _, _ ->
                                listenerDelete?.invoke(sortedList[pos])
                                sortedList.removeItemAt(pos)

                                showMessage("Bazadan muvaffaqiyatli o'chirildi.")
                            }
                            dialog.setNegativeButton("No") { _, _ -> }
                            dialog.setIcon(R.drawable.ic_trash)
                            dialog.setMessage("Ushbu vazifani butunlay o'chirishni istaysizmi?")
                            dialog.show()
                        }
                        R.id.popup_menu_restore -> {
//                            sortedList[pos].todo_deleted = false
                            listenerRestore?.invoke(sortedList[pos])
//                            sortedList.removeItemAt(pos)
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

    private fun showMessage(string: String) {
        Toast.makeText(
            context,
            string,
            Toast.LENGTH_LONG
        ).show()
    }
}