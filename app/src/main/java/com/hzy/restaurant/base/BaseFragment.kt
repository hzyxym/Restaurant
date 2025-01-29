package com.hzy.restaurant.base

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.dc.lg_ac012.util.dialog.WaitDialog
import com.hzy.restaurant.R
import com.hzy.restaurant.databinding.BaseFragmentBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    lateinit var mContext: Context
    private lateinit var mRootLl: FrameLayout
    private var contentView: View? = null
    private var emptyView: View? = null
    private var customizeView: View? = null
    private var errorView: View? = null
    private var waitDialog: WaitDialog? = null
    protected lateinit var binding: VB
    private lateinit var rootBinding: BaseFragmentBinding
    lateinit var config: Configuration
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        config = resources.configuration
        binding = getViewBinding(inflater, container)
        rootBinding = BaseFragmentBinding.inflate(inflater, container, false)
        mRootLl = rootBinding.rootLl
        if (getEmptyViewId() > 0) {
            emptyView = LayoutInflater.from(mContext).inflate(getEmptyViewId(), null)
            mRootLl.addView(emptyView)
        }
        if (setCustomizeViewId() > 0) {
            customizeView = LayoutInflater.from(mContext).inflate(setCustomizeViewId(), null)
            mRootLl.addView(customizeView)
        }
        if (setErrorViewId() > 0) {
            errorView = LayoutInflater.from(mContext).inflate(setErrorViewId(), null)
            mRootLl.addView(errorView)
        }
        contentView = binding.root
        mRootLl.addView(contentView)
        mRootLl.bringChildToFront(contentView)
        return rootBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(rootBinding.root)
        initLocal()
        createObserver()
    }

    //加载布局
    protected abstract fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): VB

    protected open fun getEmptyViewId(): Int = 0

    protected open fun setCustomizeViewId(): Int = 0

    protected open fun setErrorViewId(): Int = 0

    protected open fun setNetWorkErrorViewId(): Int = 0

    protected open fun initLocal() {}

    protected open fun initViews(rootView: View?) {}

    protected open fun createObserver() {}

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
    }

    fun showLoading(message: String? = getString(R.string.loading)) {
        showDialog(message, null)
    }

    fun showDialog(message: String?, timeoutMillis: Long? = null) {
        if (waitDialog == null) {
            waitDialog = WaitDialog(mContext)
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

    override fun onPause() {
        super.onPause()
        goneLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        goneLoading()
    }

    val calenderStart: Calendar
        get() {
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = 2010
            calendar[Calendar.MONTH] = 0
            calendar[Calendar.DAY_OF_MONTH] = 1
            return calendar
        }

    val calenderEnd: Calendar
        get() {
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = 2030
            calendar[Calendar.MONTH] = 0
            calendar[Calendar.DAY_OF_MONTH] = 1
            return calendar
        }

    fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(@StringRes resId: Int) {
        Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()
    }

    @ColorInt
    fun getCompatColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(requireContext(), id)
    }

    fun getCompatDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), id)
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
