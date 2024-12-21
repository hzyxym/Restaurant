package com.dc.lg_ac012.util.dialog

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.hzy.restaurant.R

class WaitDialog private constructor(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    private var mAnim: ObjectAnimator? = null
    private var messageTv: TextView? = null

    constructor(context: Context) : this(context, R.style.waitDialog)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_wait)
        val ivWait = findViewById<ImageView>(R.id.iv_wait)
        messageTv = findViewById(R.id.message)
        mAnim = ObjectAnimator.ofFloat(ivWait, "rotation", 0f, 360f)
        mAnim?.duration = 1000
        mAnim?.interpolator = LinearInterpolator()
        mAnim?.repeatCount = ValueAnimator.INFINITE
        mAnim?.repeatMode = ValueAnimator.RESTART
    }

    fun setMessage(message: String?) {
        if (message != null) messageTv?.text = message
    }

    override fun show() {
        super.show()
        mAnim?.start()
    }

    override fun dismiss() {
        super.dismiss()
        mAnim?.cancel()
    }
}