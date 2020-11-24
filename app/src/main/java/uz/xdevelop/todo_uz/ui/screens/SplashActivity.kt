package uz.xdevelop.todo_uz.ui.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uz.xdevelop.todo_uz.R
import java.util.concurrent.Executors

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Executors.newSingleThreadExecutor().execute {
            Thread.sleep(2000)
            startActivityForResult(Intent(this, IntoActivity::class.java), 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1 && requestCode == 1) {
            finish()
        }
    }
}