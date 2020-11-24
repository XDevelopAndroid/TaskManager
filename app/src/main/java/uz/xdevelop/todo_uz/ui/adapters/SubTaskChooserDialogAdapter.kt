package uz.xdevelop.todo_uz.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.utils.SingleBlock
import uz.xdevelop.todo_uz.utils.extensions.bindItem
import uz.xdevelop.todo_uz.utils.extensions.inflate

class SubTaskChooserDialogAdapter(val ls: List<SubTaskData>, val taskList: List<TaskData>) :
    RecyclerView.Adapter<SubTaskChooserDialogAdapter.ViewHolder>() {
    private var listener: SingleBlock<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.item_chooser_subtask))

    override fun getItemCount() = ls.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.info_text)
        private val background: View = view.findViewById(R.id.background)
        private val cardView: LinearLayout = view.findViewById(R.id.cardView)

        init {
            cardView.setOnClickListener { listener?.invoke(ls[adapterPosition].id) }
        }

        fun bind() = bindItem {
            val d = ls[adapterPosition]
            text.text = d.task_name
            val task = taskList.filter { d.task_id == it.id }

            this@ViewHolder.background.setBackgroundColor(task[0].color)
        }
    }

    fun setOnClickListener(block: SingleBlock<Long>) {
        listener = block
    }
}