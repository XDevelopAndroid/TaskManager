package uz.xdevelop.todo_uz.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.dialog_emoji.view.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.app.App
import uz.xdevelop.todo_uz.ui.adapters.ImageChooserDialogAdapter
import uz.xdevelop.todo_uz.utils.IconsUtil.Companion.STICKERS
import uz.xdevelop.todo_uz.utils.SingleBlock

class ImageChooserDialog(
    context: Context, keyCode: Int
) : AlertDialog(context) {
    @SuppressLint("InflateParams")
    private val contentView =
        LayoutInflater.from(context).inflate(R.layout.dialog_emoji, null, false)
    private var listener: SingleBlock<Int>? = null

    init {
        setView(contentView)
        contentView.apply {
            text_header.text = if (keyCode == STICKERS) "Stickers" else "Emotions"
            list.layoutManager = GridLayoutManager(App.instance.baseContext, 4)
            val adapter = ImageChooserDialogAdapter(keyCode)
            list.adapter = adapter

            adapter.setOnClickListener {
                listener?.invoke(it)
                dismiss()
            }
        }
    }

    fun setOnClickListener(block: SingleBlock<Int>) {
        listener = block
    }
}