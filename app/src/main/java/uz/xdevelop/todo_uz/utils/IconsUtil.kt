package uz.xdevelop.todo_uz.utils

import uz.xdevelop.todo_uz.R

class IconsUtil {
    companion object {
        private const val STICKERS_COUNT = 69
        const val STICKERS = 1

        private const val EMOJI_COUNT = 49
        const val EMOJI = 2

        val stickersList: ArrayList<Int> = arrayListOf()
        val emojiList: ArrayList<Int> = arrayListOf()

        fun fillStickers() {
            var name = "ic_block"
            var res = R.drawable::class.java
            var field = res.getField(name)
            var resId = field.getInt(name)
            stickersList.add(resId)

            for (i in 1..STICKERS_COUNT) {
                name = "sticker_$i"

                res = R.drawable::class.java
                field = res.getField(name)
                resId = field.getInt(name)
                stickersList.add(resId)
            }
        }

        fun fillEmoji() {
            var name = "ic_block"
            var res = R.drawable::class.java
            var field = res.getField(name)
            var resId = field.getInt(name)
            emojiList.add(resId)

            for (i in 1..EMOJI_COUNT) {
                name = "emoji_$i"

                res = R.drawable::class.java
                field = res.getField(name)
                resId = field.getInt(null)
                emojiList.add(resId)
            }
        }

        /*fun getResId(resName: String?, c: Class<*>): Int {
            return try {
                val idField: Field = c.getDeclaredField(resName!!)
                idField.getInt(idField)
            } catch (e: Exception) {
                e.printStackTrace()
                -1
            }
        }*/
    }
}