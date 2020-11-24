package uz.xdevelop.todo_uz.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chooser.view.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.utils.IconsUtil.Companion.STICKERS
import uz.xdevelop.todo_uz.utils.IconsUtil.Companion.emojiList
import uz.xdevelop.todo_uz.utils.IconsUtil.Companion.stickersList
import uz.xdevelop.todo_uz.utils.SingleBlock
import uz.xdevelop.todo_uz.utils.extensions.bindItem
import uz.xdevelop.todo_uz.utils.extensions.inflate

@Suppress("DEPRECATION")
class ImageChooserDialogAdapter(keyCode: Int) :
    RecyclerView.Adapter<ImageChooserDialogAdapter.ViewHolder>() {
    private val list: ArrayList<Int> = if (keyCode == STICKERS) {
        stickersList
    } else emojiList
    private var listener: SingleBlock<Int>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.item_chooser))

    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.image

        fun bind() = bindItem {
            this@ViewHolder.image.setImageDrawable(resources.getDrawable(list[adapterPosition]))
            this@ViewHolder.image.setOnClickListener { listener?.invoke(list[adapterPosition]) }
        }
    }

    fun setOnClickListener(block: SingleBlock<Int>) {
        listener = block
    }
}