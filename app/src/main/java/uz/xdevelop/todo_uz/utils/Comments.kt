package uz.xdevelop.todo_uz.utils

//change params of view

/*val displayMetrics = DisplayMetrics()
windowManager.defaultDisplay.getMetrics(displayMetrics)

val displayWidth = displayMetrics.widthPixels
val displayHeight = displayMetrics.heightPixels

val layoutParams = WindowManager.LayoutParams()
layoutParams.copyFrom(dialog.window?.attributes)

val dialogWindowWidth = displayWidth * 0.7f.toInt()
val dialogWindowHeight = displayHeight * 0.5f.toInt()

layoutParams.width = dialogWindowWidth
layoutParams.height = dialogWindowHeight

dialog.window?.attributes = layoutParams*/


//create Animations

//    lateinit var animator: BaseItemAnimator
//    private lateinit var recyclerView: RecyclerView
/*createAnimations()
recyclerView = parent as RecyclerView
recyclerView.itemAnimator = animator*/

/*private fun createAnimations() {
    animator = object : BaseItemAnimator() {
        override fun animateRemoveImpl(holder: RecyclerView.ViewHolder?) {
            ViewCompat.animate(holder!!.itemView)
                .translationX(holder.itemView.rootView.width.toFloat())
                .setDuration(removeDuration)
                .setInterpolator(mInterpolator)
                .setListener(DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start()
        }

        override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder?) {
            ViewCompat.setTranslationX(
                holder!!.itemView,
                (-holder.itemView.rootView.width).toFloat()
            )
        }

        override fun animateAddImpl(holder: RecyclerView.ViewHolder?) {
            ViewCompat.animate(holder!!.itemView)
                .translationX(0f)
                .setDuration(addDuration)
                .setInterpolator(mInterpolator)
                .setListener(DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start()
        }
    }
}*/