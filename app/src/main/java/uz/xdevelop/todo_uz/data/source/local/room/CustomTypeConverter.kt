package uz.xdevelop.todo_uz.data.source.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.xdevelop.todo_uz.data.source.models.ListData
import java.lang.reflect.Type

object CustomTypeConverter {
    private val gson = Gson()

    private inline fun <reified T> parseArray(json: String, typeToken: Type): T {
        return gson.fromJson(json, typeToken)
    }

    @JvmStatic
    @TypeConverter
    fun fromListData(data: ArrayList<ListData>?): String = gson.toJson(data)

    @JvmStatic
    @TypeConverter
    fun toListData(data: String): ArrayList<ListData>? {
        val type = object : TypeToken<ArrayList<ListData>?>() {}.type
        return parseArray(
            data,
            type
        )
    }

    @JvmStatic
    @TypeConverter
    fun fromListTags(data: ArrayList<String>?): String = gson.toJson(data)

    @JvmStatic
    @TypeConverter
    fun toListTags(data: String): ArrayList<String>? {
        val type = object : TypeToken<ArrayList<String>?>() {}.type
        return parseArray(
            data,
            type
        )
    }
}