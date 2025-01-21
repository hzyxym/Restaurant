package com.hzy.restaurant.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.dc.lg_ac012.util.dialog.TipDialog
import com.dc.lg_ac012.util.dialog.WaitDialog
import com.hzy.restaurant.R
import com.hzy.restaurant.databinding.BaseActivityBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    //ViewBinding 视图
    protected lateinit var binding: VB
    private lateinit var rootBinding: BaseActivityBinding
    private var contentView: View? = null
    private var emptyView: View? = null
    private var customizeView: View? = null
    private var errorView: View? = null
    private var networkErrorView: View? = null
    private lateinit var mRootLl: FrameLayout
    private var waitDialog: WaitDialog? = null
    private var tipDialog: TipDialog? = null
    lateinit var config: Configuration

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        config = resources.configuration
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(getCompatColor(R.color.title_bar)))
        rootBinding = BaseActivityBinding.inflate(layoutInflater)
        setContentView(rootBinding.root)
        setActivityPadding()
        findViewById<View>(R.id.left).setOnClickListener {
            leftIconClick() //返回键
        }
        findViewById<View>(R.id.right).setOnClickListener {
            rightIconClick() //右上角
        }
        mRootLl = findViewById(R.id.root_ll)
        mRootLl.removeAllViews()
        if (getEmptyViewId() > 0) {
            emptyView = LayoutInflater.from(this).inflate(getEmptyViewId(), null)
            mRootLl.addView(emptyView)
        }
        if (setCustomizeViewId() > 0) {
            customizeView = LayoutInflater.from(this).inflate(setCustomizeViewId(), null)
            mRootLl.addView(customizeView)
        }
        if (setErrorViewId() > 0) {
            errorView = LayoutInflater.from(this).inflate(setErrorViewId(), null)
            mRootLl.addView(errorView)
        }
        if (setNetWorkErrorViewId() > 0) {
            networkErrorView = LayoutInflater.from(this).inflate(setNetWorkErrorViewId(), null)
            mRootLl.addView(networkErrorView)
        }
        binding = getViewBinding()
        contentView = binding.root
        mRootLl.addView(contentView)
        mRootLl.bringChildToFront(contentView)
        initLocal()
        createObserver()
    }

    protected abstract fun getViewBinding(): VB

    protected open fun getEmptyViewId(): Int = 0

    protected open fun setCustomizeViewId(): Int = 0

    protected open fun setErrorViewId(): Int = 0

    protected open fun setNetWorkErrorViewId(): Int = 0

    protected open fun getEmptyView(): View? {
        return emptyView
    }

    protected open fun initLocal() {}

    protected open fun createObserver() {}

    protected open fun leftIconClick() {
        finish()
    }

    protected open fun rightIconClick() {
    }

    fun getRightText(): TextView {
        return rootBinding.toolbar.right
    }

    fun setToolsBarVisible(show: Boolean) {
        rootBinding.toolbar.toolbarLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setToolBarBackground(@ColorInt color: Int) {
        rootBinding.toolbar.toolbarLayout.setBackgroundColor(color)
    }

    fun setTitle(title: String?) {
        rootBinding.toolbar.title.text = title
    }

    fun setRightText(rightText: String?) {
        rootBinding.toolbar.right.text = rightText
    }

    fun setRightTextColor(color: Int) {
        rootBinding.toolbar.right.setTextColor(color)
    }

    fun setLeftText(leftText: String?) {
        rootBinding.toolbar.left.text = leftText
    }

    fun setLeftBg(resId: Int) {
        rootBinding.toolbar.left.setBackgroundResource(resId)
    }

    fun setLeftIcon(resId: Int) {
        val drawable = ContextCompat.getDrawable(this, resId)
        drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        rootBinding.toolbar.left.setCompoundDrawables(drawable, null, null, null)
    }

    fun setRightIcon(resId: Int) {
        val drawable = ContextCompat.getDrawable(this, resId)
        drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        rootBinding.toolbar.right.setCompoundDrawables(drawable, null, null, null)
    }

    fun showContent() {
        goneLoading()
        mRootLl.bringChildToFront(contentView)
    }

    fun showEmpty() {
        goneLoading()
        mRootLl.bringChildToFront(emptyView)
    }

    /**
     * 接口需要时使用该方法
     */
    fun showCustomizeView() {
        goneLoading()
        mRootLl.bringChildToFront(customizeView)
    }

    /**
     * 接口需要时使用该方法
     */
    fun showErrorView() {
        goneLoading()
        mRootLl.bringChildToFront(errorView)
    }

    open fun showError() {
        goneLoading()
    }

    open fun showNoNetwork() {
        goneLoading()
        mRootLl.bringChildToFront(networkErrorView)
    }

    fun showLoading(message: String? = getString(R.string.loading)) {
        showLoading(message, null)
    }

    fun showLoading(message: String? = getString(R.string.loading), timeoutMillis: Long? = null) {
        if (waitDialog == null) {
            waitDialog = WaitDialog(this)
        }
        if (!waitDialog!!.isShowing) {
            waitDialog!!.show()
            waitDialog!!.setCancelable(false)
        }
        waitDialog!!.setMessage(message)
        if (timeoutMillis != null) {
            startCountdownTimer(timeoutMillis)
        }
    }

    fun goneLoading() {
        if (waitDialog != null && waitDialog!!.isShowing) waitDialog!!.dismiss()
        stopCountdownTimer()
    }

    public override fun onPause() {
        super.onPause()
        goneLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        goneLoading()
        if (tipDialog != null && tipDialog!!.isShowing) {
            tipDialog!!.dismiss()
            tipDialog = null
        }
    }

    fun getTipDialog(): TipDialog {
        if (tipDialog == null) {
            tipDialog = TipDialog(this)
        }
        tipDialog?.setSureResource(R.drawable.tip_dialog_right_selector)
        tipDialog?.setSureTextColor(getCompatColor(R.color.white))
        tipDialog?.setIconVisible(View.GONE)
        tipDialog?.setGravity(Gravity.CENTER_HORIZONTAL)
        return tipDialog!!
    }

    private fun setActivityPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.base)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * 解决android 8.0 Only fullscreen opaque activities can request orientation 问题
     */
    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating) {
            return
        }
        super.setRequestedOrientation(requestedOrientation)
    }

    private val isTranslucentOrFloating: Boolean
        @SuppressLint("PrivateApi")
        get() {
            var isTranslucentOrFloating = false
            try {
                val styleableRes = Class.forName("com.android.internal.R\$styleable")
                    .getField("Window")[null] as IntArray
                val ta = obtainStyledAttributes(styleableRes)
                val m = ActivityInfo::class.java.getMethod(
                    "isTranslucentOrFloating",
                    TypedArray::class.java
                )
                m.isAccessible = true
                isTranslucentOrFloating = m.invoke(null, ta) as Boolean
                m.isAccessible = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return isTranslucentOrFloating
        }

    fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    @ColorInt
    fun getCompatColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(this, id)
    }

    fun getCompatDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(this, id)
    }

    private var countdownJob: Job? = null

    // 启动倒计时取消loading
    private fun startCountdownTimer(timeoutMillis: Long) {
        countdownJob = lifecycleScope.launch {
            delay(timeoutMillis)
            goneLoading()
        }
    }

    //取消倒计时
    private fun stopCountdownTimer() {
        if (countdownJob?.isActive == true) {
            countdownJob?.cancel()
        }
    }
}
