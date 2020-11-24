package uz.xdevelop.todo_uz.data.source.local.storage

import android.content.Context

class LocalStorage private constructor(context: Context) {
    companion object {
        lateinit var instance: LocalStorage; private set

        fun init(context: Context) {
            instance =
                LocalStorage(
                    context
                )
        }
    }

    private val pref = context.getSharedPreferences("LocalStorage", Context.MODE_PRIVATE)
//    private val pref = SecurePreferences(context, "55555", "LocalStorage")

    var lastTaskId: Long by LongPreference(
        pref
    )

    var subTaskId: Long by LongPreference(
        pref
    )

    var oldColor: Int by IntPreference(
        pref
    )

    var currentColor: Int by IntPreference(
        pref
    )

    var filter: Boolean by BooleanPreference(
        pref
    )

    var isFirst: Boolean by BooleanPreferenceDefTrue(pref)

    fun clear() {
        pref.edit().clear().apply()
    }
}