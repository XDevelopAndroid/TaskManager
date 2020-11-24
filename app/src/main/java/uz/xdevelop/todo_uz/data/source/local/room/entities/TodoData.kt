package uz.xdevelop.todo_uz.data.source.local.room.entities

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import uz.xdevelop.todo_uz.data.source.models.ListData
import java.io.Serializable

@Entity(
    tableName = "todo",
    foreignKeys = [ForeignKey(
        entity = SubTaskData::class,
        parentColumns = ["id"],
        childColumns = ["sub_task_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TodoData(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var todo_text: String = "",
    var todo_date: Long = 0,
    var todo_created: Long = 0,
    var todo_priority: Int = 0,
    var todo_finished: Int = 0,
    var todo_favourite: Boolean = false,
    var todo_deleted: Boolean = false,
    var cancelled: Boolean = false,
    var emotion_id: Int = 0,
    var sticker_id: Int = 0,
    var image_id: Int = 0,
    var tags: ArrayList<String>? = null,
    var sub_task_id: Long = 0,
    @SerializedName("list")
    var list: ArrayList<ListData>? = null
) : Serializable {
    companion object {
        val ITEM_DIFF = object : DiffUtil.ItemCallback<TodoData>() {
            override fun areItemsTheSame(oldItem: TodoData, newItem: TodoData) =
                TextUtils.equals(oldItem.id.toString(), newItem.id.toString())

            override fun areContentsTheSame(oldItem: TodoData, newItem: TodoData) =
                oldItem.todo_text == newItem.todo_text && oldItem.todo_date == newItem.todo_date &&
                        oldItem.todo_priority == newItem.todo_priority && oldItem.todo_finished == newItem.todo_finished &&
                        oldItem.emotion_id == newItem.emotion_id && oldItem.sticker_id == newItem.sticker_id &&
                        oldItem.image_id == newItem.image_id &&
                        oldItem.list == newItem.list && oldItem.sub_task_id == newItem.sub_task_id
        }
    }
}