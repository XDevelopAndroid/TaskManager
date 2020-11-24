package uz.xdevelop.todo_uz.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_task.*
import kotlinx.android.synthetic.main.dialog_task.view.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.entities.TaskData
import uz.xdevelop.todo_uz.utils.SingleBlock

@Suppress("DEPRECATION")
class TaskDialog(context: Context) : AlertDialog(context) {
    @SuppressLint("InflateParams")
    private val contentView =
        LayoutInflater.from(context).inflate(R.layout.dialog_task, null, false)
    private var listener: SingleBlock<TaskData>? = null
    private var taskData: TaskData? = null

    init {
        setView(contentView)
        setButton(BUTTON_POSITIVE, "Saqlash") { _, _ -> }
        setButton(BUTTON_NEGATIVE, "Bekor qilish") { _, _ ->
            cancel()
        }
        val c = (contentView.layoutHeader.background as ColorDrawable).color
        contentView.color_picker.setInitialColor(c, true)
        changeTextColors(c)

        setOnShowListener {
            val b = getButton(DialogInterface.BUTTON_POSITIVE)
            b.setOnClickListener {
                val bool =
                    contentView.textName.text.toString() != ""
                if (bool) {
                    val data = taskData ?: TaskData()
                    data.task_name = contentView.textName.text.toString()
                    data.color = contentView.color_picker.selectedColor
                    listener?.invoke(data)
                    cancel()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.toast_fill_field),
                        Toast.LENGTH_SHORT
                    ).show()
                    contentView.textName.requestFocus()
                }
            }
        }

        contentView.color_picker.addOnColorChangedListener {
            layoutHeader.setBackgroundColor(it)
            changeTextColors(color_picker.selectedColor)
        }
    }

    fun setTaskData(taskData: TaskData) = with(contentView) {
        this@TaskDialog.taskData = taskData
        /*inputFullName.setText(studentData.full_name)
        inputNumber.setText(studentData.mobile)*/
        color_picker.setInitialColor(taskData.color, true)
        textName.setText(taskData.task_name)
        layoutHeader.setBackgroundColor(taskData.color)
        changeTextColors(taskData.color)
    }

    private fun changeTextColors(@ColorInt color: Int) = with(contentView) {
        val c =
            if (isColorDark(color)) android.R.color.white else android.R.color.black
        textName.setTextColor(context.resources.getColor(c))
        text_header.setTextColor(context.resources.getColor(c))
        val colorHint =
            if (isColorDark(color)) "#B3FFFFFF" else "#B3000000"
        textName.setHintTextColor(Color.parseColor(colorHint))
    }

    fun setOnClickListener(block: SingleBlock<TaskData>) {
        listener = block
    }
}

fun isColorDark(color: Int, percent: Double = 0.5): Boolean {
    val darkness =
        1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness >= percent
}