package com.hzy.restaurant.utils.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.hzy.restaurant.R
import com.hzy.restaurant.databinding.SingleTipDialogBinding

/**
 * Created by hzy in 2024/2/18
 * description: 车队提示dialog
 * */
class SingleTipDialog @JvmOverloads constructor(context: Context, themeResId: Int = R.style.DialogStyle) : Dialog(context, themeResId) {
    private lateinit var binding: SingleTipDialogBinding
    private var listener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SingleTipDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.close.setOnClickListener {
            this.dismiss()
        }
        binding.ok.setOnClickListener {
            listener?.invoke()
            this.dismiss()
        }
    }
    //设置标题
    fun setTitleText(title: String): SingleTipDialog {
        binding.title.text = title
        return this
    }

    //设置内容
    fun setContentText(content: String): SingleTipDialog {
        binding.content.text = content
        return this
    }

    //设置按钮文字
    fun setOkText(text: String): SingleTipDialog {
        binding.ok.text = text
        return this
    }

    //设置按钮文字及背景
    fun setOkText(text: String, @DrawableRes drawable: Int): SingleTipDialog {
        binding.ok.text = text
        binding.ok.setBackgroundResource(drawable)
        return this
    }

    //设置按钮背景
    fun setOkBackground(@DrawableRes drawable: Int, @ColorInt textColor: Int): SingleTipDialog {
        binding.ok.setBackgroundResource(drawable)
        binding.ok.setTextColor(textColor)
        return this
    }

    //设置按钮点击监听
    fun setDialogListener(listener: () -> Unit): SingleTipDialog  {
        this.listener = listener
        return this
    }

    //设置关闭按钮是否可用
    fun setCloseVisible(isVisible: Boolean): SingleTipDialog  {
        binding.close.visibility = if (isVisible) View.VISIBLE else View.GONE
        return this
    }

    fun setContentGravity(gravity: Int = Gravity.CENTER_HORIZONTAL): SingleTipDialog {
        binding.content.gravity = gravity
        return this
    }

    //设置点击外部取消
    fun setCanceledOnTouch(cancel: Boolean = true): SingleTipDialog {
        this.setCanceledOnTouchOutside(cancel)
        return this
    }

}