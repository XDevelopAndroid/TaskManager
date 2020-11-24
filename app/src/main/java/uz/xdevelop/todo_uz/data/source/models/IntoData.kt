package uz.xdevelop.todo_uz.data.source.models

import java.io.Serializable

data class IntoData(
    var header: String,
    var image: Int,
    var background: Int,
    var text: String
) : Serializable