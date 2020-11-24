package uz.xdevelop.todo_uz.data.source.local.room.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "sub_tasks",
    foreignKeys = [ForeignKey(
        entity = TaskData::class,
        parentColumns = ["id"],
        childColumns = ["task_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SubTaskData(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var task_name: String = "",
    var task_id: Long = 0
) : Serializable/* {
    companion object {
        val ITEM_DIFF = object : DiffUtil.ItemCallback<SubTaskData>() {
            override fun areItemsTheSame(oldItem: SubTaskData, newItem: SubTaskData) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SubTaskData, newItem: SubTaskData) =
                oldItem.task_name == newItem.task_name && oldItem.task_id == newItem.task_id
        }
    }
}*/