package uz.xdevelop.todo_uz.utils.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDatetime(pattern: String = "yyyy-MM-dd HH:mm:ss"): String? = SimpleDateFormat(pattern,  Locale.getDefault()).format(this)