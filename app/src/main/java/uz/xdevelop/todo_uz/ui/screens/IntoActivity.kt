package uz.xdevelop.todo_uz.ui.screens

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_intro.*
import uz.xdevelop.todo_uz.R
import uz.xdevelop.todo_uz.data.source.local.storage.LocalStorage
import uz.xdevelop.todo_uz.data.source.models.IntoData
import uz.xdevelop.todo_uz.ui.adapters.IntoPagerAdapter
import uz.xdevelop.todo_uz.utils.extensions.changeNavigationBarColor
import uz.xdevelop.todo_uz.utils.extensions.changeStatusBarColor
import uz.xdevelop.todo_uz.utils.extensions.toDarkenColor
import java.io.File
import java.io.FileOutputStream

@Suppress("NAME_SHADOWING", "SameParameterValue")
class IntoActivity : AppCompatActivity() {
    private lateinit var adapter: IntoPagerAdapter

    private val data = arrayListOf(
        IntoData(
            "Tartib",
            R.drawable.page1,
            R.drawable.path1,
            "Ishlaringizni qaydlar orqali tartibga soling"
        ),
        IntoData(
            "Qulaylik",
            R.drawable.page2,
            R.drawable.path2,
            "Qulay interfeysdan maroq bilan foydalaning"
        ),
        IntoData(
            "Oddiylik",
            R.drawable.page3,
            R.drawable.path3,
            "Rejalarni oddiy tartib asosida yarating"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        if (LocalStorage.instance.isFirst) {
            loadViews()
        } else {
            startActivityForResult(Intent(this, MainActivity::class.java), 2)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(1, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 2 && requestCode == 2) {
            onBackPressed()
        }
    }

    private fun loadViews() {
        terms.setOnClickListener { openPdf("terms.pdf") }
        adapter = IntoPagerAdapter(
            data,
            this
        )
        pager.adapter = adapter
        indicator.setViewPager2(pager)

        button_dalee.setOnClickListener {
            if (pager.currentItem != data.size - 1) {
                pager.setCurrentItem(pager.currentItem + 1, true)
            } else {
                startActivityForResult(Intent(this, MainActivity::class.java), 2)
                LocalStorage.instance.isFirst = false
            }
        }

        changeStatusBarColor(Color.parseColor("#6200EE").toDarkenColor())
        changeNavigationBarColor(Color.parseColor("#6200EE").toDarkenColor())
    }

    private fun openPdf(name: String) {
        // Open the PDF file from raw folder
        val inputStream =
            when (name) {
                "instruction.pdf" -> resources.openRawResource(R.raw.instruction)
                else -> resources.openRawResource(R.raw.terms)
            }
        // Copy the file to the cache folder
        inputStream.use { inputStream ->
            val file = File(cacheDir, name)
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }

        val cacheFile = File(cacheDir, name)

        // Get the URI of the cache file from the FileProvider
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", cacheFile)
        if (uri != null) {
            // Create an intent to open the PDF in a third party app
            val pdfViewIntent = Intent(Intent.ACTION_VIEW)
            pdfViewIntent.data = uri
            pdfViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(pdfViewIntent, "Choos PDF viewer"))
        }
    }
}
