package uz.xdevelop.todo_uz.utils.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Long.toFormattedDateString(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
    return formatter.format(Date(this))
}