package com.dc.lg_ac012.util.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import com.blankj.utilcode.util.ConvertUtils
import com.hzy.restaurant.R

class TipDialog private constructor(context: Context, themeResId: Int) : Dialog(context, themeResId), View.OnClickListener {
    private var mListener: TipClickListener? = null
    private var mCancel: TextView? = null
    private var mSure: TextView? = null
    private var title: TextView? = null
    private var message: TextView? = null
    private var close: ImageView? = null
    private var icon: ImageView? = null
    private var cardView: CardView? = null

    constructor(context: Context) : this(context, R.style.DialogStyle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_tip)
        mCancel = findViewById(R.id.cancel)
        mSure = findViewById(R.id.sure)
        title = findViewById(R.id.tip_title)
        message = findViewById(R.id.tip_message)
        close = findViewById(R.id.close)
        cardView = findViewById(R.id.card_view)
        mCancel?.setOnClickListener(this)
        mSure?.setOnClickListener(this)
        close?.setOnClickListener { dismiss() }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancel -> {
                dismiss()
                mListener?.clickCancel()
            }

            R.id.sure -> {
                dismiss()
                mListener?.clickSure()
            }
        }
    }

    /**
     * 模拟确认按钮点击
     */
    fun setSureClick() {
        mListener?.clickSure()
        dismiss()
    }

    fun setTipTitle(tipTitle: String?): TipDialog {
        if (TextUtils.isEmpty(tipTitle)) {
            title?.visibility = View.GONE
        } else {
            title?.visibility = View.VISIBLE
            title?.text = tipTitle
        }
        return this
    }

    fun setTipTitle(tipTitle: String?, color: Int): TipDialog {
        if (TextUtils.isEmpty(tipTitle)) {
            title?.visibility = View.GONE
        } else {
            title?.visibility = View.VISIBLE
            title?.text = tipTitle
            title?.setTextColor(color)
        }
        return this
    }

    fun setTipMessage(tipMessage: String?): TipDialog {
        if (TextUtils.isEmpty(tipMessage)) {
            message?.visibility = View.GONE
        } else {
            message?.visibility = View.VISIBLE
            message?.text = tipMessage
        }
        return this
    }

    fun setTipMessage(tipMessage: String?, drawable: Drawable): TipDialog {
        if (TextUtils.isEmpty(tipMessage)) {
            message?.visibility = View.GONE
        } else {
            message?.visibility = View.VISIBLE
            message?.text = tipMessage
        }
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        message?.compoundDrawablePadding = ConvertUtils.dp2px(5f)
        message?.setCompoundDrawables(null, drawable, null, null)
        return this
    }

    fun setGravity(gravity: Int = Gravity.CENTER_HORIZONTAL): TipDialog {
        message?.gravity = gravity
        return this
    }

    fun setCancelText(text: String): TipDialog {
        mCancel?.text = text
        return this
    }

    fun setCancelText(text: String, @DrawableRes drawable: Int): TipDialog {
        mCancel?.text = text
        mCancel?.setBackgroundResource(drawable)
        return this
    }

    fun setSureText(text: String): TipDialog {
        mSure?.text = text
        return this
    }

    fun setSureResource(@DrawableRes drawable: Int): TipDialog {
        mSure?.setBackgroundResource(drawable)
        return this
    }

    fun setSureText(text: String, @DrawableRes drawable: Int): TipDialog {
        mSure?.text = text
        mSure?.setBackgroundResource(drawable)
        return this
    }

    fun setSureTextColor(@ColorInt color: Int? = null, isBold: Boolean = false, size: Float? = null): TipDialog {
        color?.let { mSure?.setTextColor(color) }
        mSure?.typeface = if (isBold) Typeface.defaultFromStyle(Typeface.BOLD) else Typeface.defaultFromStyle(Typeface.NORMAL)
        size?.let { mSure?.textSize = size }
        return this
    }

    fun getMessage(): TextView? {
        message?.visibility = View.VISIBLE
        return message
    }

    fun setCloseVisible(visible: Int) {
        close?.visibility = visible
    }

    fun setDialogBackgroundColor(@ColorInt color: Int): TipDialog {
        cardView?.setCardBackgroundColor(color)
        return this
    }

    fun setIconResource(@DrawableRes drawable: Int, with: Int? = null, height: Int? = null): TipDialog {
        icon?.visibility = View.VISIBLE
        icon?.setImageResource(drawable)
        val layoutParams = icon?.layoutParams
        with?.let { layoutParams?.width = with }
        height?.let { layoutParams?.height = height }
        icon?.layoutParams = layoutParams
        return this
    }

    fun setIconVisible(visible: Int): TipDialog {
        icon?.visibility = visible
        return this
    }

    fun setTipClickListener(listener: TipClickListener?) {
        mListener = listener
    }

    fun setSingleButton(b: Boolean): TipDialog {
        if (b) {
            mCancel?.visibility = View.GONE
        } else {
            mCancel?.visibility = View.VISIBLE
        }
        return this
    }

    abstract class TipClickListener {
        open  fun clickCancel() {}
        open fun clickSure() {}
    }
}