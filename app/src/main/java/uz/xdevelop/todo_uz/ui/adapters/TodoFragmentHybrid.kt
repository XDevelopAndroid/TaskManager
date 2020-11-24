package uz.xdevelop.todo_uz.ui.adapters

import uz.xdevelop.todo_uz.data.source.local.room.entities.SubTaskData
import uz.xdevelop.todo_uz.ui.fragments.TodoFragment

data class TodoFragmentHybrid(
    var fragment: TodoFragment,
    var data: SubTaskData
)
