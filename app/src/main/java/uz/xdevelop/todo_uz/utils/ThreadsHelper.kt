package uz.xdevelop.contactonlinetrainingroommvp.ui.presenters

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

abstract class ThreadsHelper {
    private val handle = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    fun runOnUIThread(f: () -> Unit) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            f()
        } else {
            handle.post { f() }
        }
    }

    fun runOnWorkerThread(f: () -> Unit) {
        executor.execute(f)
    }
}