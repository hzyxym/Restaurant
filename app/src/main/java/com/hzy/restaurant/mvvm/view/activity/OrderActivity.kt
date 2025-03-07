package com.hzy.restaurant.mvvm.view.activity

import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContentProviderCompat.requireContext
import com.dc.tc601.util.dialog.MonthDialog
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.databinding.ActivityOrderBinding
import com.hzy.restaurant.utils.TimeUtils
import java.util.Calendar

/**
 * Created by hzy in 2025/3/6
 * description:
 * */
class OrderActivity : BaseActivity<ActivityOrderBinding>(), OnClickListener {
    private lateinit var mCalendar: Calendar
    override fun getViewBinding(): ActivityOrderBinding {
        return ActivityOrderBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()

        mCalendar = Calendar.getInstance()
        mCalendar.set(Calendar.DAY_OF_MONTH, 1)
        mCalendar.set(Calendar.HOUR_OF_DAY, 0)
        mCalendar.set(Calendar.MINUTE, 0)
        mCalendar.set(Calendar.SECOND, 0)
        mCalendar.set(Calendar.MILLISECOND, 0)
        binding.cal.text = TimeUtils.getyyyyMMTime(mCalendar.time.time)
        binding.rightCal.isEnabled = false
        binding.leftCal.setOnClickListener(this)
        binding.cal.setOnClickListener(this)
        binding.rightCal.setOnClickListener(this)

        binding.view.setBackgroundResource(R.drawable.home_detection)

    }

    override fun onClick(v: View?) {
        when(v) {
            binding.leftCal -> {
                mCalendar.add(Calendar.MONTH, -1)
                binding.cal.text = TimeUtils.getyyyyMMTime(mCalendar.time.time)
                binding.rightCal.isEnabled =
                    mCalendar.timeInMillis <= Calendar.getInstance().timeInMillis && !TimeUtils.getyyyyMMTime(
                        mCalendar.timeInMillis
                    )!!.contentEquals(
                        TimeUtils.getyyyyMMTime(
                            Calendar.getInstance().timeInMillis
                        )
                    )
            }

            binding.cal -> {

            }

            binding.rightCal -> {

            }
        }
    }

    private var monthDialog: MonthDialog? = null

    private fun onMonthPicker() {
        monthDialog = MonthDialog(this).apply {
            this.show()
            this.setMyAdapt(mCalendar.time.time)
            this.setMonthClickListener(object : MonthDialog.MonthClickListener {
                override fun clickMonth(time: Long, isAll: Boolean) {
                    mCalendar.timeInMillis = time
                    binding.rightCal.isEnabled =
                        mCalendar.timeInMillis <= Calendar.getInstance().timeInMillis && !TimeUtils.getyyyyMMTime(
                            mCalendar.timeInMillis
                        )!!.contentEquals(
                            TimeUtils.getyyyyMMTime(
                                Calendar.getInstance().timeInMillis
                            )
                        )
                    binding.cal.text = TimeUtils.getyyyyMMTime(mCalendar.time.time)
//                    getHistory()
                }

                override fun clickLeft(time: Long) {
//                    calculate(time)
                }

                override fun clickRight(time: Long) {
//                    calculate(time)
                }
            })
//            calculate(mCalendar.time.time)
        }
    }
}