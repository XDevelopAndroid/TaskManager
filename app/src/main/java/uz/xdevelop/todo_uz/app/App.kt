package uz.xdevelop.todo_uz.app

import android.app.Application
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.utils.IconsUtil

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalStorage.init(instance)

        IconsUtil.fillEmoji()
        IconsUtil.fillStickers()
    }

    companion object {
        lateinit var instance: App
    }
}