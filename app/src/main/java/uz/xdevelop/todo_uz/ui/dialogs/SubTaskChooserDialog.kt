package uz.xdevelop.todo_uz.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_subtask_copy.view.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.app.App
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.ui.adapters.SubTaskChooserDialogAdapter
import uz.xdevelop.todo_uz.utils.SingleBlock

class SubTaskChooserDialog(
    context: Context,
    val ls: List<SubTaskData>,
    private val taskList: List<TaskData>
) : AlertDialog(context) {
    @SuppressLint("InflateParams")
    private val contentView =
        LayoutInflater.from(context).inflate(R.layout.dialog_subtask_copy, null, false)
    private var listener: SingleBlock<Long>? = null

    init {
        setView(contentView)
        contentView.apply {
            contentView.listCopy.layoutManager = LinearLayoutManager(App.instance.baseContext, LinearLayoutManager.VERTICAL, false)
            val adapter = SubTaskChooserDialogAdapter(ls, taskList)
            contentView.listCopy.adapter = adapter

            adapter.setOnClickListener {
                listener?.invoke(it)
                dismiss()
            }
        }
    }

    fun setOnClickListener(block: SingleBlock<Long>) {
        listener = block
    }
}