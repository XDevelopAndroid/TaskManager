package uz.xdevelop.todo_uz.utils.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }

    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.changeStatusBarColorWithAnimation(colorTo: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val colorFrom = window.statusBarColor
        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimator.duration = 600
        colorAnimator.addUpdateListener {
            window.statusBarColor = (it.animatedValue as Int)
        }
        colorAnimator.start()
    }
}

fun AppCompatActivity.changeNavigationBarColorWithAnimation(colorTo: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val colorFrom = window.navigationBarColor
        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimator.duration = 600
        colorAnimator.addUpdateListener {
            window.navigationBarColor = (it.animatedValue as Int)
        }
        colorAnimator.start()
    }
}

fun AppCompatActivity.changeStatusBarColor(color: Int){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = color
    }
}

fun AppCompatActivity.changeNavigationBarColor(color: Int){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.navigationBarColor = color
    }
}