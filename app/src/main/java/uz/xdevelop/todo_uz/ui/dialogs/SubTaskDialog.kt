package uz.xdevelop.todo_uz.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_task.view.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.utils.SingleBlock

@Suppress("DEPRECATION")
class SubTaskDialog(
    context: Context,
    @ColorInt initialColor: Int = Color.parseColor(R.color.colorPrimary.toString())
) : AlertDialog(context) {
    @SuppressLint("InflateParams")
    private val contentView =
        LayoutInflater.from(context).inflate(R.layout.dialog_subtask, null, false)
    private var listener: SingleBlock<SubTaskData>? = null
    private var subTaskData: SubTaskData? = null

    init {
        setView(contentView)
        setButton(BUTTON_POSITIVE, "Saqlash") { _, _ -> }
        setButton(BUTTON_NEGATIVE, "Bekor qilish") { _, _ ->
            cancel()
        }
        changeTextColors(initialColor)

        setOnShowListener {
            val b = getButton(DialogInterface.BUTTON_POSITIVE)
            b.setOnClickListener {
                val bool =
                    contentView.textName.text.toString() != ""
                if (bool) {
                    val data = subTaskData ?: SubTaskData()
                    data.task_name = contentView.textName.text.toString()
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
    }

    fun setSubTaskData(subTaskData: SubTaskData) = with(contentView) {
        this@SubTaskDialog.subTaskData = subTaskData
        textName.setText(subTaskData.task_name)
    }

    private fun changeTextColors(@ColorInt color: Int) = with(contentView) {
        layoutHeader.setBackgroundColor(color)
        val c =
            if (isColorDark(color)) android.R.color.white else android.R.color.black
        textName.setTextColor(context.resources.getColor(c))
        text_header.setTextColor(context.resources.getColor(c))
        val colorHint =
            if (isColorDark(color)) "#B3FFFFFF" else "#B3000000"
        textName.setHintTextColor(Color.parseColor(colorHint))
    }

    fun setOnClickListener(block: SingleBlock<SubTaskData>) {
        listener = block
    }
}