package uz.xdevelop.todo_uz.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_add_todo.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.room.entities.TodoData
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.ui.dialogs.ImageChooserDialog
import uz.xdevelop.todo_uz.ui.dialogs.isColorDark
import uz.xdevelop.todo_uz.utils.IconsUtil.Companion.EMOJI
import uz.xdevelop.todo_uz.utils.IconsUtil.Companion.STICKERS
import uz.xdevelop.todo_uz.utils.extensions.toFormattedDateString
import java.util.*

@Suppress("DEPRECATION")
class AddTodoActivity : AppCompatActivity() {
    companion object {
        const val RESTORE: Int = 302
        const val CANCEL: Int = 601
        const val DELETE: Int = 602
        const val DELETE_FOREVER: Int = 303
    }

    private var requestCode: Int = -1
    private var position: Int = -1
    private val currentColor = LocalStorage.instance.currentColor
    private var timer: CountDownTimer? = null

    // filtr langan rang
    val color =
        if (isColorDark(currentColor)) android.R.color.white else android.R.color.black
    private lateinit var colorFilter: PorterDuffColorFilter

    var data: TodoData = TodoData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        loadViews()
    }

    private fun loadViews() {
//        textInfo.requestFocus()
        colorFilter = PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_ATOP)
        requestCode = intent.getIntExtra("request", -1)
        position = intent.getIntExtra("position", -1)
        val type = intent.getStringExtra("type")

        if (!type.isNullOrEmpty() && type == "info") {
            layoutForInfo.visibility = View.VISIBLE
            layoutForInfo.setOnClickListener { makeText("Todo ni o'zgartirish cheklangan!") }
            saveButton.visibility = View.GONE

            ratingBar.isEnabled = false
            textTag.visibility = View.GONE
            textInfo.isEnabled = false
            chip_group.isEnabled = false
            image_emotions.isEnabled = false
            image_sticker.isEnabled = false
            img_love.isEnabled = false
            img_alarm.isEnabled = false
            checkbox.isEnabled = false
        }

        layoutTop.requestFocus()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""

        if (type.isNullOrEmpty()) {
            img_alarm.setOnClickListener { openDateTimePickerDialog() }
            img_love.setOnClickListener { changeFavouriteType() }
            checkbox.setOnClickListener { changeCheckbox() }

            image_emotions.setOnClickListener { openImageChooserDialog(EMOJI) }
            image_sticker.setOnClickListener { openImageChooserDialog(STICKERS) }
            saveButton.setOnClickListener { saveClicked() }
            ratingBar.setOnRatingBarChangeListener { _, fl, _ ->
                data.todo_priority = fl.toInt()
            }
        }
        textTag.addTextChangedListener(textWatcher)

        changeColors()

        val dataIntent = intent.getSerializableExtra("todo")
        if (dataIntent != null) fillData(dataIntent as TodoData)
    }

    private fun changeBySelectedImage(keyCode: Int, id: Int) {
        val view = if (keyCode == STICKERS) image_sticker else image_emotions
        if (id == R.drawable.ic_block) {
            view.colorFilter = colorFilter
            if (keyCode == STICKERS) {
                view.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_sticker))
                data.sticker_id = 0
            } else {
                view.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_insert_emoticon))
                data.emotion_id = 0
            }
            return
        }
        view.clearColorFilter()
        view.setImageDrawable(resources.getDrawable(id))
        if (keyCode == STICKERS) {
            data.sticker_id = id
        } else {
            data.emotion_id = id
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillData(todoData: TodoData) {
        data = todoData

        changeCheckbox(todoData.todo_finished)
        changeFavouriteType(todoData.todo_favourite)

        if (todoData.sticker_id != 0) {
            changeBySelectedImage(STICKERS, todoData.sticker_id)
            image_sticker.clearColorFilter()
        }
        if (todoData.emotion_id != 0) {
            changeBySelectedImage(EMOJI, todoData.emotion_id)
            image_emotions.clearColorFilter()
        }
        if (data.todo_date != 0L)
            textAlarm.text = todoData.todo_date.toFormattedDateString()

        if (todoData.todo_date != 0L) {
            img_alarm.clearColorFilter()
            img_alarm.setImageDrawable(resources.getDrawable(R.drawable.clock1))

            timer =
                object : CountDownTimer(
                    data.todo_date - System.currentTimeMillis(),
                    1000
                ) {
                    @SuppressLint("SetTextI18n")
                    override fun onFinish() {
                        textTimer.text = "Vaqt tugadi!"
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        val sec = (millisUntilFinished / 1000) % 60
                        val min = (millisUntilFinished / (1000 * 60)) % 60
                        val hr = (millisUntilFinished / (1000 * 60 * 60)) % 24
                        val day = ((millisUntilFinished / (1000 * 60 * 60)) / 24).toInt()
                        textTimer.text = String.format(
                            "%02d-%02d-%02d-%02d",
                            day,
                            hr,
                            min,
                            sec
                        )
                    }

                }
            timer?.start()
        }
        ratingBar.rating = todoData.todo_priority.toFloat()
        textInfo.setText(todoData.todo_text)

        if (todoData.tags != null) {
            Log.d("TTTTT", todoData.tags!!.size.toString())
            todoData.tags!!.forEach {
                val chip = Chip(this)
                chip.text = "#$it"
                chip.closeIcon = resources.getDrawable(R.drawable.ic_baseline_close)
                chip.isCloseIconVisible = true
                chip.gravity = Gravity.CENTER
                chip.textSize = 20f
                chip.setTextColor(Color.parseColor("#F57C00"))
                chip.chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                chip_group.addView(chip)
                chip.setOnCloseIconClickListener { v ->
                    chip_group.removeView(v)
                    data.tags?.remove(it)
                }
            }
        }
    }

    private fun saveClicked() {
        if (check()) {
            when (requestCode) {
                100 -> {
                    data.todo_created = Calendar.getInstance().timeInMillis
                }
                402 -> {
                    data.todo_created = Calendar.getInstance().timeInMillis
                    data.id = 0L
                }
                102 -> {
                }
                103 -> {
                }
            }
            data.todo_text = textInfo.text.toString()
            val intent = Intent()
            intent.putExtra("todo", data)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun check(): Boolean {
        if (textInfo.text.isEmpty()) {
            makeText("Matn maydoni bo'sh bo'lishi mumkin emas!")
            return false
        }
        return true
    }

    private fun makeText(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    private fun openImageChooserDialog(keyCode: Int) {
        val dialog = ImageChooserDialog(this, keyCode)
        dialog.show()
        dialog.setOnClickListener {
            changeBySelectedImage(keyCode, it)
        }
    }

    private fun changeFavouriteType() {
        if (data.todo_favourite) {
            img_love.colorFilter = colorFilter
            img_love.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24))
            data.todo_favourite = false
        } else {
            img_love.clearColorFilter()
            img_love.setImageDrawable(resources.getDrawable(R.drawable.love))
            data.todo_favourite = true
        }
    }

    private fun changeCheckbox() {
        if (data.todo_finished == 1) {
            checkbox.colorFilter = colorFilter
            checkbox.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_check_circle))
            data.todo_finished = 0
        } else {
            checkbox.clearColorFilter()
            checkbox.setImageDrawable(resources.getDrawable(R.drawable.checked))
            data.todo_finished = 1
        }
    }

    private fun changeCheckbox(id: Int) {
        if (id == 1) {
            checkbox.clearColorFilter()
            checkbox.setImageDrawable(resources.getDrawable(R.drawable.checked))
        } else {
            checkbox.colorFilter = colorFilter
            checkbox.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_check_circle))
        }
    }

    private fun changeFavouriteType(id: Boolean) {
        if (id) {
            img_love.clearColorFilter()
            img_love.setImageDrawable(resources.getDrawable(R.drawable.love))
        } else {
            img_love.colorFilter = colorFilter
            img_love.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val type = intent.getStringExtra("type")
        if (intent.getIntExtra("search", -1) != 1)
            if (!type.isNullOrEmpty() && type == "info") {
                if (position == -1) {
                    menuInflater.inflate(R.menu.menu_popup_deleted, menu)
                }
            } else
                menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            R.id.popup_menu_restore -> {
                val intent = Intent()
                intent.putExtra("data", data)
                setResult(RESTORE, intent)
                finish()
            }
            R.id.add_menu_cancel -> {
                if (data.id != 0L) {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Bekor qilish")
                    dialog.setPositiveButton("Ha") { _, _ ->
                        val intent = Intent()
                        this.data.cancelled = true
                        intent.putExtra("todo", this.data)
                        setResult(CANCEL, intent)
                        finish()
                    }
                    dialog.setNegativeButton("Yo'q") { _, _ -> }
                    dialog.setIcon(R.drawable.ic_cancelled)
                    dialog.setMessage("Ushbu vazifani bekor qilmoqchimisiz?")
                    dialog.show()
                } else {
                    makeText("Vazifani bekor qilish uchun avval uni saqlang!")
                }
            }
            R.id.add_menu_delete -> {
                if (data.id != 0L) {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("O'chirish")
                    dialog.setPositiveButton("Ha") { _, _ ->
                        val intent = Intent()
                        this.data.todo_deleted = true
                        intent.putExtra("todo", this.data)
                        setResult(DELETE, intent)
                        finish()
                    }
                    dialog.setNegativeButton("Yo'q") { _, _ -> }
                    dialog.setIcon(R.drawable.ic_delete_popup)
                    dialog.setMessage("Ushbu vazifani o'chirmoqchimisiz?")
                    dialog.show()
                } else {
                    makeText("Vazifani o'chirish uchun avval uni saqlang!")
                }
            }
            R.id.popup_menu_delete_forever -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Remove Totally")
                dialog.setPositiveButton("Yes") { _, _ ->
                    val intent = Intent()
                    intent.putExtra("data", data)
                    setResult(DELETE_FOREVER, intent)
                    makeText("Bazadan muvaffaqiyatli o'chirildi.")
                    finish()
                }
                dialog.setNegativeButton("No") { _, _ -> }
                dialog.setIcon(R.drawable.ic_trash)
                dialog.setMessage("Vazifani butunlay o'chirmoqchimisiz?")
                dialog.show()
            }
        }
        return true
    }

    //TODO delete alarm qo'yish kerak
    @SuppressLint("NewApi", "SimpleDateFormat")
    fun openDateTimePickerDialog() {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val mHour = c.get(Calendar.HOUR_OF_DAY)
                val mMinute = c.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        val date = Calendar.getInstance()
                        date.set(year, monthOfYear, dayOfMonth, hour, minute)

                        textAlarm.text = date.timeInMillis.toFormattedDateString()
                        data.todo_date = date.timeInMillis

                        img_alarm.clearColorFilter()
                        img_alarm.setImageDrawable(resources.getDrawable(R.drawable.clock1))
                        if (date.timeInMillis > System.currentTimeMillis()) {
                            timer =
                                object : CountDownTimer(
                                    data.todo_date - System.currentTimeMillis(),
                                    1000
                                ) {
                                    @SuppressLint("SetTextI18n")
                                    override fun onFinish() {
                                        textTimer.text = "Vaqt tugadi!"
                                    }

                                    override fun onTick(millisUntilFinished: Long) {
                                        val sec = (millisUntilFinished / 1000) % 60
                                        val min = (millisUntilFinished / (1000 * 60)) % 60
                                        val hr = (millisUntilFinished / (1000 * 60 * 60)) % 24
                                        val day =
                                            ((millisUntilFinished / (1000 * 60 * 60)) / 24).toInt()
                                        textTimer.text = String.format(
                                            "%02d-%02d-%02d-%02d",
                                            day,
                                            hr,
                                            min,
                                            sec
                                        )
                                    }
                                }
                            timer?.cancel()
                            timer?.start()
                        } else {
                            timer?.cancel()
                            textTimer.text = ""
                        }
                    },
                    mHour,
                    mMinute,
                    false
                )
                timePickerDialog.show()
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "O'chirish") { _, _ ->
            img_alarm.colorFilter = colorFilter
            img_alarm.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_alarm))
            textAlarm.text = ""
            timer?.cancel()
            textTimer.text = ""
            data.todo_date = 0L
        }
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable) {}
        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}

        @SuppressLint("SetTextI18n", "DefaultLocale")
        //TODO tagning textini simvollardan holiligini tekshirish kerak
        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            if (p0.toString().contains("\n")) {
                if (p0.toString().length == 1) {
                    textTag.text.clear()
                    return
                }
                if (!checkChipGroup(p0.toString().toLowerCase())) {
                    val chip = Chip(this@AddTodoActivity)
                    chip.text = "#${p0.toString().toLowerCase()}"
                    chip.closeIcon = resources.getDrawable(R.drawable.ic_baseline_close)
                    chip.isCloseIconVisible = true
                    chip.gravity = Gravity.CENTER
                    chip.textSize = 20f
                    chip.setTextColor(Color.parseColor("#F57C00"))
                    chip.chipBackgroundColor =
                        ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                    chip.setOnCloseIconClickListener {
                        chip_group.removeView(it)
                        data.tags?.remove((it as Chip).text.substring(1))
                    }

                    chip_group.addView(chip)

                    if (data.tags == null)
                        data.tags = arrayListOf()
                    if (p0.isNotEmpty()) {
                        data.tags?.add(p0.toString().toLowerCase())
                        textTag.text.clear()
                    }
                }
                textTag.text.clear()
            }
            if (p0.toString().contains(" ")) {
                makeText("#tag bo'sh joydan iborat bo'la olmaydi")
                val l = textTag.text.length
                textTag.setText(textTag.text.toString().replace(" ", ""))
                textTag.setSelection(l - 1)
            }
        }
    }

    private fun checkChipGroup(str: String): Boolean {
        for (i in 0 until chip_group.size) {
            if (chip_group[i] is Chip) {
                val ch = chip_group[i] as Chip
                if (ch.text.toString() == "#$str")
                    return true
            }
        }
        return false
    }

    private fun changeColors() {
        toolbar.setBackgroundColor(currentColor)
        cardView.setCardBackgroundColor(currentColor)
        textTimer.setTextColor(color)

        //tepa layout itemlarining ranglarini moslashtirish
        toolbar.forEach {
            if (it is ImageButton) {
                it.colorFilter = colorFilter
            }
            if (it is TextView) {
                it.setTextColor(resources.getColor(color))
            }
        }

        //main layout itemlarining ranglarini moslashtirish
        mainLayout.forEach {
            if (it is ImageButton) {
                it.colorFilter = colorFilter
            }
            if (it is TextView) {
                it.setTextColor(resources.getColor(color))
            }
        }

        val colorHint =
            if (isColorDark(currentColor)) "#B3FFFFFF" else "#B3000000"
        textInfo.setHintTextColor(Color.parseColor(colorHint))

        img_love.colorFilter = colorFilter
        img_alarm.colorFilter = colorFilter
        image_sticker.colorFilter = colorFilter
        image_emotions.colorFilter = colorFilter
        checkbox.colorFilter = colorFilter
    }

    override fun onStop() {
        super.onStop()
        data = TodoData()
    }
}